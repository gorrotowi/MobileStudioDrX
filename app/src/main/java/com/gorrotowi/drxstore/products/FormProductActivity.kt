package com.gorrotowi.drxstore.products

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.gorrotowi.drxstore.R
import com.gorrotowi.drxstore.utils.loge
import com.gorrotowi.drxstore.utils.logv
import kotlinx.android.synthetic.main.activity_form_product.*

class FormProductActivity : AppCompatActivity() {

    private var quantity = 0
    private var idProduct = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_product)

        setUpViews()
        setUpListeners()
        getExtrasProducto(intent)

    }

    private fun getExtrasProducto(productIntent: Intent?) {
        productIntent?.extras?.apply {
            idProduct = getString(PRODUCT_ID, "")
            val name = getString(PRODUCT_NAME, "")
            val descrip = getString(PRODUCT_DESCRIPTION, "")
            quantity = getInt(PRODUCT_QUANTITY, 0)
            val code = getString(PRODUCT_CODE, "")
            val urlImg = getString(
                PRODUCT_URL_IMG
                , ""
            )
            val price = getFloat(PRODUCT_PRICE, 0f)

            edtxFormProductName?.setText(name)
            edtxFormProductDescription?.setText(descrip)
            txtFormProductQuantity?.text = "$quantity"
            edtxFormProductPrice?.setText("$price")

            if (urlImg?.isNotBlank() == true) {
                Glide.with(this@FormProductActivity)
                    .asBitmap()
                    .load(urlImg)
                    .placeholder(R.drawable.image_placeholder)
                    .into(imgFormProduct)
            }
        }
    }

    private fun setUpListeners() {

        btnFormProductSave?.setOnClickListener {
            returnProductInfo()
        }

        btnFormProductSubstract?.setOnClickListener {
            if (quantity > 0) {
                quantity -= 1
                txtFormProductQuantity?.text = "$quantity"
            }
        }

        btnFormProductAdd?.setOnClickListener {
            quantity += 1
            txtFormProductQuantity?.text = "$quantity"
        }

        btnFormProductTakePhoto?.setOnClickListener {
            val intentCamera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intentCamera, REQUEST_CODE_CAMERA)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        logv("$resultCode requestCode = $requestCode")
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_CAMERA) {
            val bitmapPhoto = data?.extras?.get("data") as? Bitmap
            loge("Bitmap ->>> $bitmapPhoto")
            imgFormProduct?.setImageBitmap(bitmapPhoto)
        }
    }

    private fun returnProductInfo() {
        val intent = Intent().apply {
            putExtra(PRODUCT_ID, idProduct)
            putExtra(PRODUCT_NAME, edtxFormProductName?.text?.toString())
            putExtra(PRODUCT_DESCRIPTION, edtxFormProductDescription?.text?.toString())
            putExtra(PRODUCT_QUANTITY, quantity)
            putExtra(PRODUCT_PRICE, edtxFormProductPrice?.text?.toString()?.toFloatOrNull())
        }

        setResult(Activity.RESULT_OK, intent)
        finish()

    }

    private fun cancelAddProduct() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    private fun setUpViews() {
        toolbarFormProduct?.let { tlb ->
            tlb.title = "Agregar producto"
            setSupportActionBar(tlb)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val REQUEST_CODE_CAMERA = 5001
        const val ADD_PRODUCT_REQUEST_CODE = 4020
        const val UPDATE_PRODUCT_REQUEST_CODE = 4030
        const val PRODUCT_ID = "PRODUCT_ID"
        const val PRODUCT_NAME = "PRODUCT_NAME"
        const val PRODUCT_DESCRIPTION = "PRODUCT_DESCRIPTION"
        const val PRODUCT_QUANTITY = "PRODUCT_QUANTITY"
        const val PRODUCT_CODE = "PRODUCT_CODE"
        const val PRODUCT_URL_IMG = "PRODUCT_URL_IMG"
        const val PRODUCT_PRICE = "PRODUCT_PRICE"
    }

}
