package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl

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
    //private val repository: PostRepository = PostRepositoryFileImpl(application)
    private val repository: PostRepository = PostRepositoryImpl(
        AppDb.getInstance(application).postDao()
    )

    //private val repository: PostRepository = PostRepositoryInMemoryImpl()
    val data = repository.getAll()

    val edited: MutableLiveData<Post> = MutableLiveData(empty)

    fun likeById(id: Long) = repository.likeById(id)
    fun removeById(id: Long) = repository.removeById(id)
    fun shareById(id: Long) = repository.shareById(id)

    fun edit(post: Post) {
        edited.value = post
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
            repository.save(it)
            edited.value = empty
        }
    }

}

