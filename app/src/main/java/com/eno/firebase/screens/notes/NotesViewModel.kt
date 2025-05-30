package com.eno.firebase.screens.notes

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eno.firebase.data.Note
import com.eno.firebase.data.NotesRepository
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.launch

class NotesViewModel : ViewModel() {
    private val repository = NotesRepository()

    // UI State
    private val _notes = mutableStateOf<List<Note>>(emptyList())
    val notes: State<List<Note>> = _notes

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    private val _selectedNote = mutableStateOf<Note?>(null)
    val selectedNote: State<Note?> = _selectedNote

    // Real-time listener
    private var notesListener: ListenerRegistration? = null

    init {
        startListeningToNotes()
    }

    private fun startListeningToNotes() {
        notesListener = repository.listenToUserNotes { notesList ->
            _notes.value = notesList
        }
    }

    // Create new note
    fun createNote(title: String, content: String, color: String = "#FFFFFF") {
        if (title.isBlank() && content.isBlank()) {
            _errorMessage.value = "Note cannot be empty"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            repository.addNote(title, content, color).fold(
                onSuccess = {
                    // Note will be automatically added to list via listener
                },
                onFailure = { exception ->
                    _errorMessage.value = exception.message ?: "Failed to create note"
                }
            )

            _isLoading.value = false
        }
    }

    // Update existing note
    fun updateNote(noteId: String, title: String, content: String, color: String) {
        if (title.isBlank() && content.isBlank()) {
            _errorMessage.value = "Note cannot be empty"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            repository.updateNote(noteId, title, content, color).fold(
                onSuccess = {
                    // Note will be automatically updated in list via listener
                },
                onFailure = { exception ->
                    _errorMessage.value = exception.message ?: "Failed to update note"
                }
            )

            _isLoading.value = false
        }
    }

    // Delete note
    fun deleteNote(noteId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            repository.deleteNote(noteId).fold(
                onSuccess = {
                    // Note will be automatically removed from list via listener
                },
                onFailure = { exception ->
                    _errorMessage.value = exception.message ?: "Failed to delete note"
                }
            )

            _isLoading.value = false
        }
    }

    // Load specific note for editing
    fun loadNote(noteId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            repository.getNoteById(noteId).fold(
                onSuccess = { note ->
                    _selectedNote.value = note
                },
                onFailure = { exception ->
                    _errorMessage.value = exception.message ?: "Failed to load note"
                }
            )

            _isLoading.value = false
        }
    }

    // Search notes
    fun searchNotes(query: String, onResult: (List<Note>) -> Unit) {
        viewModelScope.launch {
            repository.searchNotes(query).fold(
                onSuccess = { searchResults ->
                    onResult(searchResults)
                },
                onFailure = { exception ->
                    _errorMessage.value = exception.message ?: "Search failed"
                    onResult(emptyList())
                }
            )
        }
    }

    // Clear error message
    fun clearError() {
        _errorMessage.value = null
    }

    // Clear selected note
    fun clearSelectedNote() {
        _selectedNote.value = null
    }

    override fun onCleared() {
        super.onCleared()
        notesListener?.remove()
    }
}