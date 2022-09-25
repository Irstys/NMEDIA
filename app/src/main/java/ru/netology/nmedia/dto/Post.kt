package ru.netology.nmedia.dto

data class Post(
    val id: Long,
    val author: String,
    //val authorAvatar: String,
    val content: String,
    val published: Long,
    val likes: Int,
    val likedByMe: Boolean,
    val repost: Int,
    val views: Int,
    val video: String? = null
)

