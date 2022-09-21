package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.util.SingleLiveEvent
import java.io.IOException
import kotlin.concurrent.thread

private val empty = Post(
    id = 0,
    content = "",
    //  authorAvatar = "",
    author = "",
    likedByMe = false,
    published = "Once upon a time",
    likes = 0,
    repost = 0,
    views = 0
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    // упрощённый вариант
    private val repository: PostRepository = PostRepositoryImpl()
    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel>
        get() = _data
    val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    init {
        loadPosts()
    }

    fun loadPosts() {
        thread {
            // Начинаем загрузку
            _data.postValue(FeedModel(loading = true))
            try {
                // Данные успешно получены
                val posts = repository.getAll()
                FeedModel(posts = posts, empty = posts.isEmpty())
            } catch (e: IOException) {
                // Получена ошибка
                FeedModel(error = true)
            }.also(_data::postValue)
        }
    }

    fun likeById(id: Long, likedByMe: Boolean) {
        thread {
            val newPost = repository.liked(id, likedByMe)
            // Оптимистичная модель
            _data.postValue(
                _data.value?.copy(posts = _data.value?.posts.orEmpty()
                    .map {
                        if(it.id != id) it
                        else newPost
                    }
                )
            )
        }
    }

    fun shareById(id: Long) {
        thread { repository.shareById(id) }
    }

    fun edit(post: Post) {
        thread { edited.value = post }
        //navigateToEditPostContentScreenEvent.value = post.content
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }

    fun saveContent() {
        edited.value?.let {
            repository.save(it)
            edited.value = empty
        }
    }

    fun save() {
        edited.value?.let {
            thread {
                val newPost = repository.save(it)
                val old = _data.value?.posts.orEmpty()
                val posts = listOf(newPost) + old
                _postCreated.postValue(Unit)
            }
         }
        edited.value = empty
    }

    fun removeById(id: Long) {
        thread {
            // Оптимистичная модель
            val old = _data.value?.posts.orEmpty()
            _data.postValue(
                _data.value?.copy(posts = _data.value?.posts.orEmpty()
                    .filter { it.id != id }
                )
            )
            try {
                repository.removeById(id)
            } catch (e: IOException) {
                _data.postValue(_data.value?.copy(posts = old))
            }
        }
    }
}

