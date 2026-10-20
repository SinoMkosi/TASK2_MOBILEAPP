package com.example.zaspetroleum

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import com.example.zaspetroleum.ui.AppGraph
import com.example.zaspetroleum.ui.ZasRoot
import com.example.zaspetroleum.ui.theme.ZasPetroleumTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ZasPetroleumTheme(darkTheme = AppGraph.darkMode ?: isSystemInDarkTheme()) {
                ZasRoot()
            }
        }
    }
}
