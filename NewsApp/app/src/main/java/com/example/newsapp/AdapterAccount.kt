package com.example.newsapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import models.Newsdata

/**
 * Adapter class for populating topics news elements on account fragment
 */

class AdapterAccount(private val listTopics: MutableList<Newsdata>) :
    RecyclerView.Adapter<AdapterAccount.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        /**
         * changed layout to news
         */
        val view = LayoutInflater.from(parent.context).inflate(R.layout.news, parent, false)
        return ViewHolder(view)

    }

    /**
     * Size of the array of items
     * @param size
     */
    override fun getItemCount(): Int {
        return listTopics.size
    }

    /**
     * Remove element from specific position
     */
    fun removeAt(position: Int) {
        listTopics.removeAt(position)
        notifyItemRemoved(position)
    }


    /**
     * Inner class with initialising the layout
     */
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        lateinit var urlImag: String
        lateinit var topicName: String

        private var emptyUrlimg = "https://encrypted-tbn2.gstatic.com/" +
                "images?q=tbn:ANd9GcTgTt76bpR_R2opJ5Aa3yYPbRBK0hSQW7AOCXyM9O7BxNGAhnkp"

        var titleNews = itemView.findViewById<TextView>(R.id.title)
        var urlImgview = itemView.findViewById<ImageView>(R.id.image)
        var cardView = itemView.findViewById<CardView>(R.id.card)

        /**
         * Bind the data on the layout
         * @param newsdata class
         */
        fun bind(topics: Newsdata) {
            topicName = topics.topic.toString()
            urlImag = topics.urlImg.toString()

            if (topicName.isNullOrEmpty()) {
                titleNews.text = "Saved topic"
            } else {
                titleNews.text = topicName
            }

            if (urlImag.isNullOrEmpty()) {
                urlImgview.setImageResource(R.drawable.gradient_shadow)
                Picasso.get().load(emptyUrlimg).into(urlImgview)//load picture thumbnail

            } else {
                Picasso.get().load(urlImag).into(urlImgview)
            }
        }

        init {
            cardView.setOnClickListener(this)
        }

        /**
         * Go to the fragment with the specific news topic
         */
        override fun onClick(v: View?) {
            var args = Bundle()
            args.putString("topic", topicName)
            var fragment = Newsfragment()
            fragment.arguments = args
            openFragment(fragment, v)

        }


    }

    /**
     * Get the newsdata from specific position
     * @return newsdata from specification
     */
    fun getUserdata(position: Int): Newsdata {
        return listTopics.get(position)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listTopics[position])
    }

    /**
     * Close current fragment and start initialise the next fragment
     * @param  fragment new to initialise
     * @param view current view
     */
    private fun openFragment(fragment: Fragment, v: View?) {
        val activity = v?.getContext() as AppCompatActivity
        val fragmentManager = activity.supportFragmentManager

        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.mainframe, fragment)
        transaction.addToBackStack(null)
        transaction.commit()


    }


}