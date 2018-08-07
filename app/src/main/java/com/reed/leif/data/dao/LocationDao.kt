package com.reed.leif.data.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.reed.leif.data.entity.Location

@Dao
interface LocationDao {

    @Query("SELECT * FROM location WHERE session_id = :sessionId")
    fun getLocations(sessionId: Long): LiveData<List<Location>>

    @Insert
    fun insertLocation(l: Location)
}