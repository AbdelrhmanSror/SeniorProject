package com.example.home.ui.mapHome

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import androidx.activity.addCallback
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.home.*
import com.example.home.databinding.FragmentNavMapBinding
import com.example.home.models.MapModel
import com.example.home.viewmodel.MapViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

/**
 * A simple [Fragment] subclass.
 */
class NavMapFragment : Fragment(), OnMapReadyCallback {
    private lateinit var binding: FragmentNavMapBinding
    private val mapViewModel by viewModels<MapViewModel>()

    companion object {
        const val REQUEST_LOCATION_PERMISSION = 1
        val TAG = MainActivity::class.java.simpleName

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNavMapBinding.inflate(inflater, container, false)
        prepareMap()
        binding.lifecycleOwner = this
        binding.viewModel = mapViewModel
        setUpSnackBar()
        setHasOptionsMenu(true)
        // Set up the views
        return binding.root
    }


    private fun prepareMap() {
        // Inflate the layout for this fragment
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun requestPermission() = requestPermissions(
        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
        REQUEST_LOCATION_PERMISSION
    )


    //show snack bar to tell user to direct user to settings to enable permission
    private fun setUpSnackBar() {
        view?.setupSnackbar(this, mapViewModel.snackbarText, Snackbar.LENGTH_LONG)

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleDrawerLayout()
    }

    private fun handleDrawerLayout() {
        (requireActivity() as MainActivity).apply {
            setUserDetailsNavHeader(mapViewModel.currentUser)
            onBackPressedDispatcher.addCallback(this@NavMapFragment) {
                if (findViewById<DrawerLayout>(R.id.drawerLayout).isDrawerOpen(GravityCompat.START)) {
                    findViewById<DrawerLayout>(R.id.drawerLayout).closeDrawer(GravityCompat.START)
                } else {
                    finish()
                }
            }
        }
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
                mapViewModel.setPermissionGranted()
                mapViewModel.verifyAllEnabled()
            } else {
                // permission was not granted
                //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
                // shouldShowRequestPermissionRationale will return true
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    showPermissionAlertDialog()

                } //permission is denied (and never ask again is  checked)
                //shouldShowRequestPermissionRationale will return false
                else {
                    mapViewModel.showSnackbarMessage(R.string.enable_permission_settings)
                }
            }
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

    override fun onResume() {
        //start searching for location if there  was a location coordinates coming  from search fragment
        arguments?.getParcelable<MapModel>(PLACE_DETAILS)?.let {
            mapViewModel.startSearching(it)
        }

        super.onResume()
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
        Log.v("placesTriigeed","mapready")
        mapViewModel.setMap(this, googleMap)

        mapViewModel.gpsEnabled.observe(viewLifecycleOwner, EventObserver {
            if (it) {
                mapViewModel.goToCurrentLocation()
            } else {
                showGpsAlertDialog()
            }
        })
        mapViewModel.requestPermission.observe(viewLifecycleOwner, EventObserver {
            if (it)
                requestPermission()
        })
        mapViewModel.searchPlace.observe(viewLifecycleOwner, Observer {
            Log.v("placesTriigeed","done")
            mapViewModel.goToSpecificPlace(it)
        })


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
        R.id.search -> {
            findNavController().navigate(R.id.action_navMapFragment_to_searchFragment)
            true
        }
        else -> NavigationUI.onNavDestinationSelected(
            item,
            view!!.findNavController()
        ) || super.onOptionsItemSelected(item)

    }


}


