package com.gorrotowi.drxstore.sessions

import android.content.Context
import android.util.Log
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task

class GoogleSessions {

    fun initGClient(ctx: Context): GoogleSignInClient? {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("1032254686561-ieole9bfs7lda2rcf4g2u44dciibf37l.apps.googleusercontent.com")
            .requestEmail()
            .build()

        return GoogleSignIn.getClient(ctx, gso)
    }

    fun getCurrentSession(ctx: Context): GoogleSignInAccount? {
        return GoogleSignIn.getLastSignedInAccount(ctx)
    }

    fun closeGoogleSession(ctx: Context) {
        initGClient(ctx)?.signOut()
    }

    fun handleSignInRequest(completedTask: Task<GoogleSignInAccount>): GoogleSignInAccount? {
        return try {
            completedTask.getResult(ApiException::class.java)
        } catch (e: ApiException) {
            when (e.statusCode) {
                GoogleSignInStatusCodes.SIGN_IN_CANCELLED -> {
                    Log.e("SignInError", "CANCELLED", e)
                }
                GoogleSignInStatusCodes.SIGN_IN_CURRENTLY_IN_PROGRESS -> {
                    Log.e("SignInError", "INPROGRESS", e)
                }
                GoogleSignInStatusCodes.SIGN_IN_FAILED -> {
                    Log.e("SignInError", "FAILED", e)
                }
                GoogleSignInStatusCodes.SIGN_IN_REQUIRED -> {
                    Log.e("SignInError", "REQUIRED", e)
                }
            }
            null
        }

    }

}