package com.example.home

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.navigation.NavigationView
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable


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
    Glide.with(this.context).load(uri)
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

/**
 * extension function to check if user granted the permissions
 */
fun Application.isLocationPermissionGranted(): Boolean {
    return ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
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
 * extension function to initialize drawer layout and setup it with toolbar and back button
 */
fun DrawerLayout.initialize(fragment: Fragment, toolbar: androidx.appcompat.widget.Toolbar) {
    val toggle = object :
        ActionBarDrawerToggle(
            fragment.activity,
            this,
            toolbar,
            R.string.open_drawer,
            R.string.close_drawer
        ) {
        private var callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (this@initialize.isDrawerOpen(GravityCompat.START)) {
                    this@initialize.closeDrawer(GravityCompat.START)
                } else {
                    fragment.requireActivity().finish()
                }
            }

        }

        init {
            // This callback will only be called when MyFragment is at least Started.
            fragment.requireActivity().onBackPressedDispatcher.addCallback(fragment, callback)
        }
    }

    this.addDrawerListener(toggle)
    toggle.syncState()
}

/**
 * extension function for hiding the keyboard after finishing from typing hiding the keyboard
 */
fun Activity.hideKeyBoard() {
    val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(
        currentFocus?.windowToken, 0
    )
}

