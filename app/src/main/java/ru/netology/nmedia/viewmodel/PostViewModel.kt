package ru.netology.nmedia.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryInMemoryImpl
import ru.netology.nmedia.util.SingleLiveEvent

private val empty = Post(
    id = 0,
    content = "",
    author = "",
    likedByMe = false,
    published = "",
    likes=0,
    repost = 0,
    views=0
)

class PostViewModel : ViewModel(){
    // упрощённый вариант
    private val repository: PostRepository = PostRepositoryInMemoryImpl()
    val data = repository.getAll()

    val sharePostContent = SingleLiveEvent<String>()
    val navigateToPostContentScreenEvent = SingleLiveEvent<String>()

    val playVideo = SingleLiveEvent<String>()

    val currentPost = MutableLiveData<Post?>(null)

    fun onSaveButtonClicked(content: String) {
        if (content.isBlank()) return

        val post = currentPost.value?.copy(
            content = content
        ) ?: Post(
            id = PostRepository.NEW_POST_ID,
            author = "Vladimir",
            content = content,
            published = "Date"
        )
        repository.save(post)
        currentPost.value = null
    }

    val edited = MutableLiveData(empty)

    fun save() {
        edited.value?.let {
            repository.save(it)
        }
        edited.value = empty
    }

    fun edit(post: Post) {
        currentPost.value = post
        navigateToPostContentScreenEvent.value = post.content
    }

    fun onAddClicked() {
        navigateToPostContentScreenEvent.call()
    }

    fun onCloseEditClicked() {
        currentPost.value = null
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }

    fun likeById(id:Long) = repository.likeById(id)
    fun shareById(post: Post) {
        sharePostContent.value = post.content
    }
    fun removeById(id:Long) =repository.removeById(id)
    fun PlayVideoClicked (post: Post) {
        val url: String = requireNotNull(post.video) {
            "Url must not be null"
        }
        playVideo.value = url
    }
}

