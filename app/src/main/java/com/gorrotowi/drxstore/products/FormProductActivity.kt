package com.gorrotowi.drxstore.products

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.gorrotowi.drxstore.R
import com.gorrotowi.drxstore.utils.logv
import kotlinx.android.synthetic.main.activity_form_product.*
import java.util.*

class FormProductActivity : AppCompatActivity() {

    private var imgBitmap: Bitmap? = null
    private val formProductViewModel by lazy {
        ViewModelProviders.of(this).get(FormProductViewModel::class.java)
    }

    private var quantity = 0
    private var idProduct = ""
    private var pathImg = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_product)

        setUpViews()
        setUpObservables()
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
            pathImg = getString(PRODUCT_PATH_IMG, "")
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

    private fun setUpObservables() {
        formProductViewModel.uploadSuccess.observe(this, Observer { result ->
            progressDialog?.visibility = View.GONE
            if (result.second != null) {
                Toast.makeText(this, result.second, Toast.LENGTH_LONG).show()
            } else {
                returnProductInfo(result.first?.path, result?.first?.url)
            }
        })

        formProductViewModel.deleteSuccess.observe(this, Observer { result ->
            clearImage()
            if (result.first == false) {
                Toast.makeText(this, "${result.second}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun setUpListeners() {

        btnFormProductSave?.setOnClickListener {
            //            val bmp = imgBitmap
//            if (bmp != null) {
//                formProductViewModel.uploadBitmapImg("", bmp)
//            } else {
//                returnProductInfo()
//            }

            imgBitmap?.let { bitmap ->
                progressDialog?.visibility = View.VISIBLE
                formProductViewModel.uploadBitmapImg("${Calendar.getInstance().timeInMillis}", bitmap)
            } ?: returnProductInfo()
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
            //            val intentCamera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//            startActivityForResult(intentCamera, REQUEST_CODE_CAMERA)

            ImagePicker.with(this)
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start(REQUEST_CODE_CAMERA)
        }

        btnFormDeleteImage?.setOnClickListener {
            if (pathImg.isNotEmpty()) {
                formProductViewModel.deleteImage(pathImg)
            } else {
                clearImage()
//                Toast.makeText(
//                    this,
//                    "Para poder borrar una imagen, primero debes tomar una",
//                    Toast.LENGTH_LONG
//                ).show()
            }
        }

    }

    fun clearImage() {
        imgBitmap = null
        pathImg = ""
        imgFormProduct?.setImageResource(R.drawable.image_placeholder)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        logv("$resultCode requestCode = $requestCode")
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_CAMERA) {
//            val bitmapPhoto = data?.extras?.get("data") as? Bitmap
//            loge("Bitmap ->>> $bitmapPhoto")
//            imgBitmap = bitmapPhoto
//            imgFormProduct?.setImageBitmap(bitmapPhoto)

            val fileUri = data?.data
            imgFormProduct?.setImageURI(fileUri)
            val file = ImagePicker.getFilePath(data)
            val bOptions = BitmapFactory.Options()
            bOptions.inPreferredConfig = Bitmap.Config.ARGB_8888
            imgBitmap = BitmapFactory.decodeFile(file, bOptions)

        }
    }

    private fun returnProductInfo(pathImg: String? = null, urlImage: String? = null) {
        val intent = Intent().apply {
            putExtra(PRODUCT_ID, idProduct)
            putExtra(PRODUCT_NAME, edtxFormProductName?.text?.toString())
            putExtra(PRODUCT_DESCRIPTION, edtxFormProductDescription?.text?.toString())
            putExtra(PRODUCT_QUANTITY, quantity)
            putExtra(PRODUCT_PRICE, edtxFormProductPrice?.text?.toString()?.toFloatOrNull())
            putExtra(PRODUCT_URL_IMG, urlImage)
            putExtra(PRODUCT_PATH_IMG, pathImg)
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
        const val PRODUCT_PATH_IMG = "PRODUCT_PATH_IMG"
        const val PRODUCT_PRICE = "PRODUCT_PRICE"
    }

}
