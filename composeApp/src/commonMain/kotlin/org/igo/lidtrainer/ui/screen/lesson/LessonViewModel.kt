package org.igo.lidtrainer.ui.screen.lesson

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.igo.lidtrainer.domain.model.LessonFilter
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

    // Map<noteId, Set<clickedAnswerIndexes>> — какие ответы нажаты в текущей сессии
    private val _clickedAnswers = MutableStateFlow<Map<Long, Set<Int>>>(emptyMap())
    val clickedAnswers: StateFlow<Map<Long, Set<Int>>> = _clickedAnswers.asStateFlow()

    val showTranslation = MutableStateFlow(false)

    val showCorrectImmediately: StateFlow<Boolean> = settingsRepository.showCorrectImmediatelyState

    val isTranslationAvailable: StateFlow<Boolean> get() = _isTranslationAvailable
    private val _isTranslationAvailable = MutableStateFlow(true)

    private val _showSwipeHint = MutableStateFlow(!settingsRepository.hasSeenSwipeHint())
    val showSwipeHint: StateFlow<Boolean> = _showSwipeHint.asStateFlow()

    val contentLanguageCode: StateFlow<String> = settingsRepository.languageContentState

    private val _lessonFilter = MutableStateFlow(LessonFilter.ALL)
    val lessonFilter: StateFlow<LessonFilter> = _lessonFilter.asStateFlow()

    private var loadNotesJob: Job? = null

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

    fun setFilter(filter: LessonFilter) {
        _lessonFilter.value = filter
        _currentIndex.value = 0
        _clickedAnswers.value = emptyMap()
        _notes.value = emptyList()
        loadNotes()
    }

    private fun loadNotes() {
        loadNotesJob?.cancel()
        loadNotesJob = viewModelScope.launch {
            settingsRepository.bundeslandState.collectLatest { bundesland ->
                val filter = _lessonFilter.value

                if (filter == LessonFilter.PRACTICE_TEST) {
                    // Одноразовая случайная выборка: 30 общих + 3 региональных
                    val general = noteRepository.getNotesByCategory("GENERAL").first()
                    val regional = if (bundesland.isNotEmpty()) {
                        noteRepository.getNotesByRegional(bundesland).first()
                    } else emptyList()
                    val selected = general.shuffled().take(30) + regional.shuffled().take(3)
                    _notes.value = selected
                    return@collectLatest
                }

                val flow = when (filter) {
                    LessonFilter.ALL -> {
                        if (bundesland.isNotEmpty()) noteRepository.getNotesByBundesland(bundesland)
                        else noteRepository.getAllNotes()
                    }
                    LessonFilter.GENERAL -> noteRepository.getNotesByCategory("GENERAL")
                    LessonFilter.REGIONAL -> {
                        if (bundesland.isNotEmpty()) noteRepository.getNotesByRegional(bundesland)
                        else noteRepository.getNotesByCategory("GENERAL") // fallback
                    }
                    LessonFilter.FAVORITES -> {
                        if (bundesland.isNotEmpty()) noteRepository.getFavoriteNotesByBundesland(bundesland)
                        else noteRepository.getFavoriteNotes()
                    }
                    else -> noteRepository.getAllNotes()
                }
                flow.collectLatest { notesList ->
                    _notes.value = notesList
                    // Инициализируем clickedAnswers из БД для уже отвеченных вопросов
                    val currentClicked = _clickedAnswers.value.toMutableMap()
                    notesList.forEach { note ->
                        if (note.userAnswerIndex != null && note.id !in currentClicked) {
                            currentClicked[note.id] = setOf(note.userAnswerIndex)
                        }
                    }
                    _clickedAnswers.value = currentClicked
                    if (_currentIndex.value >= notesList.size) {
                        _currentIndex.value = 0
                    }
                }
            }
        }
    }

    fun onAnswerClick(answerIndex: Int, noteId: Long) {
        val note = _notes.value.find { it.id == noteId } ?: return

        val isCorrect = answerIndex == note.correctAnswerIndex

        // Добавляем ответ в набор нажатых
        val currentClicked = _clickedAnswers.value.toMutableMap()
        val existing: Set<Int> = currentClicked[noteId] ?: emptySet()
        currentClicked[noteId] = existing + answerIndex
        _clickedAnswers.value = currentClicked

        // Сохраняем в БД при первом ответе (ещё не было ответа в БД)
        if (note.userAnswerIndex == null) {
            viewModelScope.launch {
                noteRepository.updateUserAnswer(noteId, answerIndex, isCorrect)
            }
        }
    }

    fun toggleFavorite(noteId: Long) {
        viewModelScope.launch {
            noteRepository.toggleFavorite(noteId)
        }
    }

    fun toggleTranslation() {
        showTranslation.value = !showTranslation.value
    }

    fun setCurrentIndex(index: Int) {
        _currentIndex.value = index
    }

    fun onSwipeHintSeen() {
        _showSwipeHint.value = false
        settingsRepository.setSeenSwipeHint()
    }
}
