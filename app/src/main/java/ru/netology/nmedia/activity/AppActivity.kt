package ru.netology.nmedia.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_INDEFINITE
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.CardPostFragment.Companion.textArg
import ru.netology.nmedia.databinding.ActivityAppBinding

class AppActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityAppBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent?.let {
            if (intent.action != Intent.ACTION_SEND) {
                return@let
            }
            val text = intent.getStringExtra(Intent.EXTRA_TEXT)

            if (text.isNullOrBlank()) {
                Snackbar.make(binding.root, "Content can't be empty", LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok) {
                        finish()
                    }
                    .show()
                return@let
            } else {
                supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment

                findNavController(R.id.fragmentContainer).navigate(
                    R.id.action_feedFragment_to_postContentActivity,
                    Bundle().apply {
                        textArg = text
                    }
                )
            }
        }
    }
}