package com.example.zaspetroleum.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.zaspetroleum.data.Outcome
import com.example.zaspetroleum.ui.AppGraph
import com.example.zaspetroleum.ui.ZasIcons
import com.example.zaspetroleum.ui.components.ErrorBanner
import com.example.zaspetroleum.ui.components.PrimaryButton
import com.example.zaspetroleum.ui.components.ZasField
import com.example.zaspetroleum.util.Validators
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(onRegistered: () -> Unit, onBack: () -> Unit) {
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirm by rememberSaveable { mutableStateOf("") }
    var errors by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var serverError by remember { mutableStateOf<String?>(null) }
    var busy by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    fun clear(key: String) { errors = errors - key }

    fun submit() {
        if (busy) return
        serverError = null
        val found = buildMap {
            Validators.fullName(name).error?.let { put("name", it) }
            Validators.email(email).error?.let { put("email", it) }
            Validators.phone(phone).error?.let { put("phone", it) }
            Validators.password(password).error?.let { put("password", it) }
            Validators.confirmPassword(password, confirm).error?.let { put("confirm", it) }
        }
        errors = found
        if (found.isNotEmpty()) return
        busy = true
        scope.launch {
            when (val r = AppGraph.auth.register(name, email, phone, password, confirm)) {
                is Outcome.Success -> onRegistered()
                is Outcome.Error -> serverError = r.message
            }
            busy = false
        }
    }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Spacer(Modifier.height(24.dp))
        LogoHeader("Create your account")
        serverError?.let { ErrorBanner(it) }
        ZasField(name, { name = it; clear("name") }, "Full name", error = errors["name"],
            leading = ZasIcons.Person, capitalization = KeyboardCapitalization.Words)
        ZasField(email, { email = it; clear("email") }, "Email", error = errors["email"],
            leading = ZasIcons.Email, keyboardType = KeyboardType.Email)
        ZasField(phone, { phone = it; clear("phone") }, "Phone number", error = errors["phone"],
            leading = ZasIcons.Phone, keyboardType = KeyboardType.Phone, placeholder = "0821234567")
        ZasField(password, { password = it; clear("password") }, "Password", error = errors["password"],
            helper = "At least 8 characters, with an uppercase letter, a lowercase letter and a number",
            leading = ZasIcons.Lock, password = true)
        ZasField(confirm, { confirm = it; clear("confirm") }, "Confirm password", error = errors["confirm"],
            leading = ZasIcons.Lock, password = true, imeAction = ImeAction.Done, onDone = ::submit)
        PrimaryButton("Create account", ::submit, loading = busy)
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Already have an account?", color = MaterialTheme.colorScheme.onSurfaceVariant)
            TextButton(onClick = onBack) { Text("Log in", fontWeight = FontWeight.Bold) }
        }
    }
}
