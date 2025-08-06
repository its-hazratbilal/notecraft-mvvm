package com.hazratbilal.notecraft.remote

import com.hazratbilal.notecraft.model.NoteRequest
import com.hazratbilal.notecraft.model.NotesResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST


interface NotesAPI {

    @POST("add_note")
    @Headers("Require-Auth: true")
    suspend fun addNote(@Body noteRequest: NoteRequest): Response<NotesResponse>

    @GET("notes")
    @Headers("Require-Auth: true")
    suspend fun getNotes(): Response<NotesResponse>

    @POST("delete_note")
    @Headers("Require-Auth: true")
    suspend fun deleteNote(@Body noteRequest: NoteRequest): Response<NotesResponse>

    @POST("update_note")
    @Headers("Require-Auth: true")
    suspend fun updateNote(@Body noteRequest: NoteRequest): Response<NotesResponse>

}