package com.gorrotowi.drxstore

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_drawer_home.*

class DrawerHomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val navController by lazy {
        Navigation.findNavController(this, R.id.hostFragment)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawer_home)

        toolbarHome?.let { tbl ->
            setSupportActionBar(tbl)
            supportActionBar?.setDisplayShowHomeEnabled(true)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

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
