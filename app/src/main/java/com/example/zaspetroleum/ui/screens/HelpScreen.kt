package com.example.zaspetroleum.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.zaspetroleum.ui.ZasIcons
import com.example.zaspetroleum.ui.components.ScreenTitle
import com.example.zaspetroleum.ui.components.ZasCard
import com.example.zaspetroleum.ui.components.ZasTopBar

private val faq = listOf(
    "How do I book a service?" to "Open the Book tab, choose your vehicle and service, pick a date and a free time, then tap Confirm booking.",
    "How do I add a vehicle?" to "On the Book tab tap + Add a vehicle. Enter the make, model, year and registration (for example CAW123GP).",
    "Why can't I pick a date?" to "Dates in the past are greyed out. Choose today or a later date.",
    "Why is a time greyed out?" to "That time is already booked. Pick another time or another day.",
    "How do I cancel a booking?" to "Open My bookings, find the booking under Upcoming and tap Cancel, then confirm.",
    "What makes a good password?" to "At least 8 characters with an uppercase letter, a lowercase letter and a number.",
    "How do I change my name or phone number?" to "Go to Profile › Settings, edit the field and tap Save changes.",
    "How do I turn on dark mode?" to "Go to Profile › Settings, switch on Dark mode and tap Save changes.",
    "I see \"Can't reach the server\"" to "Check that your phone is online, then tap Try again.",
    "Still stuck?" to "Please contact ZAS Petroleum and quote your booking reference if you have one."
)

@Composable
fun HelpScreen(onBack: () -> Unit) {
    Column(Modifier.fillMaxSize()) {
        ZasTopBar(onBack)
        Column(Modifier.verticalScroll(rememberScrollState()).padding(20.dp)) {
            ScreenTitle("Help", "Tap a question to see the answer")
            faq.forEach { (q, a) -> FaqItem(q, a) }
        }
    }
}

@Composable
private fun FaqItem(question: String, answer: String) {
    var open by remember { mutableStateOf(false) }
    ZasCard(Modifier.padding(bottom = 10.dp)) {
        Column(Modifier.clickable { open = !open }.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(question, Modifier.weight(1f), fontWeight = FontWeight.SemiBold)
                Icon(if (open) ZasIcons.Expand else ZasIcons.Chevron, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (open) Text(answer, Modifier.padding(top = 8.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
