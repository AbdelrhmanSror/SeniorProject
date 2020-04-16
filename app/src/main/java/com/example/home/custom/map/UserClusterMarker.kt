package com.example.home.custom.map

import com.example.home.models.MapModel
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

class UserClusterMarker(
    var mapModel: MapModel
    , private val snippet: String? = null
) : ClusterItem {
    override fun getSnippet(): String? {
        return snippet
    }

    override fun getTitle(): String? {
        return mapModel.userModel?.userName
    }

    override fun getPosition(): LatLng {
        return LatLng(mapModel.latitude, mapModel.longitude)
    }
}