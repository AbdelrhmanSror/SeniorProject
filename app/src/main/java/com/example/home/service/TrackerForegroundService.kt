/*
package com.example.home.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

enum class NotificationAction(val value: String) {

    ACTION_DELETE("delete notification")

}

class TrackerForegroundService : Service() {
    private lateinit var application: Context

    // indicates how to behave if the service is killed.
    private var mStartMode = START_STICKY

    // interface for clients that bind.
    private var mBinder: IBinder = TrackerBinder()
    //responsible for creating media player notification;
    private lateinit var trackerNotification: TrackerNotification
    //responsible for updating the notification
    private lateinit var notificationManager: NotificationManagerCompat
    private lateinit var mFirebaseDataBase: FirebaseDatabase
    private lateinit var mMessageDatabaseRefrence: DatabaseReference
    private lateinit var mFirebaseAuth: FirebaseAuth
    private lateinit var mAuthStateListener: FirebaseAuth.AuthStateListener

    private var messageListener: ChildEventListener? = null


    override fun onCreate() {
        // The service is being created.
        mFirebaseDataBase = FirebaseDatabase.getInstance()
        mFirebaseAuth = FirebaseAuth.getInstance()
        mMessageDatabaseRefrence = mFirebaseDataBase.reference.child("Tracks")
        notificationManager = NotificationManagerCompat.from(this@TrackerForegroundService)
        application = applicationContext

    }

    internal inner class TrackerBinder : Binder() {
        val service: TrackerForegroundService
            get() = this@TrackerForegroundService

    }


    private fun handleIntent(intent: Intent?) {
        intent?.let {
            when (it.action) {
                NotificationAction.ACTION_DELETE.value ->
                    cancelForeground()

            }
        }
    }

    private fun setUpNotification(intent: Intent) {

        trackerNotification = TrackerNotification(application)
        startForeground(NOTIFICATION_ID, trackerNotification.build())

    }

    private fun cancelForeground() {
        //remove the notification and stop the service when user press the close button on notification
        stopForeground(false)
        notificationManager.cancelAll()
        stopSelf()
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int, startId: Int
    ): Int {
        // The service is starting, due to a call to startService().
        handleIntent(intent)
        return mStartMode
    }

    override fun onBind(intent: Intent): IBinder? {
        return mBinder
    }


}*/
