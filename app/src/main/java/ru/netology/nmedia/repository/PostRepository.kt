package ru.netology.nmedia.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.MediaUpload
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.User

interface PostRepository {
    val data: Flow<PagingData<Post>>

    fun getNewerCount(): Flow<Int>
    suspend fun getNewPosts()

    suspend fun getAll()

    suspend fun likeById(id: Long, likedByMe: Boolean)

    suspend fun shareById(id: Long)
    suspend fun save(post: Post)
    suspend fun saveWithAttachment(post: Post, upload: MediaUpload)
    suspend fun uploadWithContent(upload: MediaUpload): Media

    suspend fun newerPostsViewed()

    suspend fun updateUser(login: String, pass: String): User


    suspend fun registerUser(login: String, pass: String, name: String): User

    suspend fun removeById(id: Long)
    suspend fun markRead()

    suspend fun getPostById(id: Long): Post
    suspend fun getMaxId(): Long
}