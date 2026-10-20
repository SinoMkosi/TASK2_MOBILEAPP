package com.example.zaspetroleum.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zaspetroleum.data.Outcome
import com.example.zaspetroleum.ui.AppGraph
import com.example.zaspetroleum.ui.ZasIcons
import com.example.zaspetroleum.ui.components.ErrorBanner
import com.example.zaspetroleum.ui.components.PrimaryButton
import com.example.zaspetroleum.ui.components.SecondaryButton
import com.example.zaspetroleum.ui.components.ZasField
import com.example.zaspetroleum.util.Validators
import kotlinx.coroutines.launch

@Composable
fun LogoHeader(subtitle: String) {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(ZasIcons.OilCan, null, Modifier.size(48.dp), tint = MaterialTheme.colorScheme.primary)
        Text("ZAS Petroleum", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.secondary)
        Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun LoginScreen(onLoggedIn: () -> Unit, onRegister: () -> Unit) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var serverError by remember { mutableStateOf<String?>(null) }
    var busy by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    fun submit() {
        if (busy) return
        emailError = Validators.email(email).error
        passwordError = Validators.required(password, "Password").error
        serverError = null
        if (emailError != null || passwordError != null) return
        busy = true
        scope.launch {
            when (val r = AppGraph.auth.login(email, password)) {
                is Outcome.Success -> onLoggedIn()
                is Outcome.Error -> serverError = r.message
            }
            busy = false
        }
    }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Spacer(Modifier.height(40.dp))
        LogoHeader("Log in to book your service")
        Spacer(Modifier.height(12.dp))
        serverError?.let { ErrorBanner(it) }
        ZasField(email, { email = it; emailError = null }, "Email", error = emailError,
            leading = ZasIcons.Email, keyboardType = KeyboardType.Email)
        ZasField(password, { password = it; passwordError = null }, "Password", error = passwordError,
            leading = ZasIcons.Lock, password = true, imeAction = ImeAction.Done, onDone = ::submit)
        PrimaryButton("Log in", ::submit, loading = busy)
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("New here?", color = MaterialTheme.colorScheme.onSurfaceVariant)
            TextButton(onClick = onRegister) { Text("Create an account", fontWeight = FontWeight.Bold) }
        }
        SecondaryButton("Continue with Single Sign-On", onClick = { serverError = "Single Sign-On is coming soon." })
    }
}
