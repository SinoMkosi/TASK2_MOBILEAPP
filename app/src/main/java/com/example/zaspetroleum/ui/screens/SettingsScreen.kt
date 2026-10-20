package com.example.zaspetroleum.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.example.zaspetroleum.data.Outcome
import com.example.zaspetroleum.data.ZasRepository
import com.example.zaspetroleum.model.Profile
import com.example.zaspetroleum.ui.AppGraph
import com.example.zaspetroleum.ui.Load
import com.example.zaspetroleum.ui.ZasIcons
import com.example.zaspetroleum.ui.components.ErrorBanner
import com.example.zaspetroleum.ui.components.FailedBox
import com.example.zaspetroleum.ui.components.LoadingBox
import com.example.zaspetroleum.ui.components.LocalSnackbar
import com.example.zaspetroleum.ui.components.PrimaryButton
import com.example.zaspetroleum.ui.components.ScreenTitle
import com.example.zaspetroleum.ui.components.SectionLabel
import com.example.zaspetroleum.ui.components.ZasField
import com.example.zaspetroleum.ui.components.ZasTopBar
import com.example.zaspetroleum.ui.rememberLoader
import com.example.zaspetroleum.util.Validators
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val loader = rememberLoader { ZasRepository.profile() }
    Column(Modifier.fillMaxSize()) {
        ZasTopBar(onBack)
        when (val s = loader.state) {
            is Load.Loading -> LoadingBox()
            is Load.Failed -> FailedBox(s.message) { loader.reload() }
            is Load.Ready -> SettingsForm(s.data, onBack)
        }
    }
}

@Composable
private fun SettingsForm(profile: Profile, onSaved: () -> Unit) {
    var name by remember { mutableStateOf(profile.fullName) }
    var phone by remember { mutableStateOf(profile.phone) }
    var notifications by remember { mutableStateOf(profile.notificationsOn) }
    var dark by remember { mutableStateOf(profile.theme.equals("dark", ignoreCase = true)) }
    var nameError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var serverError by remember { mutableStateOf<String?>(null) }
    var busy by remember { mutableStateOf(false) }
    val snackbar = LocalSnackbar.current
    val scope = rememberCoroutineScope()

    fun save() {
        if (busy) return
        serverError = null
        nameError = Validators.fullName(name).error
        phoneError = Validators.phone(phone).error
        if (nameError != null || phoneError != null) return
        busy = true
        scope.launch {
            val r = ZasRepository.saveSettings(
                name.trim(), Validators.normalisePhone(phone), if (dark) "dark" else "light", notifications
            )
            busy = false
            when (r) {
                is Outcome.Success -> {
                    AppGraph.darkMode = dark
                    snackbar.showSnackbar("Settings saved")
                    onSaved()
                }
                is Outcome.Error -> serverError = r.message
            }
        }
    }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        ScreenTitle("Settings")
        serverError?.let { ErrorBanner(it) }
        SectionLabel("Your details")
        ZasField(name, { name = it; nameError = null }, "Full name", error = nameError,
            leading = ZasIcons.Person, capitalization = KeyboardCapitalization.Words)
        ZasField(profile.email, {}, "Email", enabled = false, leading = ZasIcons.Email,
            helper = "Your email cannot be changed")
        ZasField(phone, { phone = it; phoneError = null }, "Phone number", error = phoneError,
            leading = ZasIcons.Phone, keyboardType = KeyboardType.Phone)
        SectionLabel("Preferences")
        ToggleRow("Notifications", "Booking reminders and updates", notifications) { notifications = it }
        ToggleRow("Dark mode", "Easier on the eyes at night", dark) { dark = it }
        PrimaryButton("Save changes", ::save, loading = busy)
    }
}

@Composable
private fun ToggleRow(title: String, subtitle: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.SemiBold)
            Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
        }
        Switch(checked = checked, onCheckedChange = onChange)
    }
}
