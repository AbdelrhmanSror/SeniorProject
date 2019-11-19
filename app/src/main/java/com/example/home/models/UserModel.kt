package com.example.home.models

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class UserModel(val userName:String?=null, val email:String?=null, val userImage: Uri?=null)