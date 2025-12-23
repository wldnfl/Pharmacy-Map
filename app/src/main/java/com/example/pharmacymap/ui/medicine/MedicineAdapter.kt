package com.example.pharmacymap.ui.medicine

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
    private var items: List<MedicineEntity>,
    private val itemClick: (MedicineEntity) -> Unit
) : RecyclerView.Adapter<MedicineAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val img: ImageView = view.findViewById(R.id.imgMedicine)
        val tvName: TextView = view.findViewById(R.id.tvMedicineName)
        val tvPurpose: TextView = view.findViewById(R.id.tvPurpose)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_medicine, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val medicine = items[position]
        holder.tvName.text = medicine.name
        holder.tvPurpose.text = medicine.purpose

        if (medicine.imagePath.isNotEmpty())
            Glide.with(holder.img.context)
                .load(File(medicine.imagePath))
                .into(holder.img)
        else
            holder.img.setImageResource(R.drawable.ic_default_medicine)

        holder.itemView.setOnClickListener {
            itemClick(medicine)
        }
    }

    fun setItems(newItems: List<MedicineEntity>) {
        items = newItems
        notifyDataSetChanged()
    }
}
