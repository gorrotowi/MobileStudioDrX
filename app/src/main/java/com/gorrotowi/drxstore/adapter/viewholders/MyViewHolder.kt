package com.gorrotowi.drxstore.adapter.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.gorrotowi.core.ProductEntity

abstract class MyViewHolder(item: View) : RecyclerView.ViewHolder(item) {
    open fun bindView(productEntity: ProductEntity) {}
    open fun onControlQuantityChange(func: (quantity: Int) -> Unit) {}
}