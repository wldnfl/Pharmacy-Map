package com.example.pharmacymap.ui.medicine

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pharmacymap.R
import com.example.pharmacymap.data.local.entity.MedicineEntity
import java.io.File

class MedicineAdapter(
    private val items: List<MedicineEntity>,
    private val onClick: (MedicineEntity) -> Unit
) : RecyclerView.Adapter<MedicineAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val img: ImageView = view.findViewById(R.id.imgMedicine)
        val name: TextView = view.findViewById(R.id.tvMedicineName)
        val purpose: TextView = view.findViewById(R.id.tvPurpose)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_medicine, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.name.text = item.name
        holder.purpose.text = item.purpose

        if (item.imagePath.isNotEmpty()) {
            Glide.with(holder.itemView)
                .load(Uri.parse(item.imagePath))
                .into(holder.img)
        }

        holder.itemView.setOnClickListener { onClick(item) }
    }

    override fun getItemCount() = items.size
}
