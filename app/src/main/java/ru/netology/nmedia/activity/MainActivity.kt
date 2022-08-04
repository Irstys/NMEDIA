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

    val viewModel: PostViewModel by viewModels()

    private val postContentActivityLauncher =
        registerForActivityResult(PostContentActivity.PostContentResultContract) { postContent ->
            postContent ?: return@registerForActivityResult
            viewModel.onSaveButtonClicked(postContent)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


//        run {
//            val preferences = getPreferences(Context.MODE_PRIVATE)
//            preferences.edit().apply {
//                putString("key", "value") // putX
//                commit() // commit - синхронно, apply - асинхронно
//            }
//        }
//
//        run {
//            getPreferences(Context.MODE_PRIVATE)
//                .getString("key", "no value")?.let {
//                    Snackbar.make(binding.root, it, BaseTransientBottomBar.LENGTH_INDEFINITE)
//                        .show()
//                }
//        }

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
                // binding.group.visibility = View.VISIBLE
                viewModel.edit(post)
            }

            override fun onPlayVideoListener(post: Post) {
                viewModel.playVideoClicked(post)
            }

            override fun onAddListener() {
                viewModel.addPost()
            }
        })

        binding.list.adapter = adapter
        viewModel.data.observe(this) { posts ->
            adapter.submitList(posts)
        }
        /*  val newPostLauncher = registerForActivityResult(
            NewPostResultContract()
        ) { result ->
            result ?: return@registerForActivityResult
            viewModel.changeContent(result)
            viewModel.save()
        }*/

        /* binding.addPost.setOnClickListener {
            //    viewModel.onAddClicked()
            postContentActivityLauncher.launch()
        }*/

        viewModel.edited.observe(this) { (id) ->
            if (id == 0L) {
                return@observe
            }
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
}


