package ru.netology.nmedia.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.inflate
import android.view.ViewGroup
import com.bumptech.glide.Glide
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.NewPostFragment.Companion.textArg
import ru.netology.nmedia.databinding.FragmentImageBinding

class ImageFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val attachmentUrl = arguments?.textArg
        val binding = FragmentImageBinding.inflate(inflater, container, false)
        val url = "http://10.0.2.2:9999/media/$attachmentUrl"

        Glide.with(this)
            .load(url)
            .timeout(10_000)
            .into(binding.imageAttachmentFullScreen)

        return binding.root
    }

}