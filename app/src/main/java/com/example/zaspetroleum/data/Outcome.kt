package com.example.zaspetroleum.data

import com.example.zaspetroleum.model.ApiError
import com.google.gson.Gson
import retrofit2.Response
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

// Every API call ends up as either Success(data) or Error(message)
sealed class Outcome<out T> {
    data class Success<out T>(val data: T) : Outcome<T>()
    data class Error(val message: String, val code: Int? = null) : Outcome<Nothing>()
}

// Wraps any API call so a failure becomes an Error message instead of a crash
suspend fun <T> safeApiCall(call: suspend () -> Response<T>): Outcome<T> {
    return try {
        val response = call()
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) Outcome.Success(body)
            else Outcome.Error("The server sent an empty response", response.code())
        } else {
            Outcome.Error(readError(response), response.code())
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: IOException) {
        Outcome.Error("Can't reach the server. Check your internet connection and try again.")
    } catch (e: Exception) {
        Outcome.Error("Something went wrong. Please try again.")
    }
}

// Reads Banathi's {"error": "..."} message so the user sees it
private fun <T> readError(response: Response<T>): String {
    val text = try { response.errorBody()?.string() } catch (e: Exception) { null }
    val message = try { Gson().fromJson(text, ApiError::class.java)?.error } catch (e: Exception) { null }
    return message ?: when (response.code()) {
        401 -> "Please log in again"
        403 -> "You don't have permission to do that"
        else -> "Something went wrong (error ${response.code()})"
    }
}