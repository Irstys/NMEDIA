package ru.netology.nmedia.activity

import android.content.Intent
import android.os.Build.VERSION_CODES.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.FeedFragment.Companion.idArg
import ru.netology.nmedia.databinding.FragmentCardPostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.numbersToString
import ru.netology.nmedia.viewmodel.AuthViewModel
import ru.netology.nmedia.viewmodel.PostViewModel
@AndroidEntryPoint
class CardPostFragment : Fragment() {
    private val viewModel: PostViewModel by activityViewModels()
    private val viewModelAuth: AuthViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentCardPostBinding.inflate(
            inflater,
            container,
            false
        )
        val id = arguments?.idArg

        val post: Post = arguments?.get("post") as Post
        post.let {
            with(binding.postLayout) {
                if (post.id.toInt() == id) {
                    author.text = post.author
                    published.text = post.published.toString()
                    content.text = post.content
                    views.text = numbersToString(post.views)
                    like.isChecked = post.likedByMe
                    like.text = numbersToString(post.likes)
                    share.text = numbersToString(post.repost)
                    attachment.visibility = View.GONE

                    val url = "http://10.0.2.2:9999/avatars/${post.authorAvatar}"
                    Glide.with(avatar)
                        .load(url)
                        .placeholder(R.drawable.ic_loading_24)
                        .error(R.drawable.ic_baseline_error_outline_24)
                        .timeout(10_000)
                        .circleCrop()
                        .into(avatar)

                    val urlAttachment = "http://10.0.2.2:9999/images/${post.attachment?.url}"
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

                    /*  if (!post.video.isNullOrEmpty()) {
                        binding.postLayout.videoGroup.visibility = View.VISIBLE
                    } else binding.postLayout.videoGroup.visibility = View.GONE*/

                    like.setOnClickListener {
                        if (viewModelAuth.authenticated) {
                            viewModel.likeById(post.id, post.likedByMe)
                        } else {
                            authenticate()
                        }
                    }
                    share.setOnClickListener {
                        val intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, post.content)
                            type = "text/plain"
                        }
                        val shareIntent =
                            Intent.createChooser(intent, getString(R.string.chooser_share_post))
                        startActivity(shareIntent)
                        viewModel.shareById(post.id)
                    }
                    /*videoBanner.setOnClickListener {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(post.video))
                        startActivity(intent)
                    }
                    playVideo.setOnClickListener {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(post.video))
                        startActivity(intent)
                    }*/
                    menu.setOnClickListener {
                        PopupMenu(it.context, it).apply {
                            inflate(R.menu.options_post)

                            setOnMenuItemClickListener { item ->
                                when (item.itemId) {
                                    R.id.remove -> {
                                        if (viewModelAuth.authenticated) {
                                            viewModel.removeById(post.id)
                                            findNavController().navigateUp()

                                        } else {
                                            authenticate()
                                        }
                                        true
                                    }
                                    R.id.edit -> {
                                        if (viewModelAuth.authenticated) {
                                            viewModel.edit(post)
                                            findNavController().navigate(
                                                R.id.action_cardPostFragment_to_editPostFragment,
                                                Bundle().apply {
                                                    textArg = post.content
                                                }
                                            )

                                        } else {
                                            authenticate()
                                        }
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
        return binding.root
    }

    private fun authenticate() = findNavController().navigate(R.id.action_cardPostFragment_to_signInFragment)

    companion object {

        private const val TEXT_KEY = "TEXT_KEY"
        var Bundle.textArg: String?
            set(value) = putString(TEXT_KEY, value)
            get() = getString(TEXT_KEY)
    }

}