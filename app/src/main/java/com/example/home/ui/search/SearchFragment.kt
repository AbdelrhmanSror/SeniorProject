package com.example.home.ui.search


import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.example.home.PLACE_DETAILS
import com.example.home.R
import com.example.home.databinding.FragmentSearchBinding
import com.example.home.extensions.hideKeyBoard
import com.example.home.models.MapModel
import com.example.home.viewmodel.SearchViewModel
import com.google.android.libraries.places.api.Places
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.*


/**
 * A simple [Fragment] subclass.
 */

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    lateinit var autoCompleteSearchAdapter: AutoCompleteSearchAdapter
    private val recog = 1
    private lateinit var mapModel: MapModel
    private val searchViewModel by viewModels<SearchViewModel>()
    private lateinit var mTTS: TextToSpeech
    private var done = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        requireActivity().actionBar?.hide()
        initViewModel()
        initRecyclerView()
        initAutoCompleteSearch()
        initTextToSpeech()
        return binding.root
    }


    private fun initTextToSpeech() {
        mTTS = TextToSpeech(requireContext(), TextToSpeech.OnInitListener { status ->
            if (status == TextToSpeech.SUCCESS) {

                val result = mTTS.setLanguage(Locale.ENGLISH)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "Lang NOt SUPP")
                    mTTS.language = Locale.ENGLISH
                }

            }
        })
        mTTS.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onDone(utteranceId: String) {
                Log.v("Voiceddbj", "done $mapModel")
                if (!done) {
                    // Get a handler that can be used to post to the main thread
                    val mainHandler = Handler(requireActivity().getMainLooper())

                    val myRunnable = Runnable {
                        MaterialAlertDialogBuilder(requireContext())
                            .setTitle("Confirmation")
                            .setMessage(getString(R.string.locationConfirmation))
                            .setPositiveButton("Accept") { _, _ ->
                                searchViewModel.startNavigation(mapModel)
                            }.setNegativeButton("Decline")
                            { dialog, _ -> dialog.cancel() }.show()
                        done = true
                        speak(getString(R.string.locationConfirmation))
                    }
                    mainHandler.post(myRunnable)
                }


            }

            override fun onError(utteranceId: String) {
                Log.v("Voiceddbj", "error")

            }

            override fun onStart(utteranceId: String) {
                Log.v("Voiceddbj", "start")

            }
        })
    }

    private fun initViewModel() {
        binding.searchViewModel = searchViewModel
        binding.lifecycleOwner = this
        searchViewModel.navigateToMap.observe(viewLifecycleOwner, Observer {
            //hiding the keyboard after pressing on location to search
            requireActivity().hideKeyBoard()
            view?.findNavController()?.navigate(
                R.id.action_searchFragment_to_navMapFragment,
                bundleOf(PLACE_DETAILS to it)
            )
        })
        searchViewModel.record.observe(viewLifecycleOwner, Observer {
            if (it != null && it) {
                val speechIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                speechIntent.putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )
                speechIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "speech to text")
                startActivityForResult(speechIntent, recog)

            }

        })


    }

    private fun initAutoCompleteSearch() {
        Places.initialize(requireActivity().applicationContext, getString(R.string.PlaceApiKey))
        val autoCompleteSearch =
            AutoCompleteSearch.init(requireActivity().applicationContext)

        binding.inputEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // searchViewModel.startSearchVoiceAnimation(!p0.isNullOrEmpty())
                //execute our method for searching
                autoCompleteSearch.fetchAutoCompleteSearchList(p0.toString()) {
                    if (it.isNullOrEmpty()) {
                        binding.listSearch.visibility = View.GONE
                    } else {
                        binding.listSearch.visibility = View.VISIBLE

                    }
                    autoCompleteSearchAdapter.submitList(null)
                    autoCompleteSearchAdapter.submitList(it)
                }

            }
        })
    }

    private fun initRecyclerView() {
        autoCompleteSearchAdapter =
            AutoCompleteSearchAdapter(PlacesListener { s: String, mapModel: MapModel ->
                this.mapModel = mapModel
                speak(s)

            })
        binding.listSearch.layoutManager = WrapContentLinearLayoutManager(requireContext())

        binding.listSearch.adapter = autoCompleteSearchAdapter


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == recog && resultCode == RESULT_OK) {
            val matches = data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            binding.inputEditText.setText(matches[0]!!)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun speak(text: String) // transform text to string and playing  use Que style
    {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            mTTS.speak(
                text,
                TextToSpeech.QUEUE_FLUSH,
                null,
                TextToSpeech.ACTION_TTS_QUEUE_PROCESSING_COMPLETED
            );
        else
            mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null)
    }

    // to stop the speaking
    override fun onDestroy() {
        mTTS.stop()
        mTTS.shutdown()
        super.onDestroy()
    }
}

class WrapContentLinearLayoutManager(context: Context) : LinearLayoutManager(context) {
    //... constructor
    override fun onLayoutChildren(
        recycler: Recycler,
        state: RecyclerView.State
    ) {
        try {
            super.onLayoutChildren(recycler, state)
        } catch (e: IndexOutOfBoundsException) {
        }
    }
}