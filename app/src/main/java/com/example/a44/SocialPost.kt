package com.example.a44

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SocialPost(
    val id: Int,
    val userId: Int,
    val title: String,
    @SerialName(value = "body")
    val text: String,
    val avatarUrl: String
)