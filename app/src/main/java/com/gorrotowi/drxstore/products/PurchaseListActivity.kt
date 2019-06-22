package com.gorrotowi.drxstore.products

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.zxing.integration.android.IntentIntegrator
import com.gorrotowi.drxstore.R
import com.gorrotowi.drxstore.utils.logv
import kotlinx.android.synthetic.main.activity_purchase_list.*

class PurchaseListActivity : AppCompatActivity() {

    val purchaseViewModel by lazy {
        ViewModelProviders.of(this@PurchaseListActivity)
            .get(PurchaseViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_purchase_list)

        setUpListeners()
        setUpObservers()
    }

    private fun setUpObservers() {
        purchaseViewModel.productData.observe(this@PurchaseListActivity, Observer { item ->
            logv("ItemSearch -> ${item?.toString()}")
        })

        purchaseViewModel.errorMessage.observe(this@PurchaseListActivity, Observer { errorMessage ->
            Toast.makeText(this, "Error al buscar producto $errorMessage", Toast.LENGTH_SHORT).show()
        })
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
                purchaseViewModel.searchProduct(result.contents)
            }
        }
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
    }
}
