package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post

interface PostRepository {
    val data: LiveData<List<Post>>

    suspend fun getAll()

    suspend fun likeById(id: Long, likedByMe: Boolean)

    suspend fun shareById(id: Long)
    suspend fun save(post: Post)
    suspend fun removeById(id: Long)

    interface Callback<T> {
        fun onSuccess(posts: T) {}
        fun onError(e: Exception) {}
    }
}