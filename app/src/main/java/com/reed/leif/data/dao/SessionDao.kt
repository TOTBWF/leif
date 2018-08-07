package com.reed.leif.data.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
import com.reed.leif.data.entity.Session
import com.reed.leif.data.repository.SessionRepository

@Dao
interface SessionDao {
    @Query("SELECT image_path FROM session WHERE id = :sessionId")
    fun getImagePath(sessionId: Long): LiveData<String>

    @Insert
    fun insertSession(session: Session): Long

    @Query("UPDATE session SET calibration_x = :x, calibration_y = :y WHERE id = :sessionId")
    fun setCalibrationPoint(sessionId: Long, x: Float, y:Float)

    @Query("SELECT calibration_x, calibration_y FROM session WHERE id = :sessionId")
    fun getCalibrationPoint(sessionId: Long): LiveData<SessionRepository.CalibrationPoint>
}