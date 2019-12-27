package com.example.home

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.icu.text.CaseMap
import android.location.GnssNavigationMessage
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.snackbar.Snackbar


/**
 * extension function for starting foreground service
 */
fun Context.startForeground(foregroundIntent: Intent) {
    //Start service:
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        startForegroundService(foregroundIntent)

    } else {
        startService(foregroundIntent)

    }
}

/**
 * extension function to load image using glide into image view with
 */
fun ImageView.setImageUri(uri: Uri?, onImageLoaded: () -> Unit) {
    Glide.with(this.context).load(uri?:R.drawable.person)
        .apply(RequestOptions.circleCropTransform().apply { RequestOptions.centerInsideTransform() })
        .into(object : CustomTarget<Drawable>() {
            override fun onLoadCleared(placeholder: Drawable?) {
            }

            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                this@setImageUri.setImageDrawable(resource)
                onImageLoaded()
            }
        })
}



fun Application.isGpsEnabled(): Boolean {
    val manager = (getSystemService(Context.LOCATION_SERVICE)) as LocationManager
    if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
        return false
    }
    return true
}

/**
 * extension function to make rounded corner for navigation  view
 */
fun NavigationView.roundedCorner(radius: Float) {
    val navViewBackground = this.background as MaterialShapeDrawable
    navViewBackground.shapeAppearanceModel = navViewBackground.shapeAppearanceModel
        .toBuilder()
        .setTopRightCorner(CornerFamily.ROUNDED, radius)
        .setBottomRightCorner(CornerFamily.ROUNDED, radius)
        .build()
}


/**
 * extension function for hiding the keyboard after finishing from typing
 */
fun Activity.hideKeyBoard() {
    val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(
        currentFocus?.windowToken, 0
    )
}


fun View.showSnackbar(snackbarText: String, timeLength: Int) {
    Snackbar.make(this, snackbarText, timeLength).run {
        setAction(context.getString(R.string.go_to_settings)) {
            val intent = Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + BuildConfig.APPLICATION_ID)
            )
            context.startActivity(intent)
        }
        show()
    }
}

/**
 * Triggers a snackbar message when the value contained by snackbarTaskMessageLiveEvent is modified.
 *show snack bar to tell user to direct user to settings to enable permission
 */
fun View.setupSnackbar(
    lifecycleOwner: LifecycleOwner,
    snackbarEvent: LiveData<Event<Int>>,
    timeLength: Int
) {

    snackbarEvent.observe(lifecycleOwner, EventObserver { event ->
        showSnackbar(context.getString(event), timeLength)
    })
}
