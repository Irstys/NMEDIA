package ru.netology.nmedia.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Post

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long,
    val author: String,
    val content: String,
    val published: String,
    val likes: Int = 0,
    val likedByMe: Boolean,
    val repost: Int = 0,
    val views: Int = 0,
    val video: String? = null
) {
    fun toDto() = Post(id, author, content, published, likes, likedByMe, repost, views, video)

    companion object {
        fun fromDto(dto: Post) =
            PostEntity(
                id = dto.id,
                author = dto.author,
                content = dto.content,
                published = dto.published,
                likes = dto.likes,
                likedByMe = dto.likedByMe,
                repost = dto.repost,
                views = dto.views,
                video = dto.video
            )

    }
}