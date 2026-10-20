package com.example.zaspetroleum.util

/**
 * Checks for the Book screen that the shared Validators do not cover.
 * Each returns null when fine, or the message to show under the field.
 * Dates are ISO strings (yyyy-MM-dd), which compare correctly as plain text.
 */
object BookingChecks {
    fun vehicle(vehicleId: Int?): String? =
        if (vehicleId == null) "Please select a vehicle." else null

    fun service(serviceId: Int?): String? =
        if (serviceId == null) "Please select a service type." else null

    fun date(isoDate: String?, todayIso: String): String? = when {
        isoDate.isNullOrBlank() -> "Please choose a date."
        isoDate < todayIso -> "You cannot book a date in the past. Please choose today or a later date."
        else -> null
    }

    fun slot(slotId: Int?): String? =
        if (slotId == null) "Please select an available time slot." else null
}
