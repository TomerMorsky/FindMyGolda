package com.example.findmygolda

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.findmygolda.databinding.ActivityMainBinding
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import org.jetbrains.anko.alert
import org.jetbrains.anko.yesButton

class MainActivity : AppCompatActivity(), PermissionsListener {
    lateinit var permissionManager: PermissionsManager
    lateinit var binding: ActivityMainBinding
    var locationServices : com.example.findmygolda.location.LocationManager? = null
        get() = locationServices

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
            locationServices = com.example.findmygolda.location.LocationManager(application)
            setupNavigation()
        } else {
            // If there is no permissions ask for them
            permissionManager = PermissionsManager(this)
            permissionManager.requestLocationPermissions(this)
        }

        if (!isLocationEnabled(applicationContext)) {
            showLocationIsDisabledAlert()
        }

    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
        Toast.makeText(applicationContext,
            "This app needs location permission to be able to show your location on the map",
            Toast.LENGTH_SHORT).show()
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            setContentView(R.layout.activity_main)
        } else {
            finish()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<out String>,
                                            grantResults: IntArray) {
        permissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    /**
     * Called when the hamburger menu or back button are pressed on the Toolbar
     *
     * Delegate this to Navigation.
     */
    override fun onSupportNavigateUp() = navigateUp(findNavController(R.id.myNavHostFragment), binding.drawerLayout)

    /**
     * Setup Navigation for this Activity
     */
    private fun setupNavigation() {
        // first find the nav controller
        val navController = findNavController(R.id.myNavHostFragment)
        setSupportActionBar(binding.toolbar)

        // then setup the action bar, tell it about the DrawerLayout
        setupActionBarWithNavController(navController, binding.drawerLayout)

        // finally setup the left drawer (called a NavigationView)
        binding.navigationView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination: NavDestination, _ ->
            val toolBar = supportActionBar ?: return@addOnDestinationChangedListener
            toolBar.setDisplayShowTitleEnabled(false)
            binding.heroImage.visibility = View.VISIBLE
        }
    }
     fun isLocationEnabled(mContext: Context): Boolean {
        val lm = mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER) || lm.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER)
    }

    private fun showLocationIsDisabledAlert() {
        alert("We can't show your position because you disabled the location service for your device.") {
            yesButton {
                finish()
                moveTaskToBack(true)
                android.os.Process.killProcess(android.os.Process.myPid())
                System.exit(1)
            }
            neutralPressed("Settings") {
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
        }.show()
    }
}
