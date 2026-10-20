package com.example.zaspetroleum.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.zaspetroleum.data.Outcome
import com.example.zaspetroleum.data.ZasRepository
import com.example.zaspetroleum.model.BookingItem
import com.example.zaspetroleum.ui.Load
import com.example.zaspetroleum.ui.components.BookingCard
import com.example.zaspetroleum.ui.components.EmptyBox
import com.example.zaspetroleum.ui.components.FailedBox
import com.example.zaspetroleum.ui.components.LoadingBox
import com.example.zaspetroleum.ui.components.LocalSnackbar
import com.example.zaspetroleum.ui.components.ScreenTitle
import com.example.zaspetroleum.ui.rememberLoader
import kotlinx.coroutines.launch

enum class BookingFilter(val label: String) { Upcoming("Upcoming"), History("History") }

@Composable
fun BookingsScreen() {
    val loader = rememberLoader { ZasRepository.bookings() }
    var filter by rememberSaveable { mutableStateOf(BookingFilter.Upcoming) }
    var toCancel by remember { mutableStateOf<BookingItem?>(null) }
    val snackbar = LocalSnackbar.current
    val scope = rememberCoroutineScope()

    Column(Modifier.fillMaxSize().padding(top = 20.dp)) {
        Column(Modifier.padding(horizontal = 20.dp)) { ScreenTitle("My bookings") }
        TabRow(selectedTabIndex = filter.ordinal) {
            BookingFilter.values().forEach {
                Tab(selected = filter == it, onClick = { filter = it }, text = { Text(it.label) })
            }
        }
        Column(Modifier.verticalScroll(rememberScrollState()).padding(20.dp)) {
            when (val s = loader.state) {
                is Load.Loading -> LoadingBox()
                is Load.Failed -> FailedBox(s.message) { loader.reload() }
                is Load.Ready -> {
                    val shown = s.data.filter { (filter == BookingFilter.Upcoming) == it.isActive }
                        .sortedBy { it.date + it.time }
                    if (shown.isEmpty()) {
                        EmptyBox(if (filter == BookingFilter.Upcoming) "No upcoming bookings." else "No past bookings yet.")
                    }
                    shown.forEach { b ->
                        BookingCard(b, onCancel = if (b.isActive) ({ toCancel = b }) else null)
                    }
                }
            }
        }
    }

    toCancel?.let { b ->
        AlertDialog(
            onDismissRequest = { toCancel = null },
            title = { Text("Cancel booking?") },
            text = { Text("Cancel your ${b.serviceName} booking on ${b.date}?") },
            confirmButton = {
                TextButton(onClick = {
                    toCancel = null
                    scope.launch {
                        when (val r = ZasRepository.cancel(b.id)) {
                            is Outcome.Success -> { snackbar.showSnackbar("Booking cancelled", duration = SnackbarDuration.Short); loader.reload() }
                            is Outcome.Error -> snackbar.showSnackbar(r.message)
                        }
                    }
                }) { Text("Yes, cancel") }
            },
            dismissButton = { TextButton(onClick = { toCancel = null }) { Text("Keep it") } }
        )
    }
}
