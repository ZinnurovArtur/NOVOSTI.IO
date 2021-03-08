package com.example.newsapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import android.widget.Switch
import android.widget.ToggleButton
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatToggleButton
import androidx.appcompat.widget.SwitchCompat
import androidx.core.widget.CompoundButtonCompat

/**
 * Setting activity with dark theme feature
 */
class SettingsActivity :AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        supportActionBar?.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM)
        supportActionBar?.setCustomView(R.layout.action_layout)
        super.onCreate(savedInstanceState)

        // Using this method to save instance of switch toggle when opening next time
        val savedPreference = getSharedPreferences("save", MODE_PRIVATE)
        if(savedPreference.getBoolean("value",true)){
            setTheme(R.style.AppTheme)
        }else{
            setTheme(R.style.DarkTheme)
        }

        setContentView(R.layout.settingsactivity)
        val switchCompat = findViewById<SwitchCompat>(R.id.switch_bar)

        switchCompat.isChecked = savedPreference.getBoolean("value", true)


        switchCompat.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if (switchCompat.isChecked) {
                    val editor = getSharedPreferences("save", MODE_PRIVATE).edit()
                    editor.putBoolean("value", true)
                    editor.apply()
                    switchCompat.isChecked = true
                    toRestart(1)
                } else {
                    val editor = getSharedPreferences("save", MODE_PRIVATE).edit()
                    editor.putBoolean("value", false)
                    editor.apply()
                    switchCompat.isChecked = false
                    toRestart(2)
                }

            }
        })

    }

    /**
     * Restarting the main activity with specific theme preference
     * @param int code to pass
     */
    fun toRestart(int: Int) {
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.putExtra("code", int)
        startActivity(intent)
        finish()
    }
}

