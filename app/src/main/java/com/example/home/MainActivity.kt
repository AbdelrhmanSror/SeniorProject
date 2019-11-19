package com.example.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.home.databinding.ActivityMainBinding
import com.example.home.databinding.DrawerHeaderBinding
import com.example.home.models.UserModel
import com.firebase.ui.auth.AuthUI


interface NavigationViewHandler{
    fun setUserDetailsNavHeader(userModel: UserModel)
}
class MainActivity : AppCompatActivity() ,NavigationViewHandler{
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerHeaderBinding: DrawerHeaderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        drawerHeaderBinding=DrawerHeaderBinding.bind(binding.navView.getHeaderView(0))
        binding.root.tag = binding
        setSupportActionBar(findViewById(R.id.toolbar))
        navController = findNavController(R.id.nav_host_fragment)
        appBarConfiguration = AppBarConfiguration.Builder(R.id.navMapFragment).setDrawerLayout(binding.drawerLayout).build()
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)
        setupNavView()
        setUpNavSwipe()

    }

    private fun setupNavView() {
        binding.navView.apply {
            roundedCorner(100f)
            setNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.signOut -> {
                        AuthUI.getInstance()
                            .signOut(this@MainActivity)
                            .addOnCompleteListener {
                                //navigate to login screen when user sign out
                                navController.navigate(R.id.authFragment)
                            }
                        true
                    }
                    else ->
                        false

                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment).navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }


    //preventing navigation drawer from being swiped anywhere other than the start destination
    private fun setUpNavSwipe() {
        navController.addOnDestinationChangedListener { _, nd: NavDestination, _ ->
            if (nd.id == R.id.navMapFragment) {
                supportActionBar?.show()
                binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)

            } else {
                supportActionBar?.hide()
                binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
        }
    }

    //set the data for the header of drawer layout
    override fun setUserDetailsNavHeader(userModel: UserModel) {
        drawerHeaderBinding.user = userModel
    }

}