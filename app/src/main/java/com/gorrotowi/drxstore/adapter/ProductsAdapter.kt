package com.gorrotowi.drxstore.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gorrotowi.core.ProductEntity
import com.gorrotowi.drxstore.R
import com.gorrotowi.drxstore.adapter.viewholders.ProductsViewHolder
import com.gorrotowi.drxstore.utils.inflateView
import kotlin.properties.Delegates

class ProductsAdapter : RecyclerView.Adapter<ProductsViewHolder>() {

    var dataSource: List<ProductEntity?> by Delegates.observable(emptyList()) { _, _, _ -> notifyDataSetChanged() }
    lateinit var onItemListener: (ProductEntity) -> Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        return ProductsViewHolder(parent.inflateView(R.layout.item_products))
    }

    override fun getItemCount(): Int = dataSource.size

    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {
        dataSource[position]?.let { holder.bindView(it) }
        holder.itemView.setOnClickListener {
            if (::onItemListener.isInitialized) {
                dataSource[position]?.let { it1 -> onItemListener(it1) }
            }
        }
    }

    fun setOnItemClickListener(block: (ProductEntity) -> Unit) {
        onItemListener = block
    }
}
