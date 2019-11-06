package com.example.home.customMap

import android.app.Application
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Geocoder
import android.location.Location
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.example.home.R
import com.example.home.isGpsEnabled
import com.example.home.isLocationPermissionGranted
import com.example.home.models.MapModel
import com.example.home.models.UserModel
import com.example.home.ui.mapHome.NavMapFragment
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.maps.DirectionsApiRequest
import com.google.maps.GeoApiContext
import com.google.maps.PendingResult
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.model.DirectionsResult
import kotlinx.coroutines.*
import java.util.*

class ApplicationMap private constructor(
    private val lifecycleOwner: LifecycleOwner,
    private val application: Application,
    private val map: GoogleMap,
    private val user: UserModel
) : LifecycleObserver {


    private val fusedLocationProviderClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(application)
    }
    //last known location of the user
    private lateinit var lastKnownLocation: Location

    private val geoApiContext: GeoApiContext by lazy {
        GeoApiContext.Builder()
            .apiKey(application.getString(R.string.google_api_key)).build()
    }
    private var marker: Marker? = null

    private lateinit var currentUserClusterMarker: UserClusterMarker

    private lateinit var customUserManagerRenderer: CustomUserManagerRenderer

    private val job = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + job)

    private val markerAnimation: MarkerAnimation by lazy {
        MarkerAnimation.create(map, application,lifecycleOwner)
    }

    companion object {
        /**
         * how zoomed in i want to be on the mMap
        1: World
        5: Landmass/continent
        10: City
        15: Streets
        20: Buildings
         */
        private const val DEFAULT_ZOOM = 15f

        fun create(
            lifecycleOwner: LifecycleOwner,
            application: Application,
            map: GoogleMap,
            user: UserModel
        ): ApplicationMap {
            return ApplicationMap(lifecycleOwner, application, map, user)
        }


    }


    init {
        //register this class as life cycle observer
        lifecycleOwner.lifecycle.addObserver(this)
    }

    //enable normal style of map
    fun enableNormalMap() {
        map.mapType = GoogleMap.MAP_TYPE_NORMAL
    }

    //enable SATELLITE style of map
    fun enableSatelliteMap() {
        map.mapType = GoogleMap.MAP_TYPE_SATELLITE

    }

    //enable Hybrid style of map
    fun enableHybridMap() {
        map.mapType = GoogleMap.MAP_TYPE_HYBRID

    }


    /**
     * will return true if all permission is granted and gps is enabled
     * also will set the current device location on the map
     */
    fun loadCurrentDeviceLocation(): Boolean {
        if (!application.isGpsEnabled() || !application.isLocationPermissionGranted()) {
            return false
        }
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        // map.isMyLocationEnabled = true
        //disable the default icon of navigation
        map.uiSettings.isMyLocationButtonEnabled = false
        map.uiSettings.isMapToolbarEnabled = false
        // Set the mMap's camera position to the current location of the device.
        requestCurrentDeviceLocation { location ->
            Log.v("locationReadt", "done")
            map.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(location.latitude, location.longitude)
                    ,
                    DEFAULT_ZOOM
                )
            )

        }
        return true
    }

    /**
     * request the current user location
     * this function accept higher order function to execute when thelocationn is ready
     */
    private fun requestCurrentDeviceLocation(onLocationResult: ((Location) -> Unit)) {
        val locationResult = fusedLocationProviderClient.lastLocation
        locationResult.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Set the mMap's camera position to the current location of the device.
                task.result?.let {
                    //if there was a valid function then execute it
                    onLocationResult(it)
                    lastKnownLocation = it

                }
            }
        }
    }
    /**
     *listener for location changes
     * here we can update the marker position on map
     */
    fun requestLocationUpdates(update: (mapModel: MapModel) -> Unit) {
        val request = LocationRequest()
        request.interval = 10000
        request.fastestInterval = 5000
        request.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        fusedLocationProviderClient.requestLocationUpdates(
            request,
            object : LocationCallback() {
                override fun onLocationResult(result: LocationResult?) {
                    result?.let {
                        val mapModel = MapModel(
                            it.lastLocation.latitude,
                            it.lastLocation.longitude
                        )
                        //update the current user cluster position
                        currentUserClusterMarker.mapModel = mapModel
                        customUserManagerRenderer.updateMarkerPosition(currentUserClusterMarker)
                        update(mapModel)

                    }

                }

            },
            null
        )
    }

    /**
     * function to get location details using lat and lng of the location
     */
    private fun getLocationDetails(lat: Double, long: Double): String? {
        val geo = Geocoder(application, Locale.getDefault())
        //here we specify the lat and lng and max result to get
        val addresses = geo.getFromLocation(lat, long,1)
        if (!addresses.isNullOrEmpty()) {
            return addresses[0].getAddressLine(0)
            //yourtextboxname.setText(addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality() +", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName());
        }
        return null

    }

    /**
     * will return true if all permission is granted and gps is enabled
     */
    fun goToSpecificPlace(mapModel: MapModel): Boolean {
        marker?.remove()
        val latLng = LatLng(mapModel.latitude, mapModel.longitude)
        marker = map.addMarker(
            MarkerOptions().position(latLng)
                .title(
                    getLocationDetails(mapModel.latitude, mapModel.longitude)
                        ?: application.getString(R.string.dropped_pin)
                ).icon(
                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)
                )
        )
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        map.animateCamera(CameraUpdateFactory.zoomTo(13f))
        return !(!application.isGpsEnabled() || !application.isLocationPermissionGranted())
    }

    /**
     * This click listener places a marker on the mMap immediately when the user clicks on a POI.
     * The click listener also displays an info window that contains the POI name
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private fun setOnPoiClick() {
        map.setOnPoiClickListener {
            marker?.remove()
            marker = map.addMarker(
                MarkerOptions().position(it.latLng).title(
                    getLocationDetails(
                        it.latLng.latitude,
                        it.latLng.longitude
                    )
                )
            )
            marker?.showInfoWindow()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private fun setMapStyle() {
        try {
            // Customize the styling of the base mMap using a JSON object defined
            // in a raw resource file.
            val success = map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    application, R.raw.map_styling
                )
            )

            if (!success) {
                Log.e(NavMapFragment.TAG, "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(NavMapFragment.TAG, "Can't find style. Error: ", e)
        }
    }


    /**
     * loading vector image as marker
     */
    private fun Int.bitmapDescriptorFromVector(context: Context): BitmapDescriptor? {
        return ContextCompat.getDrawable(context, this)?.run {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            val bitmap =
                Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
            draw(Canvas(bitmap))
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }

    /**
     *when user long click on a location on the mMap i move marker to that location
     * also changing the color of marker
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private fun setOnMapLongClick() {
        map.setOnMapLongClickListener {
            marker?.remove()
            val snippet =
                application.getString(R.string.lat_long_snippet, it.latitude, it.longitude)
            marker = map.addMarker(
                MarkerOptions().position(it)
                    .title(
                        getLocationDetails(it.latitude, it.longitude)
                            ?: application.getString(R.string.dropped_pin)
                    ).snippet(
                        snippet
                    ).icon(
                        R.drawable.map_marker.bitmapDescriptorFromVector(application)
                    )
            )
            calculateDirections(marker!!)

        }
    }


    /**
     * drawing cluster image on map to represent current user location
     * we can call this method to redraw cluster on map whenever the user current location changes
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private fun setUpCluster() {
        requestCurrentDeviceLocation { location ->
            // Initialize the manager with the context and the map.
            val clusterMarkerManager = ClusterManager<UserClusterMarker>(application, map)
            customUserManagerRenderer =
                CustomUserManagerRenderer(application, map, clusterMarkerManager)
            clusterMarkerManager.renderer = customUserManagerRenderer

            // Point the map's listeners at the listeners implemented by the cluster
            map.setOnCameraIdleListener(clusterMarkerManager)
            map.setOnMarkerClickListener(clusterMarkerManager)

            currentUserClusterMarker =
                UserClusterMarker(MapModel(location.latitude, location.longitude), user)
            clusterMarkerManager.addItem(currentUserClusterMarker)
            clusterMarkerManager.cluster()
        }

    }

    //calculating directions between user current location and the route he determines
    private fun calculateDirections(marker: Marker) {
        val destination = com.google.maps.model.LatLng(
            marker.position.latitude,
            marker.position.longitude
        )
        val directions = DirectionsApiRequest(geoApiContext)
        directions.alternatives(true)
        directions.origin(
            com.google.maps.model.LatLng(
                lastKnownLocation.latitude,
                lastKnownLocation.longitude
            )
        )
        directions.destination(destination)
            .setCallback(object : PendingResult.Callback<DirectionsResult> {
                override fun onFailure(e: Throwable?) {
                    e?.let {
                        Log.e("DirectionApi", "onFailure: " + e.message)

                    }
                }

                override fun onResult(result: DirectionsResult?) {
                    result?.let {
                        coroutineScope.launch {
                            withContext(Dispatchers.Main) {
                                    markerAnimation.startMarkerAnimation(marker, it)
                                    //markerAnimation.drawPolyLineOnMap(it)


                            }

                        }
                    }
                }
            })
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun onClean() {
        job.cancel()
        lifecycleOwner.lifecycle.removeObserver(this)
    }

}