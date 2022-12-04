package ru.netology.nmedia.entity


import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.Post


@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long,
    val authorId: Long,
    val author: String,
    val authorAvatar: String,
    val content: String,
    val published: String,
    val likedByMe: Boolean,
    val likes: Int = 0,
    @Embedded
    var attachment: Attachment?,
    val repost: Int = 0,
    val views: Int = 0,
    val video: String? = null,
    val ownedByMe: Boolean = false,
    val viewed: Boolean = false,

    ) {
    fun toDto() = Post(
        id,
        authorId,
        author,
        authorAvatar,
        content,
        published,
        likedByMe,
        likes,
        attachment?.toDto(),
        repost,
        views,
        video,
        ownedByMe,
        viewed,

        )

    companion object {
        fun fromDto(dto: Post) =
            PostEntity(
                dto.id,
                dto.authorId,
                dto.author,
                dto.authorAvatar,
                dto.content,
                dto.published,
                dto.likedByMe,
                dto.likes,
                Attachment.fromDto(dto.attachment),
                dto.repost,
                dto.views,
                dto.video,
                dto.ownedByMe,
                dto.viewed,
            )

    }
}

fun List<PostEntity>.toDto(): List<Post> = map(PostEntity::toDto)
fun List<Post>.toEntity(): List<PostEntity> = map(PostEntity::fromDto)