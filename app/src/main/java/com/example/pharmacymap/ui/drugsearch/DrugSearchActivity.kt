package com.example.pharmacymap.ui.drugsearch

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pharmacymap.R
import com.example.pharmacymap.data.remote.DrugApi
import com.example.pharmacymap.data.remote.DrugItem
import com.example.pharmacymap.data.remote.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DrugSearchActivity : AppCompatActivity() {

    private val drugList = mutableListOf<DrugItem>()
    private lateinit var adapter: DrugSearchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.drug_search)

        val etSearch = findViewById<EditText>(R.id.etSearchDrug)
        val btnSearch = findViewById<Button>(R.id.btnSearchDrug)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerDrugSearch)

        adapter = DrugSearchAdapter(drugList) { drug ->
            val intent = Intent(this, DrugDetailActivity::class.java)
            intent.putExtra("drug", drug)
            startActivity(intent)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        btnSearch.setOnClickListener {
            val keyword = etSearch.text.toString()
            if (keyword.isNotEmpty()) searchDrugs(keyword)
        }
    }

    private fun searchDrugs(keyword: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d("DrugSearch", "검색 시작: $keyword")

                val api = RetrofitClient.retrofit.create(DrugApi::class.java)
                val response = api.searchDrugs(
                    serviceKey = "9129b222e0da4ac9b5ca62167e9b940201e05b8a1c8972e920b28a90ad914315",
                    itemName = keyword
                ).execute()

                Log.d("DrugSearch", "API 호출 완료, isSuccessful: ${response.isSuccessful}")

                if (response.isSuccessful) {
                    val items = response.body()?.body?.items ?: emptyList()
                    Log.d("DrugSearch", "조회 결과 개수: ${items.size}")

                    items.forEach {
                        Log.d("DrugSearch", "약품: ${it.itemName}, 효능: ${it.efcyQesitm}")
                    }

                    withContext(Dispatchers.Main) {
                        drugList.clear()
                        drugList.addAll(items)
                        adapter.notifyDataSetChanged()
                    }
                } else {
                    Log.e("DrugSearch", "API 호출 실패: ${response.code()}, ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
