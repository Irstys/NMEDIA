package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun getAll(): List<Post>
    fun getAllAsync(callback: Callback<List<Post>>)

    fun likedAsync(id: Long, likedByMe: Boolean, callback: Callback<Post>)

    fun shareById(id: Long)
    fun saveAsync(post: Post, callback: Callback<Post>)
    fun removeByIdAsync(id: Long, callback: Callback<Post>)

    interface Callback<T> {
        fun onSuccess(posts: T) {}
        fun onError(e: Exception) {}
    }
}