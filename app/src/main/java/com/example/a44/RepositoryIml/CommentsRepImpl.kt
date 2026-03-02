package com.example.a44.RepositoryIml

import com.example.a44.Comment
import com.example.a44.JsonDataSource
import com.example.a44.SocialPost
import com.example.a44.repository.CommentsRep
import kotlinx.coroutines.delay

class CommentsRepImpl(private val dataSource: JsonDataSource): CommentsRep {
    private var comments: List<Comment>? = null

    private suspend fun ensureLoaded() {
        if (comments == null) {
            comments = dataSource.getCommentsFromJson()
        }
    }

    override suspend fun getComments(): List<Comment> {
        ensureLoaded()
        delay(1000)
        return comments ?: emptyList()
    }

    override suspend fun getCommentsFromPostWithId(postId: Int): List<Comment> {
        delay(1500)
        return comments!!.filter { it.postId == postId }
    }
}