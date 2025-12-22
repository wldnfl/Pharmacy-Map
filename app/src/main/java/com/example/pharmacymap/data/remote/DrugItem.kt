package com.example.pharmacymap.data.remote

import java.io.Serializable
data class DrugItem(
    val itemName: String?,
    val efcyQesitm: String?,
    val useMethodQesitm: String?,
    val atpnWarnQesitm: String?,
    val atpnQesitm: String?,
    val intrcQesitm: String?,
    val seQesitm: String?,
    val depositMethodQesitm: String?
) : Serializable
