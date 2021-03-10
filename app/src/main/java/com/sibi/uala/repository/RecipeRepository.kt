package com.sibi.uala.repository

import com.sibi.uala.model.RecipeResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RecipeRepository @Inject constructor(private val recipeService: RecipeService) {

    suspend fun searchRecipes(searchString: String): RecipeResponse? = withContext(Dispatchers.IO) {
        val gifResponse = recipeService.searchRecipes(searchString)
        return@withContext if (gifResponse.isSuccessful) {
            gifResponse.body()
        } else {
            null
        }
    }
}
