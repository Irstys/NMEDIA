package ru.netology.nmedia.activity


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.databinding.ActivityEditPostBinding



private const val RESULT_KEY = "postNewContent"
private const val RESULT_KEY_EDIT = "postEditContent"

class EditPostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityEditPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val extras = intent.extras
        if (extras != null) {
            binding.textEditPost.setText(extras.getString("postEditContent"))
        }

        binding.textEditPost.requestFocus()

        binding.editOk.setOnClickListener {
            val intent = Intent()
            if (binding.textEditPost.text.isNullOrBlank()) {
                setResult(Activity.RESULT_CANCELED, intent)
            } else {
                val content = binding.textEditPost.text.toString()
                intent.putExtra(Intent.EXTRA_TEXT, content)
                setResult(Activity.RESULT_OK, intent)
            }
            finish()
        }
        binding.editCancel.setOnClickListener {
            val intent = Intent()

            setResult(Activity.RESULT_CANCELED, intent)

            finish()
        }
    }
}

class EditPostContentResultContract :
    ActivityResultContract<String?, String?>() {

    override fun createIntent(context: Context, input: String?): Intent =
        Intent(
            context,
            EditPostActivity()::class.java
        ).apply { putExtra(Intent.EXTRA_TEXT, input) }

    override fun parseResult(resultCode: Int, intent: Intent?): String? =
        if (resultCode == Activity.RESULT_OK) {
            intent?.getStringExtra(Intent.EXTRA_TEXT)
        } else {
            null
        }
}