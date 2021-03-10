package com.sibi.uala.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.sibi.uala.R
import com.sibi.uala.databinding.MainFragmentBinding
import com.sibi.uala.model.RecipeUiModel
import com.sibi.uala.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainFragment : Fragment() {

    private var _binding: MainFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    private val mealAdapter = RecipesAdapter {
        viewModel.selectedRecipe(it)
        this@MainFragment.view?.findNavController()?.navigate(R.id.to_detailFragment)
    }

    companion object {
        private const val REQUEST_CODE = 1010
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MainFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupSearchToolbar()

        binding.recyclerView.adapter = mealAdapter

        viewModel.apply {
            getRecipeUiModels().observe(viewLifecycleOwner, Observer { handleUpdate(it) })
        }
    }

    private fun setupSearchToolbar() {
        binding.apply {
            eventsSearchToolbar.apply {
                searchEditText.addTextChangedListener(
                    object : TextWatcher {
                        override fun beforeTextChanged(
                            s: CharSequence,
                            start: Int,
                            count: Int,
                            after: Int
                        ) {
                        }

                        override fun onTextChanged(
                            s: CharSequence,
                            start: Int,
                            before: Int,
                            count: Int
                        ) {
                            if (searchEditText.text.toString().isEmpty()) {
                                searchClear.visibility = View.GONE
                                voiceSearch.visibility = View.VISIBLE
                            } else {
                                searchClear.visibility = (View.VISIBLE)
                                voiceSearch.visibility = (View.GONE)
                            }
                            viewModel.searchMeals(searchEditText.text.toString())
                        }

                        override fun afterTextChanged(s: Editable) {}
                    }
                )

                searchEditText.setOnEditorActionListener { _, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        searchEditText.clearFocus()
                        hideSoftKeyboard()
                        viewModel.searchMeals(searchEditText.text.toString())
                        loadingProgressBar.visibility = View.VISIBLE
                    }
                    false
                }

                searchEditText.setOnFocusChangeListener { _, hasFocus ->
                    if (hasFocus) {
                        val imm =
                            activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS)
                    }
                }

                searchBack.setOnClickListener { _ ->
                    hideSoftKeyboard()
                    viewModel.onBackClicked()
                }

                voiceSearch.setOnClickListener { _ -> startVoiceRecognitionActivity() }

                searchClear.setOnClickListener { _ ->
                    searchEditText.setText("")
                }
            }
        }
    }

    private fun startVoiceRecognitionActivity() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, resources.getString(R.string.voice_search))
        startActivityForResult(
            intent,
            REQUEST_CODE
        )
    }

    private fun hideSoftKeyboard() {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    private fun handleUpdate(uiModel: RecipeUiModel) {
        when (uiModel) {
            is RecipeUiModel.OnRecipesLoading -> {
                binding.loadingProgressBar.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
            }
            is RecipeUiModel.OnRecipesErrorLoad -> {
                binding.loadingProgressBar.visibility = View.GONE
                binding.recyclerView.visibility = View.GONE
            }
            is RecipeUiModel.OnRecipesLoaded -> {
                binding.loadingProgressBar.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
                mealAdapter.setData(uiModel.meals)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}