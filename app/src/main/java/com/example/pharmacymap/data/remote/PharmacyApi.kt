package com.example.pharmacymap.data.remote

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PharmacyApi {

    // 약국 위치정보 조회 (반경 검색)
    @GET("B552657/ErmctInsttInfoInqireService/getParmacyLcinfoInqire")
    fun getNearbyPharmacies(
        @Query("serviceKey") serviceKey: String,
        @Query("WGS84_LAT") lat: Double,
        @Query("WGS84_LON") lon: Double,
        @Query("pageNo") pageNo: Int = 1,
        @Query("numOfRows") numOfRows: Int = 50
    ): Call<ResponseBody>
}
