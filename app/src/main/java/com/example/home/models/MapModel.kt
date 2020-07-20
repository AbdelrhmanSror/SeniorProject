package com.example.home.models

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MapModel(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    var userModel: UserModel? = null
) : Parcelable

@Parcelize
data class MapModel2(
    val latitude: String = "",
    val longitude: String = "",
    var userModel: UserModel? = null
) : Parcelable
fun MapModel.toMapModel2():MapModel2{
    val lat=this.latitude.toString()
    val long=this.longitude.toString()
    return MapModel2(lat,long,this.userModel)
}
fun MapModel2.toMapModel():MapModel{
    val lat=this.latitude.toDouble()
    val long=this.longitude.toDouble()
    return MapModel(lat,long,this.userModel)
}

fun String.toUri(): Uri {
    return Uri.parse(this)
}



