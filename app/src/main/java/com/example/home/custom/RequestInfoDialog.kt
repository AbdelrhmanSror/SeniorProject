package com.example.home.custom

import android.content.Context
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.example.home.R


object RequestInfoDialog {
    fun showCustomViewDialog(
        context: Context,
        dialog: (MaterialDialog) -> Unit,positiveClicked: () -> Unit
    ) {
       val infoDialog= MaterialDialog(context, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            title(R.string.monitorRequest)
            customView(R.layout.request_info, scrollable = true, horizontalPadding = true)
            positiveButton(R.string.connect) {
               positiveClicked()
            }
            cancelable(false)
            negativeButton(android.R.string.cancel)
        }
        dialog(infoDialog)
    }
}
