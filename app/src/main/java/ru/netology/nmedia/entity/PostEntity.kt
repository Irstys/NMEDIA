package ru.netology.nmedia.entity

import Attachment
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.enumeration.AttachmentType

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long,
    val author: String,
    val authorAvatar: String,
    val content: String,
    val published: Long,
    val likedByMe: Boolean,
    val likes: Int = 0,
    @Embedded
    var attachment: AttachmentEmbeddable?,
    val viewed: Boolean = false,
    val repost: Int = 0,
    val views: Int = 0,
    val video: String? = null,

    ) {
    fun toDto() = Post(
        id,
        author,
        authorAvatar,
        content,
        published,
        likedByMe,
        likes,
        attachment?.toDto(),
        viewed,
        repost,
        views,
        video,

        )

    companion object {
        fun fromDto(dto: Post) =
            PostEntity(
                dto.id,
                dto.author,
                dto.authorAvatar,
                dto.content,
                dto.published,
                dto.likedByMe,
                dto.likes,
                AttachmentEmbeddable.fromDto(dto.attachment),
                dto.viewed,
                dto.repost,
                dto.views,
                dto.video,

                )

    }
}

data class AttachmentEmbeddable(
    var url: String,
    //var description: String?,
    var type: AttachmentType,
) {
    fun toDto() = Attachment(url, type)


    companion object {
        fun fromDto(dto: Attachment?) = dto?.let {
            AttachmentEmbeddable(it.url,it.type)
        }
    }
}


fun List<PostEntity>.toDto(): List<Post> = map(PostEntity::toDto)
fun List<Post>.toEntity(): List<PostEntity> = map(PostEntity::fromDto)