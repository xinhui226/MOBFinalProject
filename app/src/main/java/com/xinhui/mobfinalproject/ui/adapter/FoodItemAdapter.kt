package com.xinhui.mobfinalproject.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xinhui.mobfinalproject.data.model.Product
import com.xinhui.mobfinalproject.databinding.HorizontalItemsBinding
import com.xinhui.mobfinalproject.databinding.ShowItemLayoutBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class FoodItemAdapter(
    private var products: List<Product>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var listener: Listener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ShowItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FoodItemViewHolder(binding)
    }

    override fun getItemCount() = products.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val pro = products[position]
        if (holder is FoodItemViewHolder) {
            holder.bind(pro)
        }
    }

    fun showItems(pro: List<Product>) {
        this.products = pro
        notifyDataSetChanged()
    }

    inner class FoodItemViewHolder(
        private val binding: ShowItemLayoutBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            binding.run {
                tvFood.text = product.productName
                tvLocation.text = product.storagePlace

                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                val expiryDate = LocalDate.parse(product.expiryDate, formatter)
                val currDate = LocalDate.now()
                val daysUntilExpired = ChronoUnit.DAYS.between(currDate, expiryDate)

                // Update the tvExpired TextView
                tvExpired.text = "Expiring in $daysUntilExpired days"

                ivDelete.setOnClickListener {
                    listener?.onDelete(product)
                }
            }
        }
    }

    interface Listener {
        fun onDelete(product: Product)
    }

}

