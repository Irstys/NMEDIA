package ru.netology.nmedia.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.launch
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostViewModel


class MainActivity : AppCompatActivity() {
    companion object {
        private const val NEW_POST_REQUEST_CODE = 1
        private const val EDIT_POST_REQUEST_CODE = 1
    }
    val viewModel : PostViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
            }

            override fun onRemoveListener(post: Post) {
                viewModel.removeById(post.id)
            }

            override fun onEditListener(post: Post) {
                //            binding.group.visibility = View.VISIBLE
                viewModel.edit(post)
            }
            override fun onPlayVideoClicked(post: Post){
                viewModel.PlayVideoClicked(post)
            }

        })

        binding.list.adapter = adapter
        viewModel.data.observe(this) { posts ->
            adapter.submitList(posts)
        }

        binding.addPost.setOnClickListener {
            viewModel.onAddClicked()
        }

        viewModel.edited.observe(this) { post ->
            if (post.id == 0L) {
                return@observe
            }
        }
        viewModel.sharePostContent.observe(this) { postContent ->
            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, postContent)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(
                intent, getString(R.string.chooser_share_post)
            )
            startActivity(shareIntent)
        }

        viewModel.playVideo.observe(this) { videoUrl ->
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl))
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            }
        }

        val postContentActivityLauncher = registerForActivityResult(
            PostContentActivity.PostContentResultContract
        ) { postContent ->
            postContent ?: return@registerForActivityResult
            viewModel.onSaveButtonClicked(postContent)

        }
        viewModel.navigateToPostContentScreenEvent.observe(this) {
            val contentForEdit = viewModel.currentPost.value?.content
            postContentActivityLauncher.launch(contentForEdit)
        }
    }


 /*       viewModel.edited.observe(this) { post ->

            if (post.id == 0L) {
                return@observe
            }
            with(binding.content) {
                requestFocus()
                setText(post.content)
            }
        }

        binding.save.setOnClickListener {
            with(binding.content) {
                if (text.isNullOrBlank()) {
                    Toast.makeText(
                        this@MainActivity,
                        context.getString(R.string.error_empty_content),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                viewModel.changeContent(text.toString())
                viewModel.save()

                setText("")

                clearFocus()
                AndroidUtils.hideKeyboard(this)
                binding.group.visibility = View.GONE
            }
        }
        binding.cancel.setOnClickListener {
              with(binding.content) {
                setText("")
                clearFocus()
                AndroidUtils.hideKeyboard(this)
                binding.group.visibility = View.GONE
            }
        }
        binding.content.setOnClickListener{
            binding.group.visibility = View.VISIBLE
        }*/
        /*viewModel.data.observe(this) { posts ->
            posts.map{post->
                CardPostBinding.inflate(layoutInflater,binding.container, true).apply {
                    author.text = post.author
                    published.text = post.published
                    content.text = post.content
                    viewsCount.text = numbersToString(post.views)
                    like.setImageResource(
                        if (post.likedByMe) R.drawable.ic_liked_24 else R.drawable.ic_like_24
                    )
                    likeCount.text = numbersToString(post.likes)
                    shareCount.text = numbersToString(post.repost)
                    like.setOnClickListener {
                        viewModel.likeById(post.id)
                    }
                    share.setOnClickListener {
                        viewModel.shareById(post.id)
                    }
                }.root
            }
        }
    }*/
}

