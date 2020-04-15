package com.example.home.database

import android.util.Log
import com.example.home.models.MapModel
import com.example.home.models.Monitors
import com.example.home.models.currentUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges

class FireStore {
    private val fireStore = FirebaseFirestore.getInstance()

    //reference to main collection
    private val mainCollectionRef =
        fireStore.collection("userLocation")

    //ref to monitors documents
    private val monitorCollectionRef =
        mainCollectionRef.document("${currentUser.userName}")
            .collection("monitors").document("monitor")

    //ref to current user document
    private val userCollectionRef =
        mainCollectionRef.document("${currentUser.userName}")

    fun updateUserData(mapModel: MapModel) {
        userCollectionRef.set(mapModel)
    }


    /**
     * get the location of current user monitors
     */
    fun getCurrentMonitorsModel(whatEverToDo: (id: String, userLocation: MapModel) -> Unit) {
        getCurrentUserMonitors {
            mainCollectionRef.addSnapshotListener { value, e ->
                if (e != null) {
                    Log.w("mapModelTrigger", "Listen failed.", e)
                    return@addSnapshotListener
                }
                for (doc in value!!) {
                    if (it.monitorId != null && it.monitorId.contains(doc.id)) {
                        Log.v("currentMonitors", doc.id)
                        val userLocation = doc.toObject(MapModel::class.java)
                        whatEverToDo(doc.id, userLocation)
                    }
                }
            }


        }
    }

    /**
     * get list of current user monitors id
     */
    private fun getCurrentUserMonitors(listOfMonitors: (monitors: Monitors) -> Unit) {
        monitorCollectionRef.addSnapshotListener(MetadataChanges.INCLUDE) { value, e ->
            if (e != null) {
                Log.w("mapModelTrigger", "Listen failed.", e)
                return@addSnapshotListener
            }
            //val listOfUser = ArrayList<RemoteSourceModel>()
            value?.let {
                val monitors = it.toObject(Monitors::class.java)
                Log.v("currentMonitors","$monitors")
                listOfMonitors(monitors!!)
            }
        }
    }

    /**
     * get current user location
     */
    fun getCurrentUserModel(whatEverToDo: (id: String, userLocation: MapModel) -> Unit) {
        userCollectionRef.addSnapshotListener(MetadataChanges.INCLUDE) { value, e ->
            if (e != null) {
                Log.w("mapModelTrigger", "Listen failed.", e)
                return@addSnapshotListener
            }
            //val listOfUser = ArrayList<RemoteSourceModel>()
            value?.let {
                val userLocation = it.toObject(MapModel::class.java)
                Log.v("currentUser","$userLocation")
                whatEverToDo(value.id, userLocation!!)
            }


        }
    }
}