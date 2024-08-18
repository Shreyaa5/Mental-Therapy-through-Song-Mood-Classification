package com.example.myexample.utilis

import com.example.myexample.data.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface APIConsumer {

    @POST("users/validate-unique-email")
    suspend fun validateEmailAddress(@Body body: ValidateEmailBody): Response<UniqueEmailValidationResponse>

    @POST("users/register")
    suspend fun registerUser(@Body body: RegisterBody): Response<AuthResponse>

    @POST("users/login")
    suspend fun loginUser(@Body body: LoginBody): Response<AuthResponse>
}