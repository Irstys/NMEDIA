package ru.netology.nmedia.repository

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.nmedia.dto.Post
import java.util.concurrent.TimeUnit

class PostRepositoryImpl : PostRepository {

    private var posts = emptyList<Post>()

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()
    private val gson = Gson()
    private val typeToken = object : TypeToken<List<Post>>() {}
    private val typeTokenPost = object : TypeToken<Post>() {}

    companion object {
        private const val BASE_URL = "http://10.0.2.2:9999"
        private val jsonType = "application/json".toMediaType()
    }

    override fun getAll(): List<Post> {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts")
            .build()

        posts = client.newCall(request)
            .execute()
            .let { it.body?.string() ?: throw RuntimeException("body is null") }
            .let {
                gson.fromJson(it, typeToken.type)
            }
        return posts
    }

    override fun liked(id: Long, likedByMe: Boolean):Post {
        val isLiked: Boolean = !likedByMe;
        val likesUrl = "${BASE_URL}/api/posts/${id}/likes"
        val request: Request = if (isLiked) {
            Request.Builder()
                .post(gson.toJson(id).toRequestBody(jsonType))
                .url(likesUrl)
                .build()
        } else {
            Request.Builder()
                .delete(gson.toJson(id).toRequestBody(jsonType))
                .url(likesUrl)
                .build()
        }
        return client.newCall(request)
            .execute()
            .let { it.body?.string() ?: throw RuntimeException("body is null") }
            .let {
                gson.fromJson(it, Post::class.java)
            }

    }

    override fun shareById(id: Long) {
        //TODO("Not yet implemented")
        Log.e("PostRepositoryImpl", "Share is not yet implemented")
    }
    /*override fun shareById(id: Long) {
        val request: Request = Request.Builder()
            .get()
            .url("${BASE_URL}/api/slow/posts/$id")
            .build()

        client.newCall(request)
            .execute()
            .close()
    }*/

    override fun removeById(id: Long) {
        val request: Request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/slow/posts/$id")
            .build()

        client.newCall(request)
            .execute()
            .close()
    }


    override fun save(post: Post):Post {
        val request: Request = Request.Builder()
            .post(gson.toJson(post).toRequestBody(jsonType))
            .url("${BASE_URL}/api/slow/posts")
            .build()

        return client.newCall(request)
            .execute()
            .let { it.body?.string() ?: throw RuntimeException("body is null") }
            .let {
                gson.fromJson(it, Post::class.java)
            }
    }
}
