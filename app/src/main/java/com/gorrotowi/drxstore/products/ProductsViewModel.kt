package com.gorrotowi.drxstore.products

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.gorrotowi.core.ProductEntity
import com.gorrotowi.repository.sessions.RepositoryProductTransaction
import com.gorrotowi.repository.sessions.ResultProductTransaction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class ProductsViewModel(application: Application) : AndroidViewModel(application) {

    private val parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main

    private val scope = CoroutineScope(coroutineContext)

    private val repository: RepositoryProductTransaction by lazy {
        RepositoryProductTransaction()
    }

    val productsList = MutableLiveData<List<ProductEntity?>>()
    val isSuccessTransation = MutableLiveData<Boolean>()
    val isSuccessDeleteTransation = MutableLiveData<Boolean>()
    val isSuccessUpdateTransation = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String>()

    fun addProduct(product: ProductEntity) {
        scope.launch {
            when (val result = repository.addProduct(product)) {
                is ResultProductTransaction.SUCCESS -> {
                    isSuccessTransation.postValue(result.data)
                }
                is ResultProductTransaction.ERROR -> {
                    errorMessage.postValue(result.error.message)
                }
            }
        }
    }

    fun updateProduct(product: ProductEntity) {
        scope.launch {
            when (val result = repository.updateProduct(product)) {
                is ResultProductTransaction.SUCCESS -> {
                    isSuccessUpdateTransation.postValue(result.data)
                }
                is ResultProductTransaction.ERROR -> {
                    errorMessage.postValue(result.error.message)
                }
            }
        }
    }

    fun deleteProduct(product: ProductEntity) {
        scope.launch {
            when (val result = repository.deleteProduct(product.id)) {
                is ResultProductTransaction.SUCCESS -> {
                    isSuccessDeleteTransation.postValue(result.data)
                }
                is ResultProductTransaction.ERROR -> {
                    errorMessage.postValue(result.error.message)
                }
            }
        }
    }

    fun getProducts() {
        repository.getProducts { result ->
            when (result) {

                is ResultProductTransaction.SUCCESS -> {
                    productsList.postValue(result.data)
                }
                is ResultProductTransaction.ERROR -> {
                    errorMessage.postValue(result.error.message)
                }
            }

        }
//            when (val result = repository.getProducts()) {
//                is ResultProductTransaction.SUCCESS -> {
//                    productsList.postValue(result.data)
//                }
//                is ResultProductTransaction.ERROR -> {
//                    errorMessage.postValue(result.error.message)
//                }
//            }
    }

}