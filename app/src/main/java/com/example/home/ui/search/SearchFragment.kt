package com.example.home.ui.search


import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.example.home.PLACE_DETAILS
import com.example.home.R
import com.example.home.databinding.FragmentSearchBinding
import com.example.home.hideKeyBoard
import com.example.home.service.SpeechService
import com.example.home.viewmodel.SearchViewModel
import com.google.android.libraries.places.api.Places

/**
 * A simple [Fragment] subclass.
 */

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    lateinit var autoCompleteSearchAdapter: AutoCompleteSearchAdapter
    private val searchViewModel by viewModels<SearchViewModel>()
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
        return binding.root
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
                searchViewModel.startSearchVoiceAnimation(!p0.isNullOrEmpty())
                //execute our method for searching
                autoCompleteSearch.fetchAutoCompleteSearchList(p0.toString()) {
                    if (it.isNullOrEmpty()) {
                        binding.listSearch.visibility = View.GONE
                    } else {
                        binding.listSearch.visibility = View.VISIBLE

                    }
                    autoCompleteSearchAdapter.submitList(it)
                }

            }
        })
    }

    private fun initRecyclerView() {
        autoCompleteSearchAdapter =
            AutoCompleteSearchAdapter(PlacesListener {
                searchViewModel.startNavigation(it)

            })
        binding.listSearch.adapter = autoCompleteSearchAdapter


    }



}
