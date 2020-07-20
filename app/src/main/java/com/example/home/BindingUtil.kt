package com.example.home

import android.widget.ImageButton
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton


/**
 * loading an image into imageView
 * if there is no album for this audio replace it with default one
 */
@BindingAdapter("circularImageUri")
fun setCircularImageUri(imageView: ImageView, imageUri: String?) {
    imageUri?.let {
        Glide.with(imageView.context)
            .load(imageUri)
            .apply(RequestOptions.circleCropTransform().apply { RequestOptions.centerCropTransform() })
            .into(imageView)
    }

}

/**
 * binding adapter to change the image of floating action button based on gps is enabled or disabled
 */
@BindingAdapter("fabDrawable")
fun fabDrawable(fab: FloatingActionButton, enabled: Boolean) {
    if (enabled) {
        fab.setImageResource(R.drawable.ic_my_location_black_24dp)
    } else {
        fab.setImageResource(R.drawable.ic_location_disabled_black_24dp)

    }
}

