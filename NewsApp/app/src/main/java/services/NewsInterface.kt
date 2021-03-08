package services

import models.Everythingnews
import models.News
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import io.reactivex.Observable


/**
 * Interface for NewsApi requests
 */
interface NewsInterface {


    @GET("everything?q=top&from=2020-11-16&sortBy=publishedAt&pageSize=100")
    fun getAllnews(
        @Query("apiKey") apiKey: String
    ): Call<Everythingnews>

    @GET("everything")
    fun getUserSearchInput(
        @Query("apiKey") apiKey: String,
        @Query("q") q: String,
        @Query("from") date: String,
        @Query("sortBy") sort: String,
        @Query("pageSize") num: Int,
        @Query("language") language: String,


        ): Call<Everythingnews>

    @GET("top-headlines")
    fun getTopheadlines(
        @Query("apiKey") apiKey: String,
        @Query("category") cat: String,
        @Query("pageSize") num: Int,
        @Query("country") country: String,
        @Query("language") language: String,
        @Query("sortBy") sort: String,


        ): Call<Everythingnews>

}
