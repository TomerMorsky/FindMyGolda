package com.example.findmygolda

import android.os.Bundle
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

class MainActivity : AppCompatActivity(), PermissionsListener {
    lateinit var permissionManager: PermissionsManager
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
            setupNavigation()
        } else {
            // If there is no permissions ask for them
            permissionManager = PermissionsManager(this)
            permissionManager.requestLocationPermissions(this)
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
}
