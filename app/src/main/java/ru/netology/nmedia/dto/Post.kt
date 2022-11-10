package ru.netology.nmedia.dto

import ru.netology.nmedia.enumeration.AttachmentType


data class Post(
    val id: Long,
    val author: String,
    val authorAvatar: String,
    val content: String,
    val published: Long,
    val likedByMe: Boolean,
    val likes: Int = 0,
    val attachment: Attachment? = null,
    val viewed: Boolean = false,
    val repost: Int = 0,
    val views: Int=0,
    val video: String? = null,

    )

data class Attachment(
    val url: String,
    //val description: String? = null,
    val type: AttachmentType,
) {
    fun toDto() = Attachment(url, type)

    companion object {
        fun fromDto(dto: Attachment?) = dto?.let {
            Attachment(it.url, it.type)
        }
    }
}

