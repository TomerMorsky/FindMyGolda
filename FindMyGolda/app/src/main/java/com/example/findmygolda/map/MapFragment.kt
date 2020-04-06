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
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.databinding.Observable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController

import com.example.findmygolda.R
import com.example.findmygolda.databinding.FragmentMapBinding
import com.example.findmygolda.network.BranchProperty
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*

import com.mapbox.android.core.location.LocationEngine
import com.mapbox.android.core.location.LocationEngineListener
import com.mapbox.android.core.location.LocationEnginePriority
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.android.core.permissions.PermissionsListener
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
import kotlin.properties.Delegates.observable
import kotlin.reflect.KProperty


class MapFragment : Fragment(), PermissionsListener, LocationEngineListener, OnMapReadyCallback, MapboxMap.OnMapClickListener {
    lateinit var mapView: MapView
    lateinit var mapViewModel: MapViewModel


    val REQUEST_CHECK_SETTINGS = 1
    var settingsClient: SettingsClient? = null

    lateinit var map: MapboxMap
    lateinit var permissionManager: PermissionsManager

    var originLocation: Location? = null

    var locationEngine: LocationEngine? = null
    var locationComponent: LocationComponent? = null

    // For listen on every change in user location
    var locaion: Location by observable(Location("")) { _, oldValue, newValue ->
        onTitleChanged?.invoke(oldValue, newValue)
    }

    var onTitleChanged: ((Location, Location) -> Unit)? = null

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val activity = activity as Context
        Mapbox.getInstance(activity, getString(R.string.mapbox_access_token));
        mapViewModel = ViewModelProviders.of(this).get(MapViewModel::class.java)
        // Inflate the layout for this fragment
        val binding = DataBindingUtil.inflate<FragmentMapBinding>(inflater,
            R.layout.fragment_map,container,false)
        mapView = binding.mapView
        binding.viewModel = mapViewModel

        mapView.onCreate(savedInstanceState)

        // Wait for response with the branches list


        mapViewModel.focusOnUserLocation.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                originLocation?.let { it1 -> setCameraPosition(it1) }
                mapViewModel.doneFocusOnUserLocation()
            }
        })

        mapViewModel.navigateToAlertsFragment.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                this.findNavController()
                    .navigate(R.id.action_mapFragment_to_alertsFragment)
                mapViewModel.doneNavigateToAlertsFragment()
            }
        })

        mapView.getMapAsync(this)
        settingsClient = LocationServices.getSettingsClient(activity)

        //getGoldaBranches()


        return binding.root
    }

    override fun onStart() {
        super.onStart()
        //mapViewModel.getGoldaBranches()
        if (PermissionsManager.areLocationPermissionsGranted(activity)) {
            locationEngine?.requestLocationUpdates()
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
        locationEngine?.removeLocationUpdates()
        locationComponent?.onStop()
        mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        locationEngine?.deactivate()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
        Toast.makeText(Mapbox.getApplicationContext(),
            "This app needs location permission to be able to show your location on the map",
            Toast.LENGTH_LONG).show()
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            enableLocation()
        } else {
            Toast.makeText(Mapbox.getApplicationContext(), "User location was not granted", Toast.LENGTH_LONG).show()
            activity?.finish()
        }
    }

    override fun onLocationChanged(location: Location?) {
        location?.run {
            originLocation = this
            locaion = this
            //_liveLocation.value = this
            Toast.makeText(Mapbox.getApplicationContext(), "Location change", Toast.LENGTH_LONG).show()
            //setCameraPosition(this)
        }
    }

    override fun onConnected() {
        locationEngine?.requestLocationUpdates()
    }

    override fun onMapReady(mapboxMap: MapboxMap?) {

        //1
        map = mapboxMap ?: return

        mapViewModel.branches.observe(viewLifecycleOwner, Observer { branches ->
            for (branch in branches) {
                addGoldaMarker(branch)
            }
            //Toast.makeText(Mapbox.getApplicationContext(),branches[0].id, Toast.LENGTH_SHORT).show()
        })
//2
        val locationRequestBuilder = LocationSettingsRequest.Builder().addLocationRequest(
            LocationRequest()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY))
//3
        val locationRequest = locationRequestBuilder?.build()

        settingsClient?.checkLocationSettings(locationRequest)?.run {
            addOnSuccessListener {
                enableLocation()
            }

            addOnFailureListener {
                val statusCode = (it as ApiException).statusCode

                if (statusCode == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                    val resolvableException = it as? ResolvableApiException
                    startIntentSenderForResult(resolvableException?.getResolution()?.getIntentSender(),
                        REQUEST_CHECK_SETTINGS, null, 0, 0, 0, null)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<out String>,
                                            grantResults: IntArray) {
        permissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    //1
    fun enableLocation() {
        if (PermissionsManager.areLocationPermissionsGranted(activity)) {
            // Show the user location on map
            initializeLocationComponent()
            initializeLocationEngine()
            map.addOnMapClickListener(this)
        } else {
            // If there is no permissions ask for them
            permissionManager = PermissionsManager(this)
            permissionManager.requestLocationPermissions(activity)
        }
    }
    //2
    @SuppressWarnings("MissingPermission")
    fun initializeLocationEngine() {
        locationEngine = LocationEngineProvider(activity).obtainBestLocationEngineAvailable()// Get the location
        locationEngine?.priority = LocationEnginePriority.HIGH_ACCURACY
        locationEngine?.activate()
        locationEngine?.addLocationEngineListener(this)

        val lastLocation = locationEngine?.lastLocation
        if (lastLocation != null) {
            originLocation = lastLocation
            setCameraPosition(lastLocation)
        } else {
            locationEngine?.addLocationEngineListener(this)
        }

    }

    @SuppressWarnings("MissingPermission")
    fun initializeLocationComponent() {
        locationComponent = map.locationComponent
        locationComponent?.activateLocationComponent(activity!!)
        locationComponent?.isLocationComponentEnabled = true
        locationComponent?.cameraMode = CameraMode.TRACKING
    }

    //3
    fun setCameraPosition(location: Location) {
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(location.latitude,
            location.longitude), 15.0))
    }

    override fun onMapClick(point: LatLng) {
        //map.addOnMapClickListener(this)
        //map.addMarker(MarkerOptions().position(point))
    }

    fun addGoldaMarker(branch: BranchProperty){
        val point = LatLng(branch.latitude, branch.longtitude)
        map.addMarker(MarkerOptions().setTitle(branch.name).setSnippet(branch.address).position(point))
    }
}
