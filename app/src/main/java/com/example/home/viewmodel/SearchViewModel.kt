package com.example.home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.home.models.MapModel

class SearchViewModel : ViewModel() {

    private val _navigateToMap = MutableLiveData<MapModel>()
    val navigateToMap: LiveData<MapModel>
        get() = _navigateToMap

    private val _searchVoiceAnimation = MutableLiveData<Boolean>()
    val searchVoiceAnimation: LiveData<Boolean>
        get() = _searchVoiceAnimation


    fun startNavigation(mapModel: MapModel) {
        _navigateToMap.value = mapModel
    }
    //to trigger live data that is being observed by ui and ui through data binding will play animation
    fun startSearchVoiceAnimation(typing:Boolean){
        _searchVoiceAnimation.value=typing
    }

    //when the back ic_search_black_24dp pressed we will call this methode
    fun onBackDrawablePressed() {
        _navigateToMap.value = null
    }


}