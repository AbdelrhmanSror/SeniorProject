package com.example.home.database

import android.util.Log
import com.example.home.models.*
import com.google.firebase.firestore.FieldValue
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

    //load current user location on map or update it
    fun updateUserData(mapModel: MapModel) {
        userCollectionRef.set(mapModel)
    }


    fun addMonitoredPerson(monitorId: monitorId,monitoredPersonId:monitorId){
        mainCollectionRef.document(monitorId).collection("monitors").document("monitor").update("monitorId", FieldValue.arrayUnion(monitoredPersonId))

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
                    Log.v("mapModelTrigger", "request has received.")
                    monitorRequestRef.set(MonitorRequest(isRequest = false))
                    requestObserver(request)
                }


            }
        }
    }

    //temporarily just search for user using his/her name but edit it to be by email
    fun sendMonitoringRequest(request: MonitorRequest) {
        mainCollectionRef.document("${request.to?.userName}").collection("Request").document("MonitorRequest").set(request)
    }


    fun getMonitoredPersonsLocation(whatEverToDo: (id: String, userLocation: MapModel) -> Unit) {
        getMonitoredPersons {
            mainCollectionRef.addSnapshotListener { value, e ->
                if (e != null) {
                    Log.w("mapModelTrigger", "Listen failed.", e)
                    return@addSnapshotListener
                }
                for (doc in value!!) {
                    if (it.contains(doc.id)) {
                        Log.v("currentMonitors", doc.id)
                        if(doc["latitude"] is String){
                            val userLocation = doc.toObject(MapModel2::class.java)
                            whatEverToDo(doc.id, userLocation.toMapModel())

                        }else{
                            val userLocation = doc.toObject(MapModel::class.java)
                            whatEverToDo(doc.id, userLocation)

                        }
                    }
                }
            }


        }
    }

    /**
     * get list of  id
     */
    private fun getMonitoredPersons(listOfMonitors: (monitors: HashSet<monitorId>) -> Unit) {
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
     * return the data of specific user
     */
    fun fetchUserDataBasedOnEmail(email: String, userData: (UserModel?) -> Unit) {
        mainCollectionRef
            .whereEqualTo("userModel.email",email)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    if(document["latitude"] is String){
                        val userLocation = document.toObject(MapModel2::class.java)
                        userData(userLocation.userModel)

                    }else{
                        val userLocation = document.toObject(MapModel::class.java)
                        userData(userLocation.userModel)
                    }
                    Log.v("documentRequest","done ${document.id}")
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
                if(it["latitude"] is String){
                   val userLocation = it.toObject(MapModel2::class.java)
                    whatEverToDo(value.id, userLocation!!.toMapModel())

                }else{
                   val userLocation = it.toObject(MapModel::class.java)
                    whatEverToDo(value.id, userLocation!!)
                }
            }


        }
    }
}