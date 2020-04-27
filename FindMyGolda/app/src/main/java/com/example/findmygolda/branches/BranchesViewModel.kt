package com.example.findmygolda.branches

import android.app.Application
import android.location.Location
import android.view.View
import androidx.lifecycle.AndroidViewModel
import com.example.findmygolda.database.BranchDatabaseDAO
import com.example.findmygolda.database.BranchEntity

class BranchesViewModel(val database: BranchDatabaseDAO,
                        application: Application
): AndroidViewModel(application) {
    val branches = database.getBranches()

//    fun chipPicked(chip: View){
//        if (chip.tag as String == "ABC") {
//            branches.value = branches.value?.sortAtoZ()
//        }
//
//        if (chip.tag as String == "Location") {
//            branches.value = branches.value?.sortByLocation()
//        }
//    }


//    private fun List<BranchEntity>?.sortByLocation(): List<BranchEntity>? =
//        this?.sortedBy {
//            location.distanceTo(branchLocation(it))
//        }

    private fun List<BranchEntity>.sortAtoZ(): List<BranchEntity>? =
        this.sortedBy { it.name }

    private fun branchLocation(branch: BranchEntity): Location {
        val branchLocation = Location("branchLocation")
        branchLocation.longitude = branch.longtitude
        branchLocation.latitude = branch.latitude
        return branchLocation
    }
}