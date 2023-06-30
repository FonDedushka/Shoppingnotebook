package com.mikula441.shoppingnotebook.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.mikula441.shoppingnotebook.R
import com.mikula441.shoppingnotebook.databinding.ActivityMainBinding
import com.mikula441.shoppingnotebook.dialogs.NewListDialog
import com.mikula441.shoppingnotebook.fragments.FragmentManager
import com.mikula441.shoppingnotebook.fragments.NoteFragment
import com.mikula441.shoppingnotebook.fragments.ShopListNamesFragment
import com.mikula441.shoppingnotebook.settings.SettingsActivity

class MainActivity : AppCompatActivity(), NewListDialog.Listener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var defPref: SharedPreferences
    private var currentMenuItemId = R.id.shop_list
    private var currentTheme = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        defPref = PreferenceManager.getDefaultSharedPreferences(this)
        setTheme(getSelectedTheme())
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        currentTheme = defPref.getString("theme_key", "green").toString()
        FragmentManager.setFragment(ShopListNamesFragment.newInstance(), this)
        setBottomNavListener()
    }

    private fun setBottomNavListener(){
        binding.bNav.setOnItemSelectedListener {
            when(it.itemId){
                R.id.settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                }
                R.id.notes -> {
                    currentMenuItemId = R.id.notes
                    FragmentManager.setFragment(NoteFragment.newInstance(), this)
                }
                R.id.shop_list -> {
                    currentMenuItemId = R.id.shop_list
                    FragmentManager.setFragment(ShopListNamesFragment.newInstance(), this)
                }
                R.id.new_item -> {
                   FragmentManager.currentFrag?.onClickNew()
                }
            }
            true
        }
    }

    override fun onResume() {
        super.onResume()
        binding.bNav.selectedItemId = currentMenuItemId
        if (defPref.getString("theme_key", "green") != currentTheme) recreate()
    }

    private fun getSelectedTheme(): Int{
        return if (defPref.getString("theme_key", "green") == "green"){
            R.style.Base_Theme_ShoppingNotebookGreen
        } else {
            R.style.Base_Theme_ShoppingNotebookViolet
        }

    }

    override fun onClick(name: String) {
        Log.d("MyLog", "Name")
    }
}