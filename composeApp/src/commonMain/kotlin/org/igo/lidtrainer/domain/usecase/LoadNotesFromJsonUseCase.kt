package org.igo.lidtrainer.domain.usecase

import kotlinx.serialization.json.Json
import lidtrainer.composeapp.generated.resources.Res
import org.igo.lidtrainer.data.dto.JPack
import org.igo.lidtrainer.data.mapper.mapToPreNote
import org.igo.lidtrainer.data.mapper.mergePreNotes
import org.igo.lidtrainer.domain.rep_interface.NoteRepository
import org.jetbrains.compose.resources.ExperimentalResourceApi

class LoadNotesFromJsonUseCase(
    private val noteRepository: NoteRepository
) {
    private val json = Json { ignoreUnknownKeys = true }

    @OptIn(ExperimentalResourceApi::class)
    suspend operator fun invoke(nativeLanguageCode: String) {
        println("PackUpdate: LoadNotes invoke, lang=$nativeLanguageCode")
        if (!noteRepository.isDbEmpty()) {
            println("PackUpdate: DB not empty, skipping local load")
            return
        }
        println("PackUpdate: DB empty, loading bundled resources...")

        // Load German pack (always required)
        val deJsonString = Res.readBytes("files/pack_de.json").decodeToString()
        val dePack = json.decodeFromString<JPack>(deJsonString)
        val dePreNotes = dePack.jNotes.map { it.mapToPreNote(dePack.language) }

        // Load native language pack
        val nativePreNotes = try {
            val nativeJsonString = Res.readBytes("files/pack_$nativeLanguageCode.json").decodeToString()
            val nativePack = json.decodeFromString<JPack>(nativeJsonString)
            nativePack.jNotes.map { it.mapToPreNote(nativePack.language) }
        } catch (e: Exception) {
            null
        }

        // Merge DE + native into Note entities
        val notes = dePreNotes.mapIndexed { index, dePreNote ->
            mergePreNotes(dePreNote, nativePreNotes?.getOrNull(index))
        }

        noteRepository.insertNotes(notes)
        println("PackUpdate: Inserted ${notes.size} notes from bundled resources")
    }
}
