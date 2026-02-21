package org.igo.lidtrainer.ui.screen.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.igo.lidtrainer.domain.model.Note
import org.igo.lidtrainer.domain.rep_interface.NoteRepository
import org.igo.lidtrainer.domain.rep_interface.SettingsRepository

class StatisticsViewModel(
    private val noteRepository: NoteRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            val bundesland = settingsRepository.bundeslandState.value
            val notesFlow = if (bundesland.isNotEmpty()) {
                noteRepository.getNotesByBundesland(bundesland)
            } else {
                noteRepository.getAllNotes()
            }
            notesFlow.collect { _notes.value = it }
        }
    }
}
