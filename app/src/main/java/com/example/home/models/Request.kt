package com.example.home.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MonitorRequest(
    val from: UserModel? = null
    , val to: UserModel? = null
    , var isRequest: Boolean = true
): Parcelable