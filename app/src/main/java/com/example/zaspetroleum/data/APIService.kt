package com.example.zaspetroleum.data

import com.example.zaspetroleum.model.*
import com.google.gson.JsonElement
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface ApiService {
    @POST("index.php?route=register")
    suspend fun register(@Body body: RegisterRequest): Response<JsonElement>

    @POST("index.php?route=login")
    suspend fun login(@Body body: LoginRequest): Response<LoginResponse>

    @GET("index.php?route=settings")
    suspend fun getSettings(): Response<JsonElement>

    @PUT("index.php?route=settings")
    suspend fun saveSettings(@Body body: SettingsRequest): Response<JsonElement>

    @GET("index.php?route=services")
    suspend fun services(): Response<JsonElement>

    @GET("index.php?route=vehicles")
    suspend fun vehicles(): Response<JsonElement>

    @POST("index.php?route=vehicles")
    suspend fun addVehicle(@Body body: VehicleRequest): Response<JsonElement>

    @GET("index.php?route=slots")
    suspend fun slots(@Query("date") date: String? = null): Response<JsonElement>

    @GET("index.php?route=bookings")
    suspend fun bookings(): Response<JsonElement>

    @POST("index.php?route=bookings")
    suspend fun book(@Body body: BookingRequest): Response<JsonElement>

    @POST("index.php?route=cancel_booking")
    suspend fun cancel(@Body body: CancelRequest): Response<JsonElement>

    @POST("index.php?route=feedback")
    suspend fun feedback(@Body body: FeedbackRequest): Response<JsonElement>
}