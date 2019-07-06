package com.gorrotowi.drxstore.products

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.gorrotowi.core.FIlePropierties
import com.gorrotowi.repository.sessions.uploads.RepositoryUploadFiles
import com.gorrotowi.repository.sessions.uploads.ResultRepositoryUpload
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class FormProductViewModel(application: Application) : AndroidViewModel(application) {

    private val parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main

    private val scope = CoroutineScope(coroutineContext)

    val repositoryUpload by lazy {
        RepositoryUploadFiles()
    }

    val uploadSuccess = MutableLiveData<Pair<FIlePropierties?, String?>>()
    val deleteSuccess = MutableLiveData<Pair<Boolean?, String?>>()

    fun uploadBitmapImg(filename: String, img: Bitmap) {
        scope.launch {
            repositoryUpload.uploadBitmapImage(filename, img) { resultRepositoryUpload ->
                when (resultRepositoryUpload) {
                    is ResultRepositoryUpload.SUCCESS -> {
                        uploadSuccess.postValue(Pair(resultRepositoryUpload.data, null))
                    }
                    is ResultRepositoryUpload.ERROR -> {
                        uploadSuccess.postValue(Pair(null, resultRepositoryUpload.error.message))
                    }
                }
            }
        }
    }

    fun deleteImage(path: String) {
        scope.launch {
            when (val result = repositoryUpload.deleteImg(path)) {
                is ResultRepositoryUpload.SUCCESS -> {
                    deleteSuccess.postValue(Pair(result.data, null))
                }
                is ResultRepositoryUpload.ERROR -> {
                    deleteSuccess.postValue(Pair(false, result.error.message))
                }
            }
        }
    }

}