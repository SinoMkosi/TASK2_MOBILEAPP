package com.example.zaspetroleum.model

data class ServiceItem(
    val id: Int, val name: String, val description: String,
    val durationMinutes: Int, val price: Double
)

data class VehicleItem(
    val id: Int, val make: String, val model: String,
    val year: Int, val registration: String
)

data class SlotItem(
    val id: Int, val date: String, val startTime: String,
    val endTime: String, val available: Boolean
)

data class BookingItem(
    val id: Int, val reference: String, val serviceName: String, val vehicle: String,
    val date: String, val time: String, val status: String, val notes: String
) {
    // Upcoming = still pending, confirmed or in progress
    val isActive: Boolean
        get() = status.lowercase().let { "pending" in it || "confirm" in it || "progress" in it }
}

data class Profile(
    val fullName: String, val email: String, val phone: String,
    val theme: String, val notificationsOn: Boolean
)