package com.gorrotowi.drxstore.userpreferences

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class UserLocalData(ctx: Context) {

    lateinit var sharedPreferences: SharedPreferences

    init {
        sharedPreferences = ctx.getSharedPreferences("DrxPreferences", Context.MODE_PRIVATE)
    }

    fun saveUserData(userData: UserData) {
        sharedPreferences.edit {
            putString(SHAREDP_NAME, userData.name)
            putString(SHAREDP_MAIL, userData.mail)
            putString(SHAREDP_PHOTOURL, userData.photoUrl)
        }
    }

    fun getUserData(): UserData? {
        val userName = sharedPreferences.getString(SHAREDP_NAME, null)
        val userMail = sharedPreferences.getString(SHAREDP_MAIL, null)
        val userPhotoUrl = sharedPreferences.getString(SHAREDP_PHOTOURL, null)
        return if (userName != null && userMail != null && userPhotoUrl != null) {
            UserData(userName, userMail, userPhotoUrl)
        } else {
            null
        }
    }

    companion object {
        const val SHAREDP_NAME = "NAME"
        const val SHAREDP_MAIL = "MAIL"
        const val SHAREDP_PHOTOURL = "PHOTOURL"
    }

}

data class UserData(val name: String, val mail: String, val photoUrl: String)