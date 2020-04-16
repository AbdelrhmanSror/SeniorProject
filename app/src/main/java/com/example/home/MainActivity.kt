package com.example.home

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.ModalDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.example.home.custom.LoadingDialog
import com.example.home.custom.RequestInfoDialog
import com.example.home.databinding.ActivityMainBinding
import com.example.home.databinding.DrawerHeaderBinding
import com.example.home.extensions.hide
import com.example.home.extensions.roundedCorner
import com.example.home.extensions.setImageUri
import com.example.home.extensions.show
import com.example.home.models.UserModel
import com.example.home.models.toUri
import com.example.home.viewmodel.MapViewModel
import com.example.home.viewmodel.NavMapViewModelFactory
import com.firebase.ui.auth.AuthUI


interface NavigationViewHandler {
    fun setUserDetailsNavHeader(userModel: UserModel)
}

class MainActivity : AppCompatActivity(), NavigationViewHandler {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerHeaderBinding: DrawerHeaderBinding
    private lateinit var audioPermission: AudioPermission
    private val mapViewModel: MapViewModel by lazy {
        ViewModelProvider(this, NavMapViewModelFactory(application)).get(
            MapViewModel::class.java
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        navController = findNavController(R.id.nav_host_fragment)
        audioPermission = AudioPermission(this) {
            navigateToStartDestination()
        }
        appBarConfiguration =
            AppBarConfiguration.Builder(R.id.navMapFragment).setOpenableLayout(binding.drawerLayout)
                .build()
        drawerHeaderBinding = DrawerHeaderBinding.bind(binding.navView.getHeaderView(0))
        binding.root.tag = binding
        setSupportActionBar(findViewById(R.id.toolbar))
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)
        setupNavView()
        setUpNavSwipe()


    }

    private fun signOut() {
        AuthUI.getInstance()
            .signOut(this@MainActivity)
            .addOnCompleteListener {
                //navigate to login screen when user sign out
                navController.navigate(R.id.authFragment)
            }
    }

    private fun setupNavView() {
        binding.navView.apply {
            roundedCorner(100f)
            setNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.signOut -> {
                        signOut()
                        true
                    }
                    else -> {
                        MaterialDialog(context, ModalDialog).show {
                            title(R.string.monitorRequest)
                            customView(R.layout.request_typo)
                            positiveButton(R.string.sendRequest) { trackDialog ->
                                //once user writes the email i go search for it in the database
                                mapViewModel.searchForUsingEmail(trackDialog.getCustomView().findViewById<EditText>(R.id.monitorEmail).text.toString()) { mapModel ->
                                    Log.v("userModelMonitoREQuest", "$mapModel")
                                    //show loading dialog for 2sec
                                    LoadingDialog.showLoadingDialog(this@MainActivity, 2000) {
                                        //show info of the person has received monitoring request
                                        RequestInfoDialog.showCustomViewDialog(this@MainActivity,{infoDialog->
                                            val view = infoDialog.getCustomView()
                                            val userImage =
                                                view.findViewById<ImageView>(R.id.receiverImage)
                                            val userName =
                                                view.findViewById<TextView>(R.id.receiverName)
                                            val userEmail =
                                                view.findViewById<TextView>(R.id.receiverEmail)
                                            userImage.setImageUri(mapModel?.userModel?.userImageUri?.toUri())
                                            userName.text = mapModel?.userModel?.userName
                                            userEmail.text = mapModel?.userModel?.email

                                        }){
                                            //on positive button clicked
                                        }
                                    }

                                }


                            }
                            negativeButton(R.string.dismissRequest)

                        }
                        false

                    }

                }
            }
        }
    }

    private fun navigateToStartDestination() {
        val inflater = navController.navInflater
        val graph = inflater.inflate(R.navigation.map_navigation)
        //show the bottom nav view after permission is granted
        graph.startDestination = R.id.authFragment
        navController.setGraph(graph, null)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        audioPermission.onRequestPermissionsResult(requestCode, permissions, grantResults)
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
                binding.drawerLayout.show()

            } else {
                supportActionBar?.hide()
                binding.drawerLayout.hide()
            }
        }
    }

    //set the data for the header of drawer layout
    override fun setUserDetailsNavHeader(userModel: UserModel) {
        drawerHeaderBinding.user = userModel
    }

}