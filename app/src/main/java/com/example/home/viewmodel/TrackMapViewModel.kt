/*
package com.example.home.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.home.customMap.ApplicationMap

import com.google.android.gms.maps.GoogleMap

class TrackMapViewModel(application: Application, map: GoogleMap) : AndroidViewModel(application) {
    private val applicationMap = ApplicationMap.create(application, map)

    //live data will be trigger if fab is clicked
    private val _navigationFabClicked = MutableLiveData<Boolean>()
    val navigationFabClicked: LiveData<Boolean> = _navigationFabClicked

    //live data to check if permission is granted or not and if not we request it again
    private val _onNavigationFabDrawableChange = MutableLiveData<Boolean>()
    val onNavigationFabDrawableChange: LiveData<Boolean> = _onNavigationFabDrawableChange


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


    fun goToCurrentLocation() {
        _onNavigationFabDrawableChange.value = true
        applicationMap.loadCurrentDeviceLocation()

    }

    fun onNavigationFabClicked() {
        _navigationFabClicked.value = true

    }

    fun setFabDrawableMode(enabled: Boolean) {
        _onNavigationFabDrawableChange.value = enabled
    }
}

class TrackMapViewModelFactory(private val application: Application, private val map: GoogleMap) :
    ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(TrackMapViewModel::class.java)) {
            return TrackMapViewModel(application, map) as T

        }
        throw IllegalArgumentException("unknown class")
    }

}*/
