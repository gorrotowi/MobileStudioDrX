package com.gorrotowi.drxstore.products

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.gorrotowi.core.ProductEntity
import com.gorrotowi.drxstore.utils.loge
import com.gorrotowi.drxstore.utils.logv
import com.gorrotowi.repository.sessions.RepositoryPurchaseTransaction
import com.gorrotowi.repository.sessions.ResultPurchaseTransaction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class PurchaseViewModel(application: Application) : AndroidViewModel(application) {

    val productData = MutableLiveData<ProductEntity?>()
    val errorMessage = MutableLiveData<String>()

    private val parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main

    private val scope = CoroutineScope(coroutineContext)

    private val repositoryPurchase by lazy {
        RepositoryPurchaseTransaction()
    }

    fun searchProduct(id: String) {
        scope.launch {
            when (val result = repositoryPurchase.searchProduct(id)) {
                is ResultPurchaseTransaction.SUCCESS -> {
                    productData.postValue(result.data)
                }
                is ResultPurchaseTransaction.ERROR -> {
                    errorMessage.postValue(result.error.message)
                }
            }
        }
    }

    fun updateProductsAfterCheckout(products: List<ProductEntity?>) {
        scope.launch {
            val error = ""
            products.map { productItem ->
                productItem?.let { product ->
                    when (val result =
                        repositoryPurchase.updateProduct(product.id, product.quantity)) {
                        is ResultPurchaseTransaction.ERROR -> {
//                    errorMessage.postValue()
                            loge("${result.error.message}")
                            error.plus("Product ${product.productName} can not be updated\n")
                        }
                        is ResultPurchaseTransaction.SUCCESS -> {
                            logv("${product.id} updated")
                        }
                    }
                }
            }
            errorMessage.postValue(error)
        }
    }

}