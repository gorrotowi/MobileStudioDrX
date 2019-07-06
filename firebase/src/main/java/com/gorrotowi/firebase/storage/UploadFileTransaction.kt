package com.gorrotowi.firebase.storage

import android.graphics.Bitmap
import android.net.Uri
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.gorrotowi.core.FIlePropierties
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class UploadFileTransaction {

    val firebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }

    suspend fun uploadBitmap(filename: String, img: Bitmap, block: (ResultFBUpload<FIlePropierties>) -> Any) =
        withContext(Dispatchers.IO) {
            val imgToUpload = firebaseStorage.reference.child("drxstore/$filename.jpg")

            val baos = ByteArrayOutputStream()
            img.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val dataToUpload = baos.toByteArray()

//        Tasks.await(imgToUpload.putBytes(dataToUpload))
            try {
//            val resultUpload = imgToUpload.putBytes(dataToUpload).await()
//            resultUpload.metadata?.path
//                imgToUpload.putBytes(dataToUpload).addOnProgressListener { taskSnapshot ->
//                    val bytesTransfered = taskSnapshot.bytesTransferred
//                    val totaBytes = taskSnapshot.totalByteCount
//                    val progress = (100 * bytesTransfered) / totaBytes
//                    Log.v("UploadPress", "Progress Upload $filename %$progress")
//                }.addOnCompleteListener { task ->
//                    if (task.isSuccessful) {
//                        val urlPath = runBlocking(Dispatchers.IO) { Tasks.await(imgToUpload.downloadUrl).path }
////                        val urlPath = task.result?.metadata?.path ?: ""
//                        Log.v("SUCCESSFULL task", task.result?.metadata?.path)
//                        Log.v("SUCCESSFULL task", task.result?.metadata?.name)
//                        Log.v("SUCCESSFULL taskDown", urlPath)
//                        block(ResultFBUpload.SUCCESS(urlPath))
//                    } else {
//                        block(ResultFBUpload.ERROR(Throwable("File can not be uploaded")))
//                    }
//                }

                imgToUpload.putBytes(dataToUpload)
                    .continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                        if (!task.isSuccessful) {
                            task.exception?.let { throw it }
                        }
                        return@Continuation imgToUpload.downloadUrl
                    }).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val downloadUrl = task.result.toString()
//                            imgToUpload.metadata.result?.name
                            val pathFile = task.result?.lastPathSegment
                            block(ResultFBUpload.SUCCESS(FIlePropierties(pathFile, downloadUrl)))
                        } else {
                            task.exception?.let { throw it }
                        }
                    }
            } catch (e: Exception) {
                e.printStackTrace()
                block(ResultFBUpload.ERROR(e))
            }
        }

    suspend fun deleteImage(path: String) = withContext(Dispatchers.IO) {
        val imgToDelete = firebaseStorage.reference.child(path)
        try {
            imgToDelete.delete().await()
            ResultFBUpload.SUCCESS(true)
        } catch (e: Exception) {
            e.printStackTrace()
            ResultFBUpload.ERROR(e)
        }
    }

}

sealed class ResultFBUpload<out T> {
    class SUCCESS<out T>(val data: T) : ResultFBUpload<T>()
    class ERROR(val error: Throwable) : ResultFBUpload<Nothing>()
}