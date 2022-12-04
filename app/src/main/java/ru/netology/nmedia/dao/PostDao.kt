package ru.netology.nmedia.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.enumeration.AttachmentType

@Dao
interface PostDao {
    @Query("UPDATE posts SET viewed = 1")
    suspend fun allViewedTrue()

    @Query("SELECT * FROM posts ORDER BY id DESC")
    fun getAll(): Flow<List<PostEntity>>

    @Query("SELECT COUNT(*) == 0 FROM posts")
    suspend fun isEmpty(): Boolean

    @Query("UPDATE posts SET viewed = 1 WHERE viewed = 0")
    suspend fun markRead()

    @Query("SELECT COUNT(*) FROM posts")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: PostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(posts: List<PostEntity>)

    @Query("UPDATE posts SET content = :content WHERE id = :id")
    suspend fun updateContentById(id: Long, content: String)

    @Query(
        """
                UPDATE posts SET
                likes = likes + CASE WHEN likes THEN -1 ELSE 1 END,
                likedByMe = CASE WHEN likedByMe THEN 0 ELSE 1 END
                WHERE id = :id AND likedByMe=:likedByMe
                """
    )
    suspend fun likeById(id: Long, likedByMe: Boolean)

    @Query(
        """
                UPDATE posts SET
                repost = repost + 1
                WHERE id = :id;
                """
    )
    fun shareById(id: Long)

    @Query("DELETE FROM posts WHERE id = :id")
    suspend fun removeById(id: Long)

    suspend fun save(post: PostEntity) =
        if (post.id == 0L) insert(post) else updateContentById(post.id, post.content)

    @Query("UPDATE posts SET viewed = 1 WHERE viewed = 0")
    suspend fun viewedPosts()

    @Query("DELETE FROM posts")
    suspend fun removeAll()

    @Query("SELECT * FROM posts WHERE id = :id")
    suspend fun getPostById(id: Long): PostEntity?

    @Query("SELECT * FROM posts ORDER BY id DESC LIMIT 1")
    suspend fun getPostMaxId(): PostEntity?
}
class Converters {
    @TypeConverter
    fun toAttachmentType(value: String) = enumValueOf<AttachmentType>(value)
    @TypeConverter
    fun fromAttachmentType(value: AttachmentType) = value.name


}
