package com.example.home.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.home.Event
import com.example.home.customMap.ApplicationMap
import com.example.home.isGpsEnabled
import com.example.home.models.MapModel
import com.example.home.models.UserModel
import com.google.android.gms.maps.GoogleMap
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class MapViewModel(application: Application) : AndroidViewModel(application) {
    private lateinit var applicationMap: ApplicationMap
    private val fireStore = FirebaseFirestore.getInstance()

    //represent the current user location of lat and long
    private lateinit var mapModel: MapModel

    private val _gpsEnabled = MutableLiveData<Event<Boolean>>()
    val gpsEnabled: LiveData<Event<Boolean>>
        get() = _gpsEnabled

    private val _onNavigationFabDrawableChange = MutableLiveData<Boolean>()
    val onNavigationFabDrawableChange: LiveData<Boolean>
        get() = _onNavigationFabDrawableChange

    private val _searchPlace = MutableLiveData<MapModel>()
    val searchPlace: LiveData<MapModel>
        get() = _searchPlace

    val currentUser: UserModel by lazy {
        val user = FirebaseAuth.getInstance().currentUser
        UserModel(user!!.displayName, user.email, user.photoUrl)
    }


    //checking if permission is granted and gps is enabled then every thing is setuped
    private fun setOnLocationChangeListener() {
        if (!getApplication<Application>().isGpsEnabled()) {
            _onNavigationFabDrawableChange.value = false
            _gpsEnabled.value = Event(false)
        } else {
            _onNavigationFabDrawableChange.value = true
            _gpsEnabled.value = Event(true)
            //once all setuped we start track the user current location
            onLocationChangeListener()
        }
    }

    init {
        //initial setup for user location when first time open the app
        setOnLocationChangeListener()
        //location of other users
        updateUsersLocationOnMap(fireStore.collection("userLocation").document("user").collection("UsersLocation"))
        //location of me
        updateUsersLocationOnMap(fireStore.collection("userLocation"))
    }


    fun setMap(lifecycleOwner: LifecycleOwner, map: GoogleMap) {
        if (!::applicationMap.isInitialized)
            applicationMap =
                ApplicationMap.create(lifecycleOwner, getApplication(), map)
        else
            applicationMap.updateMapRef(map)

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


    /**
     *every time location is changing we call this method
     *
     */
    private fun onLocationChangeListener() {
        if (::applicationMap.isInitialized) {
            applicationMap.setOntLocationChangeListener { mapModel ->
                this.mapModel = mapModel
                //track the current location of a user then push into firestore database so it will be shared across all devices
                fireStore.document("userLocation/user").set(mapModel.apply {
                    userImage = currentUser.userImage.toString()
                    userName = currentUser.userName.toString()
                })
            }
        }
    }

    /**
     * we call this function if we want to go to specific place and give it map model parameter which represent la and lng on map
     */
    fun goToSpecificPlace(mapModel: MapModel) {
        applicationMap.goToSpecificPlace(mapModel)

    }

    /**
     * we call this function to go to user current location
     */
    fun goToCurrentLocation() {
        applicationMap.loadCurrentDeviceLocation()
    }

    //called by databinding
    fun onNavigationFabClicked() {
        setOnLocationChangeListener()
    }

    fun startSearching(mapModel: MapModel) {
        _searchPlace.value = mapModel
    }

    private fun updateUsersLocationOnMap(collectionReference: CollectionReference) {
        collectionReference.addSnapshotListener { value, e ->
            if (e != null) {
                Log.w("mapModelTrigger", "Listen failed.", e)
                return@addSnapshotListener
            }
            //val listOfUser = ArrayList<RemoteSourceModel>()
            for (doc in value!!) {
                val userLocation = doc.toObject(MapModel::class.java)
                Log.v("mapModelTrigger", "${userLocation.userName}")
                applicationMap.updateCluster(doc.id, userLocation)

            }
        }

    }


}


class NavMapViewModelFactory(private val application: Application) : ViewModelProvider.Factory {


    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
            return MapViewModel(application) as T

        }
        throw IllegalArgumentException("unknown class")
    }

}