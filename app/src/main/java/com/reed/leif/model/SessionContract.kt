package com.reed.leif.model

import android.content.ContentValues
import android.graphics.PointF
import android.provider.BaseColumns
import android.se.omapi.Session
import com.reed.leif.model.SessionContract.SessionEntry
import java.util.*


class SessionContract {
    object SessionEntry : BaseColumns {
        const val TABLE_NAME = "session"
        const val COLUMN_IMAGE_PATH = "image_path"
        const val COLUMN_DATE = "date"
        const val COLUMN_CALIBRATION_X = "calibration_x"
        const val COLUMN_CALIBRATION_Y = "calibration_y"

        const val SQL_CREATE_TABLE =
                "CREATE TABLE ${SessionEntry.TABLE_NAME} (" +
                        "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                        "${SessionEntry.COLUMN_IMAGE_PATH} TEXT," +
                        "${SessionEntry.COLUMN_DATE} TEXT," +
                        "${SessionEntry.COLUMN_CALIBRATION_X} FLOAT," +
                        "${SessionEntry.COLUMN_CALIBRATION_Y} FLOAT)"

        const val SQL_DELETE_TABLE = "DROP TABLE IF EXISTS ${SessionEntry.TABLE_NAME}"
    }
}