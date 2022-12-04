package ru.netology.nmedia.dto

import com.google.gson.annotations.SerializedName
import ru.netology.nmedia.enumeration.AttachmentType

data class Post(
    @SerializedName("id")
    override val id: Long,
    @SerializedName("authorId")
    val authorId: Long,
    @SerializedName("author")
    val author: String,
    @SerializedName("authorAvatar")
    val authorAvatar: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("published")
    val published: String,
    @SerializedName("likedByMe")
    val likedByMe: Boolean,
    @SerializedName("likes")
    val likes: Int = 0,
    @SerializedName("attachment")
    val attachment: Attachment? = null,
    val repost: Int = 0,
    val views: Int = 0,
    val video: String? = null,
    @SerializedName("ownedByMe")
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

