package com.gorrotowi.firebase.transactions

import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.firestore.FirebaseFirestore
import com.gorrotowi.core.ProductEntity
import com.gorrotowi.firebase.pojos.ProductPojo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowViaChannel
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ProductTransactions {

    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    suspend fun addProduct(product: ProductEntity) = withContext(Dispatchers.IO) {

        /*
        val productPojo = product.run {
            return@run ProductPojo(
                productName,
                productDescrp,
                quantity,
                productCode,
                urlImg
            )
        }
        */
        val productPojo: ProductPojo = with(product) {
            ProductPojo(
                productName,
                productDescrp,
                quantity,
                productCode,
                urlImg,
                price,
                pathImg
            )
        }

        try {

            firestore.collection(PRODUCTS_COLUMN).add(productPojo).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //return ok
                } else {
                    task.exception?.printStackTrace()
                }
            }

            firestore.collection(PRODUCTS_COLUMN).add(productPojo).await()
            return@withContext true
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext false
        }
    }

    @ExperimentalCoroutinesApi
    @FlowPreview
    fun getProductFlow(): Flow<ProductPojo> {

        return flowViaChannel<ProductPojo> { channel ->
            firestore.collection(PRODUCTS_COLUMN)
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    if (firebaseFirestoreException != null) {
                        channel.close(firebaseFirestoreException)
                    } else {
                        querySnapshot?.let { snapshot ->
                            snapshot.documents.map { doc ->
                                val message = doc.toObject(ProductPojo::class.java)
                                message?.let { channel.sendBlocking(it) }
                            }
                        }
                    }
                }
        }
    }

    fun getProductsLmb(result: (ResultFBTransaction<List<ProductEntity?>>) -> Unit) {

        firestore.collection(PRODUCTS_COLUMN)
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                firebaseFirestoreException?.let {
                    result(ResultFBTransaction.ERROR(it))
                }
                querySnapshot?.let { snapshot ->
                    val listDocuments = snapshot.documents.map { doc ->
                        val id = doc.id
                        val pojo = doc.toObject(ProductPojo::class.java)
                        pojo?.run {
                            ProductEntity(
                                id,
                                productName,
                                productDescrp,
                                quantity,
                                productCode,
                                urlImg,
                                price,
                                pathImg
                            )
                        }
                    }
                    Log.d("ListDocuments", "${listDocuments.map { it?.toString() }}")
                    result(ResultFBTransaction.SUCCESS(listDocuments))
                }
            }
    }

    suspend fun updateProduct(product: ProductEntity) = withContext(Dispatchers.IO) {
        val mapProduct = HashMap<String, Any>()
        product.run {
            mapProduct["productName"] = productName
            mapProduct["productDescrp"] = productDescrp
            mapProduct["quantity"] = quantity
            mapProduct["productCode"] = productCode
            mapProduct["urlImg"] = urlImg
            mapProduct["price"] = price
            mapProduct["pathImg"] = pathImg
        }
        try {
            firestore.collection(PRODUCTS_COLUMN).document(product.id).update(mapProduct).await()
            return@withContext true
        } catch (ex: FirebaseException) {
            ex.printStackTrace()
            return@withContext false
        }


    }

    suspend fun deleteProduct(id: String) = withContext(Dispatchers.IO) {
        try {
            firestore.collection(PRODUCTS_COLUMN).document(id).delete().await()
            return@withContext true
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext false
        }

    }

    companion object {
        const val PRODUCTS_COLUMN = "productsStore"
    }

}

sealed class ResultFBTransaction<out T> {
    class SUCCESS<out T>(val data: T) : ResultFBTransaction<T>()
    class ERROR(val error: Throwable) : ResultFBTransaction<Nothing>()
}