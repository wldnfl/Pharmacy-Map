package com.example.pharmacymap.data

data class Pharmacy(
    val name: String,       // 약국명
    val address: String,    // 주소
    val phone: String,      // 전화번호
    val latitude: Double,   // 위도
    val longitude: Double   // 경도
)