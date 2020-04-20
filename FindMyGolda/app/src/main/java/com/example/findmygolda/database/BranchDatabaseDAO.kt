package com.example.findmygolda.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BranchDatabaseDAO {

    @Query("select * from branches")
    fun getBranches(): LiveData<List<BranchEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert( videos: List<BranchEntity>)
}