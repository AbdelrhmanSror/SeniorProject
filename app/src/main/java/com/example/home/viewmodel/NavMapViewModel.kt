package com.example.home.viewmodel

import android.app.Activity
import android.app.Application
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import com.example.home.customMap.ApplicationMap
import com.example.home.models.MapModel
import com.example.home.models.UserModel

import com.google.android.gms.maps.GoogleMap
import com.google.firebase.auth.FirebaseAuth

class MapViewModel(application: Application) : AndroidViewModel(application) {
    private lateinit var applicationMap: ApplicationMap

    //represent the current user location of lat and long
    private lateinit var mapModel: MapModel
    private val _navigationFabClicked = MutableLiveData<Boolean>()
    //live data will be trigger if fab is clicked
    val navigationFabClicked: LiveData<Boolean> = _navigationFabClicked


    private val _onGpsChange = MutableLiveData<Boolean>()
    //live data to check if permission is granted or not and if not we request it again
    val onNavigationFabDrawableChange = Transformations.map(_onGpsChange) {
        Log.v("placesApi", "ic_search_black_24dp chnage ")

        it
    }

     val currentUser: UserModel by lazy {
        val user = FirebaseAuth.getInstance().currentUser
        UserModel(user!!.displayName, user.email, user.photoUrl)
    }

    fun setMap(lifecycleOwner: LifecycleOwner,map: GoogleMap) {
        applicationMap = ApplicationMap.create(lifecycleOwner,getApplication(), map,currentUser)

    }

    //enable normal style of map
    fun enableNormalMap() {
        applicationMap.enableNormalMap()

    }

    //enable SATELLITE style of map
    fun enableSatelliteMap() {
        applicationMap.enableSatelliteMap()

    }

    //enable Hybrid style of map
    fun enableHybridMap() {
        applicationMap.enableHybridMap()

    }

    //every time location is changing we end data container lat and long of new location
    fun onLocationChangeListener(update: (mapModel: MapModel) -> Unit) {
        applicationMap.requestLocationUpdates { mapModel ->
            this.mapModel=mapModel
            update(mapModel)
        }
    }

    fun goToSpecificPlace(mapModel: MapModel): Boolean {
        val isAllPermissionGranted = applicationMap.goToSpecificPlace(mapModel)
        _onGpsChange.value = isAllPermissionGranted
        return isAllPermissionGranted
    }

    fun goToCurrentLocation(): Boolean {
        val isAllPermissionGranted = applicationMap.loadCurrentDeviceLocation()
        _onGpsChange.value = isAllPermissionGranted
        return isAllPermissionGranted

    }

    fun onNavigationFabClicked() {
        _navigationFabClicked.value = true

    }


    fun setGpsMode(enable: Boolean) {
        _onGpsChange.value = enable
    }


}
/*

class MapViewModelFactory(private val application: Application, private val map: GoogleMap) :
    ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
            return MapViewModel(application, map) as T

        }
        throw IllegalArgumentException("unknown class")
    }

}*/
