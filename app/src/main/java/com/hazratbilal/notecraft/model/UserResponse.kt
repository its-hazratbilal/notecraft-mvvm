package com.hazratbilal.notecraft.model
import com.google.gson.annotations.SerializedName

data class UserResponse (
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("user")
    var user: User,

) {
    data class User(
        @SerializedName("full_name")
        var full_name: String = "",
        @SerializedName("email")
        var email: String = "",
        @SerializedName("gender")
        var gender: String = "",
        @SerializedName("token")
        var token: String = ""

    )
}