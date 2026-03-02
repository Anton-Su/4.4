package com.example.a44.repository

import com.example.a44.SocialPost

interface PostsRep {
    suspend fun getPosts(): List<SocialPost>
    // suspend fun getPostById(id: Int): SocialPost
}