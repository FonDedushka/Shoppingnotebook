package com.mikula441.shoppingnotebook.settings

import android.annotation.SuppressLint
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.preference.PreferenceManager
import com.mikula441.shoppingnotebook.R

class SettingsActivity : AppCompatActivity() {
    private lateinit var defPref: SharedPreferences
    @SuppressLint("CommitTransaction")
    override fun onCreate(savedInstanceState: Bundle?) {
        defPref = PreferenceManager.getDefaultSharedPreferences(this)
        setTheme(getSelectedTheme())
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        if (savedInstanceState == null){
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.placeHolder, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)finish()
        return super.onOptionsItemSelected(item)
    }

    private fun getSelectedTheme(): Int{
        return if (defPref.getString("theme_key", "green") == "green"){
            R.style.Base_Theme_ShoppingNotebookGreen
        } else {
            R.style.Base_Theme_ShoppingNotebookViolet
        }
    }
}