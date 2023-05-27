package com.example.mystory.data.respone

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

)
