package com.example.home

import android.Manifest
import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

class AudioPermission(private val activityCompat: AppCompatActivity
                      , private val onPermissionGranted: () -> Unit) {

    init {
        checkPermission()
    }

    companion object {
        const val MY_PERMISSIONS_MAP_REQUEST = 5
    }

    private fun checkPermission() {
        // Here, thisActivity is the current activity
        if (!activityCompat.application.isPermissionGranted()) {
            requestPermission()
        } else {
            onPermissionGranted()
        }

    }

    /**
     * extension function to check if user granted the permissions
     */
    private fun Application.isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED&&ContextCompat.checkSelfPermission(
            this,Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() = ActivityCompat.requestPermissions(activityCompat,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.RECORD_AUDIO),
            MY_PERMISSIONS_MAP_REQUEST
    )

    //show snack bar to tell user to direct user to settings to enable permission
    private fun showPermissionEnableSnackBar() {
        Snackbar.make(
            activityCompat.window.decorView.findViewById(android.R.id.content),
            activityCompat.getString(R.string.go_to_settings),
            Snackbar.LENGTH_INDEFINITE
        ).setAction(activityCompat.getString(R.string.go_to_settings)) {
            val intent = Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + BuildConfig.APPLICATION_ID)
            )
            activityCompat.startActivity(intent)

        }.show()
    }

    private fun showPermissionAlertDialog() {
        MaterialAlertDialogBuilder(activityCompat)
            .setTitle(activityCompat.getString(R.string.permission_denied))
            .setMessage(activityCompat.getString(R.string.permission_clarify))
            .setPositiveButton(activityCompat.getString(R.string.accept_permission)) { _, _ ->
                requestPermission()
            }.setNegativeButton(activityCompat.getString(R.string.refuse_permission)) { dialog, _ ->
                dialog.cancel()
                activityCompat.finish()
            }
            .show()
    }

    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        // Check if location permissions are granted and if so enable the
        // location data layer.
        //if the permission is granted but gps is not enabled then ask user to enable it
        //else if the use did not enable it then go to default location
        if (requestCode == MY_PERMISSIONS_MAP_REQUEST) {
            if (grantResults.isNotEmpty() && (grantResults[0] == PackageManager.PERMISSION_GRANTED&&grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                onPermissionGranted()
            } else {
                // permission was not granted
                //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
                // shouldShowRequestPermissionRationale will return true
                if (ActivityCompat.shouldShowRequestPermissionRationale(activityCompat, Manifest.permission.ACCESS_FINE_LOCATION)||
                    ActivityCompat.shouldShowRequestPermissionRationale(activityCompat, Manifest.permission.RECORD_AUDIO)) {
                    showPermissionAlertDialog()

                } //permission is denied (and never ask again is  checked)
                //shouldShowRequestPermissionRationale will return false
                else {
                    Log.v("permssionDenied","done")
                    showPermissionEnableSnackBar()
                }
            }
        }

    }
}