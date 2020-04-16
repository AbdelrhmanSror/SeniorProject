package com.example.home.database

import android.util.Log
import com.example.home.models.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges

class FireStore() {
    private val fireStore = FirebaseFirestore.getInstance()

    //reference to main collection
    private val mainCollectionRef =
        fireStore.collection("userLocation")

    //ref to current user document
    private val userCollectionRef =
        mainCollectionRef.document("${currentUser.userName}")

    //ref to monitors documents
    private val monitorCollectionRef =
        userCollectionRef.collection("monitors").document("monitor")

    private val monitorRequestRef =
        userCollectionRef.collection("Request").document("MonitorRequest")

    fun updateUserData(mapModel: MapModel) {
        userCollectionRef.set(mapModel)
    }


    fun observeMonitoringRequest(requestObserver: (request: MonitorRequest) -> Unit) {
        monitorRequestRef.addSnapshotListener(MetadataChanges.INCLUDE) { value, e ->
            if (e != null) {
                Log.w("mapModelTrigger", "Listen failed.", e)
                return@addSnapshotListener
            }
            value?.let {
                val request = it.toObject(MonitorRequest::class.java)
                if (request!!.isRequest) {
                    monitorRequestRef.set(MonitorRequest(isRequest = false))
                    requestObserver(request)
                }


            }
        }
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
                    if (it.contains(doc.id)) {
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
    private fun getCurrentUserMonitors(listOfMonitors: (monitors: HashSet<monitorId>) -> Unit) {
        monitorCollectionRef.addSnapshotListener(MetadataChanges.INCLUDE) { value, e ->
            if (e != null) {
                Log.w("mapModelTrigger", "Listen failed.", e)
                return@addSnapshotListener
            }
            //val listOfUser = ArrayList<RemoteSourceModel>()
            value?.let {
                val monitors = it.toObject(Monitors::class.java)
                listOfMonitors(monitors!!.monitorId!!.toHashSet())
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
                Log.v("currentUser", "$userLocation")
                whatEverToDo(value.id, userLocation!!)
            }


        }
    }
}