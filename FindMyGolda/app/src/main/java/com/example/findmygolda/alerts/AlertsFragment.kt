package com.example.findmygolda.alerts

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.findmygolda.R
import com.example.findmygolda.database.AlertDatabase
import com.example.findmygolda.databinding.FragmentAlertsBinding

class AlertsFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentAlertsBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_alerts, container, false)

        val application = requireNotNull(this.activity).application

        val dataSource = (AlertDatabase.getInstance(application)).alertDatabaseDAO

        val viewModelFactory = AlertViewModelFactory(dataSource, application)

        val alertsTrackerViewModel =
            ViewModelProviders.of(
                this, viewModelFactory).get(AlertsViewModel::class.java)

        binding.setLifecycleOwner(this)
        binding.alertsViewModel = alertsTrackerViewModel

        val adapter = AlertAdapter()
        binding.alertsList.adapter = adapter

        alertsTrackerViewModel.alerts.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.isThereNotifications = it.isNotEmpty()
                adapter.data = it
            }
        })

        return binding.root

    }

}
