package com.example.pharmacymap.data.remote

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface DrugApi {

    @GET("1471000/DrbEasyDrugInfoService/getDrbEasyDrugList")
    fun searchDrugs(
        @Query("serviceKey") serviceKey: String,
        @Query("itemName") itemName: String,
        @Query("pageNo") pageNo: Int = 1,
        @Query("numOfRows") numOfRows: Int = 20,
        @Query("type") type: String = "json"
    ): Call<DrugResponse>
}
