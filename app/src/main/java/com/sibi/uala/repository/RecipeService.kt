package com.sibi.uala.repository

import com.sibi.uala.model.RecipeResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RecipeService {

    @GET("api/json/v1/1/search.php")
    suspend fun searchRecipes(
        @Query("s") searchString: String
    ): Response<RecipeResponse?>
}