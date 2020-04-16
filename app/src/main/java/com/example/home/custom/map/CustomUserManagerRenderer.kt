package com.example.home.custom.map

import android.content.Context
import android.graphics.Bitmap
import android.view.ViewGroup
import android.widget.ImageView
import com.example.home.R
import com.example.home.extensions.setImageUri
import com.example.home.models.toUri
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.google.maps.android.ui.IconGenerator

/**
 * Draws profile photos inside markers (using IconGenerator).
 */
class CustomUserManagerRenderer(
    context: Context,
    map: GoogleMap,
    private val clusterMarkerManager: ClusterManager<UserClusterMarker>
) : DefaultClusterRenderer<UserClusterMarker>(context, map, clusterMarkerManager)
{

    private val iconGenerator = IconGenerator(context)
    private val imageView: ImageView = ImageView(context)
    private val dimension: Int = context.resources.getDimension(R.dimen.custom_map_image).toInt()
    private lateinit var icon: Bitmap

    init {
        imageView.layoutParams = ViewGroup.LayoutParams(dimension, dimension)
        // val padding = context.resources?.getDimension(R.dimen.large_margin)!!.toInt()
        //imageView.setPadding(padding, padding, padding, padding)
        iconGenerator.setContentView(imageView)
    }


    override fun onClusterItemRendered(person: UserClusterMarker?, marker: Marker?) {
        imageView.setImageUri(person?.mapModel?.userImage?.toUri())
        {
            icon = iconGenerator.makeIcon()
            marker?.setIcon(BitmapDescriptorFactory.fromBitmap(icon))
            marker?.title = person?.title
            marker?.snippet = "${person?.position}"
        }
    }

    fun updateMarkerPosition(userClusterMarker: UserClusterMarker) {
        val marker = getMarker(userClusterMarker)
        if (marker != null) {
            // Add cluster items (markers) to the cluster manager.
            marker.position = userClusterMarker.position
        } else {
            //if there was cluster at previous position then we remove it and update it to new one
            clusterMarkerManager.addItem(userClusterMarker)
        }
        clusterMarkerManager.cluster()
    }

    override fun shouldRenderAsCluster(clusterMarker: Cluster<UserClusterMarker>?): Boolean {
        return false
    }
}