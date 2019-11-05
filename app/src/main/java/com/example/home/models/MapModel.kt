package com.example.home.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MapModel(val latitude:Double=0.0,val longitude:Double=0.0):Parcelable