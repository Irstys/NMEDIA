package ru.netology.nmedia.viewmodel

import android.net.Uri
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dto.*
import ru.netology.nmedia.enumeration.AttachmentType
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.model.PhotoModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.util.RetryTypes
import ru.netology.nmedia.util.SingleLiveEvent
import java.io.File
import javax.inject.Inject
import kotlin.random.Random

private val empty = Post(
    id = 0,
    content = "",
    authorAvatar = "",
    author = "",
    authorId = 0,
    likedByMe = false,
    published = 221220L,
    likes = 0,
    viewed = false,
    repost = 0,
    views = 0,
    video = "",
    attachment = Attachment(
        url = "http://10.0.2.2:9999/media/d7dff806-4456-4e35-a6a1-9f2278c5d639.png",
        type = AttachmentType.IMAGE
    )
)
private val noPhoto = PhotoModel()

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel

class PostViewModel @Inject constructor(
    private val repository: PostRepository,
    auth: AppAuth,
) : ViewModel() {

    /*  private val cached = repository
        .data
        .cachedIn(viewModelScope)*/
    private val cached: Flow<PagingData<FeedItem>> = repository
        .data
        .map { pagingData ->
            pagingData.insertSeparators(
                generator = { before, after ->
                    if (before?.id?.rem(5) == 0L) {
                        Ad(Random.nextLong(), "figma.jpg")
                    } else {
                        null
                    }
                })
        }
        .cachedIn(viewModelScope)

    val data: Flow<PagingData<FeedItem>> = auth.authStateFlow
        .flatMapLatest { (myId, _) ->
            cached.map { pagingData ->
                pagingData.map { post ->
                    if (post is Post) {
                        post.copy(ownedByMe = post.authorId == myId)
                    } else {
                        post
                    }
                }
            }
        }


    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState

    val edited: MutableLiveData<Post> = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated


    val newerCount: LiveData<Long> = MutableLiveData(empty.id)
    /* val newerCount: LiveData<Int> = data.switchMap {
            repository.getNewerCount(it.posts.firstOrNull()?.id ?: 0L)
                .catch { e -> e.printStackTrace() }
                .asLiveData(Dispatchers.Default)
        }
    }*/

    private val _photo = MutableLiveData(noPhoto)
    val photo: LiveData<PhotoModel>
        get() = _photo

    private val scope = MainScope()

    init {
        loadPosts()
    }

    fun loadPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(loading = true)
            repository.getAll()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun likeById(id: Long, likedByMe: Boolean) = viewModelScope.launch {
        try {
            repository.likeById(id, likedByMe)
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value =
                FeedModelState(error = true, retryType = RetryTypes.REMOVE, retryId = id)
        }
    }

    fun refreshPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(refreshing = true)
            repository.getAll()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun shareById(id: Long) = viewModelScope.launch {
        repository.shareById(id)
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }


    fun save() {
        edited.value?.let {
            _postCreated.value = Unit
            viewModelScope.launch {
                try {
                    when (_photo.value) {
                        noPhoto -> repository.save(it)
                        else -> _photo.value?.file?.let { file ->
                            repository.saveWithAttachment(it, MediaUpload(file))
                        }
                    }
                    _dataState.value = FeedModelState()
                } catch (e: Exception) {
                    _dataState.value = FeedModelState(error = true)
                }
            }
        }
        edited.value = empty
        _photo.value = noPhoto
    }

    fun retrySave(post: Post?) {
        viewModelScope.launch {
            try {
                if (post != null) {
                    repository.save(post)
                    loadPosts()
                }
            } catch (e: Exception) {
                _dataState.value =
                    FeedModelState(error = true, retryType = RetryTypes.SAVE, retryPost = post)
            }
        }
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun changePhoto(uri: Uri?, file: File?) {
        _photo.value = if (uri != null && file != null) {
            PhotoModel(uri, file)
        } else {
            null
        }
    }

    fun removeById(id: Long) = viewModelScope.launch {
        try {
            repository.removeById(id)
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value =
                FeedModelState(error = true, retryType = RetryTypes.REMOVE, retryId = id)
        }
    }

    override fun onCleared() {
        super.onCleared()
        scope.cancel()
    }

    fun loadNewPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(loading = true)
            repository.getNewPosts()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun markRead() {
        viewModelScope.launch {
            repository.markRead()
        }
    }

}

