package com.example.zaspetroleum.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.zaspetroleum.logic.AuthManager

/** The few things every screen shares. */
object AppGraph {
    /** One AuthManager for the whole app, so the login token is kept. */
    val auth = AuthManager()

    /** true = dark, false = light, null = follow the phone. Set from Settings. */
    var darkMode by mutableStateOf<Boolean?>(null)
}
