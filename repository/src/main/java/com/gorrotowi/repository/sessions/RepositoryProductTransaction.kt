package com.gorrotowi.repository.sessions

import com.gorrotowi.core.ProductEntity
import com.gorrotowi.firebase.transactions.ProductTransactions
import com.gorrotowi.firebase.transactions.ResultFBTransaction

class RepositoryProductTransaction {

    private val firebaseTransaction: ProductTransactions by lazy {
        ProductTransactions()
    }

    suspend fun addProduct(product: ProductEntity): ResultProductTransaction<Boolean> {
        //todo transformar product:ProductApp a ProductEntity
        val isSuccessTransaction = firebaseTransaction.addProduct(product)
        return if (isSuccessTransaction) {
            ResultProductTransaction.SUCCESS(true)
        } else {
            ResultProductTransaction.ERROR(Throwable("Producto no pudo ser agregado"))
        }
    }
    suspend fun updateProduct(product: ProductEntity): ResultProductTransaction<Boolean> {
        //todo transformar product:ProductApp a ProductEntity
        val isSuccessTransaction = firebaseTransaction.updateProduct(product)
        return if (isSuccessTransaction) {
            ResultProductTransaction.SUCCESS(true)
        } else {
            ResultProductTransaction.ERROR(Throwable("Producto no pudo ser actualizado"))
        }
    }

    suspend fun deleteProduct(id: String): ResultProductTransaction<Boolean> {
        val isSuccessTransaction = firebaseTransaction.deleteProduct(id)
        return if (isSuccessTransaction) {
            ResultProductTransaction.SUCCESS(true)
        } else {
            ResultProductTransaction.ERROR(Throwable("Producto no pudo ser eliminado"))
        }
    }

    fun getProducts(result: (ResultProductTransaction<List<ProductEntity?>>) -> Unit) {
        firebaseTransaction.getProductsLmb { resultFBTransaction ->
            when (resultFBTransaction) {
                is ResultFBTransaction.SUCCESS -> {
                    val data = resultFBTransaction.data
                    result(ResultProductTransaction.SUCCESS(data))
                }
                is ResultFBTransaction.ERROR -> {
                    val error = resultFBTransaction.error
                    result(ResultProductTransaction.ERROR(error))
                }
            }
        }
    }

}

sealed class ResultProductTransaction<out T> {
    class SUCCESS<out T>(val data: T) : ResultProductTransaction<T>()
    class ERROR(val error: Throwable) : ResultProductTransaction<Nothing>()
}