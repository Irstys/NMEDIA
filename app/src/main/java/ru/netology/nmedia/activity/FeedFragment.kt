package ru.netology.nmedia.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.CardPostFragment.Companion.textArg
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostViewModel
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class FeedFragment : Fragment() {

    internal val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)
    private val binding by lazy {
        FragmentFeedBinding.inflate(layoutInflater)
    }

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
                viewModel.likeById(post.id)
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
                viewModel.shareById(post)
            }

            override fun onRemoveListener(post: Post) {
                viewModel.removeById(post.id)
            }

            override fun onEditListener(post: Post) {
                viewModel.edit(post)
                findNavController().navigate(
                    R.id.action_feedFragment_to_postContentActivity,
                    Bundle().apply {
                        textArg = post.content
                    }
                )
            }

            override fun onPlayVideoListener(post: Post) {
                viewModel.playVideoClicked(post)
            }

            override fun onAddListener() {
                viewModel.addPost()
            }

            override fun onPost(post: Post) {
                findNavController().navigate(
                    R.id.action_feedFragment_to_cardPostFragment,
                    Bundle().apply {
                        idArg = post.id.toInt()
                    }
                )
            }
        })
        binding.list.adapter = adapter
        viewModel.data.observe(viewLifecycleOwner) { posts ->
            val newPost = adapter.itemCount < posts.size
            adapter.submitList(posts) {
                if (newPost) binding.list.smoothScrollToPosition(0)
            }
        }

        binding.addPost.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_postContentActivity)
        }

     /*   viewModel.edited.observe(viewLifecycleOwner) { (id) ->
            if (id == 0L) {
                return@observe
            }
            findNavController().navigate(R.id.action_feedFragment_to_postContentActivity)
        }
        viewModel.playVideo.observe(viewLifecycleOwner) { video ->
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(video))
            startActivity(intent)
        }*/
        viewModel.navigateToPostContentScreenEvent.observe(viewLifecycleOwner) {
            findNavController().navigate(R.id.action_feedFragment_to_cardPostFragment)
        }
        setFragmentResultListener(requestKey = CardPostFragment.REQUEST_KEY) { requestKey, bundle ->
            if (requestKey != CardPostFragment.REQUEST_KEY) return@setFragmentResultListener
            val newPostContent =
                bundle.getString(CardPostFragment.RESULT_KEY) ?: return@setFragmentResultListener
            viewModel.onSaveButtonClicked(newPostContent)
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


