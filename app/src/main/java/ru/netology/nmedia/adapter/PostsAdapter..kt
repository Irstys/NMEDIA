package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.numbersToString

interface OnInteractionListener {
    fun onLikeListener(post: Post) {}
    fun onShareListener(post: Post) {}
    fun onRemoveListener(post: Post) {}
    fun onEditListener(post: Post) {}
    fun onPlayVideoListener(post: Post)
    fun onAddListener()
}

class PostsAdapter(
    private val onInteractionListener: OnInteractionListener
) : ListAdapter<Post, PostViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, onInteractionListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }
}
class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {
    private lateinit var post: Post

    fun bind(post: Post) {
        binding.apply {
            author.text = post.author
            published.text = post.published
            content.text = post.content
            views.text = numbersToString(post.views)
            //         like.setImageResource(
            //            if (post.likedByMe) R.drawable.ic_liked_24 else R.drawable.ic_like_24
            //        )
            like.isChecked = post.likedByMe
            like.text = numbersToString(post.likes)
            share.text = numbersToString(post.repost)

            like.setOnClickListener {
                onInteractionListener.onLikeListener(post)
            }
            share.setOnClickListener {
                onInteractionListener.onShareListener(post)
            }
            videoBanner.setOnClickListener {
                onInteractionListener.onPlayVideoListener(post)
            }
            playVideo.setOnClickListener {
                onInteractionListener.onPlayVideoListener(post)
            }
            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onInteractionListener.onRemoveListener(post)
                                true
                            }
                            R.id.edit -> {
                                onInteractionListener.onEditListener(post)
                                true
                            }

                            else -> false
                        }
                    }
                }.show()
            }
        }
    }
}

class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }

}
