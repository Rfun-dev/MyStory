package com.example.mystory.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.mystory.data.database.entity.RemoteKeys
import com.example.mystory.data.database.entity.StoryEntity
import com.example.mystory.data.database.room.RemoteKeysDao
import com.example.mystory.data.database.room.StoryDao

@Database(
    entities = [StoryEntity::class,RemoteKeys::class],
    version = 3,
    exportSchema = false
)
abstract class StoryDatabase : RoomDatabase(){
    abstract fun storyDao() : StoryDao
    abstract fun remoteKeysDao() : RemoteKeysDao

    companion object{
        @Volatile
        private var INSTANCE : StoryDatabase? = null

        fun getInstance(context: Context) : StoryDatabase{
            return INSTANCE ?: synchronized(this){
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    StoryDatabase::class.java,
                    "story_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}