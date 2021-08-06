package com.example.myfriends.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.myfriends.FriendDao
import com.example.myfriends.FriendModel


@Database(
    entities = [FriendModel::class],
    version = 1
)

abstract class FriendDatabase: RoomDatabase() {

    abstract fun friendDao(): FriendDao

    companion object {

        @Volatile private var instance: FriendDatabase? = null
        private var LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance?: buildDatabase(context).also {

            }
        }

        private fun buildDatabase(context: Context)= Room.databaseBuilder(
            context.applicationContext,
            FriendDatabase::class.java,
            "FriendDatabase.db"
        ).build()

    }

}