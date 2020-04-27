package com.example.findmygolda.branches

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

import com.example.findmygolda.R
import com.example.findmygolda.database.AlertDatabase
import com.example.findmygolda.databinding.FragmentAlertsBinding
import com.example.findmygolda.databinding.FragmentBranchesBinding
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.android.synthetic.main.fragment_branches.*


class BranchesFragment : Fragment() {
    private lateinit var banchViewModel : BranchesViewModel
    private  lateinit var adapter: BranchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentBranchesBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_branches, container, false)

        val application = requireNotNull(this.activity).application

        val dataSource = (AlertDatabase.getInstance(application)).branchDatabaseDAO

        val viewModelFactory = BranchViewModelFactorty(dataSource, application)

        banchViewModel =
            ViewModelProviders.of(
                this, viewModelFactory).get(BranchesViewModel::class.java)

        binding.setLifecycleOwner(this)
        //binding.alertsViewModel = alertsTrackerViewModel

        adapter =BranchAdapter(BranchAdapter.BranchClickListener { nightId ->
            Toast.makeText(context, "${nightId}", Toast.LENGTH_LONG).show()
        })
        binding.branchesList.adapter = adapter

        val chipGroup = binding.chipGroup
        setLisetenerOnChips(chipGroup)

        banchViewModel.branches.observe(viewLifecycleOwner, Observer {
            it?.let {
               // binding.isThereNotifications = it.isNotEmpty()
                adapter.data = it
            }
        })


        return binding.root
    }

    private fun setLisetenerOnChips(chipGroup: ChipGroup) {
        for (index in 0 until chipGroup.childCount) {
            val chip: Chip = chipGroup.getChildAt(index) as Chip
            // Set the chip checked change listener
            chip.setOnCheckedChangeListener { view, isChecked ->
                if (isChecked) {
                    Toast.makeText(context, view.text.toString(), Toast.LENGTH_LONG).show()
                }
            }
        }
    }


}
