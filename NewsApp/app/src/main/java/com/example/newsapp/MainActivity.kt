package com.example.newsapp

import android.app.*
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.annotation.NonNull
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.ktx.storage

/**
 * Main class for inirialising application
 */
class MainActivity : AppCompatActivity() {
    private var mAuth = FirebaseAuth.getInstance()
    private var storage = Firebase.storage.reference

    /**
     * Initialise activity
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        supportActionBar?.setCustomView(R.layout.action_layout);

        super.onCreate(savedInstanceState)
        var code = intent.getIntExtra("code", 1)
        if (code == 1) {
            //Check if setting activity switch has value 1 and change the theme for app
            setTheme(R.style.AppTheme)
        } else if (code == 2) {
            //Check if setting activity switch has value 2 and change the theme for app
            setTheme(R.style.DarkTheme)
        }
        setContentView(R.layout.activity_main)


        val bottom_menu = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottom_menu.setOnNavigationItemSelectedListener(navigationBar)

        openFragment(Newsfragment())


    }

    /**
     * Navigation bar button listener to move between fragments
     */
    private val navigationBar = BottomNavigationView.OnNavigationItemSelectedListener { item ->

        when (item.itemId) {
            R.id.account_button -> {

                var fragment = AccountFragment()
                item.setIconTintList(null)
                loadThumbnail(item)


                openFragment(fragment)



                return@OnNavigationItemSelectedListener true
            }
            R.id.news_button -> {
                var fragment = Newsfragment()
                openFragment(fragment)



                return@OnNavigationItemSelectedListener true
            }
            R.id.menu_button -> {
                var fragment = SettingMenu()
                openFragment(fragment)



                return@OnNavigationItemSelectedListener true
            }

        }
        false


    }

    /**
     * Open fragment and cancel current fragmen
     * @param fragment new
     */
    private fun openFragment(fragment: Fragment) {
        var transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.mainframe, fragment)
        transaction.addToBackStack(null)
        transaction.commit()


    }

    /**
     * Load thumbnail on button navigation avatar asynchronously
     * @param menuItem avatar item
     */
    private fun loadThumbnail(menuItem: MenuItem) {


        mAuth.currentUser?.let {
            storage.child(it.uid).child("images/Profile_img")
                .downloadUrl
                .addOnCompleteListener(object : OnCompleteListener<Uri> {
                    override fun onComplete(p0: Task<Uri>) {


                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            menuItem.iconTintList = null
                            menuItem.iconTintMode = null
                        }
                        if (p0.isSuccessful) {
                            GlideApp.with(applicationContext)
                                .asBitmap().load(p0.result.toString()).apply(
                                    RequestOptions().circleCrop()
                                        .placeholder(R.drawable.ic_baseline_account_circle_24)
                                )
                                .error(R.drawable.ic_baseline_account_circle_24)
                                .into(object : SimpleTarget<Bitmap>() {
                                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                        val drawable = BitmapDrawable(resources, resource) as Drawable
                                        menuItem.setIcon(drawable)

                                    }
                                })
                        }
                    }
                })
                .addOnFailureListener(object : OnFailureListener {
                    override fun onFailure(@NonNull p0: Exception) {
                        if (p0 is StorageException &&
                            (p0 as StorageException).errorCode == StorageException.ERROR_OBJECT_NOT_FOUND
                        ) {
                            GlideApp.with(applicationContext)
                                .asBitmap().load(
                                    "https://toppng.com/uploads/preview/" +
                                            "app-icon-set-login-icon-comments-avatar-icon-11553436380yill0nchdm.png"
                                ).apply(
                                    RequestOptions().circleCrop()
                                        .placeholder(R.drawable.ic_baseline_account_circle_24)
                                )
                                .error(R.drawable.ic_baseline_account_circle_24)
                                .into(object : SimpleTarget<Bitmap>() {
                                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                        val drawable = BitmapDrawable(resources, resource) as Drawable
                                        menuItem.setIcon(drawable)
                                        Log.i("Error storage", "File not exist");

                                    }

                                })


                        }
                    }
                })
        }
    }
}


