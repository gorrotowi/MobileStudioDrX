package com.gorrotowi.drxstore.products

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.zxing.integration.android.IntentIntegrator
import com.gorrotowi.drxstore.R
import com.gorrotowi.drxstore.adapter.ProductsAdapter
import com.gorrotowi.drxstore.utils.logv
import com.gorrotowi.drxstore.utils.toast
import kotlinx.android.synthetic.main.activity_purchase_list.*
import kotlin.text.Typography.dollar

class PurchaseListActivity : Fragment() {

    private val adapterProducts by lazy {
        ProductsAdapter()
    }

    private val purchaseViewModel by lazy {
        ViewModelProviders.of(this@PurchaseListActivity)
            .get(PurchaseViewModel::class.java)
    }

    private var alertDialog: AlertDialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_purchase_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
                activity?.toast("Error al buscar producto $errorMessage")
            } else {
                activity?.toast("Update products successfull")
            }
        })

        adapterProducts
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        logv("FRAGMENT!!!! ")
        val intentIntegratorScanner = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        intentIntegratorScanner?.let { result ->
            if (result.contents == null) {
                activity?.toast("Escaneo fallido, intenta nuevamente")
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
            val intentBarCode = IntentIntegrator.forSupportFragment(this@PurchaseListActivity)
            intentBarCode.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            intentBarCode.setPrompt("Escanea un producto de la tienda para agregarlo a la lista")
            intentBarCode.setBeepEnabled(false)
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

            alertDialog = activity?.let { ctx ->
                AlertDialog.Builder(ctx).apply {
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
            }
            alertDialog?.show()
        }

        adapterProducts.setOnItemClickListener { position, _ ->
            logv("Click on Item $position")
            alertDialog = activity?.let { ctx ->
                AlertDialog.Builder(ctx).apply {
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
            }

            alertDialog?.show()
        }
    }
}
