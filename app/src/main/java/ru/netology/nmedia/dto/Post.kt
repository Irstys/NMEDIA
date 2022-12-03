package ru.netology.nmedia.dto

import ru.netology.nmedia.enumeration.AttachmentType

data class Post(
    override val id: Long,
    val authorId: Long,
    val author: String,
    val authorAvatar: String,
    val content: String,
    val published: Long,
    val likedByMe: Boolean,
    val likes: Int = 0,
    val attachment: Attachment? = null,
    val repost: Int = 0,
    val views: Int = 0,
    val video: String? = null,
    val ownedByMe: Boolean = false,
    val viewed: Boolean = false,
) : FeedItem

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

