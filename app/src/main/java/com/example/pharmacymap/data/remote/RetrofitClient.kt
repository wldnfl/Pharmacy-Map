package com.example.pharmacymap.data.remote

import retrofit2.Retrofit

object RetrofitClient {

    private const val BASE_URL = "https://apis.data.go.kr/"

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .build()
}
