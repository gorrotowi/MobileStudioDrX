package com.gorrotowi.drxstore.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.gorrotowi.drxstore.DrawerHomeActivity
import com.gorrotowi.drxstore.R
import com.gorrotowi.drxstore.register.RegisterActivity
import com.gorrotowi.drxstore.sessions.GoogleSessions
import com.gorrotowi.drxstore.userpreferences.UserData
import com.gorrotowi.drxstore.userpreferences.UserLocalData
import com.gorrotowi.drxstore.utils.loge
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private val loginViewModel: LoginViewModel by lazy {
        ViewModelProviders.of(this@LoginActivity).get(LoginViewModel::class.java)
    }

    private val gSessions: GoogleSessions by lazy {
        GoogleSessions()
    }

    private val gClient: GoogleSignInClient? by lazy {
        gSessions.initGClient(this)
    }

    private val userLocalData by lazy {
        UserLocalData(this@LoginActivity)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

//        loge(BuildConfig.FLAVOR)
//        startActivity(Intent(this@LoginActivity, DrawerHomeActivity::class.java))
//        finish()
//        if (BuildConfig.FLAVOR == "develop") {
//        startActivity(Intent(this@LoginActivity, ProductsActivity::class.java))
//        } else {
//        startActivity(Intent(this@LoginActivity, PurchaseListActivity::class.java))
//        }
        setUpObservables()
        setUpListeners()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        loge("ActivityResult requestCode -> $requestCode resultCode -> $resultCode")
        if (requestCode == 3005) {
            val account =
                gSessions.handleSignInRequest(GoogleSignIn.getSignedInAccountFromIntent(data))
            loge("Account -> ${account?.toString()}")
            loge("Account -> ${account?.idToken}")
//            account?.displayName
//            account?.email
//            account?.photoUrl
            account?.idToken?.let {
                loginViewModel.loginWithGoogle(it)
            }
        }
    }

    private fun setUpObservables() {
        loginViewModel.userData.observe(this, Observer { userData ->
            userData?.let {
                val userDataToSave = UserData(userData.name, userData.mail, userData.photoUrl)
                loge("UserSavedData -> $userDataToSave")
                userLocalData.saveUserData(userDataToSave)
                Toast.makeText(this@LoginActivity, userData.name, Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@LoginActivity, DrawerHomeActivity::class.java))
                finish()
            }
        })

        loginViewModel.error.observe(this, Observer { errorMessage ->

            errorMessage?.let {
                Toast.makeText(this@LoginActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun setUpListeners() {

        btnLogin?.setOnClickListener {
            val intent = gClient?.signInIntent
            intent?.let {
                startActivityForResult(intent, 3005)
            }
        }

        btnGoToRegister?.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }
    }
}
