package ru.netology.nmedia.activity


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.CardPostFragment.Companion.textArg
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostLoadStateAdapter
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.util.RetryTypes.*
import ru.netology.nmedia.viewmodel.AuthViewModel
import ru.netology.nmedia.viewmodel.PostViewModel
import javax.inject.Inject
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

@AndroidEntryPoint
class FeedFragment : Fragment() {
    @Inject
    lateinit var repository: PostRepository

    @Inject
    lateinit var auth: AppAuth

    private val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)

    private val viewModelAuth: AuthViewModel by viewModels(ownerProducer = ::requireParentFragment)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = FragmentFeedBinding.inflate(
            inflater,
            container,
            false
        )
        val adapter = PostsAdapter(object : OnInteractionListener {
            override fun onLikeListener(post: Post) {
                if (viewModelAuth.authenticated) {
                    viewModel.likeById(post.id, post.likedByMe)
                } else {
                    authenticate()
                }
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
                if (viewModelAuth.authenticated) {
                    viewModel.removeById(post.id)
                } else {
                    authenticate()
                }
            }

            override fun onEditListener(post: Post) {
                if (viewModelAuth.authenticated) {
                    viewModel.edit(post)
                    findNavController().navigate(
                        R.id.action_feedFragment_to_editPostFragment,
                        Bundle().apply {
                            textArg = post.content
                        }
                    )
                } else {
                    authenticate()
                }
            }

            override fun onImageListner(image: String) {
                val bundle = Bundle().apply {
                    putString("image", image)
                }
                findNavController().navigate(
                    R.id.action_feedFragment_to_imageFragment, bundle
                )
            }
            /* override fun onPlayVideoListener(post: Post) {
                val intentVideo = Intent(Intent.ACTION_VIEW, Uri.parse(post.video))
                startActivity(intentVideo)
            }*/

            override fun onPostListner(post: Post) {
                findNavController().navigate(
                    R.id.action_feedFragment_to_cardPostFragment,
                    Bundle().apply {
                        idArg = post.id.toInt()
                    }
                )
            }

            override fun onAuth() {
                findNavController().navigate(R.id.action_feedFragment_to_signInFragment)
            }
        },

            auth
        )

        binding.list.adapter = adapter.withLoadStateHeaderAndFooter(
            header = PostLoadStateAdapter { adapter.retry() },
            footer = PostLoadStateAdapter { adapter.retry() },
        )

        lifecycleScope.launchWhenCreated {
            viewModel.data.collectLatest {
                adapter.submitData(it)
            }
        }
        lifecycleScope.launchWhenCreated {
            adapter.loadStateFlow.collectLatest { state ->
                binding.swipeRefresh.isRefreshing = state.refresh is LoadState.Loading
                        || state.prepend is LoadState.Loading
                        || state.append is LoadState.Loading
                if (state.refresh is LoadState.Loading) {
                    binding.list.smoothScrollToPosition(0)
                }
            }
        }

        binding.swipeRefresh.setOnRefreshListener {
            adapter.refresh()
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

        /*viewModel.data.observe(viewLifecycleOwner) { state ->
            val newPosts = state.posts.size > adapter.currentList.size
            adapter.submitList(state.posts) {
                if (newPosts) {
                    binding.list.smoothScrollToPosition(0)
                }
            }
            binding.emptyText.isVisible = state.empty
        }*/


        binding.retryButton.setOnClickListener {
            viewModel.loadPosts()
        }

        binding.addPost.setOnClickListener {
            if (viewModelAuth.authenticated) {
                findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
            } else {
                authenticate()
            }
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

    private fun authenticate() =
        findNavController().navigate(R.id.action_feedFragment_to_signInFragment)

    object IntArg : ReadWriteProperty<Bundle, Int> {
        override fun getValue(thisRef: Bundle, property: KProperty<*>): Int {
            return thisRef.getInt(property.name)
        }

        override fun setValue(thisRef: Bundle, property: KProperty<*>, value: Int) {
            thisRef.putInt(property.name, value)
        }
    }
}


