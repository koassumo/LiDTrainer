package org.igo.lidtrainer.ui.screen.lesson

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.igo.lidtrainer.domain.model.Note
import org.igo.lidtrainer.domain.rep_interface.NoteRepository
import org.igo.lidtrainer.domain.rep_interface.SettingsRepository

class LessonViewModel(
    private val noteRepository: NoteRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes.asStateFlow()

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex.asStateFlow()

    // Map<noteId, Set<clickedAnswerIndexes>> — какие ответы нажаты для каждой карточки
    private val _clickedAnswers = MutableStateFlow<Map<Long, Set<Int>>>(emptyMap())
    val clickedAnswers: StateFlow<Map<Long, Set<Int>>> = _clickedAnswers.asStateFlow()

    val showTranslation = MutableStateFlow(false)

    val showCorrectImmediately: StateFlow<Boolean> = settingsRepository.showCorrectImmediatelyState

    val isTranslationAvailable: StateFlow<Boolean> get() = _isTranslationAvailable
    private val _isTranslationAvailable = MutableStateFlow(true)

    init {
        viewModelScope.launch {
            settingsRepository.languageContentState.collect { langCode ->
                val available = langCode != "de"
                _isTranslationAvailable.value = available
                if (!available) showTranslation.value = false
            }
        }
        loadNotes()
    }

    private fun loadNotes() {
        viewModelScope.launch {
            settingsRepository.bundeslandState.collectLatest { bundesland ->
                if (bundesland.isNotEmpty()) {
                    noteRepository.getNotesByBundesland(bundesland).collectLatest { notesList ->
                        _notes.value = notesList
                        // Сбрасываем индекс если он за пределами нового списка
                        if (_currentIndex.value >= notesList.size) {
                            _currentIndex.value = 0
                        }
                    }
                } else {
                    noteRepository.getAllNotes().collectLatest { notesList ->
                        _notes.value = notesList
                    }
                }
            }
        }
    }

    fun onAnswerClick(answerIndex: Int, noteId: Long) {
        val note = _notes.value.find { it.id == noteId } ?: return

        // Добавляем ответ в набор нажатых
        val currentClicked = _clickedAnswers.value.toMutableMap()
        val existing: Set<Int> = currentClicked[noteId] ?: emptySet()
        currentClicked[noteId] = existing + answerIndex
        _clickedAnswers.value = currentClicked

        // Сохраняем в БД если правильный ответ
        val isCorrect = answerIndex == note.correctAnswerIndex
        if (isCorrect) {
            viewModelScope.launch {
                noteRepository.updateUserAnswer(noteId, answerIndex, true)
            }
        }
    }

    fun toggleTranslation() {
        showTranslation.value = !showTranslation.value
    }

    fun setCurrentIndex(index: Int) {
        _currentIndex.value = index
    }
}
