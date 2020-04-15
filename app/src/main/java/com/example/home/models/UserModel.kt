package com.example.home.models

import android.os.Parcelable
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserModel(val userName:String?=null, val email:String?=null, val userImage: String?=null):
    Parcelable

val currentUser: UserModel by lazy {
    val user = FirebaseAuth.getInstance().currentUser
    UserModel(user!!.displayName, user.email, user.photoUrl.toString())
}