package com.gorrotowi.drxstore

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.gorrotowi.drxstore.userpreferences.UserLocalData
import kotlinx.android.synthetic.main.activity_user_profile.*

class UserProfileActivity : AppCompatActivity() {

    private val userLocalData by lazy {
        UserLocalData(this@UserProfileActivity)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        toolbarProfile?.let {
            setSupportActionBar(it)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
        }

        initViews()

    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun initViews() {
        val userData = userLocalData.getUserData()
        txtUserName.text = userData?.name
        txtUserMail.text = userData?.mail
        imgUserProfile?.let { Glide.with(this).load(userData?.photoUrl).into(it) }
    }
}
