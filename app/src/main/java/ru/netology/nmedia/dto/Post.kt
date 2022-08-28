package ru.netology.nmedia.dto

data class Post(
    val id: Long,
    val author: String,
    //val authorAvatar: String,
    val content: String,
    val published: String,
    val likes: Int = 0,
    val likedByMe: Boolean,
    val repost: Int = 0,
    val views: Int = 0,
    val video: String? = null
)

