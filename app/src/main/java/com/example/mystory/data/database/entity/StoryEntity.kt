package com.example.mystory.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "story")
data class StoryEntity(
    @field:SerializedName("photo_url")
    val photoUrl: String? = null,
    val name: String? = null,
    val description: String? = null,
    @PrimaryKey
    val id: String,
    @field:SerializedName("created_at")
    val createdAt : String? = null,
    val lat : Float? = 0F,
    val lon : Float? = 0F
)