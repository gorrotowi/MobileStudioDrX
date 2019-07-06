package com.gorrotowi.drxstore

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.gorrotowi.drxstore.userpreferences.UserLocalData
import kotlinx.android.synthetic.main.activity_drawer_home.*
import kotlinx.android.synthetic.main.header_menu.view.*

class DrawerHomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val navController by lazy {
        Navigation.findNavController(this, R.id.hostFragment)
    }

    private val userLocalData by lazy {
        UserLocalData(this@DrawerHomeActivity)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawer_home)

        toolbarHome?.let { tbl ->
            setSupportActionBar(tbl)
            supportActionBar?.setDisplayShowHomeEnabled(true)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        setUpListeners()

//        NavigationUI.setupActionBarWithNavController(this, navController)
//        NavigationUI.setupWithNavController(navigationDrawer, navController)

        val toggle = ActionBarDrawerToggle(
            this,
            homeDrawer,
            toolbarHome,
            R.string.open,
            R.string.close
        )
        homeDrawer?.addDrawerListener(toggle)
        toggle.syncState()
        navigationDrawer?.setNavigationItemSelectedListener(this)

        initUserHeaderData()

    }

    private fun setUpListeners() {
        val header = navigationDrawer.getHeaderView(0)
        header.setOnClickListener {
            homeDrawer?.closeDrawers()
            startActivity(Intent(this@DrawerHomeActivity, UserProfileActivity::class.java))
        }
    }

    private fun initUserHeaderData() {
        val userData = userLocalData.getUserData()
        val header = navigationDrawer?.getHeaderView(0)
        header?.txtHeaderUsername?.text = userData?.name ?: ""
        header?.txtHeaderUserMail?.text = userData?.mail ?: ""
        header?.imgHeaderUser?.let { Glide.with(this).load(userData?.photoUrl).into(it) }
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        menuItem.isChecked = true
        homeDrawer?.closeDrawers()
        when (menuItem.itemId) {
            R.id.action_products -> {
                navController.navigate(R.id.productsActivity)
            }
            R.id.action_products_purchase -> {
                navController.navigate(R.id.purchaseListActivity)
            }
        }
        return false
    }

    override fun onBackPressed() {
        if (homeDrawer?.isDrawerOpen(GravityCompat.START) == true) {
            homeDrawer?.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
