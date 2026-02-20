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
            val versionInfo = firebaseApi.fetchVersionInfo()
            val localVersion = settingsRepository.getPackVersion()

            if (versionInfo.packVersion <= localVersion) return

            // Download German pack
            val deJsonString = firebaseApi.fetchPackJson("pack_de.json")
            val dePack = json.decodeFromString<JPack>(deJsonString)
            val dePreNotes = dePack.jNotes.map { it.mapToPreNote(dePack.language) }

            // Download native language pack
            val nativePreNotes = if (nativeLanguageCode != "de") {
                try {
                    val nativeJsonString = firebaseApi.fetchPackJson("pack_$nativeLanguageCode.json")
                    val nativePack = json.decodeFromString<JPack>(nativeJsonString)
                    nativePack.jNotes.map { it.mapToPreNote(nativePack.language) }
                } catch (e: Exception) {
                    null
                }
            } else null

            // Merge and update DB
            val notes = dePreNotes.mapIndexed { index, dePreNote ->
                mergePreNotes(dePreNote, nativePreNotes?.getOrNull(index))
            }

            noteRepository.insertNotes(notes)
            settingsRepository.setPackVersion(versionInfo.packVersion)
        } catch (_: Exception) {
            // Network error — continue with local data
        }
    }
}
