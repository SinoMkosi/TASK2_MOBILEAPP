package com.example.zaspetroleum.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.zaspetroleum.data.Outcome
import com.example.zaspetroleum.data.ZasRepository
import com.example.zaspetroleum.model.SlotItem
import com.example.zaspetroleum.ui.Load
import com.example.zaspetroleum.ui.ZasIcons
import com.example.zaspetroleum.ui.components.ErrorBanner
import com.example.zaspetroleum.ui.components.FailedBox
import com.example.zaspetroleum.ui.components.LoadingBox
import com.example.zaspetroleum.ui.components.PrimaryButton
import com.example.zaspetroleum.ui.components.ScreenTitle
import com.example.zaspetroleum.ui.components.SectionLabel
import com.example.zaspetroleum.ui.components.SlotGrid
import com.example.zaspetroleum.ui.components.ZasDropdown
import com.example.zaspetroleum.ui.components.ZasField
import com.example.zaspetroleum.ui.rememberLoader
import com.example.zaspetroleum.util.BookingChecks
import com.example.zaspetroleum.util.Validators
import com.example.zaspetroleum.util.formatDate
import com.example.zaspetroleum.util.isoFromUtcMillis
import com.example.zaspetroleum.util.todayIso
import com.example.zaspetroleum.util.utcMillisOfIso
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookScreen(onBooked: () -> Unit) {
    val vehiclesLoader = rememberLoader { ZasRepository.vehicles() }
    val servicesLoader = rememberLoader { ZasRepository.services() }

    var vehicleIdx by remember { mutableStateOf<Int?>(null) }
    var serviceIdx by remember { mutableStateOf<Int?>(null) }
    var date by remember { mutableStateOf<String?>(null) }
    var slotId by remember { mutableStateOf<Int?>(null) }
    var notes by remember { mutableStateOf("") }
    var errors by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var serverError by remember { mutableStateOf<String?>(null) }
    var busy by remember { mutableStateOf(false) }
    var showPicker by remember { mutableStateOf(false) }
    var showAddVehicle by remember { mutableStateOf(false) }
    var reference by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val today = todayIso()

    val slotsLoader = rememberLoader(date) {
        if (date == null) Outcome.Success(emptyList<SlotItem>()) else ZasRepository.slots(date!!)
    }

    val vehicles = (vehiclesLoader.state as? Load.Ready)?.data.orEmpty()
    val services = (servicesLoader.state as? Load.Ready)?.data.orEmpty()

    fun submit() {
        if (busy) return
        serverError = null
        val found = buildMap {
            BookingChecks.vehicle(vehicleIdx?.let { vehicles.getOrNull(it)?.id })?.let { put("vehicle", it) }
            BookingChecks.service(serviceIdx?.let { services.getOrNull(it)?.id })?.let { put("service", it) }
            BookingChecks.date(date, today)?.let { put("date", it) }
            BookingChecks.slot(slotId)?.let { put("slot", it) }
            Validators.notes(notes).error?.let { put("notes", it) }
        }
        errors = found
        if (found.isNotEmpty()) return
        busy = true
        scope.launch {
            val r = ZasRepository.book(
                vehicles[vehicleIdx!!].id, services[serviceIdx!!].id, slotId!!, notes.trim()
            )
            busy = false
            when (r) {
                is Outcome.Success -> reference = r.data
                is Outcome.Error -> serverError = r.message
            }
        }
    }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        ScreenTitle("Book a service", "Pick your vehicle, service and a time")
        serverError?.let { ErrorBanner(it) }

        when (val s = vehiclesLoader.state) {
            is Load.Loading -> LoadingBox()
            is Load.Failed -> FailedBox(s.message) { vehiclesLoader.reload() }
            is Load.Ready -> {
                ZasDropdown(
                    "Vehicle", s.data.map { "${it.make} ${it.model} (${it.registration})" }, vehicleIdx,
                    { vehicleIdx = it; errors = errors - "vehicle" },
                    placeholder = if (s.data.isEmpty()) "No vehicles yet" else "Select a vehicle",
                    leading = ZasIcons.Car, error = errors["vehicle"]
                )
                TextButton(onClick = { showAddVehicle = true }) { Text("+ Add a vehicle") }
            }
        }

        when (val s = servicesLoader.state) {
            is Load.Loading -> LoadingBox()
            is Load.Failed -> FailedBox(s.message) { servicesLoader.reload() }
            is Load.Ready -> ZasDropdown(
                "Service type", s.data.map { "${it.name} – R${"%.0f".format(it.price)}" }, serviceIdx,
                { serviceIdx = it; errors = errors - "service" },
                placeholder = "Select a service", leading = ZasIcons.Wrench, error = errors["service"]
            )
        }

        // Date box (tap to open the calendar)
        Box(Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = date?.let { formatDate(it) } ?: "",
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Date") },
                placeholder = { Text("Choose a date") },
                leadingIcon = { Icon(ZasIcons.Calendar, null) },
                isError = errors["date"] != null,
                supportingText = errors["date"]?.let { { Text(it) } },
                shape = RoundedCornerShape(12.dp)
            )
            Box(Modifier.matchParentSize().clickable { showPicker = true })
        }

        SectionLabel("Available times")
        errors["slot"]?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        if (date == null) {
            Text("Choose a date to see the available times.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else when (val s = slotsLoader.state) {
            is Load.Loading -> LoadingBox()
            is Load.Failed -> FailedBox(s.message) { slotsLoader.reload() }
            is Load.Ready ->
                if (s.data.isEmpty()) Text("No times on this day. Try another date.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                else SlotGrid(s.data, slotId) { slotId = it.id; errors = errors - "slot" }
        }

        ZasField(
            notes, { notes = it; errors = errors - "notes" }, "Notes (optional)",
            error = errors["notes"], helper = "${notes.length}/200",
            singleLine = false, minLines = 3
        )
        PrimaryButton("Confirm booking", ::submit, loading = busy, icon = ZasIcons.CalendarAdd)
    }

    if (showPicker) {
        val state = rememberDatePickerState(
            initialSelectedDateMillis = date?.let { utcMillisOfIso(it) },
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long) = isoFromUtcMillis(utcTimeMillis) >= today
            }
        )
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let {
                        date = isoFromUtcMillis(it)
                        slotId = null
                        errors = errors - "date"
                    }
                    showPicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showPicker = false }) { Text("Cancel") } }
        ) { DatePicker(state = state) }
    }

    if (showAddVehicle) {
        AddVehicleDialog(
            onDismiss = { showAddVehicle = false },
            onAdded = { showAddVehicle = false; vehiclesLoader.reload() }
        )
    }

    reference?.let { ref ->
        AlertDialog(
            onDismissRequest = {},
            icon = { Icon(ZasIcons.CheckCircle, null, tint = MaterialTheme.colorScheme.primary) },
            title = { Text("Booking confirmed") },
            text = { Text(if (ref.isBlank()) "Your booking has been made." else "Your reference is $ref.") },
            confirmButton = { TextButton(onClick = { reference = null; onBooked() }) { Text("View my bookings") } }
        )
    }
}
