package com.example.mystory.util

import com.example.mystory.data.api.respone.LoginResponse
import com.example.mystory.data.api.respone.LoginResult
import com.example.mystory.data.api.respone.MessageResponse
import com.example.mystory.data.database.entity.StoryEntity

class DataDummy {
    companion object {
        fun generateDummyLoginSuccess(): LoginResponse {
            return LoginResponse(
                error = false,
                message = "success",
                loginResult = LoginResult(
                    "name",
                    "userId",
                    "token"
                )
            )
        }

        fun generateDummyLoginError(): LoginResponse {
            return LoginResponse(
                error = true,
                message = "invalid password"
            )
        }

        fun generateDummyMessageSuccess(): MessageResponse {
            return MessageResponse(
                error = true,
                message = "success"
            )
        }

        fun generateDummyMessageError(): MessageResponse {
            return MessageResponse(
                error = false,
                message = "error"
            )
        }

        fun generateDummyStories(): List<StoryEntity> {
            val items: MutableList<StoryEntity> = arrayListOf()
            for (i in 0..100) {
                val story = StoryEntity(
                    id = "id",
                    name = "name",
                    description = "description",
                    photoUrl = "photoUrl",
                    createdAt = "createdAt",
                    lat = 0.01.toFloat(),
                    lon = 0.01.toFloat()
                )
                items.add(story)
            }
            return items
        }
    }
}