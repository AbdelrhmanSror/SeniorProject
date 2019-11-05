package com.example.home.ui.mapHome

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.home.*
import com.example.home.databinding.DrawerHeaderBinding
import com.example.home.databinding.FragmentNavMapBinding
import com.example.home.models.MapModel
import com.example.home.viewmodel.MapViewModel
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore

/**
 * A simple [Fragment] subclass.
 */
class NavMapFragment : Fragment(), OnMapReadyCallback {
    private lateinit var binding: FragmentNavMapBinding
    private lateinit var mapViewModel: MapViewModel
    private val fireStore = FirebaseFirestore.getInstance().document("userLocation/user")
    private lateinit var drawerHeaderBinding: DrawerHeaderBinding

    private fun isGpsEnabled(): Boolean = activity!!.application.isGpsEnabled()

    private fun isLocationPermissionGranted(): Boolean =
        activity!!.application.isLocationPermissionGranted()

    companion object {
        const val REQUEST_LOCATION_PERMISSION = 1
        val TAG = MainActivity::class.java.simpleName

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        Log.v("placesApi", "oncreate ")

        binding = FragmentNavMapBinding.inflate(inflater, container, false)
        drawerHeaderBinding = DrawerHeaderBinding.bind(binding.navView.getHeaderView(0))
        setActionBar()
        setActionBarWithNavController()
        setNavViewWithNavController()
        setUpDrawerLayout()
        binding.lifecycleOwner = this
        prepareMap()
        setHasOptionsMenu(true)
        // Set up the views
        return binding.root
    }

    private fun setActionBar() {
        (activity as MainActivity).setSupportActionBar(binding.toolbar)

    }

    private fun setActionBarWithNavController() {
        //setting drawer layout with action bar
        NavigationUI.setupActionBarWithNavController(
            activity as MainActivity,
            findNavController(),
            binding.drawerLayout
        )
    }

    private fun setNavViewWithNavController() {
        binding.navView.roundedCorner(100f)
        NavigationUI.setupWithNavController(binding.navView, findNavController())
    }

    private fun setUpDrawerLayout() {
        binding.drawerLayout.initialize(this, binding.toolbar)
        binding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.signOut -> {
                    AuthUI.getInstance()
                        .signOut(activity!!)
                        .addOnCompleteListener {
                            //navigate to login screen when user sign out
                            findNavController().navigate(R.id.action_navMapFragment_to_authFragment)
                        }
                    true
                }
                else -> false

            }
        }
    }

    private fun prepareMap() {
        // Inflate the layout for this fragment
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync(this)
    }

    private fun requestPermission() = requestPermissions(
        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
        REQUEST_LOCATION_PERMISSION
    )


    //checking if permission is granted and gps is enabled then every thing is setuped
    private fun verifyAllEnabled() {
        if (!isLocationPermissionGranted()) {
            Log.v("placesApi", "persmisson is not ")
            requestPermission()

        } else if (!isGpsEnabled()) {
            Log.v("placesApi", "gps is not ")
            showGpsAlertDialog()

        }


    }


    private fun showGpsAlertDialog() {
        MaterialAlertDialogBuilder(context)
            .setTitle(getString(R.string.gps_enable_title))
            .setMessage(getString(R.string.gps_enable))
            .setPositiveButton(getString(R.string.positive_gps_enable_answer)) { _, _ ->
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }.setNegativeButton(getString(R.string.negative_gps_enable_answer)) { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }

    private fun showPermissionAlertDialog() {
        MaterialAlertDialogBuilder(context)
            .setTitle(getString(R.string.permission_denied))
            .setMessage(getString(R.string.permission_clarify))
            .setPositiveButton(getString(R.string.accept_permission)) { _, _ ->
                requestPermission()
            }.setNegativeButton(getString(R.string.refuse_permission)) { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }

    //show snack bar to tell user to direct user to settings to enable permission
    private fun showPermissionEnableSnackBar(){
        Snackbar.make(
            binding.coordinatorLayout,
            getString(R.string.enable_permission_settings),
            Snackbar.LENGTH_LONG
        ).setAction(getString(R.string.go_to_settings)) {
            val intent = Intent(
                ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + BuildConfig.APPLICATION_ID)
            )
            startActivity(intent)

        }.show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        // Check if location permissions are granted and if so enable the
        // location data layer.
        //if the permission is granted but gps is not enabled then ask user to enable it
        //else if the use did not enable it then go to default location
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                if (!isGpsEnabled()) {
                    showGpsAlertDialog()

                }
                mapViewModel.setGpsMode(isGpsEnabled())
            } else {
                // permission was not granted
                //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
                // shouldShowRequestPermissionRationale will return true
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    showPermissionAlertDialog()

                } //permission is denied (and never ask again is  checked)
                //shouldShowRequestPermissionRationale will return false
                else {
                    showPermissionEnableSnackBar()
                }
            }
        }
    }

    private fun initMapViewModel(map: GoogleMap) {
        mapViewModel =
            ViewModelProviders.of(this).get(MapViewModel::class.java)
        mapViewModel.setMap(this,map)
        binding.viewModel = mapViewModel
        drawerHeaderBinding.user=mapViewModel.currentUser
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     * also we check if the gps is enabled if not we show alter dialog to user to enable gps
     */
    override fun onMapReady(googleMap: GoogleMap) {
        val mapSearchDetail = arguments?.getParcelable<MapModel>(PLACE_DETAILS)
        initMapViewModel(googleMap)
        mapViewModel.navigationFabClicked.observe(viewLifecycleOwner, Observer { it ->
            //if permission is mot granted then request it again
            it?.let {
                //this line of code will only be executed if all condition is valid
                //the class do that check internally so no fear of null pointer
                mapViewModel.goToCurrentLocation().let { isAllPermissionGranted ->
                    //here we check if all permission is granted if not we request it
                    if (!isAllPermissionGranted)
                        verifyAllEnabled()
                }
            }
        })
        mapViewModel.onLocationChangeListener {
            //track the current location of a user then push into realtime time database so it will be shared across all devices
            fireStore.set(it)
        }
        if (mapSearchDetail != null) {
            //when user search for location we do not care if any of permissions are accepted because user just making a search not asking for his location
            mapViewModel.goToSpecificPlace(mapSearchDetail)

        } else {
            //this line of code will only be executed if all condition is valid
            //the class do that check internally so no fear of null pointer
            mapViewModel.goToCurrentLocation().let { isAllPermissionGranted ->
                //here we check if all permission is granted if not we request it
                if (!isAllPermissionGranted)
                    verifyAllEnabled()
            }


        }
    }




    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_types, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        // Change the map type based on the user's selection.
        R.id.normal_map -> {
            mapViewModel.enableNormalMap()
            true
        }
        R.id.hybrid_map -> {
            mapViewModel.enableHybridMap()
            true
        }
        R.id.satellite_map -> {
            mapViewModel.enableSatelliteMap()
            true
        }
        R.id.search->{
            findNavController().navigate(R.id.action_navMapFragment_to_searchFragment)
            true
        }
        else -> NavigationUI.onNavDestinationSelected(
            item,
            view!!.findNavController()
        ) || super.onOptionsItemSelected(item)

    }


}


