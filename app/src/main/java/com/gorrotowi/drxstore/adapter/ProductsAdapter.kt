package com.gorrotowi.drxstore.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gorrotowi.core.ProductEntity
import com.gorrotowi.drxstore.R
import com.gorrotowi.drxstore.adapter.viewholders.MyViewHolder
import com.gorrotowi.drxstore.adapter.viewholders.ProductsViewHolder
import com.gorrotowi.drxstore.adapter.viewholders.PurchaseViewHolder
import com.gorrotowi.drxstore.utils.inflateView
import com.gorrotowi.drxstore.utils.logv
import kotlin.properties.Delegates

class ProductsAdapter : RecyclerView.Adapter<MyViewHolder>() {

    var dataSource: MutableList<ProductEntity?> by Delegates.observable(mutableListOf()) { _, _, _ ->
        logv("UpdateData")
        notifyDataSetChanged()
    }
    lateinit var onItemListener: (position: Int, product: ProductEntity) -> Unit

    private var vType = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        0 -> {
            ProductsViewHolder(parent.inflateView(R.layout.item_products))
        }
        1 -> {
            PurchaseViewHolder(parent.inflateView(R.layout.item_purchase))
        }
        else -> {
            ProductsViewHolder(parent.inflateView(R.layout.item_products))
        }
    }

    override fun getItemCount(): Int = dataSource.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        logv("OnBinder -> $position ${dataSource[position]?.toString()}")
        dataSource[position]?.let { holder.bindView(it) }
        holder.itemView.setOnClickListener {
            if (::onItemListener.isInitialized) {
                dataSource[position]?.let { it1 -> onItemListener(position, it1) }
            }
        }

        holder.onControlQuantityChange { quantity ->
            dataSource[position]?.let { product ->
                product.quantity = product.quantity + quantity
                notifyDataSetChanged()
            }
        }

    }

    fun addProduct(productEntity: ProductEntity?) {
        dataSource.add(productEntity)
        notifyDataSetChanged()
    }

    fun removeProduct(position: Int) {
        dataSource.removeAt(position)
        notifyDataSetChanged()
    }

    fun setViewType(viewType: Int) {
        vType = viewType
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return vType
    }

    fun setOnItemClickListener(block: (position: Int, product: ProductEntity) -> Unit) {
        onItemListener = block
    }
}
