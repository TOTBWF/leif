package com.reed.leif.data.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "session")
data class Session(
        @ColumnInfo(name = "image_path") val imagePath: String,
        @ColumnInfo(name = "calibration_x") val calibrationX: Float? = null,
        @ColumnInfo(name = "calibration_y") val calibrationY: Float? = null
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var sessionId: Long = 0
}

