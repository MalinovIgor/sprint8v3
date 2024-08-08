package ru.startandroid.develop.sprint8v3.ui.main


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import ru.startandroid.develop.sprint8v3.R
import ru.startandroid.develop.sprint8v3.ui.MediatekaActivity
import ru.startandroid.develop.sprint8v3.ui.Search.SearchActivity
import ru.startandroid.develop.sprint8v3.ui.Settings.SettingsActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchButton = findViewById<LinearLayout>(R.id.search_button)

        val buttonClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                val displayIntent = Intent(this@MainActivity, SearchActivity::class.java)
                displayIntent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                Log.e("ActivitySearch", "Created!")

                startActivity(displayIntent)

            }
        }

        searchButton.setOnClickListener(buttonClickListener)

        val mediatekaButton = findViewById<LinearLayout>(R.id.mediateka)

        mediatekaButton.setOnClickListener {
            val displayIntent = Intent(this@MainActivity, MediatekaActivity::class.java)
            displayIntent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            startActivity(displayIntent)
        }

        val settingsButton = findViewById<LinearLayout>(R.id.settings)

        settingsButton.setOnClickListener {
            val displayIntent = Intent(this@MainActivity, SettingsActivity::class.java)
            displayIntent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            startActivity(displayIntent)
        }

    }

    fun isMainScreen(): Boolean {
        return this::class == MainActivity::class
    }

    override fun onBackPressed() {
        if (isMainScreen()) {
            moveTaskToBack(true)
        } else {
            super.onBackPressed()
        }
    }
}