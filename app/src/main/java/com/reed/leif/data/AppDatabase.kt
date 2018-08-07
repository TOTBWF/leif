package com.reed.leif.data

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.reed.leif.util.DATABASE_NAME
import com.reed.leif.data.dao.LocationDao
import com.reed.leif.data.dao.SessionDao
import com.reed.leif.data.entity.Location
import com.reed.leif.data.entity.Session

@Database(entities = [Session::class, Location::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun sessionDao() : SessionDao
    abstract fun locationDao() : LocationDao


    companion object {
        @Volatile private var instance : AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME).build()
        }
    }
}