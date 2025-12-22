package com.example.pharmacymap

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.pharmacymap.data.Pharmacy
import com.example.pharmacymap.data.remote.PharmacyApi
import com.example.pharmacymap.data.remote.RetrofitClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import okhttp3.ResponseBody
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.StringReader

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
        tvTel = findViewById(R.id.tvPharmacyTel)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

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

    /* 현재 위치 */
    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) return

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location ?: return@addOnSuccessListener

            val lat = location.latitude
            val lon = location.longitude

            Log.d(TAG, "현재 위치 lat=$lat lon=$lon")

            val currentLatLng = LatLng(lat, lon)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
            mMap.isMyLocationEnabled = true

            loadNearbyPharmacies(lat, lon)
        }
    }

    /* 약국 API 호출 */
    private fun loadNearbyPharmacies(lat: Double, lon: Double) {

        Log.d(TAG, "약국 API 호출 시작")
        Log.d(TAG, "요청 좌표: lat=$lat, lon=$lon")

        val api = RetrofitClient.retrofit.create(PharmacyApi::class.java)

        api.getNearbyPharmacies(
            serviceKey = "9129b222e0da4ac9b5ca62167e9b940201e05b8a1c8972e920b28a90ad914315",
            lat = lat,
            lon = lon
        ).enqueue(object : Callback<ResponseBody> {

            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                Log.d(TAG, "API 응답 코드: ${response.code()}")

                val xml = response.body()?.string()
                if (xml == null) {
                    Log.e(TAG, "XML 응답이 null")
                    return
                }
                val pharmacies = parsePharmacies(xml)

                Log.d(TAG, "파싱된 약국 개수: ${pharmacies.size}")

                if (pharmacies.isEmpty()) {
                    Log.e(TAG, "약국 리스트가 비어 있음")
                    return
                }

                mMap.clear()

                pharmacies.forEachIndexed { index, pharmacy ->
                    // 좌표 유효성 체크
                    if (pharmacy.latitude == 0.0 || pharmacy.longitude == 0.0) {
                        Log.e(TAG, "❌ 좌표 0.0 → 스킵")
                        return@forEachIndexed
                    }

                    val pos = LatLng(pharmacy.latitude, pharmacy.longitude)

                    val marker = mMap.addMarker(
                        MarkerOptions()
                            .position(pos)
                            .title(pharmacy.name)
                    )

                    if (marker == null) {
                        Log.e(TAG, "마커 생성 실패!")
                    } else {
                        Log.d(TAG, "마커 생성 성공!")
                        marker.tag = pharmacy
                    }
                }

                // 마커 클릭
                mMap.setOnMarkerClickListener { marker ->
                    val item = marker.tag as? Pharmacy
                    if (item == null) {
                        Log.e(TAG, "마커 tag가 null")
                        return@setOnMarkerClickListener false
                    }

                    Log.d(TAG, "마커 클릭: ${item.name}")

                    tvName.text = item.name
                    tvAddr.text = item.address
                    tvTel.text = item.phone

                    false
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e(TAG, "API 호출 실패", t)
            }
        })
    }

    /* XML 직접 파싱 */
    private fun parsePharmacies(xml: String): List<Pharmacy> {

        val result = mutableListOf<Pharmacy>()

        val parser = XmlPullParserFactory.newInstance().newPullParser()
        parser.setInput(StringReader(xml))

        var event = parser.eventType

        var name = ""
        var addr = ""
        var tel = ""
        var lat: Double? = null
        var lon: Double? = null

        while (event != XmlPullParser.END_DOCUMENT) {

            when (event) {
                XmlPullParser.START_TAG -> {
                    when (parser.name) {
                        "dutyName" -> name = parser.nextText()
                        "dutyAddr" -> addr = parser.nextText()
                        "dutyTel1" -> tel = parser.nextText()
                        "wgs84Lat", "WGS84LAT", "latitude" ->
                            lat = parser.nextText().trim().toDoubleOrNull()

                        "wgs84Lon", "WGS84LON", "longitude" ->
                            lon = parser.nextText().trim().toDoubleOrNull()
                    }
                }

                XmlPullParser.END_TAG -> {
                    if (parser.name == "item") {

                        Log.d(
                            TAG,
                            """
                        🧩 파싱 결과
                        name=$name
                        lat=$lat
                        lon=$lon
                        """.trimIndent()
                        )

                        if (lat != null && lon != null) {
                            result.add(
                                Pharmacy(
                                    name = name,
                                    address = addr,
                                    phone = tel,
                                    latitude = lat!!,
                                    longitude = lon!!
                                )
                            )
                        } else {
                            Log.e(TAG, "❌ 좌표 변환 실패 → 스킵")
                        }

                        name = ""; addr = ""; tel = ""
                        lat = null; lon = null
                    }
                }
            }
            event = parser.next()
        }

        return result
    }


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
