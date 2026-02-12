package org.igo.lidtrainer.ui.screen.learn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.igo.lidtrainer.domain.model.Note
import org.igo.lidtrainer.domain.rep_interface.NoteRepository

class LearnViewModel(
    private val noteRepository: NoteRepository
) : ViewModel() {

    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes.asStateFlow()

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex.asStateFlow()

    // Map<noteId, Set<clickedAnswerIndexes>> — какие ответы нажаты для каждой карточки
    private val _clickedAnswers = MutableStateFlow<Map<Long, Set<Int>>>(emptyMap())
    val clickedAnswers: StateFlow<Map<Long, Set<Int>>> = _clickedAnswers.asStateFlow()

    init {
        loadNotes()
    }

    private fun loadNotes() {
        viewModelScope.launch {
            val notesList = noteRepository.getAllNotes().first()
            _notes.value = notesList
        }
    }

    fun onAnswerClick(answerIndex: Int) {
        val currentNote = _notes.value.getOrNull(_currentIndex.value) ?: return
        val noteId = currentNote.id

        // Добавляем ответ в набор нажатых
        val currentClicked = _clickedAnswers.value.toMutableMap()
        val existing: Set<Int> = currentClicked[noteId] ?: emptySet()
        currentClicked[noteId] = existing + answerIndex
        _clickedAnswers.value = currentClicked

        // Сохраняем в БД если правильный ответ
        val isCorrect = answerIndex == currentNote.correctAnswerIndex
        if (isCorrect) {
            viewModelScope.launch {
                noteRepository.updateUserAnswer(noteId, answerIndex, true)
            }
        }
    }

    fun goToNext() {
        if (_currentIndex.value < _notes.value.size - 1) {
            _currentIndex.value++
        }
    }

    fun goToPrevious() {
        if (_currentIndex.value > 0) {
            _currentIndex.value--
        }
    }
}
