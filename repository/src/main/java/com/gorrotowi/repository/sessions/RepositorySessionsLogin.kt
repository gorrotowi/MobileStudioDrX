package com.gorrotowi.repository.sessions

import android.content.Context
import com.gorrotowi.firebase.session.FirebaseGSession

class RepositorySessionsLogin {

    private val firebaseGSession: FirebaseGSession by lazy {
        FirebaseGSession()
    }

    suspend fun loginWithGoogle(idToken: String): ResultSessionLogin<UserData> {
        val user = firebaseGSession.signInWithGoogleAccountToken(idToken)
        return if (user != null) {

            val userData = UserData(user.displayName, user.email)
            ResultSessionLogin.SUCCESS(userData)
        } else {
            ResultSessionLogin.ERROR(Throwable("Error to sign in with Google"))
        }
    }

}

data class UserData(val name: String, val mail: String)

sealed class ResultSessionLogin<out T : Any> {
    class SUCCESS<out T : Any>(val data: T) : ResultSessionLogin<T>()
    class ERROR(val error: Throwable) : ResultSessionLogin<Nothing>()
}