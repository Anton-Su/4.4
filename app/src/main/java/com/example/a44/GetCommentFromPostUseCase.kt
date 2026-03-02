package com.example.a44

import com.example.a44.repository.CommentsRep

class GetCommentFromPostUseCase(private val repository: CommentsRep) {
    suspend operator fun invoke(postId: Int): List<Comment> {
        return repository.getCommentsFromPostWithId(postId)
    }
}