package org.igo.lidtrainer.domain.usecase

import kotlinx.serialization.json.Json
import org.igo.lidtrainer.data.dto.JPack
import org.igo.lidtrainer.data.mapper.mapToPreNote
import org.igo.lidtrainer.data.mapper.mergePreNotes
import org.igo.lidtrainer.data.remote.FirebaseStorageApi
import org.igo.lidtrainer.domain.rep_interface.NoteRepository
import org.igo.lidtrainer.domain.rep_interface.SettingsRepository

class CheckAndUpdatePackUseCase(
    private val firebaseApi: FirebaseStorageApi,
    private val noteRepository: NoteRepository,
    private val settingsRepository: SettingsRepository
) {
    private val json = Json { ignoreUnknownKeys = true }

    suspend operator fun invoke(nativeLanguageCode: String) {
        try {
            println("PackUpdate: Checking for updates...")
            val versionInfo = firebaseApi.fetchVersionInfo()
            val localVersion = settingsRepository.getPackVersion()
            println("PackUpdate: Server version=${versionInfo.packVersion}, local version=$localVersion")

            if (versionInfo.packVersion <= localVersion) {
                println("PackUpdate: No update needed, versions match")
                return
            }

            println("PackUpdate: New version found, downloading packs...")

            // Download German pack
            val deJsonString = firebaseApi.fetchPackJson("pack_de.json")
            val dePack = json.decodeFromString<JPack>(deJsonString)
            val dePreNotes = dePack.jNotes.map { it.mapToPreNote(dePack.language) }
            println("PackUpdate: Downloaded pack_de.json (${dePreNotes.size} questions)")

            // Download native language pack
            val nativePreNotes = if (nativeLanguageCode != "de") {
                try {
                    val nativeJsonString = firebaseApi.fetchPackJson("pack_$nativeLanguageCode.json")
                    val nativePack = json.decodeFromString<JPack>(nativeJsonString)
                    val preNotes = nativePack.jNotes.map { it.mapToPreNote(nativePack.language) }
                    println("PackUpdate: Downloaded pack_$nativeLanguageCode.json (${preNotes.size} questions)")
                    preNotes
                } catch (e: Exception) {
                    println("PackUpdate: Native pack not available: ${e.message}")
                    null
                }
            } else null

            // Merge and update DB
            val notes = dePreNotes.mapIndexed { index, dePreNote ->
                mergePreNotes(dePreNote, nativePreNotes?.getOrNull(index))
            }

            noteRepository.insertNotes(notes)
            settingsRepository.setPackVersion(versionInfo.packVersion)
            println("PackUpdate: Update complete! Saved ${notes.size} questions, version=${versionInfo.packVersion}")
        } catch (e: Exception) {
            println("PackUpdate: Error — ${e.message}")
        }
    }
}
