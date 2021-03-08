package com.example.newsapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth


/**
 * Settings fragment will additioanl features
 */
class SettingMenu : Fragment() {
    private var mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        (requireActivity() as AppCompatActivity).supportActionBar?.show()
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.settingsmenu, container, false)
        initialiseData(view)
        return view


    }

    /**
     * Initialise data on the layout activity
     */
    fun initialiseData(view: View) {
        val carddownload = view.findViewById<CardView>(R.id.card)
        val settngs = view.findViewById<CardView>(R.id.card2)
        val exit = view.findViewById<CardView>(R.id.card3)

        //Shows all downloaded items on download activity
        carddownload.setOnClickListener {
            val intent = Intent(activity, DownloadedItemActivity::class.java)
            startActivity(intent)

        }
        // Logout from program without returning back
        exit.setOnClickListener {
            mAuth.signOut()
            val intent = Intent(activity, Loginpageactivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        //Open settings for app configuration
        settngs.setOnClickListener {
            val intent = Intent(activity, SettingsActivity::class.java)
            startActivity(intent)

        }

    }


}
