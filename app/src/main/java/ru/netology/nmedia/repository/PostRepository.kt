package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun getAll(): List<Post>
    fun liked(id: Long, likedByMe: Boolean):Post
    fun shareById(id: Long)
    fun removeById(id: Long)
    fun save(post: Post):Post
}