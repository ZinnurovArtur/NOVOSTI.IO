package models

import com.google.gson.annotations.SerializedName

/**
 * Structure of news data in application
 */

data class News(
    val author: String,
    val content: String,
    val description: String,
    val publishedAt: String,
    val source: Source,
    val title: String,
    val url: String,
    val urlToImage: String
)
data class Everythingnews (
    val status: String,
    val totalResults: Int,
    val articles: List<News>
)

data class Source(
    val id: String,
    @SerializedName("_id")
    val name: String)


