package com.example.pharmacymap.ui.drugsearch

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pharmacymap.R
import com.example.pharmacymap.data.remote.DrugItem

class DrugSearchAdapter(
    private val items: List<DrugItem>,
    private val onClick: (DrugItem) -> Unit
) : RecyclerView.Adapter<DrugSearchAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvDrugName)
        val tvEfcy: TextView = view.findViewById(R.id.tvDrugEfcy)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_drug, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.tvName.text = item.itemName ?: ""
        holder.tvEfcy.text = item.efcyQesitm ?: ""
        holder.itemView.setOnClickListener { onClick(item) }
    }

    override fun getItemCount() = items.size
}
