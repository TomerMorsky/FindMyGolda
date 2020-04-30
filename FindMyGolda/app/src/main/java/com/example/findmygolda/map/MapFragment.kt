package com.example.findmygolda.map

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment
import androidx.preference.PreferenceManager
import com.example.findmygolda.MainActivity
import com.example.findmygolda.R
import com.example.findmygolda.database.BranchEntity
import com.example.findmygolda.databinding.FragmentMapBinding
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.*
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponent
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback

class MapFragment : Fragment(), OnMapReadyCallback {
    lateinit var mapView: MapView
    lateinit var mapViewModel: MapViewModel

    val REQUEST_CHECK_SETTINGS = 1
    var settingsClient: SettingsClient? = null

    lateinit var map: MapboxMap

    var locationComponent: LocationComponent? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                enableLocation()
            } else if (resultCode == Activity.RESULT_CANCELED) {
                activity?.finish()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val activity = activity as Context
        Mapbox.getInstance(activity, getString(R.string.mapbox_access_token))

        val application = requireNotNull(this.activity).application

        val preferences = PreferenceManager.getDefaultSharedPreferences(application)
        val maxDistanceFromBranch = preferences.getInt("radiusFromBranch", 5).times(100)
        val minTimeBetweenAlerts = parseMinutesToMilliseconds(preferences.getInt("timeBetweenNotifications", 1).times(5))

        val viewModelFactory = MapViewModelFactory(application, maxDistanceFromBranch, minTimeBetweenAlerts)

        mapViewModel =
            ViewModelProviders.of(
                this, viewModelFactory).get(MapViewModel::class.java)

        val binding = DataBindingUtil.inflate<FragmentMapBinding>(inflater,
            R.layout.fragment_map,container,false)
        mapView = binding.mapView
        binding.viewModel = mapViewModel

        mapView.onCreate(savedInstanceState)

        mapViewModel.focusOnUserLocation.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                mapViewModel.currentLocation()?.let { it1 -> setCameraPosition(it1) }
               // mapViewModel.currentLocation?.let { it1 -> setCameraPosition(it1) }
                mapViewModel.doneFocusOnUserLocation()
            }
        })

        mapViewModel.navigateToAlertsFragment.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                NavHostFragment.findNavController(this)
                    .navigate(R.id.action_mapFragment_to_alertsFragment)
                mapViewModel.doneNavigateToAlertsFragment()
            }
        })

        mapView.getMapAsync(this)
        settingsClient = LocationServices.getSettingsClient(this.activity!!)

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        // Check if the user dose not turn off the GPS
        //val jjjjjj = context?.let { (activity as MainActivity).isLocationEnabled(it) }
        if (PermissionsManager.areLocationPermissionsGranted(activity)) {
            locationComponent?.onStart()
        }
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        locationComponent?.onStop()
        mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onMapReady(mapboxMap: MapboxMap?) {
        map = mapboxMap ?: return
        mapViewModel.branches.observe(viewLifecycleOwner, Observer { branches ->
            for (branch in branches) {
                addGoldaMarker(branch)
            }
        })

        val locationRequestBuilder = LocationSettingsRequest.Builder().addLocationRequest(
            LocationRequest()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY))

        val locationRequest = locationRequestBuilder?.build()

        settingsClient?.checkLocationSettings(locationRequest)?.run {
            addOnSuccessListener {
                enableLocation()
            }

            addOnFailureListener {
                val statusCode = (it as ApiException).statusCode

                if (statusCode == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                     //responsible for gps to turn on
//                    val resolvableException = it as? ResolvableApiException
//                    startIntentSenderForResult(resolvableException?.getResolution()?.getIntentSender(),
//                        REQUEST_CHECK_SETTINGS, null, 0, 0, 0, null)
                }
            }
        }
    }

    fun enableLocation() {
        if (PermissionsManager.areLocationPermissionsGranted(activity)) {
            // Show the user location on map
           initializeLocationComponent()
        }
    }

    @SuppressWarnings("MissingPermission")
    fun initializeLocationComponent() {
        locationComponent = map.locationComponent
        locationComponent?.activateLocationComponent(activity!!)
        locationComponent?.isLocationComponentEnabled = true
        locationComponent?.cameraMode = CameraMode.TRACKING
    }

    fun setCameraPosition(location: Location) {
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(location.latitude,
            location.longitude), 15.0))
    }

    fun addGoldaMarker(branch: BranchEntity){
        val point = LatLng(branch.latitude, branch.longtitude)
        map.addMarker(MarkerOptions().setTitle(branch.name).setSnippet(branch.address).position(point))
    }

    fun parseMinutesToMilliseconds(minutes : Int) : Long{
        return (minutes * 60000).toLong()
    }
}
