package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.viewmodel.PostViewModel

class NewPostFragment : Fragment()  {
        private val viewModel: PostViewModel by viewModels(
            ownerProducer = ::requireParentFragment
        )

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            val binding = FragmentNewPostBinding.inflate(
                inflater,
                container,
                false
            )

            binding.editNew.requestFocus()
            arguments?.textArg
                ?.let(binding.editNew::setText)


            binding.okNew.setOnClickListener{
            viewModel.changeContent(binding.editNew.text.toString())
            viewModel.save()
            AndroidUtils.hideKeyboard(requireView())
            findNavController().navigateUp()
        }

            return binding.root
        }
    companion object {
        var Bundle.textArg: String? by StringArg
    }

    }