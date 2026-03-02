package com.example.a44.repository

import com.example.a44.Comment

interface CommentsRep {
    suspend fun getComments(): List<Comment>
    suspend fun getCommentsFromPostWithId(postId: Int): List<Comment>
}