package com.example.a44.RepositoryIml

import com.example.a44.JsonDataSource
import com.example.a44.SocialPost
import com.example.a44.repository.PostsRep
import kotlinx.coroutines.delay

class PostsRepImpl(private val dataSource: JsonDataSource): PostsRep {
    private var posts: List<SocialPost>? = null
    private suspend fun ensureLoaded() {
        if (posts == null) {
            posts = dataSource.getPostsFromJson()
        }
    }
    override suspend fun getPosts(): List<SocialPost> {
        ensureLoaded()
        delay(1000)
        return posts ?: emptyList()
    }

//    override suspend fun getPostById(id: Int): SocialPost {
//        TODO("Not yet implemented")
//    }
}