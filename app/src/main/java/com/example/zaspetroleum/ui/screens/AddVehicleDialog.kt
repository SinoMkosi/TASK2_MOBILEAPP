package com.example.zaspetroleum.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.zaspetroleum.data.Outcome
import com.example.zaspetroleum.data.ZasRepository
import com.example.zaspetroleum.ui.components.ErrorBanner
import com.example.zaspetroleum.ui.components.ZasField
import com.example.zaspetroleum.util.Validators
import kotlinx.coroutines.launch

@Composable
fun AddVehicleDialog(onDismiss: () -> Unit, onAdded: () -> Unit) {
    var make by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    var reg by remember { mutableStateOf("") }
    var errors by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var serverError by remember { mutableStateOf<String?>(null) }
    var busy by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    fun save() {
        if (busy) return
        serverError = null
        val found = buildMap {
            Validators.required(make, "Make").error?.let { put("make", it) }
            Validators.required(model, "Model").error?.let { put("model", it) }
            Validators.vehicleYear(year).error?.let { put("year", it) }
            Validators.registration(reg).error?.let { put("reg", it) }
        }
        errors = found
        if (found.isNotEmpty()) return
        busy = true
        scope.launch {
            val r = ZasRepository.addVehicle(
                make.trim(), model.trim(), year.trim().toInt(), Validators.normaliseRegistration(reg)
            )
            busy = false
            when (r) {
                is Outcome.Success -> onAdded()
                is Outcome.Error -> serverError = r.message
            }
        }
    }

    AlertDialog(
        onDismissRequest = { if (!busy) onDismiss() },
        title = { Text("Add a vehicle") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                serverError?.let { ErrorBanner(it) }
                ZasField(make, { make = it; errors = errors - "make" }, "Make", error = errors["make"],
                    capitalization = KeyboardCapitalization.Words, placeholder = "Toyota")
                ZasField(model, { model = it; errors = errors - "model" }, "Model", error = errors["model"],
                    capitalization = KeyboardCapitalization.Words, placeholder = "Corolla")
                ZasField(year, { year = it; errors = errors - "year" }, "Year", error = errors["year"],
                    keyboardType = KeyboardType.Number, placeholder = "2018")
                ZasField(reg, { reg = it; errors = errors - "reg" }, "Registration", error = errors["reg"],
                    capitalization = KeyboardCapitalization.Characters, placeholder = "CAW123GP")
            }
        },
        confirmButton = { TextButton(onClick = ::save, enabled = !busy) { Text(if (busy) "Saving…" else "Save") } },
        dismissButton = { TextButton(onClick = onDismiss, enabled = !busy) { Text("Cancel") } }
    )
}
