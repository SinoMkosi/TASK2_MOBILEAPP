package com.example.zaspetroleum.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zaspetroleum.data.ZasRepository
import com.example.zaspetroleum.ui.AppGraph
import com.example.zaspetroleum.ui.Load
import com.example.zaspetroleum.ui.ZasIcons
import com.example.zaspetroleum.ui.components.BookingCard
import com.example.zaspetroleum.ui.components.EmptyBox
import com.example.zaspetroleum.ui.components.FailedBox
import com.example.zaspetroleum.ui.components.LoadingBox
import com.example.zaspetroleum.ui.components.ScreenTitle
import com.example.zaspetroleum.ui.components.SectionLabel
import com.example.zaspetroleum.ui.rememberLoader

@Composable
private fun QuickAction(label: String, icon: ImageVector, onClick: () -> Unit, modifier: Modifier) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(icon, null, Modifier.size(28.dp), tint = MaterialTheme.colorScheme.primary)
            Text(label, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
        }
    }
}

@Composable
fun HomeScreen(onBook: () -> Unit, onBookings: () -> Unit) {
    val loader = rememberLoader { ZasRepository.bookings() }
    val first = AppGraph.auth.currentUser?.fullName?.substringBefore(' ').orEmpty()

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)) {
        ScreenTitle(if (first.isEmpty()) "Welcome" else "Hi, $first", "What would you like to do today?")
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            QuickAction("Book a service", ZasIcons.CalendarAdd, onBook, Modifier.weight(1f))
            QuickAction("My bookings", ZasIcons.List, onBookings, Modifier.weight(1f))
        }
        SectionLabel("Next booking")
        when (val s = loader.state) {
            is Load.Loading -> LoadingBox()
            is Load.Failed -> FailedBox(s.message) { loader.reload() }
            is Load.Ready -> {
                val next = s.data.filter { it.isActive }.minByOrNull { it.date + it.time }
                if (next == null) EmptyBox("You have no upcoming bookings.") else BookingCard(next)
            }
        }
    }
}
