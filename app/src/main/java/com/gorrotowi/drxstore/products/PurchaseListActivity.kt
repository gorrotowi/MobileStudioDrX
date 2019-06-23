package com.gorrotowi.drxstore.products

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.zxing.integration.android.IntentIntegrator
import com.gorrotowi.drxstore.R
import com.gorrotowi.drxstore.adapter.ProductsAdapter
import com.gorrotowi.drxstore.utils.logv
import kotlinx.android.synthetic.main.activity_purchase_list.*
import kotlin.text.Typography.dollar

class PurchaseListActivity : AppCompatActivity() {

    private val adapterProducts by lazy {
        ProductsAdapter()
    }

    private val purchaseViewModel by lazy {
        ViewModelProviders.of(this@PurchaseListActivity)
            .get(PurchaseViewModel::class.java)
    }

    private var alertDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_purchase_list)

        setUpListeners()
        setUpObservers()
        setUpViews()
    }

    private fun setUpViews() {
        adapterProducts.setViewType(1)
        rcPurchaseList?.adapter = adapterProducts
    }

    private fun setUpObservers() {
        purchaseViewModel.productData.observe(this@PurchaseListActivity, Observer { item ->
            logv("ItemSearch -> ${item?.toString()}")
            adapterProducts.addProduct(item)
            btnPurchaseListCheckOut?.isEnabled = true
        })

        purchaseViewModel.errorMessage.observe(this@PurchaseListActivity, Observer { errorMessage ->
            if (errorMessage.isNotBlank()) {
                Toast.makeText(this, "Error al buscar producto $errorMessage", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Update products successfull", Toast.LENGTH_SHORT).show()
            }
        })

        adapterProducts
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val intentIntegratorScanner = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        intentIntegratorScanner?.let { result ->
            if (result.contents == null) {
                Toast.makeText(this@PurchaseListActivity, "Escaneo fallido, intenta nuevamente", Toast.LENGTH_SHORT)
                    .show()
            } else {
                logv("Code DATA ${result.contents}")
//                Toast.makeText(this@PurchaseListActivity, "El dato es: ${result.contents}", Toast.LENGTH_SHORT).show()
                purchaseViewModel.searchProduct(result.contents.trim())
            }
        }
    }

    override fun onPause() {
        super.onPause()
        alertDialog?.dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        alertDialog?.dismiss()
    }

    private fun setUpListeners() {
        btnPurchaseListAdd?.setOnClickListener {
            val intentBarCode = IntentIntegrator(this@PurchaseListActivity)
            intentBarCode.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            intentBarCode.setPrompt("Escanea un producto de la tienda para agregarlo a la lista")
            intentBarCode.setBeepEnabled(true)
            intentBarCode.setTimeout(5_000)
            intentBarCode.initiateScan()
        }

        btnPurchaseListCheckOut?.setOnClickListener {
            var totalProducts = 0
            var totalPrice = 0f
            adapterProducts.dataSource.map { product ->
                totalProducts += product?.quantity ?: 1
                val priceProduct = product?.price?.times(product.quantity) ?: 0f
                totalPrice += priceProduct
            }

            alertDialog = AlertDialog.Builder(this).apply {
                setTitle("Venta")
                setMessage(
                    """
                    |El total de productos es de $totalProducts
                    |y el total a pagar es de $dollar$totalPrice""".trimMargin()
                )
                    .setPositiveButton("Aceptar") { dialog, _ ->
                        dialog.dismiss()
                        purchaseViewModel.updateProductsAfterCheckout(adapterProducts.dataSource.toList())
                    }
                    .setNegativeButton("Cancelar") { dialog, _ ->
                        dialog.dismiss()
                    }
            }.create()
            alertDialog?.show()
        }

        adapterProducts.setOnItemClickListener { position, _ ->
            logv("Click on Item $position")
            alertDialog = AlertDialog.Builder(this).apply {
                setMessage("Quieres eliminar el producto de la lista?")
                setCancelable(false)
                setPositiveButton("Eliminar") { dialog, _ ->
                    adapterProducts.removeProduct(position)
                    if (adapterProducts.dataSource.isEmpty()) {
                        btnPurchaseListCheckOut?.isEnabled = false
                    }
                    dialog.dismiss()
                }
                setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.dismiss()
                }
            }.create()

            alertDialog?.show()
        }
    }
}
