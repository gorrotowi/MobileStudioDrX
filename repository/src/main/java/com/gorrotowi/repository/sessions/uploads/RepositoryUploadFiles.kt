package com.gorrotowi.repository.sessions.uploads

import android.graphics.Bitmap
import com.gorrotowi.core.FIlePropierties
import com.gorrotowi.firebase.storage.ResultFBUpload
import com.gorrotowi.firebase.storage.UploadFileTransaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RepositoryUploadFiles {


    val fbUpload by lazy {
        UploadFileTransaction()
    }

    suspend fun uploadBitmapImage(
        filename: String,
        img: Bitmap,
        block: (ResultRepositoryUpload<FIlePropierties>) -> Unit
    ) = withContext(Dispatchers.Default) {
        fbUpload.uploadBitmap(filename, img) { result ->
            when (result) {
                is ResultFBUpload.SUCCESS -> {
                    block(ResultRepositoryUpload.SUCCESS(result.data))
                }
                is ResultFBUpload.ERROR -> {
                    block(ResultRepositoryUpload.ERROR(result.error))
                }
            }
        }
    }

    suspend fun deleteImg(path: String) = withContext(Dispatchers.Default) {
        return@withContext when (val result = fbUpload.deleteImage(path)) {
            is ResultFBUpload.SUCCESS -> {
                ResultRepositoryUpload.SUCCESS(result.data)
            }
            is ResultFBUpload.ERROR -> {
                ResultRepositoryUpload.ERROR(result.error)
            }
        }
    }

}

sealed class ResultRepositoryUpload<out T> {
    class SUCCESS<out T>(val data: T) : ResultRepositoryUpload<T>()
    class ERROR(val error: Throwable) : ResultRepositoryUpload<Nothing>()
}