package com.gorrotowi.drxstore.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.gorrotowi.repository.sessions.RepositorySessionsLogin
import com.gorrotowi.repository.sessions.ResultSessionLogin
import com.gorrotowi.repository.sessions.UserData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main

    private val scope = CoroutineScope(coroutineContext)
    private var respositoryLogin: RepositorySessionsLogin? = null

    val userData = MutableLiveData<UserData>()
    val error = MutableLiveData<String>()

    init {
        respositoryLogin = RepositorySessionsLogin()
    }

    fun loginWithGoogle(idToken: String) {
        scope.launch {
            when (val result = respositoryLogin?.loginWithGoogle(idToken)) {
                is ResultSessionLogin.SUCCESS -> {
                    userData.value = result.data
                }
                is ResultSessionLogin.ERROR -> {
                    error.value = result.error.message
                }
            }
        }
    }


}