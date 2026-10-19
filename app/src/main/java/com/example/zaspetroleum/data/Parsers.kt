package com.example.zaspetroleum.data

import com.example.zaspetroleum.model.*
import com.google.gson.JsonElement
import com.google.gson.JsonObject

// A list reply can be [ ... ] or {"data": [ ... ]}; both work
fun JsonElement.rows(): List<JsonObject> = when {
    isJsonArray -> asJsonArray.mapNotNull { if (it.isJsonObject) it.asJsonObject else null }
    isJsonObject -> asJsonObject.entrySet().firstOrNull { it.value.isJsonArray }?.value?.rows()
        ?: emptyList()
    else -> emptyList()
}

// A single-record reply can be {...} or {"profile": {...}}
fun JsonElement.record(): JsonObject? {
    if (!isJsonObject) return null
    val obj = asJsonObject
    for (key in listOf("profile", "user", "data")) {
        val inner = obj.get(key)
        if (inner != null && inner.isJsonObject) return inner.asJsonObject
    }
    return obj
}

private fun JsonObject.first(keys: Array<out String>): JsonElement? =
    keys.firstNotNullOfOrNull { k -> get(k)?.takeIf { !it.isJsonNull } }

fun JsonObject.str(vararg keys: String): String =
    first(keys)?.let { runCatching { it.asString }.getOrNull() } ?: ""

fun JsonObject.int(vararg keys: String): Int =
    first(keys)?.let { runCatching { it.asDouble.toInt() }.getOrNull() } ?: 0

fun JsonObject.dbl(vararg keys: String): Double =
    first(keys)?.let { runCatching { it.asDouble }.getOrNull() } ?: 0.0

fun JsonObject.boolOr(default: Boolean, vararg keys: String): Boolean {
    val v = first(keys) ?: return default
    return runCatching { v.asString.lowercase() in setOf("1", "true", "yes") }.getOrDefault(default)
}

fun JsonObject.toService() = ServiceItem(
    int("id", "service_id"), str("name", "service_name"), str("description"),
    int("duration_minutes", "duration"), dbl("price", "base_price")
)

fun JsonObject.toVehicle() = VehicleItem(
    int("id", "vehicle_id"), str("make"), str("model"), int("year"),
    str("registration", "registration_number")
)

fun JsonObject.toSlot() = SlotItem(
    int("id", "slot_id"), str("date", "slot_date"), str("start_time", "time"),
    str("end_time"), boolOr(true, "is_available", "available")
)

fun JsonObject.toBooking() = BookingItem(
    int("id", "booking_id"), str("reference", "booking_reference", "ref"),
    str("service_name", "service"), str("registration", "vehicle_registration", "vehicle"),
    str("date", "slot_date", "booking_date"), str("start_time", "time"),
    str("status"), str("special_instructions", "notes")
)

fun JsonObject.toProfile() = Profile(
    str("full_name", "name"), str("email"), str("phone"),
    str("theme").ifBlank { "light" }, boolOr(true, "notifications_on")
)

// Converts a successful reply into our own types; a weird reply becomes an error, not a crash
inline fun <T, R> Outcome<T>.mapData(f: (T) -> R): Outcome<R> = when (this) {
    is Outcome.Success -> try {
        Outcome.Success(f(data))
    } catch (e: Exception) {
        Outcome.Error("Unexpected reply from the server")
    }
    is Outcome.Error -> this
}