package ru.netology.nmedia.dto

data class Post(
    val id: Long,
    val author: String,
    val authorAvatar: String="",
    val content: String,
    val published: String,
    var likes: Int=0,
    var likedByMe: Boolean = false,
    var repost: Int=0,
    var views: Int=10,
)


