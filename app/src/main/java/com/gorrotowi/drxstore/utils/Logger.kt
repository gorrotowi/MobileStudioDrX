package com.gorrotowi.drxstore.utils

import android.util.Log
import com.gorrotowi.drxstore.BuildConfig
import com.hypertrack.hyperlog.HyperLog

fun Any.loge(message: String, exception: Exception? = null) {
    if (BuildConfig.DEBUG) {
        exception?.let {
            Log.e(this::class.java.simpleName, message, it)
        } ?: also {
            Log.e(this::class.java.simpleName, message)
        }
    } else {
        exception?.let {
            HyperLog.e(this::class.java.simpleName, message, it)
        } ?: also {
            HyperLog.e(this::class.java.simpleName, message)
        }
    }
}

fun Any.logv(message: String, exception: Exception? = null) {
    if (BuildConfig.DEBUG) {
        exception?.let {
            Log.v(this::class.java.simpleName, message, it)
        } ?: also {
            Log.v(this::class.java.simpleName, message)
        }
    } else {
        exception?.let {
            HyperLog.v(this::class.java.simpleName, message, it)
        } ?: also {
            HyperLog.v(this::class.java.simpleName, message)
        }
    }
}
