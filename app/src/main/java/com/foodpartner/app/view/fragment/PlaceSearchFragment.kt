package com.foodpartner.app.view.fragment

import android.app.Activity
import android.app.DatePickerDialog
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.app.washeruser.repository.Status
import com.bumptech.glide.Glide
import com.foodpartner.app.R
import com.foodpartner.app.baseClass.BaseFragment
import com.foodpartner.app.databinding.FragmentPlacesearchBinding
import com.foodpartner.app.view.requestmodel.LocationEvent

import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import org.greenrobot.eventbus.EventBus
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Calendar
import java.util.Timer
import kotlin.collections.map
import kotlin.concurrent.schedule
import kotlin.text.isNullOrBlank

class PlaceSearchFragment : BaseFragment<FragmentPlacesearchBinding>() {
    private lateinit var placesClient: PlacesClient
    private lateinit var etSearch: EditText
    private lateinit var lvResults: ListView

    private val predictions = mutableListOf<AutocompletePrediction>()
    private lateinit var adapter: ArrayAdapter<String>
    override fun initView(mViewDataBinding: ViewDataBinding?) {

        this.mViewDataBinding.backBtn.setOnClickListener {
            fragmentManagers!!.popBackStackImmediate()
        }


        if (!Places.isInitialized()) {
            Places.initialize(
               requireContext().applicationContext,
                getString(R.string.google_maps_key)
            )
        }
        placesClient = Places.createClient(requireContext())

        etSearch = this.mViewDataBinding.etSearchPlace
        lvResults = this.mViewDataBinding.lvPlaceResults

        // Simple ArrayAdapter to show predictions
        adapter = ArrayAdapter(
        requireContext(), android.R.layout.simple_list_item_1,
            mutableListOf()
        )
        lvResults.adapter = adapter

        // Watch for text changes
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(query: CharSequence?, start: Int, before: Int, count: Int) {
                if (!query.isNullOrBlank()) {
                    fetchPredictions(query.toString())
                } else {
                    adapter.clear()
                }
            }
        })

        // Handle click on a prediction
        lvResults.setOnItemClickListener { _, _, position, _ ->
            val prediction = predictions[position]
            fetchPlaceDetails(prediction.placeId)
        }
    }

    private fun fetchPredictions(query: String) {
        val token = AutocompleteSessionToken.newInstance()

        val request = FindAutocompletePredictionsRequest.builder()
            .setQuery(query)
            .setSessionToken(token)
            .build()

        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response ->
                predictions.clear()
                predictions.addAll(response.autocompletePredictions)

                val placeNames = predictions.map { it.getFullText(null).toString() }
                adapter.clear()
                adapter.addAll(placeNames)
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.e("CustomPlaceFragment", "Prediction fetch failed: ${exception.message}")
            }
    }

    private fun fetchPlaceDetails(placeId: String) {
        val placeFields =
            listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)

        val request = FetchPlaceRequest.builder(placeId, placeFields).build()

        placesClient.fetchPlace(request)
            .addOnSuccessListener { response ->
                val place = response.place
                Toast.makeText(requireContext(), "Selected: ${place.name}\n${place.address},\n${place.latLng}", Toast.LENGTH_LONG).show()
                fragmentManagers!!.popBackStackImmediate()
                EventBus.getDefault().postSticky(LocationEvent(place.address,place.latLng.toString()))

                Log.i("CustomPlaceFragment", "Place details: $place")
            }
            .addOnFailureListener { e ->
                Log.e("CustomPlaceFragment", "Place details fetch failed: ${e.message}")
            }
    }

    override fun getLayoutId(): Int =R.layout.fragment_placesearch


}