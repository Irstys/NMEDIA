package ru.netology.nmedia.model

import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.util.RetryTypes

data class FeedModelState(
    val loading: Boolean = false,
    val error: Boolean = false,
    val refreshing: Boolean = false,
    //val messageOfCodeError: String = ""
    val retryId: Long = 0,
    val retryType: RetryTypes? = null,
    val retryPost: Post? = null,
)