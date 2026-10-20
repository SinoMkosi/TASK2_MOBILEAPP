package com.example.zaspetroleum.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.zaspetroleum.ui.components.LocalSnackbar
import com.example.zaspetroleum.ui.screens.BookScreen
import com.example.zaspetroleum.ui.screens.BookingsScreen
import com.example.zaspetroleum.ui.screens.HelpScreen
import com.example.zaspetroleum.ui.screens.HomeScreen
import com.example.zaspetroleum.ui.screens.LoginScreen
import com.example.zaspetroleum.ui.screens.ProfileScreen
import com.example.zaspetroleum.ui.screens.RegisterScreen
import com.example.zaspetroleum.ui.screens.SettingsScreen

enum class Stage { Login, Register, Main }
enum class Tab(val label: String, val icon: ImageVector) {
    Home("Home", ZasIcons.Home),
    Book("Book", ZasIcons.CalendarAdd),
    Bookings("Bookings", ZasIcons.List),
    Profile("Profile", ZasIcons.Person)
}
enum class Overlay { None, Settings, Help }

@Composable
fun ZasRoot() {
    var stage by rememberSaveable { mutableStateOf(Stage.Login) }
    // If Android restarted the app, the login token is gone, so ask for the login again.
    if (stage == Stage.Main && AppGraph.auth.currentUser == null) stage = Stage.Login

    val snackbar = remember { SnackbarHostState() }
    CompositionLocalProvider(LocalSnackbar provides snackbar) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost = { SnackbarHost(snackbar) }
        ) { padding ->
            val box = Modifier.padding(padding)
            when (stage) {
                Stage.Login -> androidx.compose.foundation.layout.Box(box) {
                    LoginScreen(onLoggedIn = { stage = Stage.Main }, onRegister = { stage = Stage.Register })
                }
                Stage.Register -> androidx.compose.foundation.layout.Box(box) {
                    BackHandler { stage = Stage.Login }
                    RegisterScreen(onRegistered = { stage = Stage.Login }, onBack = { stage = Stage.Login })
                }
                Stage.Main -> MainShell(box, onLogout = {
                    AppGraph.auth.logout()
                    stage = Stage.Login
                })
            }
        }
    }
}

@Composable
private fun MainShell(modifier: Modifier, onLogout: () -> Unit) {
    var tab by rememberSaveable { mutableStateOf(Tab.Home) }
    var overlay by rememberSaveable { mutableStateOf(Overlay.None) }

    if (overlay != Overlay.None) {
        BackHandler { overlay = Overlay.None }
        androidx.compose.foundation.layout.Box(modifier) {
            when (overlay) {
                Overlay.Settings -> SettingsScreen { overlay = Overlay.None }
                Overlay.Help -> HelpScreen { overlay = Overlay.None }
                Overlay.None -> {}
            }
        }
        return
    }

    androidx.compose.foundation.layout.Column(modifier) {
        androidx.compose.foundation.layout.Box(Modifier.weight(1f)) {
            when (tab) {
                Tab.Home -> HomeScreen(onBook = { tab = Tab.Book }, onBookings = { tab = Tab.Bookings })
                Tab.Book -> BookScreen(onBooked = { tab = Tab.Bookings })
                Tab.Bookings -> BookingsScreen()
                Tab.Profile -> ProfileScreen(
                    onSettings = { overlay = Overlay.Settings },
                    onHelp = { overlay = Overlay.Help },
                    onLogout = onLogout
                )
            }
        }
        NavigationBar {
            Tab.values().forEach {
                NavigationBarItem(
                    selected = tab == it,
                    onClick = { tab = it },
                    icon = { Icon(it.icon, contentDescription = null) },
                    label = { Text(it.label) }
                )
            }
        }
    }
}
