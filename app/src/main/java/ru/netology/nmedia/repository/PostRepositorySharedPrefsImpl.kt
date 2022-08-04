package ru.netology.nmedia.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.netology.nmedia.dto.Post

const val POSTS_PREFS_KEY = "posts"
const val NEXT_ID_PREFS_KEY = "posts"
const val FILE_NAME = "posts.json"

class PostRepositorySharedPrefsImpl(
    context: Context,
) : PostRepository {
    private val gson = Gson()
    private val prefs = context.getSharedPreferences("repo", Context.MODE_PRIVATE)
    private val type = TypeToken.getParameterized(List::class.java, Post::class.java).type
    private val key = "posts"
    private var nextId = 1L
    private var posts = emptyList<Post>()
    private val data = MutableLiveData(posts)

    init {
        prefs.getString(key, null)?.let {
            posts = gson.fromJson(it, type)
            data.value = posts
        }
    }

    override fun getAll(): LiveData<List<Post>> = data
    override fun save(post: Post) {
        if (post.id == PostRepository.NEW_POST_ID) insert(post) else update(post)
        data.value = posts
        sync()
        return
    }

    override fun likeById(id: Long) {
        posts = posts.map {
            if (it.id != id) it else it.copy(
                likedByMe = !it.likedByMe,
                likes = if (it.likedByMe) it.likes - 1 else it.likes + 1
            )
        }
        data.value = posts
        sync()
    }

    override fun shareById(id: Long) {
        posts = posts.map { post ->
            if (post.id != id) post else post.copy(repost = post.repost + 1)
        }

    }

    override fun removeById(id: Long) {
        posts = posts.filter { it.id != id }
        data.value = posts
        sync()
    }

    private fun sync() {
        with(prefs.edit()) {
            putString(key, gson.toJson(posts))
            apply()
        }
    }

    private fun update(post: Post) {
        posts = posts.map {
            if (it.id == post.id) post else it
        }

    }

    private fun insert(post: Post) {

        posts = listOf(post.copy(id = nextId++)) + post

    }

}
