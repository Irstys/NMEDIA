package ru.netology.nmedia.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.CardPostFragment.Companion.textArg
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.util.RetryTypes
import ru.netology.nmedia.util.RetryTypes.*
import ru.netology.nmedia.viewmodel.PostViewModel
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class FeedFragment : Fragment() {

    internal val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentFeedBinding.inflate(
            inflater,
            container,
            false
        )
        val adapter = PostsAdapter(object : OnInteractionListener {
            override fun onLikeListener(post: Post) {
                viewModel.likeById(post.id, post.likedByMe)
            }

            override fun onShareListener(post: Post) {
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

            override fun onRemoveListener(post: Post) {
                viewModel.removeById(post.id)
            }

            override fun onEditListener(post: Post) {
                viewModel.edit(post)
                findNavController().navigate(
                    R.id.action_feedFragment_to_editPostFragment,
                    Bundle().apply {
                        textArg = post.content
                    }
                )
            }
            override fun onImageListner(post: Post) {
                findNavController().navigate(R.id.action_feedFragment_to_imageFragment, Bundle(). apply { textArg = post.attachment?.url })
            }
            override fun onPlayVideoListener(post: Post) {
                val intentVideo = Intent(Intent.ACTION_VIEW, Uri.parse(post.video))
                startActivity(intentVideo)
            }

            override fun onPostListner(post: Post) {
                findNavController().navigate(
                    R.id.action_feedFragment_to_cardPostFragment,
                    Bundle().apply {
                        idArg = post.id.toInt()
                    }
                )
            }
        })
        binding.list.adapter = adapter

        viewModel.data.observe(viewLifecycleOwner) { state ->
            adapter.submitList(state.posts)
            binding.emptyText.isVisible = state.empty
        }

        viewModel.dataState.observe(viewLifecycleOwner) { state ->
            binding.progress.isVisible = state.loading
            binding.swipeRefresh.isRefreshing = state.refreshing
            binding.errorGroup.isVisible = state.error
            if (state.error) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry_loading) {
                        when (state.retryType) {
                            LIKE -> viewModel.likeById(state.retryId, false)
                            UNLIKE -> viewModel.likeById(state.retryId, true)
                            SAVE -> viewModel.retrySave(state.retryPost)
                            REMOVE -> viewModel.removeById(state.retryId)
                            else -> viewModel.loadPosts()
                        }
                    }
                    .show()
            }
        }

        viewModel.data.observe(viewLifecycleOwner) { state ->
            val newPosts = state.posts.size > adapter.currentList.size
            adapter.submitList(state.posts) {
                if (newPosts) {
                    binding.list.smoothScrollToPosition(0)
                }
            }
            binding.emptyText.isVisible = state.empty
        }


        binding.retryButton.setOnClickListener {
            viewModel.loadPosts()
        }

        binding.addPost.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
        }
        binding.swipeRefresh.setColorSchemeResources(
            android.R.color.holo_blue_dark
        )

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadPosts()
        }

        viewModel.newerCount.observe(viewLifecycleOwner) { state ->
            binding.newPosts.isVisible = state > 0
            println(state)
        }
        binding.newPosts.setOnClickListener {
            binding.newPosts.isVisible = false
            viewModel.markRead()
        }
        return binding.root
    }

    companion object {
        var Bundle.idArg: Int by IntArg
    }

    object IntArg : ReadWriteProperty<Bundle, Int> {
        override fun getValue(thisRef: Bundle, property: KProperty<*>): Int {
            return thisRef.getInt(property.name)
        }

        override fun setValue(thisRef: Bundle, property: KProperty<*>, value: Int) {
            thisRef.putInt(property.name, value)
        }
    }
}


