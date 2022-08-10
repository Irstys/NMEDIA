package ru.netology.nmedia.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostFragmentBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.numbersToString
import ru.netology.nmedia.viewmodel.PostViewModel

class CardPostFragment : Fragment() {
    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = CardPostFragmentBinding.inflate(inflater, container, false).also { binding ->

        val postId = arguments?.postId
        val post: Post? = viewModel.data.value?.find {
            it.id == postId
        }
        val popupMenu by lazy {
            PopupMenu(context, binding.postLayout.menu).apply {
                inflate(R.menu.options_post)
                setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.remove -> {
                            if (post != null) {
                                viewModel.edit(post)
                            }
                            findNavController().navigateUp()
                            true
                        }
                        R.id.edit -> {
                            if (post != null) {
                                viewModel.edit(post)
                            }
                            true
                        }

                        else -> false
                    }
                }
            }.show()
        }

        viewModel.data.observe(viewLifecycleOwner) { posts ->
            val currentPost = posts.find {
                it.id == postId
            }
            with(binding.postLayout) {
                if (currentPost != null) {

                    author.text = currentPost.author
                    published.text = currentPost.published
                    content.text = currentPost.content
                    views.text = numbersToString(currentPost.views)
                    like.isChecked = currentPost.likedByMe
                    like.text = numbersToString(currentPost.likes)
                    share.text = numbersToString(currentPost.repost)

                    if (!currentPost.video.isNullOrEmpty()) {
                        videoGroup.visibility = View.VISIBLE
                    } else videoGroup.visibility = View.GONE

                    like.setOnClickListener {
                        viewModel.likeById(currentPost.id)
                    }
                    share.setOnClickListener {
                        val intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, currentPost.content)
                            type = "text/plain"
                        }
                        val shareIntent =
                            Intent.createChooser(intent, getString(R.string.chooser_share_post))
                        startActivity(shareIntent)
                        viewModel.shareById(currentPost)
                    }
                    videoBanner.setOnClickListener {
                        val intent = Intent(
                            Intent.ACTION_VIEW, Uri.parse(
                                (post
                                    ?: return@setOnClickListener).video
                            )
                        )
                        startActivity(intent)
                    }
                    playVideo.setOnClickListener {
                        val intent = Intent(
                            Intent.ACTION_VIEW, Uri.parse(
                                (post
                                    ?: return@setOnClickListener).video
                            )
                        )
                        startActivity(intent)
                    }
                }
            }

        }
        viewModel.navigateToPostContentScreenEvent.observe(viewLifecycleOwner) {

            findNavController().navigate(
                R.id.action_feedFragment_to_cardPostFragment,
                Bundle().apply {
                    textArg = it.toString()
                })
        }
    }.root

    companion object {
        const val REQUEST_KEY = "requestKey"
        const val RESULT_KEY = "postForSaveContent"
        private const val TEXT_KEY = "TEXT_KEY"
        var Bundle.textArg: String?
            set(value) = putString(TEXT_KEY, value)
            get() = getString(TEXT_KEY)
        private const val POST_ID_KEY = "POST_ID_KEY"
        var Bundle.postId: Long
            set(value) = putLong(POST_ID_KEY, value)
            get() = getLong(POST_ID_KEY)
    }

}