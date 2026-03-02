package com.example.a44

import com.example.a44.repository.CommentsRep

class GetAllCommentsUseCase(private val repository: CommentsRep) {
    suspend operator fun invoke(): List<Comment> {
        return repository.getComments()
    }
}