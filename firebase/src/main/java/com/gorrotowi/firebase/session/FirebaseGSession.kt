package com.gorrotowi.firebase.session

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FirebaseGSession {

    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    suspend fun signInWithGoogleAccountToken(token: String) = withContext(Dispatchers.IO) {
        val gCredential = GoogleAuthProvider.getCredential(token, null)
        try {
            val result = firebaseAuth.signInWithCredential(gCredential).await()
            return@withContext result?.user?.let {
                return@let UserLoginData(
                    it.displayName ?: "",
                    it.email ?: "",
                    it.getIdToken(true).await()?.token ?: ""
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        }
    }

    fun closeSession() {
        firebaseAuth.signOut()
    }

}

data class UserLoginData(val displayName: String, val email: String, val idToken: String)