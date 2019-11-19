package com.example.home.customMap

import com.example.home.models.MapModel
import com.example.home.models.UserModel
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
        return mapModel.userName
    }

    override fun getPosition(): LatLng {
        return LatLng(mapModel.latitude, mapModel.longitude)
    }
}