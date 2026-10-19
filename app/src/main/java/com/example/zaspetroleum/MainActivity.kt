package com.example.zaspetroleum
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.example.zaspetroleum.logic.AuthManager
import kotlinx.coroutines.launch
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.zaspetroleum.ui.theme.ZasPetroleumTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            val good = AuthManager().login("sipho@example.com", "Zas@2026!")
            Log.d("ZAS_TEST", "GOOD LOGIN: $good")
            val bad = AuthManager().login("sipho@example.com", "WrongPass1")
            Log.d("ZAS_TEST", "BAD LOGIN: $bad")
            val invalid = AuthManager().login("not-an-email", "")
            Log.d("ZAS_TEST", "INVALID INPUT: $invalid")
        }
        enableEdgeToEdge()
        setContent {
            ZasPetroleumTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ZasPetroleumTheme {
        Greeting("Android")
    }
}