package com.example.home.models

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MapModel(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    var userName: String? = null,
    var userImage: String? = null
) : Parcelable

fun String.toUri(): Uri {
    return Uri.parse(this)
}



