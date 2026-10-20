package com.example.zaspetroleum.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.example.zaspetroleum.data.Outcome
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/** What a screen shows while it fetches data. */
sealed interface Load<out T> {
    object Loading : Load<Nothing>
    data class Ready<T>(val data: T) : Load<T>
    data class Failed(val message: String) : Load<Nothing>
}

class Loader<T>(
    private val scope: CoroutineScope,
    private val block: suspend () -> Outcome<T>
) {
    var state by mutableStateOf<Load<T>>(Load.Loading)
        private set

    fun reload() {
        state = Load.Loading
        scope.launch {
            state = when (val result = block()) {
                is Outcome.Success -> Load.Ready(result.data)
                is Outcome.Error -> Load.Failed(result.message)
            }
        }
    }
}

/** Loads data once when the screen opens (and again when [keys] change). Call reload() to refresh. */
@Composable
fun <T> rememberLoader(vararg keys: Any?, block: suspend () -> Outcome<T>): Loader<T> {
    val scope = rememberCoroutineScope()
    val loader = remember(*keys) { Loader(scope, block) }
    LaunchedEffect(loader) { loader.reload() }
    return loader
}
