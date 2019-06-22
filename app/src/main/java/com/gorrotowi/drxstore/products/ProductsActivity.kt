package com.gorrotowi.drxstore.products

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.gorrotowi.core.ProductEntity
import com.gorrotowi.drxstore.R
import com.gorrotowi.drxstore.adapter.ProductsAdapter
import kotlinx.android.synthetic.main.activity_products.*

class ProductsActivity : AppCompatActivity() {

    private val productsViewModel: ProductsViewModel by lazy {
        ViewModelProviders.of(this).get(ProductsViewModel::class.java)
    }

    private val productsAdapter: ProductsAdapter by lazy {
        ProductsAdapter()
    }

    private var alert: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_products)

        setUpViews()
        setUpObservers()
        setUpListeners()

        productsViewModel.getProducts()
    }

    override fun onPause() {
        super.onPause()
        alert?.cancel()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val productToSaveOrUpdate = data?.extras?.let { params ->
                ProductEntity(
                    params.getString(FormProductActivity.PRODUCT_ID, ""),
                    params.getString(FormProductActivity.PRODUCT_NAME, ""),
                    params.getString(FormProductActivity.PRODUCT_DESCRIPTION, ""),
                    params.getInt(FormProductActivity.PRODUCT_QUANTITY, 1),
                    params.getString(FormProductActivity.PRODUCT_CODE, ""),
                    params.getString(FormProductActivity.PRODUCT_URL_IMG, "")
                )
            }
            productToSaveOrUpdate?.let { product ->
                when (requestCode) {
                    FormProductActivity.ADD_PRODUCT_REQUEST_CODE -> {
                        productsViewModel.addProduct(product)
                    }
                    FormProductActivity.UPDATE_PRODUCT_REQUEST_CODE -> {
                        productsViewModel.updateProduct(product)
                    }
                }
            }
        }
    }

    private fun setUpObservers() {
        productsViewModel.productsList.observe(this, Observer { productsList ->
            productsAdapter.dataSource = productsList
        })

        productsViewModel.isSuccessTransation.observe(this, Observer {
            Toast.makeText(this@ProductsActivity, "Producto agregado", Toast.LENGTH_SHORT).show()
        })

        productsViewModel.isSuccessDeleteTransation.observe(this, Observer {
            Toast.makeText(this@ProductsActivity, "Producto eliminado", Toast.LENGTH_SHORT).show()
        })

        productsViewModel.isSuccessUpdateTransation.observe(this, Observer {
            Toast.makeText(this@ProductsActivity, "Producto actualizado", Toast.LENGTH_SHORT).show()
        })

        productsViewModel.errorMessage.observe(this, Observer { errorMessage ->
            Toast.makeText(this@ProductsActivity, errorMessage, Toast.LENGTH_SHORT).show()
        })
    }

    private fun setUpListeners() {
        fabAddProducts?.setOnClickListener {
            val intent = Intent(this@ProductsActivity, FormProductActivity::class.java)
            startActivityForResult(intent, FormProductActivity.ADD_PRODUCT_REQUEST_CODE)
        }

        productsAdapter.setOnItemClickListener { productItem ->
            alert = AlertDialog.Builder(this@ProductsActivity).apply {
                setTitle("Producto ${productItem.productName}")
                setCancelable(true)
                setMessage("Quieres actualizar o eliminar este producto de tu inventario")
                setPositiveButton("Actualizar") { dialog, _ ->
                    dialog.dismiss()
                    val intent = Intent(this@ProductsActivity, FormProductActivity::class.java)
                    intent.putExtra(FormProductActivity.PRODUCT_ID, productItem.id)
                    intent.putExtra(FormProductActivity.PRODUCT_NAME, productItem.productName)
                    intent.putExtra(FormProductActivity.PRODUCT_DESCRIPTION, productItem.productDescrp)
                    intent.putExtra(FormProductActivity.PRODUCT_QUANTITY, productItem.quantity)
                    intent.putExtra(FormProductActivity.PRODUCT_CODE, productItem.productCode)
                    intent.putExtra(FormProductActivity.PRODUCT_URL_IMG, productItem.urlImg)
                    startActivityForResult(intent, FormProductActivity.UPDATE_PRODUCT_REQUEST_CODE)
                }
                setNegativeButton("Eliminar") { dialog, _ ->
                    dialog.dismiss()
                    productsViewModel.deleteProduct(productItem)
                }
            }.create()
            alert?.show()
        }
    }

    private fun setUpViews() {
        rcProductList.layoutManager = LinearLayoutManager(this@ProductsActivity)
        rcProductList.adapter = productsAdapter
    }
}
