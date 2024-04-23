package com.example.flightsearch.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Airport::class, Favorite::class], version = 1, exportSchema = false)
abstract class AirportLocalDataSource : RoomDatabase() {
    abstract fun dao(): AirportDao

    companion object {
        @Volatile
        private var instance: AirportLocalDataSource? = null

        fun getDatabase(context: Context): AirportLocalDataSource {
            return instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context = context,
                    klass = AirportLocalDataSource::class.java,
                    name = "airport_database"
                )
                    .createFromAsset("database/flight_search.db")
                    .fallbackToDestructiveMigrationOnDowngrade()
                    .build()
                    .also { instance = it }
            }
        }
    }
}