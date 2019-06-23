package com.gorrotowi.drxstore.adapter.viewholders

import android.view.View
import com.gorrotowi.core.ProductEntity
import kotlinx.android.synthetic.main.item_products.view.*

class ProductsViewHolder(private val view: View) : MyViewHolder(view) {

    override fun bindView(productEntity: ProductEntity) {
        view.txtItemProduct?.text = productEntity.productName
    }

}