package com.hazratbilal.notecraft.remote

import android.util.Log
import com.hazratbilal.notecraft.utils.Constant
import org.json.JSONObject
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException


suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): NetworkResult<T> {
    return try {
        val response = apiCall()
        if (response.isSuccessful && response.body() != null) {

            NetworkResult.Success(response.body()!!)

        } else if (response.errorBody() != null) {

            val errorText = response.errorBody()?.charStream()?.readText()
            val errorMessage = try {
                JSONObject(errorText.toString()).getString("message")
            } catch (e: Exception) {
                "Something went wrong. Error code: ${response.code()}"
            }

            Log.e(Constant.LOG_TAG, "Raw error body: $errorText")
            if (response.code() == 401) {
                NetworkResult.AuthError(errorMessage)
            } else {
                NetworkResult.Error(errorMessage)
            }

        } else {
            NetworkResult.Error("Request failed. Please try again")
        }
    } catch (e: SocketTimeoutException) {
        NetworkResult.Error("Server is too slow. Please try again")
    } catch (e: IOException) {
        NetworkResult.Error("Network error. Please check your connection")
    } catch (e: Exception) {
        NetworkResult.Error("Something went wrong: ${e.localizedMessage}")
    }
}