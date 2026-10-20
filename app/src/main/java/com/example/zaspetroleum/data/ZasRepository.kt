package com.example.zaspetroleum.data

import com.example.zaspetroleum.model.BookingItem
import com.example.zaspetroleum.model.BookingRequest
import com.example.zaspetroleum.model.CancelRequest
import com.example.zaspetroleum.model.Profile
import com.example.zaspetroleum.model.ServiceItem
import com.example.zaspetroleum.model.SettingsRequest
import com.example.zaspetroleum.model.SlotItem
import com.example.zaspetroleum.model.VehicleItem
import com.example.zaspetroleum.model.VehicleRequest

/**
 * The screens call these functions and get back an Outcome (Success or Error message).
 * Nothing here throws: network and server problems become Outcome.Error.
 */
object ZasRepository {
    private val api get() = ApiClient.api

    suspend fun services(): Outcome<List<ServiceItem>> =
        safeApiCall { api.services() }.mapData { reply -> reply.rows().map { it.toService() } }

    suspend fun vehicles(): Outcome<List<VehicleItem>> =
        safeApiCall { api.vehicles() }.mapData { reply -> reply.rows().map { it.toVehicle() } }

    suspend fun addVehicle(make: String, model: String, year: Int, registration: String): Outcome<Unit> =
        safeApiCall { api.addVehicle(VehicleRequest(make, model, year, registration)) }.mapData { }

    /** [date] is yyyy-MM-dd. */
    suspend fun slots(date: String): Outcome<List<SlotItem>> =
        safeApiCall { api.slots(date) }.mapData { reply -> reply.rows().map { it.toSlot() } }

    suspend fun bookings(): Outcome<List<BookingItem>> =
        safeApiCall { api.bookings() }.mapData { reply -> reply.rows().map { it.toBooking() } }

    /** Returns the booking reference (empty text if the server did not send one). */
    suspend fun book(vehicleId: Int, serviceId: Int, slotId: Int, notes: String): Outcome<String> =
        safeApiCall { api.book(BookingRequest(vehicleId, serviceId, slotId, notes)) }
            .mapData { reply -> reply.record()?.str("reference", "booking_reference", "ref") ?: "" }

    suspend fun cancel(bookingId: Int): Outcome<Unit> =
        safeApiCall { api.cancel(CancelRequest(bookingId)) }.mapData { }

    suspend fun profile(): Outcome<Profile> =
        safeApiCall { api.getSettings() }.mapData { reply ->
            (reply.record() ?: throw IllegalStateException("No profile")).toProfile()
        }

    suspend fun saveSettings(fullName: String, phone: String, theme: String, notificationsOn: Boolean): Outcome<Unit> =
        safeApiCall { api.saveSettings(SettingsRequest(fullName, phone, theme, notificationsOn)) }.mapData { }
}
