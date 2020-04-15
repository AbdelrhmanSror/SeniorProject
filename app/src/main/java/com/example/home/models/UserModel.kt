package com.example.home.models

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth

data class UserModel(val userName:String?=null, val email:String?=null, val userImage: Uri?=null)

val currentUser: UserModel by lazy {
    val user = FirebaseAuth.getInstance().currentUser
    UserModel(user!!.displayName, user.email, user.photoUrl)
}