package com.example.ktshw1

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
//        val navHostFragment = supportFragmentManager.findFragmentById(
//            R.id.fragment_nav
//        ) as NavHostFragment
//        val navController = navHostFragment.navController
//
//        // Setup the bottom navigation view with navController
//        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav)
//        bottomNavigationView.setupWithNavController(navController)
//
//        // Setup the ActionBar with navController and 3 top level destinations
//        appBarConfiguration = AppBarConfiguration(
//            setOf(R.id.titleScreen, R.id.leaderboard,  R.id.register)
//        )
//        setupActionBarWithNavController(navController, appBarConfiguration)
    }
}