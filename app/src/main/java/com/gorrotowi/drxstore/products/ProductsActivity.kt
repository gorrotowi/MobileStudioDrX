package com.gorrotowi.drxstore.products

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.gorrotowi.core.ProductEntity
import com.gorrotowi.drxstore.R
import com.gorrotowi.drxstore.adapter.ProductsAdapter
import com.gorrotowi.drxstore.utils.toast
import kotlinx.android.synthetic.main.activity_products.*

class ProductsActivity : Fragment() {

    private val productsViewModel: ProductsViewModel by lazy {
        ViewModelProviders.of(this).get(ProductsViewModel::class.java)
    }

    private val productsAdapter: ProductsAdapter by lazy {
        ProductsAdapter()
    }

    private var alert: AlertDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_products, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        setUpObservers()
        setUpListeners()

        productsViewModel.getProducts()
        productsViewModel.getProductsChannel()
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
                    params.getString(FormProductActivity.PRODUCT_URL_IMG, ""),
                    params.getFloat(FormProductActivity.PRODUCT_PRICE, 0f),
                    params.getString(FormProductActivity.PRODUCT_PATH_IMG, "")
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
            productsList?.toMutableList()?.let { list ->
                productsAdapter.dataSource = list
            }
        })

        productsViewModel.isSuccessTransation.observe(this, Observer {
            activity?.toast("Producto agregado")
        })

        productsViewModel.isSuccessDeleteTransation.observe(this, Observer {
            activity?.toast("Producto eliminado")
        })

        productsViewModel.isSuccessUpdateTransation.observe(this, Observer {
            activity?.toast("Producto actualizado")
        })

        productsViewModel.errorMessage.observe(this, Observer { errorMessage ->
            activity?.toast(errorMessage)
        })
    }

    private fun setUpListeners() {
        fabAddProducts?.setOnClickListener {
            val intent = Intent(activity, FormProductActivity::class.java)
            startActivityForResult(intent, FormProductActivity.ADD_PRODUCT_REQUEST_CODE)
        }

        productsAdapter.setOnItemClickListener { _, productItem ->
            alert = AlertDialog.Builder(activity).apply {
                setTitle("Producto ${productItem.productName}")
                setCancelable(true)
                setMessage("Quieres actualizar o eliminar este producto de tu inventario")
                setPositiveButton("Actualizar") { dialog, _ ->
                    dialog.dismiss()
                    val intent = Intent(activity, FormProductActivity::class.java)
                    intent.putExtra(FormProductActivity.PRODUCT_ID, productItem.id)
                    intent.putExtra(FormProductActivity.PRODUCT_NAME, productItem.productName)
                    intent.putExtra(
                        FormProductActivity.PRODUCT_DESCRIPTION,
                        productItem.productDescrp
                    )
                    intent.putExtra(FormProductActivity.PRODUCT_QUANTITY, productItem.quantity)
                    intent.putExtra(FormProductActivity.PRODUCT_CODE, productItem.productCode)
                    intent.putExtra(FormProductActivity.PRODUCT_URL_IMG, productItem.urlImg)
                    intent.putExtra(FormProductActivity.PRODUCT_PRICE, productItem.price)
                    intent.putExtra(FormProductActivity.PRODUCT_PATH_IMG, productItem.pathImg)
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
        rcProductList.layoutManager = LinearLayoutManager(activity)
        rcProductList.adapter = productsAdapter
    }
}
