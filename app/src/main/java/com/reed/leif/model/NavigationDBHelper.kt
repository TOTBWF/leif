package com.reed.leif.model

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.PointF
import android.location.Location
import android.provider.BaseColumns
import com.reed.leif.model.SessionContract.SessionEntry
import com.reed.leif.model.LocationContract.LocationEntry



class NavigationDBHelper(context : Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SessionEntry.SQL_CREATE_TABLE)
        db.execSQL(LocationEntry.SQL_CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(LocationEntry.SQL_DELETE_TABLE)
        db.execSQL(SessionEntry.SQL_DELETE_TABLE)
        onCreate(db)
    }

    fun createSession(imagePath : String) : Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(SessionEntry.COLUMN_IMAGE_PATH, imagePath)
            put(SessionEntry.COLUMN_DATE, System.currentTimeMillis() / 1000)
        }
        return db.insert(SessionEntry.TABLE_NAME, null, values)
    }

    fun getPhotoPath(sessionId: Long) : String {
        val db = readableDatabase
        val projection = arrayOf(SessionEntry.COLUMN_IMAGE_PATH)
        val selection = "${BaseColumns._ID} = ?"
        val selectionArgs = arrayOf(sessionId.toString())
        val cursor = db.query(
                SessionEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        )
        return with (cursor) {
            moveToFirst()
            getString(getColumnIndex(SessionEntry.COLUMN_IMAGE_PATH))
        }
    }

    fun setCalibrationPosition(sessionId : Long, p : PointF) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(SessionEntry.COLUMN_CALIBRATION_X, p.x)
            put(SessionEntry.COLUMN_CALIBRATION_Y, p.y)
        }
        val selection = "${BaseColumns._ID} = ?"
        db.update(SessionEntry.TABLE_NAME, values, selection, arrayOf(sessionId.toString()))
    }

    fun getCalibrationPosition(sessionId: Long) : PointF {
        val db = readableDatabase
        val projection = arrayOf(SessionEntry.COLUMN_CALIBRATION_X, SessionEntry.COLUMN_CALIBRATION_Y)
        val selection = "${BaseColumns._ID} = ?"
        val selectionArgs = arrayOf(sessionId.toString())
        val cursor = db.query(
                SessionEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        )
        return with(cursor) {
            moveToFirst()
            val x = getFloat(getColumnIndex(SessionEntry.COLUMN_CALIBRATION_X))
            val y = getFloat(getColumnIndex(SessionEntry.COLUMN_CALIBRATION_Y))
            PointF(x, y)
        }
    }

    fun insertLocation(sessionId : Long, l : Location) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(LocationEntry.COLUMN_SESSION_ID, sessionId)
            put(LocationEntry.COLUMN_LATITUDE, l.latitude)
            put(LocationEntry.COLUMN_LONGITUDE, l.longitude)
            put(LocationEntry.COLUMN_TIME, l.time)
        }
        db.insert(LocationEntry.TABLE_NAME, null, values)
    }

    companion object {
        private const val DATABASE_NAME = "navigation_db"
        private const val DATABASE_VERSION = 1


    }

}