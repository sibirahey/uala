package com.sibi.uala.model

sealed class RecipeUiModel {
    object OnRecipesLoading : RecipeUiModel()
    object OnRecipesErrorLoad : RecipeUiModel()

    class OnRecipesLoaded(val meals: List<MealUiModel>) : RecipeUiModel()
    class SelectedRecipe(val selectedRecipe: DetailMealUiModel) : RecipeUiModel()
}