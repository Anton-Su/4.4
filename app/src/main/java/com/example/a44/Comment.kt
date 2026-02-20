package com.example.a44

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Comment(
    val id: Int,
    val postId: Int,
    val name: String,
    @SerialName(value = "body")
    val text: String
)