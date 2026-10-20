package com.example.zaspetroleum.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zaspetroleum.model.BookingItem
import com.example.zaspetroleum.model.SlotItem
import com.example.zaspetroleum.ui.ZasIcons
import com.example.zaspetroleum.ui.theme.ZasDanger
import com.example.zaspetroleum.ui.theme.ZasDangerBg
import com.example.zaspetroleum.ui.theme.ZasSuccess
import com.example.zaspetroleum.ui.theme.ZasSuccessBg
import com.example.zaspetroleum.ui.theme.ZasWarning
import com.example.zaspetroleum.ui.theme.ZasWarningBg
import com.example.zaspetroleum.util.formatDate
import com.example.zaspetroleum.util.formatTime
import com.example.zaspetroleum.util.shortTime

/** Lets any screen show a snackbar message. */
val LocalSnackbar = staticCompositionLocalOf { SnackbarHostState() }

/** A text box with a label, an optional icon and a red error message underneath. */
@Composable
fun ZasField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier.fillMaxWidth(),
    error: String? = null,
    helper: String? = null,
    placeholder: String? = null,
    leading: ImageVector? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    capitalization: KeyboardCapitalization = KeyboardCapitalization.None,
    password: Boolean = false,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    minLines: Int = 1,
    imeAction: ImeAction = ImeAction.Next,
    onDone: (() -> Unit)? = null
) {
    var visible by remember { mutableStateOf(false) }
    val supporting = error ?: helper
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        singleLine = singleLine,
        minLines = minLines,
        label = { Text(label) },
        placeholder = if (placeholder != null) {
            { Text(placeholder) }
        } else null,
        leadingIcon = if (leading != null) {
            { Icon(leading, contentDescription = null) }
        } else null,
        trailingIcon = if (password) {
            { TextButton(onClick = { visible = !visible }) { Text(if (visible) "Hide" else "Show") } }
        } else null,
        isError = error != null,
        supportingText = if (supporting != null) {
            { Text(supporting) }
        } else null,
        visualTransformation = if (password && !visible) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(
            capitalization = capitalization,
            keyboardType = if (password) KeyboardType.Password else keyboardType,
            imeAction = imeAction
        ),
        keyboardActions = KeyboardActions(onDone = { onDone?.invoke() }, onGo = { onDone?.invoke() }),
        shape = RoundedCornerShape(12.dp)
    )
}

/** A drop-down list (vehicle, service type). */
@Composable
fun ZasDropdown(
    label: String,
    options: List<String>,
    selectedIndex: Int?,
    onSelect: (Int) -> Unit,
    placeholder: String,
    leading: ImageVector? = null,
    error: String? = null,
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier) {
        OutlinedTextField(
            value = selectedIndex?.let { options.getOrNull(it) } ?: "",
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(label) },
            placeholder = { Text(placeholder) },
            leadingIcon = if (leading != null) {
                { Icon(leading, contentDescription = null) }
            } else null,
            trailingIcon = { Icon(ZasIcons.Expand, contentDescription = "Open list") },
            isError = error != null,
            supportingText = if (error != null) {
                { Text(error) }
            } else null,
            shape = RoundedCornerShape(12.dp)
        )
        // Invisible layer on top so a tap anywhere on the box opens the list
        Box(
            Modifier
                .matchParentSize()
                .clickable { expanded = true }
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEachIndexed { index, option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelect(index)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth(),
    loading: Boolean = false,
    enabled: Boolean = true,
    icon: ImageVector? = null
) {
    Button(
        onClick = onClick,
        enabled = enabled && !loading,
        modifier = modifier.height(52.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.onPrimary
            )
        } else {
            if (icon != null) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
            }
            Text(text, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth(),
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(52.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Text(text, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
    }
}

/** Red box for errors that are not tied to one field (for example "Incorrect email or password"). */
@Composable
fun ErrorBanner(message: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.errorContainer,
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            message,
            modifier = Modifier.padding(12.dp),
            color = MaterialTheme.colorScheme.onErrorContainer
        )
    }
}

@Composable
fun LoadingBox(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

/** Shown when data could not be loaded, with a Try again button. */
@Composable
fun FailedBox(message: String, onRetry: () -> Unit) {
    Column(
        Modifier.fillMaxWidth().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(message, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.error)
        OutlinedButton(onClick = onRetry) { Text("Try again") }
    }
}

@Composable
fun EmptyBox(message: String) {
    Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
        Text(message, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
    }
}

@Composable
fun ScreenTitle(title: String, subtitle: String? = null) {
    Column(Modifier.padding(bottom = 16.dp)) {
        Text(title, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        if (subtitle != null) {
            Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun SectionLabel(text: String) {
    Text(
        text.uppercase(),
        modifier = Modifier.padding(top = 24.dp, bottom = 10.dp),
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
fun ZasCard(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) { content() }
}

/** Top bar for Settings and Help: a back arrow and the ZAS logo. */
@Composable
fun ZasTopBar(onBack: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().height(56.dp).padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(ZasIcons.Back, contentDescription = "Back", tint = MaterialTheme.colorScheme.secondary)
        }
        Spacer(Modifier.weight(1f))
        Icon(ZasIcons.OilCan, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(6.dp))
        Text("ZAS", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = MaterialTheme.colorScheme.secondary)
        Spacer(Modifier.weight(1f))
        Spacer(Modifier.width(48.dp))
    }
}

/** Coloured Confirmed / Pending / Completed / Cancelled label. */
@Composable
fun StatusBadge(status: String) {
    val s = status.lowercase()
    val (bg, fg) = when {
        "cancel" in s -> ZasDangerBg to ZasDanger
        "confirm" in s || "complete" in s -> ZasSuccessBg to ZasSuccess
        else -> ZasWarningBg to ZasWarning
    }
    Text(
        status.replaceFirstChar { it.uppercase() },
        modifier = Modifier.background(bg, RoundedCornerShape(20.dp)).padding(horizontal = 10.dp, vertical = 3.dp),
        color = fg,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold
    )
}

/** One booking row. Pass [onCancel] / [onView] to show an action button. */
@Composable
fun BookingCard(
    booking: BookingItem,
    onCancel: (() -> Unit)? = null,
    onView: (() -> Unit)? = null
) {
    ZasCard(Modifier.padding(bottom = 12.dp)) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(ZasIcons.Wrench, null, Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.width(8.dp))
                    Text(booking.serviceName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(ZasIcons.Calendar, null, Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "${formatDate(booking.date)} • ${formatTime(booking.time)}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                StatusBadge(booking.status)
                if (onCancel != null) {
                    OutlinedButton(
                        onClick = onCancel,
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 2.dp),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.errorContainer)
                    ) { Text("Cancel", color = MaterialTheme.colorScheme.error, fontSize = 13.sp, fontWeight = FontWeight.Bold) }
                } else if (onView != null) {
                    TextButton(onClick = onView) {
                        Text("View", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

/** Time slots in rows of three. Booked-out slots are greyed out and cannot be tapped. */
@Composable
fun SlotGrid(slots: List<SlotItem>, selectedId: Int?, onSelect: (SlotItem) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        slots.chunked(3).forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { slot ->
                    val selected = slot.id == selectedId
                    Surface(
                        onClick = { onSelect(slot) },
                        enabled = slot.available,
                        modifier = Modifier.weight(1f).height(44.dp),
                        shape = RoundedCornerShape(10.dp),
                        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (selected) Color.White else MaterialTheme.colorScheme.onSurface
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            Text(
                                shortTime(slot.startTime),
                                fontWeight = FontWeight.SemiBold,
                                color = if (slot.available) Color.Unspecified else MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
                repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}
