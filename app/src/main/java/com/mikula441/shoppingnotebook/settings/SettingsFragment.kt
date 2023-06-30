package com.mikula441.shoppingnotebook.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.mikula441.shoppingnotebook.R

class SettingsFragment: PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_preference, rootKey)
    }
}