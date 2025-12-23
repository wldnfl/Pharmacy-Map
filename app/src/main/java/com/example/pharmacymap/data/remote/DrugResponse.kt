package com.example.pharmacymap.data.remote

data class DrugResponse(
    val body: DrugBody?
)
data class DrugBody(
    val items: List<DrugItem>?
)
