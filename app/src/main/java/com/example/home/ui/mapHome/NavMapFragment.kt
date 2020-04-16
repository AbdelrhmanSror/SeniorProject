package com.example.home.ui.mapHome

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.activity.addCallback
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.home.EventObserver
import com.example.home.MainActivity
import com.example.home.PLACE_DETAILS
import com.example.home.R
import com.example.home.databinding.FragmentNavMapBinding
import com.example.home.extensions.getImageDrawable
import com.example.home.models.MapModel
import com.example.home.models.currentUser
import com.example.home.viewmodel.MapViewModel
import com.example.home.viewmodel.NavMapViewModelFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * A simple [Fragment] subclass.
 */
class NavMapFragment : Fragment(), OnMapReadyCallback {
    private lateinit var binding: FragmentNavMapBinding
    private val mapViewModel: MapViewModel by lazy {
        ViewModelProvider(requireActivity(), NavMapViewModelFactory(requireActivity().application)).get(
            MapViewModel::class.java
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNavMapBinding.inflate(inflater, container, false)
        prepareMap()
        binding.lifecycleOwner = this
        binding.viewModel = mapViewModel
        setHasOptionsMenu(true)
        handleDrawerLayout()
        monitorRequestObserver()
        // Set up the views
        return binding.root
    }


    private fun prepareMap() {
        // Inflate the layout for this fragment
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    private fun monitorRequestObserver() {
        mapViewModel.monitorRequestObserver {monitorRequest->
            monitorRequest.from?.userImageUri.getImageDrawable(requireContext()){
                MaterialAlertDialogBuilder(context).setIcon(it)
                    .setTitle("Monitor Request")
                    .setMessage("${monitorRequest.from?.userName} has requested monitoring you")
                    .setPositiveButton("Accept") { _, _ ->
                    }.setNegativeButton("Decline") { dialog, _ ->
                        dialog.cancel()
                    }.show()
            }
        }
    }


    private fun handleDrawerLayout() {
        (requireActivity() as MainActivity).apply {
            setUserDetailsNavHeader(currentUser)
            onBackPressedDispatcher.addCallback(this@NavMapFragment) {
                if (findViewById<DrawerLayout>(R.id.drawerLayout).isDrawerOpen(GravityCompat.START)) {
                    findViewById<DrawerLayout>(R.id.drawerLayout).closeDrawer(GravityCompat.START)
                } else {
                    finish()
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
            }.show()
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
        mapViewModel.setMap(this, googleMap)

        mapViewModel.gpsEnabled.observe(viewLifecycleOwner, EventObserver {
            if (it) {
                mapViewModel.goToCurrentLocation()
            } else {
                showGpsAlertDialog()
            }
        })
        mapViewModel.searchPlace.observe(viewLifecycleOwner, Observer {
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


