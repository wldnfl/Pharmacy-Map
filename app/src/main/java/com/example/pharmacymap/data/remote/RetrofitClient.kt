package com.example.pharmacymap.data.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory

object RetrofitClient {

    private const val BASE_URL = "https://apis.data.go.kr/"

    private val client = OkHttpClient.Builder()
        .build()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(SimpleXmlConverterFactory.create())
        .build()
}
