package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.viewmodel.PostViewModel



class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel: PostViewModel by viewModels()

        val adapter = PostsAdapter(object:OnInteractionListener {
            override fun onLikeListener(post: Post) {
                viewModel.likeById(post.id)
            }
            override fun onShareListener(post: Post) {
                viewModel.shareById(post.id)
            }
            override fun onRemoveListener(post: Post) {
                viewModel.removeById(post.id)
            }
            override fun onEditListener(post: Post) {
                binding.group.visibility = View.VISIBLE
                viewModel.edit(post)
            }
        })
        binding.list.adapter = adapter
        viewModel.data.observe(this) {posts ->
           adapter.submitList(posts)
        }

        viewModel.edited.observe(this) { post ->

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
        }
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
        }*/
    }
}

