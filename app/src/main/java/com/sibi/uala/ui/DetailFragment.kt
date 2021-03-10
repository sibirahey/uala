package com.sibi.uala.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings.PluginState
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.sibi.uala.R
import com.sibi.uala.databinding.DetailFragmentBinding
import com.sibi.uala.model.RecipeUiModel
import com.sibi.uala.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class DetailFragment : Fragment() {

    private var _binding: DetailFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DetailFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.apply {
            getRecipeUiModels().observe(viewLifecycleOwner, Observer { handleUpdate(it) })
        }
    }

    private fun handleUpdate(uiModel: RecipeUiModel) {
        when (uiModel) {
            is RecipeUiModel.SelectedRecipe -> {
                binding.apply {
                    mealNameTextView.text = uiModel.selectedRecipe.name
                    categoryTextView.text = uiModel.selectedRecipe.category
                    Glide.with(this.mealImageView.context).load(uiModel.selectedRecipe.imageUrl)
                        .placeholder(R.drawable.ic_place_holder)
                        .into(this.mealImageView)

                    mWebView.settings.javaScriptEnabled = true
                    mWebView.settings.pluginState = PluginState.ON
                    mWebView.loadUrl(uiModel.selectedRecipe.videoUrl + "?autoplay=1&vq=small")
                    mWebView.webChromeClient = WebChromeClient()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}