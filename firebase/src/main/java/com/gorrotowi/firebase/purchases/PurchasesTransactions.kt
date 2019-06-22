package com.gorrotowi.firebase.purchases

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.gorrotowi.firebase.pojos.ProductPojo
import com.gorrotowi.firebase.transactions.ProductTransactions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class PurchasesTransactions {

    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    suspend fun searchProduct(id: String) = withContext(Dispatchers.IO) {
        return@withContext try {
            val document = firestore
                .collection(ProductTransactions.PRODUCTS_COLUMN)
                .document(id)
                .get()
                .await()
            Log.e("Document ->>>", "${document.data}")
            if (document.data != null) {
                ResultFBPurchasesTransaction.SUCCESS(
                    Pair<String, ProductPojo?>(
                        document.id,
                        document.toObject(ProductPojo::class.java)
                    )
                )
            } else {
//                ResultFBPurchasesTransaction.ERROR(Throwable("Item not found"))
                throw Exception("Item not found")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            ResultFBPurchasesTransaction.ERROR(e)
        }

    }

    companion object {
        const val PURCHASES_COLUMN = "purchases"
    }

}

sealed class ResultFBPurchasesTransaction<out T> {
    class SUCCESS<out T>(val data: T) : ResultFBPurchasesTransaction<T>()
    class ERROR(val error: Throwable) : ResultFBPurchasesTransaction<Nothing>()
}