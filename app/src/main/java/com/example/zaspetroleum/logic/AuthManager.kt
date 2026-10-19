package com.example.zaspetroleum.logic

import com.example.zaspetroleum.data.ApiClient
import com.example.zaspetroleum.data.Outcome
import com.example.zaspetroleum.data.safeApiCall
import com.example.zaspetroleum.model.LoginRequest
import com.example.zaspetroleum.model.LoginResponse
import com.example.zaspetroleum.model.RegisterRequest
import com.example.zaspetroleum.util.Validators

class AuthManager {
    // Who is logged in (null = nobody)
    var currentUser: LoginResponse? = null
        private set

    suspend fun register(
        fullName: String?,
        email: String?,
        phone: String?,
        password: String?,
        confirmPassword: String?
    ): Outcome<Unit> {
        // Check the input first. If anything is wrong, never call the API.
        Validators.firstError(
            Validators.fullName(fullName),
            Validators.email(email),
            Validators.phone(phone),
            Validators.password(password),
            Validators.confirmPassword(password, confirmPassword)
        )?.let { return Outcome.Error(it) }

        val result = safeApiCall {
            ApiClient.api.register(
                RegisterRequest(
                    fullName = fullName.orEmpty().trim(),
                    email = email.orEmpty().trim(),
                    password = password.orEmpty(),
                    phone = Validators.normalisePhone(phone)
                )
            )
        }
        return when (result) {
            is Outcome.Success -> Outcome.Success(Unit)
            is Outcome.Error -> result
        }
    }

    suspend fun login(email: String?, password: String?): Outcome<LoginResponse> {
        Validators.firstError(
            Validators.email(email),
            Validators.required(password, "Password")
        )?.let { return Outcome.Error(it) }

        val result = safeApiCall {
            ApiClient.api.login(LoginRequest(email.orEmpty().trim(), password.orEmpty()))
        }
        if (result is Outcome.Success) {
            ApiClient.token = result.data.token // all later calls send this automatically
            currentUser = result.data
        }
        return result
    }

    fun logout() {
        ApiClient.token = null
        currentUser = null
    }
}