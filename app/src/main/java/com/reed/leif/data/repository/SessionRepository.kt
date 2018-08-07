package com.reed.leif.data.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.arch.persistence.room.ColumnInfo
import android.graphics.PointF
import com.reed.leif.data.dao.SessionDao
import com.reed.leif.data.entity.Session
import com.reed.leif.util.runOnIoThread

class SessionRepository private constructor(private val sessionDao: SessionDao) {

    data class CalibrationPoint(
            @ColumnInfo(name = "calibration_x") val calibrationX: Float,
            @ColumnInfo(name = "calibration_y") val calibrationY: Float
    )

    fun createSession(imagePath: String): Long {
        val session = Session(imagePath = imagePath)
        return sessionDao.insertSession(session)
    }

    fun getImagePath(sessionId: Long) = sessionDao.getImagePath(sessionId)

    fun setCalibrationPoint(sessionId: Long, p: PointF) =
            runOnIoThread {
                sessionDao.setCalibrationPoint(sessionId, p.x, p.y)
            }

    fun getCalibrationPoint(sessionId: Long): LiveData<PointF> {
        val src = sessionDao.getCalibrationPoint(sessionId)
        return Transformations.map(src) {
            PointF(it.calibrationX, it.calibrationY)
        }
    }


    companion object {
        @Volatile private var instance: SessionRepository? = null

        fun getInstance(sessionDao: SessionDao) =
            instance ?: synchronized(this) {
                instance ?: SessionRepository(sessionDao).also { instance = it }
            }
    }
}

