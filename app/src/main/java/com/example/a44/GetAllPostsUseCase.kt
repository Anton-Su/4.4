package com.example.a44

import com.example.a44.repository.PostsRep

class GetAllPostsUseCase(private val repository: PostsRep){
    suspend operator fun invoke(): List<SocialPost> {
        return repository.getPosts()
    }
}