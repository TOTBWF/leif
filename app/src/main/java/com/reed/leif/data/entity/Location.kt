package com.reed.leif.data.entity

import android.arch.persistence.room.*

@Entity(
        tableName = "location",
        foreignKeys = [ForeignKey(entity = Session::class, parentColumns = ["id"], childColumns = ["session_id"])],
        indices = [Index("session_id")])
data class Location(
    @ColumnInfo(name = "session_id") var sessionId : Long,
    val latitude: Double,
    val longitude: Double
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var locationId: Long = 0
}