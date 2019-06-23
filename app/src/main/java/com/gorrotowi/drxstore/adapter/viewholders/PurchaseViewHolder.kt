package com.gorrotowi.drxstore.adapter.viewholders

import android.view.View
import com.gorrotowi.core.ProductEntity
import kotlinx.android.synthetic.main.item_purchase.view.*
import java.util.*
import kotlin.text.Typography.dollar
import kotlin.text.Typography.quote

class PurchaseViewHolder(itemView: View) : MyViewHolder(itemView) {

    override fun bindView(productEntity: ProductEntity) {
        itemView.txtPurchaseProductName?.text = productEntity.productName
        itemView.txtPurchaseProductDescr?.text = productEntity.productDescrp
        itemView.txtPurchaseQuantity?.text = "${productEntity.quantity}"
        val priceFormat = try {
            "%,d".format(Locale.US, productEntity.price)
        } catch (e: Exception) {
            "%,d".format(Locale.US, 0)
        }
        itemView.txtPurchasePrice?.text = "$dollar$priceFormat"
    }

    override fun onControlQuantityChange(func: (quantity: Int) -> Unit) {
        super.onControlQuantityChange(func)
        onControlChange(func)
    }

    private fun onControlChange(block: (quantity: Int) -> Unit) {

        itemView.btnPurchaseQuantityMinus?.setOnClickListener {
            block(-1)
        }

        itemView.btnPurchaseQuantityAdd?.setOnClickListener {
            block(1)
        }
    }

}