package ru.netology.nmedia.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.numbersToString
import ru.netology.nmedia.viewmodel.PostViewModel

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel: PostViewModel by viewModels()
        viewModel.data.observe(this) { post ->
            with(binding) {
                    author.text = post.author
                    published.text = post.published
                    content.text = post.content
                    viewsCount.text = numbersToString(post.views)
                    like.setImageResource(
                        if (post.likedByMe) R.drawable.ic_liked_24 else R.drawable.ic_like_24
                    )
                    likeCount.text = numbersToString(post.likes)
                    shareCount.text = numbersToString(post.repost)
            }
        }
        binding.like.setOnClickListener {
            viewModel.like()
        }
        binding.share.setOnClickListener {
            viewModel.share()
        }
    }
}
