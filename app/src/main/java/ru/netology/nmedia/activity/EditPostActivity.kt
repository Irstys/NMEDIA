package ru.netology.nmedia.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import ru.netology.nmedia.databinding.ActivityEditPostBinding

class EditPostActivity : AppCompatActivity() {

    companion object {
        const val EDIT_CONTENT = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityEditPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.textEditPost.setText(intent.getStringExtra(EDIT_CONTENT))

        binding.editOk.setOnClickListener {
            val result =
                Intent().putExtra(Intent.EXTRA_TEXT, binding.textEditPost.text.toString())
            setResult(RESULT_OK, result)
            finish()
        }

        binding.editCancel.setOnClickListener {
            finish()
        }
    }
}