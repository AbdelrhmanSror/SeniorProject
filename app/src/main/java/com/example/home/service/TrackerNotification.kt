/*
package com.example.home.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

const val CHANNEL_ID = "5"
const val NOTIFICATION_ID = 11

class TrackerNotification(private val context: Context) {

    //creating notification channel
    private fun createNotificationChannel() {
        val notifyManager = NotificationManagerCompat.from(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create a ForegroundNotification
            val notificationChannel = NotificationChannel(CHANNEL_ID,
                "Tracker Notification", NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.description = "Tracker"
            notifyManager.createNotificationChannel(notificationChannel)
        }
    }
    init {
        createNotificationChannel()
    }

    fun build(): Notification {
      //BitmapFactory.decodeResource(context.resources, R.ic_search_black_24dp.default_image)
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("")
            .setContentText("")
            // the metadata for the currently playing track
            .setSubText("")
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            // Add an app icon and set its accent color
            .setSmallIcon(15)
           */
/* .setContentIntent(
                PendingIntent.getActivity(context,
                    NOTIFICATION_ID, notificationClickedIntent(chosenSongIndex), 0))*//*

            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setDeleteIntent(pendingIntentDelete())
            .setOnlyAlertOnce(true)
            .build()
    }

    */
/*private fun notificationClickedIntent(chosenSongIndex: Int): Intent {
        val intent = Intent(context, MainActivity::class.java)
        val bundle = Bundle()
        bundle.putParcelableArrayList(LIST_SONG, songModels)
        bundle.putInt(CHOSEN_SONG_INDEX, chosenSongIndex)
        bundle.putString(FRAGMENT_PURPOSE, PlayerActions.AUDIO_FOREGROUND_NOTIFICATION.value)
        intent.putExtras(bundle)
        return intent
    }
*//*

    private fun pendingIntentDelete(): PendingIntent {
        val deleteIntent = Intent(context, TrackerForegroundService::class.java)
        deleteIntent.action = NotificationAction.ACTION_DELETE.value
        return PendingIntent.getService(context,
            NOTIFICATION_ID, deleteIntent, 0)
    }

}
*/
