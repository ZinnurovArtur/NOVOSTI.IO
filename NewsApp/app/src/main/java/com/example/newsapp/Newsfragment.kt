package com.example.newsapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import models.Everythingnews
import models.News
import models.Newsdata
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import services.NewsAPI
import services.NewsInterface
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


/**
 * News fragment with search bar
 */
class Newsfragment : Fragment() {
    private var mAuth = FirebaseAuth.getInstance()
    private var databse = Firebase.database.reference

    private var searchView: SearchView? = null
    private var queryTextListener: SearchView.OnQueryTextListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity() as AppCompatActivity).supportActionBar?.show()
        setHasOptionsMenu(true)


    }

    /**
     * Create view and validation the topic selected recyclview item from Account fragment
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        var view = inflater.inflate(R.layout.fragment_newsfragment, container, false)
        var bundle = this.arguments
        if (bundle != null) {
            var topic = bundle.getString("topic")
            Log.i("topic", "${topic}")
            if (topic != null) {
                loadData(view.findViewById(R.id.recycleView), true, topic, true)
            }
        } else {
            loadData(view.findViewById(R.id.recycleView), false, "", false)
        }



        return view
    }

    /**
     * Creating the searchmenu  option to choose specific topic
     * @param menu Appbar of the app
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search, menu)
        var searchMenu = menu.findItem(R.id.action_search)

        val searchManager = activity!!.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        if (searchMenu != null) {
            searchView = searchMenu.actionView as SearchView
        }
        searchView?.setSearchableInfo(searchManager.getSearchableInfo(getActivity()?.getComponentName()))
        queryTextListener = object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                return true
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                loadData(view!!.findViewById(R.id.recycleView), true, query, false)
                return true
            }

        }
        searchView!!.setOnQueryTextListener(queryTextListener)
        super.onCreateOptionsMenu(menu, inflater)

    }

    /**
     * Core Asynchronous call from NewsApi and populate to the recyclview
     * @param view recyclview to populate
     * @param custom if news for specific topic will be requested
     * @param query of the user
     * @param notification if notification for the news will be needed
     */
    fun loadData(view: RecyclerView, custom: Boolean, query: String, notification: Boolean): ArrayList<News> {
        val api = NewsAPI()
        val service = api.buildService(NewsInterface::class.java)

        var request =
            service.getTopheadlines("26a4740400c147549c839bede4e81081", "", 100, "", "en", "publishedAt AND popularity")
        if (custom) {
            request = service.getUserSearchInput(
                "26a4740400c147549c839bede4e81081",
                query,
                getCurrentdate(),
                "publishedAt AND relevancy AND popularity",
                100,
                ""
            )
        }
        val listNews = ArrayList<News>()
        request.enqueue(object : Callback<Everythingnews> {
            override fun onResponse(call: Call<Everythingnews>, response: Response<Everythingnews>) {
                if (response.isSuccessful) {
                    var articleList = response.body()!!
                    Log.i("Response", "onResponse: ${response.body()}")
                    listNews.addAll(articleList.articles)
                    if (notification) {

                        mAuth.currentUser?.uid?.let { it2 ->
                            // puting  the data info of news into Firebase
                            databse.child("usersNotif").child(it2).child("notif").child(query)
                                .setValue(
                                    Newsdata(
                                        articleList.articles[0].urlToImage,
                                        query,
                                        articleList.articles[0].title
                                    )
                                )
                            getUserTopicsdatabase(query)

                        }
                    }


                } else {
                    Toast.makeText(context, "Something went wrong ${response.message()}", Toast.LENGTH_SHORT)
                        .show()
                }
                view.apply {
                    layoutManager = LinearLayoutManager(activity)
                    adapter = Adapter(listNews, query)

                }
            }

            override fun onFailure(call: Call<Everythingnews>, t: Throwable) {


                Toast.makeText(context, "Something went wrong $t", Toast.LENGTH_LONG).show()
                Log.i(
                    "log",
                    t.message + "\t" + t.localizedMessage + "\t" + t.printStackTrace() + "\t" + t.cause + "\n" + call.request()
                )
            }

        }

        )

        return listNews
    }

    /**
     * Formating the the current time for the newsapi request
     * @return formated date
     */
    private fun getCurrentdate(): String {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formatted = current.format(formatter)
        return formatted

    }

    /**
     * Send notification and update and repeat every hour
     * @param newsdata latest
     */
    fun sendNotif(newsdata: Newsdata) {

        //Sending data to broadcast reciver
        var intent = Intent(context, BroadcastNotify::class.java)
        intent.putExtra("topic", newsdata.topic)
        intent.putExtra("title", newsdata.description)
        intent.putExtra("urlImg", newsdata.urlImg)

        var intentPending = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        var alarmManager = activity?.getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager

        var current = System.currentTimeMillis()
        // Interval could be changed to the 10 for debug purposes or less
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, current, 3600000, intentPending)


    }

    /**
     * Getting the lattest infro from database about user's topic news
     * @param query get the latest user's topic news
     */
    private fun getUserTopicsdatabase(query: String) {
        var newsData :Newsdata
        mAuth.currentUser?.uid?.let { it2 ->
            databse.child("usersNotif").child(it2).child("notif").addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    for (ds in dataSnapshot.children) {
                        if (query.equals(ds.key)) {
                            val topic = ds.child("topic").value
                            val urlImg = ds.child("urlImg").value
                            val description = ds.child("description").value
                            newsData = Newsdata(urlImg.toString(), topic.toString(), description.toString())
                            sendNotif(newsData)

                        }

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }
    }

}