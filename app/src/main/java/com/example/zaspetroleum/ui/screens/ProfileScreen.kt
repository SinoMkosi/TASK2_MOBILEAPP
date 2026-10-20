package com.example.zaspetroleum.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.zaspetroleum.data.ZasRepository
import com.example.zaspetroleum.ui.Load
import com.example.zaspetroleum.ui.ZasIcons
import com.example.zaspetroleum.ui.components.FailedBox
import com.example.zaspetroleum.ui.components.LoadingBox
import com.example.zaspetroleum.ui.components.ScreenTitle
import com.example.zaspetroleum.ui.components.ZasCard
import com.example.zaspetroleum.ui.rememberLoader
import androidx.compose.ui.text.font.FontWeight

@Composable
private fun MenuRow(label: String, icon: ImageVector, onClick: () -> Unit, danger: Boolean = false) {
    val colour = if (danger) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
    Surface(onClick = onClick, color = MaterialTheme.colorScheme.surface) {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, Modifier.size(22.dp), tint = colour)
            Spacer(Modifier.width(16.dp))
            Text(label, Modifier.weight(1f), color = colour, fontWeight = FontWeight.Medium)
            Icon(ZasIcons.Chevron, null, tint = MaterialTheme.colorScheme.outline)
        }
    }
}

@Composable
fun ProfileScreen(onSettings: () -> Unit, onHelp: () -> Unit, onLogout: () -> Unit) {
    val loader = rememberLoader { ZasRepository.profile() }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)) {
        ScreenTitle("Profile")
        when (val s = loader.state) {
            is Load.Loading -> LoadingBox()
            is Load.Failed -> FailedBox(s.message) { loader.reload() }
            is Load.Ready -> ZasCard {
                Column(Modifier.padding(16.dp)) {
                    Text(s.data.fullName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                    Text(s.data.email, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(s.data.phone, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        Spacer(Modifier.height(20.dp))
        ZasCard {
            Column {
                MenuRow("Settings", ZasIcons.Settings, onSettings)
                MenuRow("Help", ZasIcons.Help, onHelp)
                MenuRow("Log out", ZasIcons.Logout, onLogout, danger = true)
            }
        }
    }
}
