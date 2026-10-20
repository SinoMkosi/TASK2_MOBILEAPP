package com.example.zaspetroleum.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/** "2026-06-25" becomes "25 June 2026". If the text cannot be read it is returned unchanged. */
fun formatDate(raw: String): String = try {
    val parsed = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(raw.take(10))
    if (parsed == null) raw else SimpleDateFormat("d MMMM yyyy", Locale.ENGLISH).format(parsed)
} catch (e: Exception) {
    raw
}

/** "14:00:00" or "14:00" becomes "2:00 PM". If the text cannot be read it is returned unchanged. */
fun formatTime(raw: String): String = try {
    val parsed = SimpleDateFormat("HH:mm", Locale.ENGLISH).parse(raw.take(5))
    if (parsed == null) raw else SimpleDateFormat("h:mm a", Locale.ENGLISH).format(parsed)
} catch (e: Exception) {
    raw
}

/** Shows slot times the way the wireframe does: "09:00". */
fun shortTime(raw: String): String = raw.take(5)

fun todayIso(nowMillis: Long = System.currentTimeMillis()): String =
    SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date(nowMillis))

/** The date picker works in UTC midnight milliseconds. */
fun isoFromUtcMillis(utcMillis: Long): String =
    SimpleDateFormat("yyyy-MM-dd", Locale.US).apply { timeZone = TimeZone.getTimeZone("UTC") }.format(Date(utcMillis))

fun utcMillisOfIso(iso: String): Long =
    SimpleDateFormat("yyyy-MM-dd", Locale.US).apply { timeZone = TimeZone.getTimeZone("UTC") }.parse(iso)?.time ?: 0L
