package com.gorrotowi.repository.sessions

import com.gorrotowi.core.ProductEntity
import com.gorrotowi.firebase.pojos.ProductPojo
import com.gorrotowi.firebase.purchases.PurchasesTransactions
import com.gorrotowi.firebase.purchases.ResultFBPurchasesTransaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RepositoryPurchaseTransaction {

    private val firebasePurchase by lazy {
        PurchasesTransactions()
    }

    suspend fun searchProduct(id: String) = withContext(Dispatchers.Default) {
        return@withContext when (val result = firebasePurchase.searchProduct(id)) {
            is ResultFBPurchasesTransaction.SUCCESS -> {
                val product = convertPojoToEntity(result.data)
                ResultPurchaseTransaction.SUCCESS(product)
            }
            is ResultFBPurchasesTransaction.ERROR -> {
                ResultPurchaseTransaction.ERROR(result.error)
            }
        }
    }

    suspend fun updateProduct(id: String, quantity: Int) = withContext(Dispatchers.Default) {
        return@withContext when (val result = firebasePurchase.updateQuantityProduct(id, quantity)) {
            is ResultFBPurchasesTransaction.SUCCESS -> {
                ResultPurchaseTransaction.SUCCESS(result.data)
            }
            is ResultFBPurchasesTransaction.ERROR -> {
                ResultPurchaseTransaction.ERROR(result.error)
            }
        }
    }

    private fun convertPojoToEntity(pojo: Pair<String, ProductPojo?>): ProductEntity? {
        return pojo.second?.run {
            ProductEntity(pojo.first, productName, productDescrp, quantity, productCode, urlImg, price, pathImg)
        }
    }

}

sealed class ResultPurchaseTransaction<out T> {
    class SUCCESS<out T>(val data: T) : ResultPurchaseTransaction<T>()
    class ERROR(val error: Throwable) : ResultPurchaseTransaction<Nothing>()
}