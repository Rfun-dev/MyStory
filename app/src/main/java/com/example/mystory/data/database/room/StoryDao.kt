package com.example.mystory.data.database.room

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mystory.data.database.entity.StoryEntity

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertStory(stories : List<StoryEntity>)

    @Query("SELECT * FROM story")
    fun getAllStories() : PagingSource<Int,StoryEntity>

    @Query("DELETE FROM story")
    suspend fun deleteStories()
}