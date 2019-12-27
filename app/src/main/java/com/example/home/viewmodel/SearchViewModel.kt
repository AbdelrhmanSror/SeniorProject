package com.example.home.viewmodel

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.home.models.MapModel
import com.example.home.service.SpeechService
import com.example.home.ui.search.VoiceRecorder

class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private val _navigateToMap = MutableLiveData<MapModel>()
    val navigateToMap: LiveData<MapModel>
        get() = _navigateToMap

    private val _searchText = MutableLiveData<String>()
    val searchText: LiveData<String>
        get() = _searchText


    private val _searchVoiceAnimation = MutableLiveData<Boolean>()
    val searchVoiceAnimation: LiveData<Boolean>
        get() = _searchVoiceAnimation
    private var mSpeechService: SpeechService? = null

    private var mVoiceRecorder: VoiceRecorder? = null
    private val mServiceConnection: ServiceConnection = object :
        ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, binder: IBinder) {
            mSpeechService = SpeechService.from(binder)
            mSpeechService?.addListener(mSpeechServiceListener)
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            mSpeechService = null
        }
    }
    private val mVoiceCallback: VoiceRecorder.Callback = object : VoiceRecorder.Callback() {
        override fun onVoiceStart() {
            mSpeechService?.startRecognizing(mVoiceRecorder!!.sampleRate)
        }

        override fun onVoice(data: ByteArray?, size: Int) {
            mSpeechService?.recognize(data, size)

        }

        override fun onVoiceEnd() {
            mSpeechService?.finishRecognizing()
        }
    }

    init {
        // Prepare Cloud Speech API
        application.bindService(
            Intent(application, SpeechService::class.java),
            mServiceConnection,
            Context.BIND_AUTO_CREATE
        )
    }

    fun startNavigation(mapModel: MapModel) {
        _navigateToMap.value = mapModel
    }

    //to trigger live data that is being observed by ui and ui through data binding will play animation
    fun startSearchVoiceAnimation(typing: Boolean) {
        _searchVoiceAnimation.value = typing
    }

    //when the back ic_search_black_24dp pressed we will call this methode
    fun onBackDrawablePressed() {
        _navigateToMap.value = null
    }

    fun startVoiceRecorder() {
        if (mVoiceRecorder != null) {
            mVoiceRecorder?.stop()
        }
        Log.v("startingavOUCVD", "DONE")

        mVoiceRecorder = VoiceRecorder(mVoiceCallback)
        mVoiceRecorder?.start()
    }

    private fun stopVoiceRecorder() {
        Log.v("startingavOUCVD", "stop")
        mVoiceRecorder?.stop()
        mVoiceRecorder = null

    }

    private val mSpeechServiceListener: SpeechService.Listener =
        SpeechService.Listener { text, isFinal ->
            if (isFinal) {
                mVoiceRecorder!!.dismiss()
                stopVoiceRecorder()

            } else {
                _searchText.postValue(text)
            }
        }

    override fun onCleared() {
        // Stop Cloud Speech API
        mSpeechService!!.removeListener(mSpeechServiceListener)
        getApplication<Application>().unbindService(mServiceConnection)
        mSpeechService = null
        super.onCleared()
    }
}