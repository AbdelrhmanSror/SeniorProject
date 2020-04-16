package com.example.home.extensions

import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.location.LocationManager
import android.os.Build
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.home.R
import com.example.home.models.toUri


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
 * extension function to get drawable from uri using glide
 */
fun String?.getImageDrawable(context: Context, onDrawableReady: (drawable: Drawable) -> Unit) {
    Glide.with(context).load(this?.toUri()?: R.drawable.ic_error)
        .apply(
            RequestOptions.circleCropTransform().apply { RequestOptions.centerInsideTransform() })
        .into(object : CustomTarget<Drawable>() {
            override fun onLoadCleared(placeholder: Drawable?) {
            }

            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                onDrawableReady(resource)
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

