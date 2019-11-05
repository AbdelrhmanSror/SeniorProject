package com.example.home.customMap

import android.app.Application
import android.os.Handler
import android.os.SystemClock
import android.util.Log
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.example.home.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.model.DirectionsResult

/**
 * this class is responsible for making marker animation between two location
 */
class MarkerAnimation private constructor(
    lifecycleOwner: LifecycleOwner,
    private val map: GoogleMap,
    private val application: Application
) : LifecycleObserver {

    private val interpolator = LinearInterpolator()
    private val listOfCurrentPolyline = arrayListOf<Polyline>()
    private val handler = Handler()
    private lateinit var runnable: Runnable


    companion object {
        fun create(
            map: GoogleMap,
            application: Application,
            lifecycleOwner: LifecycleOwner
        ): MarkerAnimation {
            return MarkerAnimation(lifecycleOwner, map, application)
        }
    }

    init {
        //register this class as life cycle observer
        lifecycleOwner.lifecycle.addObserver(this)
    }

    fun startMarkerAnimation(marker: Marker?, result: DirectionsResult) {
        removePolylines()
        var start = SystemClock.uptimeMillis()
        var startIndex = 0
        var endIndex = 1
        val listOfLatLng = PolyLineDecoder.startDecode(result)
        //crating polyline on current user position to update it late as animation goes on
        val polyline = map
            .addPolyline(
                PolylineOptions().add(listOfLatLng[0])
                    .clickable(true)
                    .color(ContextCompat.getColor(application, R.color.colorPrimary))
            )
        runnable = object : Runnable {
            override fun run() {
                //as long as the  end index did not equal to size  of list we continue as normal
                if (endIndex != listOfLatLng.size) {
                    /**
                     * calculating the elapsed time so we can observe if the animation time got to end or not
                     *so we can start new one after the elapsed time of first animation finishes
                     */
                    val elapsed = SystemClock.uptimeMillis() - start
                    if (startIndex == 0) {
                        zoomCamera(listOfLatLng[startIndex])

                    } else {
                        map.animateCamera(CameraUpdateFactory.newLatLng(listOfLatLng[endIndex]))
                    }
                    val interpolationValue = calculateLatLngInterpolation(
                        listOfLatLng[startIndex]
                        , listOfLatLng[endIndex], elapsed, 100
                    ) {
                        start = SystemClock.uptimeMillis()
                        startIndex++
                        endIndex++
                    }
                    marker?.position = interpolationValue
                    updatePolyline(interpolationValue, polyline)


                    handler.postDelayed(this, 16)
                }
            }
        }
        handler.post(runnable)


    }


    /**
     * calculate interpolation between two latlng so we we can animate marker to latlng interpolation point
     *it accept startPosition we want to start animate from
     * endPosition that we want to end animation on it
     * elapsed time which just  the time elapsed since we have started animation
     * higher order fucnction which will be executed if the the current interpolation animation has finished so we can reset every thing and  start new animation if we want
     */
    private fun calculateLatLngInterpolation(
        startPosition: LatLng,
        endPosition: LatLng,
        elapsedTime: Long,
        @Suppress("SameParameterValue") interpolationTime: Int,
        onTimeInterpolationFinished: () -> Unit
    ): LatLng {
        val t = interpolator.getInterpolation(elapsedTime.toFloat() / interpolationTime)
        Log.v("latlng", "end lat is ${endPosition.latitude} end long is ${endPosition.longitude}")
        Log.v(
            "latlng",
            "start lat is ${startPosition.latitude} start long is ${startPosition.longitude}"
        )
        Log.v("latlng", "interpolation is $t")


        val lat =
            (((endPosition.latitude - startPosition.latitude) * ((startPosition.longitude + t) - startPosition.longitude)) / (endPosition.longitude - startPosition.longitude)) + startPosition.latitude
        val lng =
            (((endPosition.longitude - startPosition.longitude) * ((startPosition.latitude + t) - startPosition.latitude)) / (endPosition.latitude - startPosition.latitude)) + startPosition.longitude

        //val lat = endPosition.latitude + (1 - t) * startPosition.latitude
        //val lng = endPosition.longitude + (1 - t) * startPosition.longitude
        Log.v("latlng", "end lat after is ${lat} end long after is ${lng}")

        if (t >= 1.0) {
            onTimeInterpolationFinished()
        }
        Log.v("interpolation", "$t")
        return LatLng(lat, lng)
    }


    /**
     * zoom the camera to the specified position
     */
    private fun zoomCamera(position: LatLng) {

        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(position, 100f),
            600,
            null
        )

    }

    //update our polyline with the new marker position, creating a trailing effect on the polyline.
    private fun updatePolyline(latLng: LatLng, polyline: Polyline) {
        val points = polyline.points
        points.add(latLng)
        polyline.points = points
        listOfCurrentPolyline.add(polyline)


    }


    //this method is drawing polyline on the map using the direction result
    fun drawPolyLineOnMap(result: DirectionsResult) {
        removePolylines()
        val polyline = map
            .addPolyline(
                PolylineOptions().addAll(PolyLineDecoder.startDecode(result))
                    .clickable(true)
                    .color(ContextCompat.getColor(application, R.color.colorPrimary))
            )
        listOfCurrentPolyline.add(polyline)

    }

    /**
     *this just remove any previous polyline if exist
     */
    private fun removePolylines() {
        if (!listOfCurrentPolyline.isNullOrEmpty()) {
            listOfCurrentPolyline.forEach {
                it.remove()
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun onDestroy() {
        if (::runnable.isInitialized)
            handler.removeCallbacks(runnable)
    }
}
