package ru.netology.nmedia.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.activity.CardPostFragment.Companion.textArg
import ru.netology.nmedia.activity.FeedFragment.Companion.idArg
import ru.netology.nmedia.databinding.FragmentPostContentBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.util.AndroidUtils.focusAndShowKeyboard
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.viewmodel.PostViewModel

private const val RESULT_KEY = "postNewContent"
private const val RESULT_KEY_EDIT = "postEditContent"

class PostContentFragment : Fragment() {

    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentPostContentBinding.inflate(
            inflater,
            container,
            false
        )

        arguments?.textArg
            ?.let { binding.edit.setText(it) }

        binding.edit.requestFocus()

        binding.edit.setText(viewModel.edited.value?.content)

        binding.buttonOk.setOnClickListener()
        {
            val text = binding.edit.text
            if (!text.isNullOrBlank()) {
                viewModel.onSaveButtonClicked(text.toString())
            }
            viewModel.changeContent(binding.edit.text.toString())
            viewModel.save()
            AndroidUtils.hideKeyboard(requireView())
            findNavController().navigateUp()
        }

        binding.buttonCancel.setOnClickListener()
        {
            AndroidUtils.hideKeyboard(it)
            findNavController().navigateUp()
        }
        return binding.root
    }

    companion object {
        private const val TEXT_KEY = "TEXT_KEY"
        var Bundle.textArg: String?
            set(value) = putString(TEXT_KEY, value)
            get() = getString(TEXT_KEY)
    }

}
