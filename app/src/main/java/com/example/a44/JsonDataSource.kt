package com.example.a44

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class JsonDataSource(private val context: Context)  {
    suspend fun getCommentsFromJson(): List<Comment> {
        return withContext(Dispatchers.IO) {
            delay(1000L)
            val json = context.assets.open("comments.json").bufferedReader().use { it.readText() }
            Json.decodeFromString<List<Comment>>(json)
        }
    }
    suspend fun getPostsFromJson(): List<SocialPost> {
        return withContext(Dispatchers.IO) {
            delay(1000L)
            val json = context.assets.open("social_posts.json").bufferedReader().use { it.readText() }
            Json.decodeFromString<List<SocialPost>>(json)
        }
    }
}