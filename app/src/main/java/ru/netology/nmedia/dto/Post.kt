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
    val repost: Int,
    val views: Int,
    val video: String?,
    //val repost: Attachment? =0,
    //val views: Int=0,
   // val video: String? = null,

)

data class Attachment(
    val url: String,
    val description: String?,
    val type: AttachmentType,
)