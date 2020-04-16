package com.example.home.custom

import android.app.Activity
import android.os.Handler
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.example.home.R

object LoadingDialog {
    fun showLoadingDialog(context: Activity, delayMs: Long, whatEverToDo: () -> Unit) {
        val dialog = MaterialDialog(context, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            customView(R.layout.loading, scrollable = true, dialogWrapContent = true)
        }
        Handler().postDelayed({
            dialog.dismiss()
            whatEverToDo()
        }, delayMs)
    }


}