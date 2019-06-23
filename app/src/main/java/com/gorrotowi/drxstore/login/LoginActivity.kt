package com.gorrotowi.drxstore.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.gorrotowi.drxstore.BuildConfig
import com.gorrotowi.drxstore.R
import com.gorrotowi.drxstore.products.ProductsActivity
import com.gorrotowi.drxstore.register.RegisterActivity
import com.gorrotowi.drxstore.sessions.GoogleSessions
import com.gorrotowi.drxstore.utils.loge
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    val loginViewModel: LoginViewModel by lazy {
        ViewModelProviders.of(this@LoginActivity).get(LoginViewModel::class.java)
    }

    val gSessions: GoogleSessions by lazy {
        GoogleSessions()
    }

    val gClient: GoogleSignInClient? by lazy {
        gSessions.initGClient(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loge(BuildConfig.FLAVOR)
//        if (BuildConfig.FLAVOR == "develop") {
        startActivity(Intent(this@LoginActivity, ProductsActivity::class.java))
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
            val account = gSessions.handleSignInRequest(GoogleSignIn.getSignedInAccountFromIntent(data))
            loge("Account -> ${account?.toString()}")
            loge("Account -> ${account?.idToken}")
            account?.idToken?.let {
                loginViewModel.loginWithGoogle(it)
            }
        }
    }

    private fun setUpObservables() {
        loginViewModel.userData.observe(this, Observer { userData ->
            userData?.let {
                Toast.makeText(this@LoginActivity, userData.name, Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@LoginActivity, ProductsActivity::class.java))
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
