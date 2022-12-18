package ru.netology.nmedia.adapter

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.BuildConfig.BASE_URL
import ru.netology.nmedia.R
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.databinding.CardAdBinding
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.databinding.CardTextItemSeparatorBinding
import ru.netology.nmedia.dto.*
import ru.netology.nmedia.view.load

interface OnInteractionListener {
    fun onLikeListener(post: Post) {}
    fun onShareListener(post: Post) {}
    fun onRemoveListener(post: Post) {}
    fun onEditListener(post: Post) {}
    fun onAdClick(ad: Ad) {}

    //  fun onPlayVideoListener(post: Post) {}
    fun onPostListner(post: Post) {}
    fun onImageListner(image: String) {}
    fun onAuth()
}

class PostsAdapter(
    private val listener: OnInteractionListener,
    private val appAuth: AppAuth,
) : PagingDataAdapter<FeedItem, RecyclerView.ViewHolder>(PostDiffCallback()) {

    override fun getItemViewType(position: Int): Int =
        when (getItem(position)) {
            is Ad -> R.layout.card_ad
            is Post -> R.layout.card_post
            is TextItemSeparator -> R.layout.card_text_item_separator
            null -> error("unknow item type")
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            R.layout.card_post -> {
                val binding =
                    CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                PostViewHolder(binding, listener, appAuth)
            }
            R.layout.card_ad -> {
                val binding =
                    CardAdBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                AdViewHolder(binding, listener, appAuth)
            }
            R.layout.card_text_item_separator -> {
                val binding =
                    CardTextItemSeparatorBinding.inflate(LayoutInflater.from(parent.context),
                        parent,
                        false)
                TextItemViewHolder(binding, listener, appAuth)
            }
            else -> error("unknow item type $viewType")
        }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>,
    ) {
        when (val item = getItem(position)) {
            is Ad -> (holder as? AdViewHolder)?.bind(item)
            is Post -> (holder as? PostViewHolder)?.bind(item)
            is TextItemSeparator -> ((holder as? TextItemViewHolder)?.bind(item))
            null -> error("unknow item type")
        }
        if (holder is PostViewHolder) {
            payloads.forEach {
                if (it is Payload) {
                    holder.bind(it)
                }
            }

            onBindViewHolder(holder, position)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is Ad -> (holder as? AdViewHolder)?.bind(item)
            is Post -> (holder as? PostViewHolder)?.bind(item)
            is TextItemSeparator -> ((holder as? TextItemViewHolder)?.bind(item))
            null -> error("unknow item type")
        }
    }
}

class TextItemViewHolder(
    private val binding: CardTextItemSeparatorBinding,
    private val listener: OnInteractionListener,
    private val appAuth: AppAuth,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(textItemSeparator: TextItemSeparator) {
        binding.text.text = textItemSeparator.text
    }
}

class AdViewHolder(
    private val binding: CardAdBinding,
    private val listener: OnInteractionListener,
    private val appAuth: AppAuth,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(ad: Ad) {
        binding.image.load("${BuildConfig.BASE_URL}/media/${ad.name}")
        binding.image.setOnClickListener {
            listener.onAdClick(ad)
        }
    }
}


class PostViewHolder(
    private val binding: CardPostBinding,
    private val listener: OnInteractionListener,
    private val appAuth: AppAuth,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(payload: Payload) {
        payload.liked?.also { liked ->

            binding.like.setIconResource(
                if (liked) R.drawable.ic_liked_24 else R.drawable.ic_like_24
            )
            if (liked) {
                ObjectAnimator.ofPropertyValuesHolder(
                    binding.like,
                    PropertyValuesHolder.ofFloat(View.SCALE_X, 1.0F, 1.2F, 1.0F, 1.2F),
                    PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.0F, 1.2F, 1.0F, 1.2F)
                ).start()
            } else {
                ObjectAnimator.ofFloat(
                    binding.like,
                    View.ROTATION,
                    0F, 360F
                ).start()
            }
        }

        payload.content?.let(binding.content::setText)
    }

    fun bind(post: Post) {
        binding.apply {

            author.text = post.author

            published.text = post.published.toString()
            content.text = post.content
            views.text = numbersToString(post.views)
            attachment.visibility = View.GONE

            menu.visibility = if (post.ownedByMe) View.VISIBLE else View.INVISIBLE
            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    menu.setGroupVisible(R.id.owned, post.ownedByMe)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                listener.onRemoveListener(post)
                                true
                            }
                            R.id.edit -> {
                                listener.onEditListener(post)
                                true
                            }

                            else -> false
                        }
                    }
                }.show()
            }

            val url = "${BASE_URL}/avatars/${post.authorAvatar}"
            Glide.with(itemView)
                .load(url)
                .placeholder(R.drawable.ic_loading_24)
                .error(R.drawable.ic_baseline_error_outline_24)
                .timeout(10_000)
                .circleCrop()
                .into(avatar)

            val urlAttachment = "${BASE_URL}/media/${post.attachment?.url}"
            if (post.attachment != null) {
                Glide.with(attachment.context)
                    .load(urlAttachment)
                    .placeholder(R.drawable.ic_loading_24)
                    .error(R.drawable.ic_baseline_error_outline_24)
                    .timeout(10_000)
                    .into(attachment)
                attachment.isVisible = true
            } else {
                attachment.isVisible = false
            }

            like.isChecked = post.likedByMe
            like.text = numbersToString(post.likes)
            share.text = numbersToString(post.repost)

            like.setOnClickListener {
                if (appAuth.authStateFlow.value.id == 0L) {
                    listener.onAuth()
                    return@setOnClickListener
                }
                listener.onLikeListener(post)
            }
            share.setOnClickListener {
                listener.onShareListener(post)
            }
            thisPost.setOnClickListener { listener.onPostListner(post) }

            /* if (post.video == null) {
                binding.videoGroup.visibility = View.GONE
            } else {
                binding.videoGroup.visibility = View.VISIBLE
            }
            videoBanner.setOnClickListener {
                listener.onPlayVideoListener(post)
            }
            playVideo.setOnClickListener {
                listener.onPlayVideoListener(post)
            }*/
            attachment.setOnClickListener {
                post.attachment?.let { attach ->
                    listener.onImageListner(attach.url)
                }

            }
        }
    }
}

class PostDiffCallback : DiffUtil.ItemCallback<FeedItem>() {
    override fun areItemsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
        if (oldItem::class != newItem::class) {
            return false
        }
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
        return oldItem == newItem
    }

    override fun getChangePayload(oldItem: FeedItem, newItem: FeedItem): Any {
        return if (oldItem::class != newItem::class) {
        } else if (oldItem is Post && newItem is Post) {
            Payload(
                liked = newItem.likedByMe.takeIf { oldItem.likedByMe != it },
                content = newItem.content.takeIf { oldItem.content != it },
            )
        } else {

        }
    }
}

data class Payload(
    val liked: Boolean? = null,
    val content: String? = null,
)