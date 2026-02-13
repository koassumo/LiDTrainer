package org.igo.lidtrainer.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.igo.lidtrainer.core.time.TimeProvider
import org.igo.lidtrainer.data.dto.PreNote
import org.igo.lidtrainer.data.mapper.mapToNote
import org.igo.lidtrainer.db.AppDatabase
import org.igo.lidtrainer.domain.model.Note
import org.igo.lidtrainer.domain.rep_interface.NoteRepository
import org.igo.lidtrainer.domain.rep_interface.NoteStatistics

class NoteRepositoryImpl(
    private val db: AppDatabase,
    private val timeProvider: TimeProvider
) : NoteRepository {

    private val queries = db.noteQueries

    override fun getAllNotes(): Flow<List<Note>> {
        return queries.getAllNotes()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities -> entities.map { it.mapToNote() } }
    }

    override fun getNoteById(id: Long): Flow<Note?> {
        return queries.getNoteById(id)
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map { it?.mapToNote() }
    }

    override fun getNotesByCategory(category: String): Flow<List<Note>> {
        return queries.getNotesByCategory(category)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities -> entities.map { it.mapToNote() } }
    }

    override fun getNotesByBundesland(bundesland: String): Flow<List<Note>> {
        return queries.getNotesByBundesland(bundesland)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities -> entities.map { it.mapToNote() } }
    }

    override suspend fun insertNotes(notes: List<Note>) {
        withContext(Dispatchers.IO) {
            db.transaction {
                notes.forEach { note ->
                    queries.insertNote(
                        id = note.id,
                        questionNumber = note.questionNumber.toLong(),
                        questionTextDe = note.questionTextDe,
                        answer1De = note.answer1De,
                        answer2De = note.answer2De,
                        answer3De = note.answer3De,
                        answer4De = note.answer4De,
                        langNative = note.langNative,
                        questionTextNative = note.questionTextNative,
                        answer1Native = note.answer1Native,
                        answer2Native = note.answer2Native,
                        answer3Native = note.answer3Native,
                        answer4Native = note.answer4Native,
                        correctAnswerIndex = note.correctAnswerIndex.toLong(),
                        category = note.category,
                        imageUrl = note.imageUrl,
                        userAnswerIndex = note.userAnswerIndex?.toLong(),
                        isAnsweredCorrectly = note.isAnsweredCorrectly?.let { if (it) 1L else 0L },
                        lastAnsweredAt = note.lastAnsweredAt
                    )
                }
            }
        }
    }

    override suspend fun updateUserAnswer(noteId: Long, answerIndex: Int, isCorrect: Boolean) {
        withContext(Dispatchers.IO) {
            queries.updateUserAnswer(
                userAnswerIndex = answerIndex.toLong(),
                isAnsweredCorrectly = if (isCorrect) 1L else 0L,
                lastAnsweredAt = timeProvider.nowEpochMillis(),
                id = noteId
            )
        }
    }

    override suspend fun updateNativeTranslations(langCode: String, translations: List<PreNote>) {
        withContext(Dispatchers.IO) {
            db.transaction {
                translations.forEach { preNote ->
                    queries.updateNativeTranslation(
                        langNative = langCode,
                        questionTextNative = preNote.questionText,
                        answer1Native = preNote.answer1,
                        answer2Native = preNote.answer2,
                        answer3Native = preNote.answer3,
                        answer4Native = preNote.answer4,
                        id = preNote.id.toLong()
                    )
                }
            }
        }
    }

    override suspend fun clearAllUserProgress() {
        withContext(Dispatchers.IO) {
            queries.clearAllUserProgress()
        }
    }

    override suspend fun isDbEmpty(): Boolean {
        return withContext(Dispatchers.IO) {
            queries.countNotes().executeAsOne() == 0L
        }
    }

    override suspend fun getStatistics(): NoteStatistics {
        return withContext(Dispatchers.IO) {
            NoteStatistics(
                totalCount = queries.countNotes().executeAsOne(),
                answeredCount = queries.countAnswered().executeAsOne(),
                correctCount = queries.countCorrect().executeAsOne()
            )
        }
    }

    override suspend fun getStatisticsByBundesland(bundesland: String): NoteStatistics {
        return withContext(Dispatchers.IO) {
            NoteStatistics(
                totalCount = queries.countNotesByBundesland(bundesland).executeAsOne(),
                answeredCount = queries.countAnsweredByBundesland(bundesland).executeAsOne(),
                correctCount = queries.countCorrectByBundesland(bundesland).executeAsOne()
            )
        }
    }
}
