package com.example.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavDestination


class MainActivity : AppCompatActivity(){
    private lateinit var navController: NavController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       setContentView(R.layout.activity_main)


    }

    //preventing navigation drawer from being swiped anywhere other than the start destination
    private fun setUpNavSwipe()
    {
        navController.addOnDestinationChangedListener { nc: NavController, nd: NavDestination, args: Bundle? ->
            if (nd.id == nc.graph.startDestination) {
               supportActionBar?.show()
            } else {
               supportActionBar?.hide()
            }
        }
    }
}