package com.gorrotowi.drxstore

import android.app.Application
import com.google.firebase.FirebaseApp
import com.hypertrack.hyperlog.HyperLog

class DrXStoreApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this@DrXStoreApplication)
        HyperLog.initialize(this)
    }
}