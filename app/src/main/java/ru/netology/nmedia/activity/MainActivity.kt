package ru.netology.nmedia.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
//import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.ActivityMainBinding
//import ru.netology.nmedia.databinding.CardPostBinding
//import ru.netology.nmedia.dto.numbersToString
import ru.netology.nmedia.viewmodel.PostViewModel
import ru.netology.nmedia.adapter.PostsAdapter

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel: PostViewModel by viewModels()
        val adapter = PostsAdapter(
            onLikeListener = {
                viewModel.likeById(it.id)
                },
            onShareListener = {
                viewModel.shareById(it.id)
                }
        )
        binding.list.adapter = adapter
        viewModel.data.observe(this) {posts ->
           adapter.submitList(posts)
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

