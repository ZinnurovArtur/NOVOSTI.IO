package com.example.newsapp

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import models.News

/**
 * Adapter for populating the news
 */
class Adapter(private val newsList: List<News>, query: String) : RecyclerView.Adapter<Adapter.ViewHolder>() {
    private var query: String = query

    /**
     * inner class for creating rhe Recyclview.viewholder and setting the infromation for each element of news
     */
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var imageView = itemView.findViewById<ImageView>(R.id.image)
        var articleTitle = itemView.findViewById<TextView>(R.id.title)
        var summury = itemView.findViewById<TextView>(R.id.sammury)
        var cardView = itemView.findViewById<CardView>(R.id.card)
        var topicView = itemView.findViewById<TextView>(R.id.topic)
        private var emptyUrlimg = "https://encrypted-tbn2.gstatic.com/" +
                "images?q=tbn:ANd9GcTgTt76bpR_R2opJ5Aa3yYPbRBK0hSQW7AOCXyM9O7BxNGAhnkp"

        lateinit var urlImag: String
        lateinit var newsTitle: String
        lateinit var newsSource: String
        lateinit var newsDate: String
        lateinit var newsArticle: String
        lateinit var urlArticle: String
        lateinit var category: String

        /**
         * Biding the information  on view layout
         */
        fun bind(news: News, query: String) {
            articleTitle.text = news.title
            newsTitle = news.title
            newsDate = news.publishedAt
            newsArticle = news.content
            urlArticle = news.url

            /**
             * Todo this needs to be validated
             */

            if (news.source.id.isNullOrEmpty() || news.source.name.isNullOrEmpty()) {
                summury.text = news.author
                if (news.author.isNullOrEmpty()) {
                    newsSource = ""
                } else {
                    newsSource = news.author
                }

            } else {
                summury.text = news.source.id
                newsSource = news.source.id
            }
            if (news.urlToImage.isNullOrEmpty()) {
                imageView.setImageResource(R.drawable.gradient_shadow)
                urlImag = emptyUrlimg

            } else {
                Picasso.get().load(news.urlToImage).into(imageView)
                urlImag = news.urlToImage
            }

            if (news.content.isNullOrEmpty()) {
                newsArticle = ""
            } else {
                newsArticle = news.content
            }

            if (news.title.isNullOrEmpty()) {
                newsTitle = "NoTitle"
            } else {
                newsTitle = news.title
            }

            if (query.isNullOrEmpty()) {
                category = "General".toUpperCase()
                topicView.text = category
            } else {
                category = query.toUpperCase()
                topicView.text = category
            }


        }

        init {
            cardView.setOnClickListener(this)
        }

        /**
         * Sending required data to the activity and use for the future
         * @param view actual view lauout
         *
         */
        override fun onClick(view: View) {

            val intent = Intent(view.context, NewsitemActivity::class.java)
            intent.putExtra("img", urlImag)
            intent.putExtra("content", newsArticle)
            intent.putExtra("date", newsDate)
            intent.putExtra("title", newsTitle)
            intent.putExtra("urlArticle", urlArticle)
            intent.putExtra("category", category)
            intent.putExtra("source", newsSource)
            view.context.startActivity(intent)


        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        /**
         * changed layout to news
         */
        val view = LayoutInflater.from(parent.context).inflate(R.layout.news, parent, false)
        return ViewHolder(view)

    }

    /**
     * Size of the array of news data
     * @return size of the arraylist
     */
    override fun getItemCount(): Int {
        return newsList.size
    }

    /**
     * Bind the specific element of news to the place and shows the in right order sequence
     * @param holder holder of recyclview
     * @param position specific place
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(newsList[position], query)
    }


}



