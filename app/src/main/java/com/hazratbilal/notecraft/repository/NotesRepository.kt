package com.hazratbilal.notecraft.repository

import com.hazratbilal.notecraft.model.NoteRequest
import com.hazratbilal.notecraft.model.NotesResponse
import com.hazratbilal.notecraft.remote.NotesAPI
import com.hazratbilal.notecraft.remote.NetworkResult
import com.hazratbilal.notecraft.remote.safeApiCall
import javax.inject.Inject


class NotesRepository @Inject constructor(private val notesAPI: NotesAPI) {

    private val cachedData = mutableListOf<NotesResponse.Note>()

    suspend fun addNote(noteRequest: NoteRequest): NetworkResult<NotesResponse> {
        return safeApiCall { notesAPI.addNote(noteRequest) }
    }

    suspend fun getNotes(): NetworkResult<NotesResponse> {
        val result = safeApiCall { notesAPI.getNotes() }
        if (result is NetworkResult.Success && result.data?.notes != null) {
            cachedData.clear()
            cachedData.addAll(result.data.notes)
        }
        return result
    }

    suspend fun updateNote(noteRequest: NoteRequest): NetworkResult<NotesResponse> {
        return safeApiCall { notesAPI.updateNote(noteRequest) }
    }

    suspend fun deleteNote(noteRequest: NoteRequest): NetworkResult<NotesResponse> {
        val result = safeApiCall { notesAPI.deleteNote(noteRequest) }
        return if (result is NetworkResult.Success && result.data?.success == true) {

            cachedData.removeIf { it.id == noteRequest.id }

            NetworkResult.Success(
                NotesResponse(
                    success = result.data.success,
                    message = result.data.message,
                    notes = ArrayList(cachedData)
                )
            )
        } else {
            result
        }
    }

}