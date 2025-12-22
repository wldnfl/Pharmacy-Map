package com.example.pharmacymap

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.pharmacymap.data.remote.PharmacyApi
import com.example.pharmacymap.data.remote.PharmacyItem
import com.example.pharmacymap.data.remote.PharmacyResponse
import com.example.pharmacymap.data.remote.RetrofitClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FindPharmacyActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mMap: GoogleMap

    private lateinit var tvName: TextView
    private lateinit var tvAddr: TextView
    private lateinit var tvTel: TextView

    companion object {
        const val LOCATION_PERMISSION_REQUEST = 1000
        const val TAG = "PHARMACY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.find_pharmacy)

        tvName = findViewById(R.id.tvPharmacyName)
        tvAddr = findViewById(R.id.tvPharmacyAddr)
        tvTel  = findViewById(R.id.tvPharmacyTel)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync { googleMap ->
            mMap = googleMap
            checkLocationPermission()
        }
    }

    /* 위치 권한 체크 */
    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getCurrentLocation()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST
            )
        }
    }

    /* 현재 위치 가져오기 */
    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) return

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                val lat = it.latitude
                val lon = it.longitude

                Log.d(TAG, "현재 위치: lat=$lat, lon=$lon")

                val currentLatLng = LatLng(lat, lon)

                mMap.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f)
                )
                mMap.isMyLocationEnabled = true

                // ⭐ 현재 위치 기준 약국 검색
                loadNearbyPharmacies(lat, lon)
            }
        }
    }

    /* 약국 API 호출 */
    private fun loadNearbyPharmacies(lat: Double, lon: Double) {

        val api = RetrofitClient.retrofit.create(PharmacyApi::class.java)

        Log.d(TAG, "약국 API 호출 시작")

        api.getNearbyPharmacies(
            serviceKey = "9129b222e0da4ac9b5ca62167e9b940201e05b8a1c8972e920b28a90ad914315",
            lat = lat,
            lon = lon
        ).enqueue(object : Callback<PharmacyResponse> {

            override fun onResponse(
                call: Call<PharmacyResponse>,
                response: Response<PharmacyResponse>
            ) {
                Log.d(TAG, "응답 코드: ${response.code()}")

                val pharmacies =
                    response.body()?.body?.items?.itemList ?: emptyList()

                Log.d(TAG, "약국 개수: ${pharmacies.size}")

                pharmacies.forEach { item ->
                    Log.d(TAG, "약국명: ${item.dutyName}")

                    val pos = LatLng(item.wgs84Lat, item.wgs84Lon)

                    val marker = mMap.addMarker(
                        MarkerOptions()
                            .position(pos)
                            .title(item.dutyName)
                    )

                    marker?.tag = item
                }

                // 마커 클릭 시 하단 정보 표시
                mMap.setOnMarkerClickListener { marker ->
                    val item = marker.tag as? PharmacyItem
                        ?: return@setOnMarkerClickListener false

                    tvName.text = item.dutyName
                    tvAddr.text = item.dutyAddr
                    tvTel.text  = item.dutyTel1

                    false
                }
            }

            override fun onFailure(call: Call<PharmacyResponse>, t: Throwable) {
                Log.e(TAG, "API 호출 실패", t)
            }
        })
    }

    /* 권한 요청 결과 */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == LOCATION_PERMISSION_REQUEST &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            getCurrentLocation()
        }
    }
}
