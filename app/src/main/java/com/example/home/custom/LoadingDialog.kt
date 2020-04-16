package com.example.home.custom

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import com.example.home.R

class LoadingDialog (private val context: Activity){
    private lateinit var dialog:AlertDialog
    fun startLoadingDialog(){
        val builder=AlertDialog.Builder(context)
        val inflater=context.layoutInflater
        builder.setView(View.inflate(context, R.layout.custom_loading,null))
        builder.setCancelable(true)
        dialog=builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

    }
    fun dismissDialog(){
        dialog.dismiss()

    }

}