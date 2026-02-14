package org.igo.lidtrainer.domain.rep_interface

import kotlinx.coroutines.flow.Flow
import org.igo.lidtrainer.data.dto.PreNote
import org.igo.lidtrainer.domain.model.Note

interface NoteRepository {
    fun getAllNotes(): Flow<List<Note>>
    fun getNoteById(id: Long): Flow<Note?>
    fun getNotesByCategory(category: String): Flow<List<Note>>
    fun getNotesByBundesland(bundesland: String): Flow<List<Note>>
    fun getNotesByRegional(bundesland: String): Flow<List<Note>>
    fun getFavoriteNotes(): Flow<List<Note>>
    fun getFavoriteNotesByBundesland(bundesland: String): Flow<List<Note>>
    suspend fun toggleFavorite(noteId: Long)
    suspend fun insertNotes(notes: List<Note>)
    suspend fun updateUserAnswer(noteId: Long, answerIndex: Int, isCorrect: Boolean)
    suspend fun updateNativeTranslations(langCode: String, translations: List<PreNote>)
    suspend fun clearAllUserProgress()
    suspend fun isDbEmpty(): Boolean
    suspend fun getStatistics(): NoteStatistics
    suspend fun getStatisticsByBundesland(bundesland: String): NoteStatistics
    suspend fun countGeneralNotes(): Long
    suspend fun countRegionalNotes(bundesland: String): Long
    suspend fun countFavorites(): Long
    suspend fun countFavoritesByBundesland(bundesland: String): Long
}

data class NoteStatistics(
    val totalCount: Long,
    val answeredCount: Long,
    val correctCount: Long
)
