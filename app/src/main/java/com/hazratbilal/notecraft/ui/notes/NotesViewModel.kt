package com.hazratbilal.notecraft.ui.notes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hazratbilal.notecraft.model.NotesResponse
import com.hazratbilal.notecraft.model.NoteRequest
import com.hazratbilal.notecraft.repository.NotesRepository
import com.hazratbilal.notecraft.remote.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class NotesViewModel @Inject constructor(private val notesRepository: NotesRepository): ViewModel() {

    private val _notesResponseLiveData = MutableLiveData<NetworkResult<NotesResponse>>()
    val notesResponseLiveData: LiveData<NetworkResult<NotesResponse>> get() = _notesResponseLiveData

    fun clearState() {
        _notesResponseLiveData.value = NetworkResult.Idle()
    }

    fun addNote(noteRequest: NoteRequest) {
        viewModelScope.launch {
            _notesResponseLiveData.value = NetworkResult.Loading()
            val result = notesRepository.addNote(noteRequest)
            _notesResponseLiveData.value = result
        }
    }

    fun getNotes() {
        viewModelScope.launch {
            _notesResponseLiveData.value = NetworkResult.Loading()
            val result = notesRepository.getNotes()
            _notesResponseLiveData.value = result
        }
    }

    fun updateNote(noteRequest: NoteRequest) {
        viewModelScope.launch {
            _notesResponseLiveData.value = NetworkResult.Loading()
            val result = notesRepository.updateNote(noteRequest)
            _notesResponseLiveData.value = result
        }
    }

    fun deleteNote(noteRequest: NoteRequest) {
        viewModelScope.launch {
            _notesResponseLiveData.value = NetworkResult.Loading()
            val result = notesRepository.deleteNote(noteRequest)
            _notesResponseLiveData.value = result
        }
    }

}