package ru.netology.nmedia.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.databinding.ActivityPostContentBinding

private const val INPUT_CONTENT_KEY = "postNewContent"
private const val RESULT_CONTENT_KEY = "text_content"

class PostContentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityPostContentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var textContent = ""
        if (intent.hasExtra(INPUT_CONTENT_KEY)) {
            textContent = intent.getStringExtra(INPUT_CONTENT_KEY).toString()
        }
        binding.edit.setText(textContent)
        binding.edit.requestFocus()
        binding.buttonOk.setOnClickListener {
            onOkButtonClicked(binding.edit.text.toString())
        }
    }

    private fun onOkButtonClicked(postContent: String?) {
        if (postContent.isNullOrBlank()) {
            setResult(Activity.RESULT_CANCELED)
        } else {
            val resultIntent = Intent()
            resultIntent.putExtra(RESULT_CONTENT_KEY, postContent)
            setResult(Activity.RESULT_OK, resultIntent)
        }
        finish()
    }
}

class ResultContract : ActivityResultContract<String?, String?>() {
    override fun createIntent(context: Context, input: String?): Intent {
        val intent = Intent(context, PostContentActivity::class.java)
        if (input != null) {
            intent.putExtra(INPUT_CONTENT_KEY, input)
        }
        return intent
    }

    override fun parseResult(resultCode: Int, intent: Intent?) =
        if (resultCode == Activity.RESULT_OK) {
            intent?.getStringExtra(RESULT_CONTENT_KEY)
        } else null
}

