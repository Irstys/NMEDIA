package ru.netology.nmedia.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryInMemoryImpl
import ru.netology.nmedia.util.AndroidUtils.hideKeyboard
import ru.netology.nmedia.util.SingleLiveEvent

private val empty = Post(
    id = 0,
    content = "",
    author = "",
    likedByMe = false,
    published = "",
    likes = 0,
    repost = 0,
    views = 0
)

class PostViewModel : ViewModel() {
    // упрощённый вариант
    private val repository: PostRepository = PostRepositoryInMemoryImpl()
    val data = repository.getAll()

    val sharePostContent = SingleLiveEvent<String>()

    val navigateToNewPostActivityEvent = SingleLiveEvent<Unit>()
    val navigateToEditPostActivityEvent = SingleLiveEvent<String?>()

    val playVideo = SingleLiveEvent<String>()

    private val currentPost = MutableLiveData<Post?>(null)

    val edited: MutableLiveData<Post> = MutableLiveData(empty)

    fun likeById(id: Long) = repository.likeById(id)
    fun removeById(id: Long) = repository.removeById(id)

    fun shareById(post: Post) {
        sharePostContent.value = post.content
        repository.shareById(post.id)
    }

    fun edit(post: Post) {
        edited.value = post
        // navigateToEditPostActivityEvent.value = post.content
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }

    fun onSaveButtonClicked(content: String) {
        if (content.isBlank()) return

        val post = currentPost.value?.copy(
            content = content
        ) ?: Post(
            id = PostRepository.NEW_POST_ID,
            author = "Irina",
            content = content,
            published = "Date"
        )
        repository.save(post)
        currentPost.value = null
    }

    fun save() {
        edited.value?.let {
            repository.save(it)
        }
        edited.value = empty
    }

    fun addPost() {
        navigateToNewPostActivityEvent.call()
    }

    fun editPost(postContent: String?) { //(post: Post)
        navigateToEditPostActivityEvent.value = postContent
    }

    /*   fun editTextCancel(view: TextView) {
        println("Edit canceled")
        edited.value = empty
        view.text = ""
        view.clearFocus()
        hideKeyboard(view)
    }*/

    fun playVideoClicked(post: Post) {
        val url: String = requireNotNull(post.video) {
            "Url must not be null"
        }
        playVideo.value = url
    }
}

