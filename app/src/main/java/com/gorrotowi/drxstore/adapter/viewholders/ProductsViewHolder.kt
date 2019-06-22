package com.gorrotowi.drxstore.adapter.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.gorrotowi.core.ProductEntity
import kotlinx.android.synthetic.main.item_products.view.*

class ProductsViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

    fun bindView(data: ProductEntity) {
        view.txtItemProduct?.text = data.productName
    }

}