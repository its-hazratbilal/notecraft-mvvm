package com.hazratbilal.notecraft.model
import com.google.gson.annotations.SerializedName

data class NotesResponse (
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("notes")
    var notes: ArrayList<Note>,

    ) {
    data class Note(
        @SerializedName("id")
        var id: String = "",
        @SerializedName("title")
        var title: String = "",
        @SerializedName("description")
        var description: String = "",
        @SerializedName("created_at")
        var created_at: String = ""

    )
}