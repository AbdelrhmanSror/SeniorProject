package com.example.home

import android.net.Uri
import androidx.databinding.BindingAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.gms.maps.model.MarkerOptions
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.MapView


/**
 * loading an image into imageView
 * if there is no album for this audio replace it with default one
 */
@BindingAdapter("circularImageUri")
fun setCircularImageUri(imageView: ImageView, imageUri: Uri?) {
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


/**
 * binding adapter to change the image of search button in search fragment so whne user start typing we animate it to voice image
 * and visa verse
 */
@BindingAdapter("searchVoiceAnim")
fun startSearchVoiceAnimation(imageButton: ImageButton, typing: Boolean) {
    imageButton.apply {
        val animatedVector: AnimatedVectorDrawableCompat?
        /**
         *representing the last state of animation if the user was typing or not
         * if he was typing and the current state typing also so no need to animate
         * the same the opposite
         */
        if (typing && this.tag != VOICE) {
            this.tag = VOICE
            animatedVector =
                AnimatedVectorDrawableCompat.create(context, R.drawable.search_voice_animation)
            setImageDrawable(animatedVector)
            animatedVector?.start()

        } else if (!typing && this.tag != SEARCH) {
            this.tag = SEARCH
            animatedVector =
                AnimatedVectorDrawableCompat.create(context, R.drawable.voice_search_animation)
            setImageDrawable(animatedVector)
            animatedVector?.start()
        }

    }

}