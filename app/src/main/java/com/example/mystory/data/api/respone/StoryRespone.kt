package com.example.mystory.data.api.respone

data class StoryResponse(
    val listStory: List<ListStoryItem>,
    val error: Boolean,
    val message: String
)

data class ListStoryItem(
    val photoUrl: String,
    val name: String,
    val description: String,
    val id: String,
    val createdAt : String,
    val lat : Float,
    val lon : Float
)
