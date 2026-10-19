package com.example.zaspetroleum.model

import com.google.gson.annotations.SerializedName

// ---------- Requests (field names match Banathi's API exactly) ----------
data class RegisterRequest(
    @SerializedName("full_name") val fullName: String,
    val email: String,
    val password: String,
    val phone: String
)

data class LoginRequest(val email: String, val password: String)

data class SettingsRequest(
    @SerializedName("full_name") val fullName: String,
    val phone: String,
    val theme: String,
    @SerializedName("notifications_on") val notificationsOn: Boolean
)

data class VehicleRequest(
    val make: String,
    val model: String,
    val year: Int,
    val registration: String
)

data class BookingRequest(
    @SerializedName("vehicle_id") val vehicleId: Int,
    @SerializedName("service_id") val serviceId: Int,
    @SerializedName("slot_id") val slotId: Int,
    @SerializedName("special_instructions") val specialInstructions: String
)

data class CancelRequest(@SerializedName("booking_id") val bookingId: Int)

data class FeedbackRequest(
    @SerializedName("booking_id") val bookingId: Int,
    val rating: Int,
    val comment: String
)

// ---------- Responses ----------
data class LoginResponse(
    val token: String,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("full_name") val fullName: String,
    val role: String
)

// Errors come back as {"error": "..."}
data class ApiError(val error: String?)