package com.example.home.ui.search

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.home.databinding.SearchLayoutAdapterBinding
import com.example.home.models.MapModel
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places.createClient
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest


data class PlaceDetails(val placeId:String,val name:String)

/**
 * this class is responsible for fetching the suggestions of places using places api
 */
class AutoCompleteSearch private constructor( context: Context) {
    //list of addresses to be returned
    private val stringList: ArrayList<PlaceDetails> = arrayListOf()
    private val token = AutocompleteSessionToken.newInstance()
    private val placesClient = createClient(context)


    companion object{
        fun init(context: Context): AutoCompleteSearch {
            return AutoCompleteSearch(context)
        }
    }
    fun fetchAutoCompleteSearchList(inputText: String,placesList:(list:List<PlaceDetails>)->Unit) {

        // Use the builder to create a FindAutocompletePredictionsRequest.
        val request = FindAutocompletePredictionsRequest.builder()
            // Call either setLocationBias() OR setLocationRestriction().
            //.setLocationBias(bounds)
            //.setLocationRestriction(bounds)
            .setCountry("egy")
            .setTypeFilter(TypeFilter.ADDRESS)
            .setSessionToken(token)
            .setQuery(inputText)
            .build()

        placesClient.findAutocompletePredictions(request).addOnSuccessListener {
            stringList.clear()
            for (prediction in it.autocompletePredictions) {

                stringList.add(
                    PlaceDetails(
                        prediction.placeId,
                        prediction.getFullText(null).toString()
                    )
                )

            }
            placesList(stringList)

        }.addOnFailureListener {
            if (it is ApiException) {
                val apiException = it
                Log.v("predicition", "Place not found: " + apiException.statusCode)
            }

        }

    }
        fun getPlaceLatLng(id: String,placeRequest:(latLng:LatLng)->Unit)
        {
            // Define a Place ID.

            // Specify the fields to return.
            val placeFields = listOf(Place.Field.LAT_LNG)

            // Construct a request object, passing the place ID and fields array.
            val request = FetchPlaceRequest.newInstance(id, placeFields)

            placesClient.fetchPlace(request).addOnSuccessListener { response ->
                val place = response.place
                place.latLng?.let{
                    placeRequest(it)

                }
            }.addOnFailureListener { exception ->
                if (exception is ApiException) {
                    val apiException = exception
                    val statusCode = apiException.statusCode
                    // Handle error with given status code.
                   // Log.e(TAG, "Place not found: " + exception.message)

                }
            }
        }

}


class AutoCompleteSearchAdapter(private val placesListener: PlacesListener) :
    ListAdapter<PlaceDetails, AutoCompleteSearchAdapter.ViewHolder>(
        DiffCallBack
    ) {
    private lateinit var context:Context

    /**
     *diff util class to calculate the difference between two list if the the old list has changed
     *with minimum changes it can do
     */
    object DiffCallBack : DiffUtil.ItemCallback<PlaceDetails>() {
        override fun areItemsTheSame(oldItem: PlaceDetails, newItem: PlaceDetails): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: PlaceDetails, newItem: PlaceDetails): Boolean {
            return oldItem.placeId == newItem.placeId
        }


    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.context=recyclerView.context
    }

    class ViewHolder(private val binding: SearchLayoutAdapterBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PlaceDetails, context: Context, placesListener: PlacesListener) {
            binding.stringAddress.text = item.name
            binding.stringAddress.setOnClickListener {
                /**
                 * when user click on the location we start getting its lat lng
                 */
                AutoCompleteSearch.init(context).getPlaceLatLng(item.placeId){
                    val mapModel= MapModel(it.latitude, it.longitude)
                    placesListener.onClick(mapModel)
                }

            }

        }




        /**
         * return the view that viewHolder will hold
         */
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = SearchLayoutAdapterBinding.inflate(inflater)
                return ViewHolder(binding)
            }
        }


    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(
            parent
        )
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position),context,placesListener)
    }


}
class PlacesListener(private val click:((mapModel: MapModel)->Unit)){
    fun onClick(mapModel: MapModel)= click(mapModel)

}