package com.sibi.uala.viewmodel


import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sibi.uala.model.DetailMealUiModel
import com.sibi.uala.model.MealUiModel
import com.sibi.uala.model.MealsResponse
import com.sibi.uala.model.RecipeUiModel
import com.sibi.uala.repository.RecipeRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class MainViewModel @ViewModelInject constructor(
    private val recipeRepository: RecipeRepository
) : ViewModel(), CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private val recipeUiModel = MutableLiveData<RecipeUiModel>()

    private val recipiesMutableLiveData = MutableLiveData<List<MealsResponse>>()

    fun getRecipeUiModels(): LiveData<RecipeUiModel> {
        return recipeUiModel
    }

    fun searchMeals(stringToSearch: String) {
        viewModelScope.launch {
            recipeUiModel.postValue(RecipeUiModel.OnRecipesLoading)
            try {
                val response = recipeRepository.searchRecipes(stringToSearch)
                if (response != null && !response.meals.isNullOrEmpty()) {
                    val mealsList = mutableListOf<MealUiModel>()
                    response.meals.forEach {
                        mealsList.add(
                            MealUiModel(
                                it.idMeal.orEmpty(),
                                it.strMeal.orEmpty(),
                                it.strCategory.orEmpty(),
                                it.strMealThumb.orEmpty()
                            )
                        )
                    }
                    recipiesMutableLiveData.value = response.meals
                    recipeUiModel.postValue(RecipeUiModel.OnRecipesLoaded(mealsList))
                } else {
                    recipeUiModel.postValue(RecipeUiModel.OnRecipesErrorLoad)
                }
            } catch (e: Exception) {
                recipeUiModel.postValue(RecipeUiModel.OnRecipesErrorLoad)
            }
        }
    }

    fun onBackClicked() {

    }

    fun selectedRecipe(selectedMeal: MealUiModel) {
        val selectedRecipe = recipiesMutableLiveData.value?.find { it.idMeal == selectedMeal.id }
        selectedRecipe?.let {
            val ingredientsList = mutableListOf<String>()
            recipeUiModel.postValue(
                RecipeUiModel.SelectedRecipe(
                    DetailMealUiModel(
                        selectedRecipe.strMeal.orEmpty(),
                        selectedRecipe.strCategory.orEmpty(),
                        selectedRecipe.strMealThumb.orEmpty(),
                        ingredientsList,
                        selectedRecipe.strYoutube.orEmpty()
                    )
                )
            )
        }
    }
}