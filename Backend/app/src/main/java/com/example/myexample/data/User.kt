package com.example.myexample.data

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("_id") val id: String,
    val fullName: String,
    val email: String,
    val phn_no: Long,
    val age: Int,
    val profession: String,
    val f_name: String,
    val f_email: String,
    val f_phn_no: Long,
    val m_name: String,
    val m_email: String,
    val m_phn_no: Long,
    val g_name: String,
    val g_email: String,
    val g_phn_no: Long,
    val address: String,
    val city_or_village: String,
    val post_office: String,
    val district: String,
    val state: String,
    val country: String,
    val Pin: String
)
