package com.example.mystory.data.database.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mystory.data.database.entity.RemoteKeys

@Dao
interface RemoteKeysDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(remoteKey : List<RemoteKeys>)

    @Query("SELECT * FROM remote_key WHERE id=:id")
    suspend fun getRemoteKeysId(id : String) : RemoteKeys?

    @Query("DELETE FROM remote_key")
    suspend fun deleteAll()
}