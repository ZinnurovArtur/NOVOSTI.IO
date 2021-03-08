package com.example.newsapp

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.print.PdfPrint
import android.print.PrintAttributes
import android.print.PrintManager
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.news_content.*
import models.Newsdata
import java.text.SimpleDateFormat
import java.util.*


/**
 * Specific news item activity
 */
class NewsitemActivity : AppCompatActivity() {

    private var mAuth = FirebaseAuth.getInstance()
    private var databse = Firebase.database.reference
    private lateinit var webView2: WebView
    var pageFinished = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.news_content)
        initialiseData()
    }

    /**
     * Populate data on the layout of the news
     */
    fun initialiseData() {
        val imageView = findViewById<ImageView>(R.id.image_description)
        val author = findViewById<TextView>(R.id.author_article)
        val date = findViewById<TextView>(R.id.date)
        val title_article = findViewById<TextView>(R.id.title_article)
        val webView = findViewById<WebView>(R.id.webview)
        val arcticle_description = findViewById<TextView>(R.id.article_description)
        val button = findViewById<AppCompatButton>(R.id.read_more)
        val buttonTosave = findViewById<AppCompatButton>(R.id.addtoFavorite)
        val download = findViewById<AppCompatButton>(R.id.download)


        val content = intent.extras?.getString("content")
        val url = intent.extras?.getString("img")
        val source = intent.extras?.getString("source")
        val dataofDate = intent.extras?.getString("date")
        val title = intent.extras?.getString("title")
        val topics = intent.extras?.getString("category")


        //Get the image
        Picasso.get().load(url).into(imageView)
        title_article.text = title
        arcticle_description.text = content
        author.text = source

        if (dataofDate != null) {
            date.text = dataofDate.toDate().formatTo("dd MMM yyyy")


        }

        //Load the webview of the news
        button.setOnClickListener {
            webView.settings.allowContentAccess = true
            webView.settings.javaScriptEnabled = false


            var weburl = intent.extras?.getString("urlArticle")
            webView.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView, url: String) {
                    super.onPageFinished(view, url)
                    webView2 = webView

                }
            }
            if (weburl != null) {
                webView.loadUrl(weburl)
                pageFinished = true
            }

        }
        var boolean = true

        //Save the news topic to profile
        buttonTosave.setOnClickListener {


            if (boolean) {
                var newsdata = Newsdata(url, topics, "")

                /**
                 * Update favorite button
                 */
                mAuth.currentUser?.uid?.let { it2 ->
                    buttonTosave.setBackgroundColor(Color.RED)
                    databse.child("users").child(it2).child("listOftopics").child(topics!!).setValue(newsdata)
                    Snackbar.make(it, "Added to favorite", Snackbar.LENGTH_LONG).show()
                    boolean = false
                }
            } else {
                mAuth.currentUser?.uid?.let { it2 ->
                    buttonTosave.setBackgroundColor(Color.parseColor("#FF8354"))
                    databse.child("users").child(it2).child("listOftopics").child(topics!!).removeValue()
                    Snackbar.make(it, "Removed from favorite", Snackbar.LENGTH_LONG).show()
                    boolean = true
                }
            }


        }

        //Download news if only page is loaded click listener
        download.setOnClickListener {
            if (pageFinished) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    downloadPage(webView, it)


                } else {
                    Snackbar.make(it, "Not available for this device", Snackbar.LENGTH_LONG).show()
                }

            } else {
                Snackbar.make(it, "Page is not loaded", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    /**
     * Preparing document to for saving from webview
     * @param webView from activity
     * @param view of the last activity
     */
    private fun downloadPage(webView: WebView, view: View) {
        var prinManager = this.getSystemService(Context.PRINT_SERVICE) as PrintManager
        var jobname = getString(R.string.app_name) + "--" + webView.title + "ID" + mAuth.currentUser?.uid
        var adapter = webView.createPrintDocumentAdapter(jobname)
        val attr = PrintAttributes.Builder().setMediaSize(PrintAttributes.MediaSize.ISO_A4)
            .setResolution(PrintAttributes.Resolution("pdf", "pdf", 600, 600))
            .setMinMargins(PrintAttributes.Margins.NO_MARGINS).build()
        val print = PdfPrint(attr)
        val downloadFolder = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absoluteFile

        if (downloadFolder != null) {
            print.print(adapter, downloadFolder, jobname + ".pdf")
            Snackbar.make(view, "Page is downloaded ", Snackbar.LENGTH_LONG).show()


        }
    }

    /**
     * Changing the Newspai date standart and convert it in readable way
     */
    fun String.toDate(
        dateFormat: String = "yyyy-MM-dd'T'HH:mm",
        timeZone: TimeZone = TimeZone.getTimeZone("UTC")
    ): Date {

        val parser = SimpleDateFormat(dateFormat, Locale.getDefault())
        parser.timeZone = timeZone
        var parsed = parser.parse(this)!!
        return parsed
    }


    fun Date.formatTo(dateFormat: String, timeZone: TimeZone = TimeZone.getDefault()): String {
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())

        formatter.timeZone = timeZone
        return formatter.format(this)
    }

}







