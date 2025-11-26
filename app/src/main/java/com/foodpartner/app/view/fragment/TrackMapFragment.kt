package com.foodpartner.app.view.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.text.TextUtils
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.app.washeruser.repository.Status
import com.foodpartner.app.R
import com.foodpartner.app.ResponseMOdel.TrackorderRespossemodel
import com.foodpartner.app.view.responsemodel.LoginResponseModel
import com.foodpartner.app.view.responsemodel.OtpResponseModel
import com.foodpartner.app.baseClass.BaseFragment
import com.foodpartner.app.databinding.FragmentLoginBinding
import com.foodpartner.app.databinding.FragmentMapBinding

import com.foodpartner.app.view.activity.HomeActivity
import com.foodpartner.app.viewModel.HomeViewModel
import com.foodpartner.app.viewModel.LoginViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.google.firebase.database.core.Context
import com.mukesh.OtpView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.net.URL
import kotlin.apply
import kotlin.code
import kotlin.io.readText
import kotlin.ranges.until
import kotlin.text.format
import kotlin.toString

class TrackMapFragment: BaseFragment<FragmentMapBinding>(),OnMapReadyCallback {
    private val homeViewModel by viewModel<HomeViewModel>()
    private lateinit var googleMap: GoogleMap

    override fun initView(mViewDataBinding: ViewDataBinding?) {
        this.mViewDataBinding.apply {

            // ✅ Initialize SupportMapFragment
            val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
            mapFragment.getMapAsync(this@TrackMapFragment)

homeViewModel.trackorder(sharedHelper.getFromUser("userid"))

            homeViewModel.response().observe(viewLifecycleOwner, Observer {
                processResponse(it)
            })
        }
    }

    override fun getLayoutId(): Int = R.layout.fragment_map

    @SuppressLint("SetTextI18n")
    private fun processResponse(response: com.foodpartner.app.network.Response) {
        when (response.status) {
            Status.SUCCESS -> {
                when (response.data) {
                    is TrackorderRespossemodel -> {
                    }

                }
            }

            Status.ERROR -> {
            }

            Status.LOADING -> {}
            Status.SECONDLOADING -> {}
            Status.DISMISS -> {}
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // Enable zoom controls (optional)
        googleMap.uiSettings.isZoomControlsEnabled = true

        // Move camera to a location
        val delhi = LatLng(9.9179189, 78.0816239) //
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(delhi, 6f))

        fetchAndDrawRoute()

    }


    private fun fetchAndDrawRoute() {
        val url = "https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=${9.9179189},${78.0816239}&destination=27.1767,78.0081" +
                "&mode=driving&departure_time=now&traffic_model=best_guess" +
                "&key=AIzaSyCWQj746sQoI8ul4djjQjopk-YhISxmi9o"

        // Use coroutine/okhttp/retrofit to fetch data
        lifecycleScope.launch(Dispatchers.IO) {
            val json = URL(url).readText()
            val jsonObject = JSONObject(json)
            val routes = jsonObject.getJSONArray("routes")
            if (routes.length() > 0) {
                val points = routes.getJSONObject(0)
                    .getJSONObject("overview_polyline")
                    .getString("points")
                val decodedPoints = decodePolyline(points)

                withContext(Dispatchers.Main) {
                    drawRoute(decodedPoints)
                    val distanceKm = calculatePolylineDistance(decodedPoints)
//                    mViewDataBinding.kilometer.text="Kilometer :"+distanceKm+" km"
                    showToast("Route distance: %.2f km".format(distanceKm))
                }
            }
        }

    }
    private fun decodePolyline(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or ((b and 0x1f) shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if ((result and 1) != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or ((b and 0x1f) shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if ((result and 1) != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val latLng = LatLng(
                lat.toDouble() / 1E5,
                lng.toDouble() / 1E5
            )
            poly.add(latLng)
        }

        return poly
    }
    private fun drawRoute(points: List<LatLng>) {
        val polylineOptions = PolylineOptions()
            .addAll(points)
            .width(12f)
            .color(Color.BLUE)
            .geodesic(true)

        googleMap.addPolyline(polylineOptions)
    }
    private fun calculatePolylineDistance(points: List<LatLng>): Float {
        var distance = 0f
        for (i in 0 until points.size - 1) {
            val start = points[i]
            val end = points[i + 1]
            val results = FloatArray(1)
            Location.distanceBetween(
                start.latitude,
                start.longitude,
                end.latitude,
                end.longitude,
                results
            )
            distance += results[0] // distance in meters
        }
        return distance / 1000 // convert to kilometers
    }

}
