package com.reed.leif.model

import android.content.ContentValues
import android.location.Location
import android.provider.BaseColumns
import com.reed.leif.model.LocationContract.LocationEntry
import com.reed.leif.model.SessionContract.SessionEntry


class LocationContract {

    object LocationEntry {
        const val TABLE_NAME = "location"
        const val COLUMN_SESSION_ID = "session_id"
        const val COLUMN_LATITUDE = "latitude"
        const val COLUMN_LONGITUDE = "longitude"
        const val COLUMN_TIME = "time"

        const val SQL_CREATE_TABLE =
                "CREATE TABLE ${LocationEntry.TABLE_NAME} (" +
                        "${LocationEntry.COLUMN_SESSION_ID} INTEGER," +
                        "${LocationEntry.COLUMN_LATITUDE} DOUBLE," +
                        "${LocationEntry.COLUMN_LONGITUDE} DOUBLE," +
                        "${LocationEntry.COLUMN_TIME} INTEGER," +
                        "FOREIGN KEY (${LocationEntry.COLUMN_SESSION_ID}) REFERENCES ${SessionEntry.TABLE_NAME}(${BaseColumns._ID}))"


        const val SQL_DELETE_TABLE = "DROP TABLE IF EXISTS ${LocationEntry.TABLE_NAME}"
    }

}