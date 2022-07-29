package ru.netology.nmedia.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.databinding.ActivityPostContentBinding
import ru.netology.nmedia.util.AndroidUtils.focusAndShowKeyboard

private const val RESULT_KEY = "postNewContent"
private const val RESULT_KEY_EDIT = "postEditContent"

class PostContentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityPostContentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val extras = intent.extras
        if (extras != null) {
            binding.edit.setText(extras.getString(RESULT_KEY_EDIT))
        }

        binding.edit.focusAndShowKeyboard()

        binding.buttonOk.setOnClickListener {
            val intent = Intent()
            if (binding.edit.text.isNullOrBlank()) {
                setResult(Activity.RESULT_CANCELED, intent)
            } else {
                val content = binding.edit.text.toString()
                intent.putExtra(Intent.EXTRA_TEXT, content)
                setResult(Activity.RESULT_OK, intent)
            }
            finish()
        }

    }

    object PostContentResultContract : ActivityResultContract<String?, String?>() {

        override fun createIntent(context: Context, input: String?): Intent =
            Intent(
                context,
                PostContentActivity::class.java
            ).putExtra(RESULT_KEY_EDIT, input)

        override fun parseResult(resultCode: Int, intent: Intent?): String? =
            if (resultCode == Activity.RESULT_OK) {
                intent?.getStringExtra(RESULT_KEY)
            } else {
                null
            }
    }
}