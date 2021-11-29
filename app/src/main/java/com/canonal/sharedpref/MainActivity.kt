package com.canonal.sharedpref

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.canonal.sharedpref.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val btnLogin = binding.btnLogin
        val cbRememberMe = binding.cbRememberMe
        val etUsername = binding.etUsername

        val sharedPref = getSharedPreferences("Sample Shared Preferences", Context.MODE_PRIVATE)
        val pref = getPreferences(Context.MODE_PRIVATE)

        //read data using object
        val usernameSharedPref = sharedPref.getString("username", "default value")
        val usernamePref = pref.getString("username", "default value")

        val checkBoxStateSharedPref = sharedPref.getBoolean("checkBoxState", false)
        val checkBoxStatePref = pref.getBoolean("checkBoxState", false)


        if (usernameSharedPref?.isEmpty() == false) {
            etUsername.setText(usernameSharedPref)
            cbRememberMe.isChecked = checkBoxStateSharedPref
        }
        if (usernamePref?.isEmpty() == false) {
            etUsername.setText(usernamePref)
            cbRememberMe.isChecked = checkBoxStatePref

        }

        //write to shared preferences
        val sharedPrefEditor = sharedPref.edit()
        val prefEditor = pref.edit()

        btnLogin.setOnClickListener {
            if (cbRememberMe.isChecked) {
                //write data using editor
                saveOrClearSharedPreferences(
                    sharedPrefEditor,
                    prefEditor,
                    etUsername.text.toString(),
                    true
                )
                applySharedPreferences(sharedPrefEditor, prefEditor)
            } else {
                saveOrClearSharedPreferences(
                    sharedPrefEditor,
                    prefEditor,
                    "",
                    false
                )
                applySharedPreferences(sharedPrefEditor, prefEditor)
            }
        }

    }

    private fun saveOrClearSharedPreferences(
        sharedPrefEditor: SharedPreferences.Editor,
        prefEditor: SharedPreferences.Editor,
        username: String,
        cbState: Boolean
    ) {
        sharedPrefEditor.putString("username", username)
        prefEditor.putString("username", username)

        sharedPrefEditor.putBoolean("checkBoxState", cbState)
        prefEditor.putBoolean("checkBoxState", cbState)
    }

    private fun applySharedPreferences(
        sharedPrefEditor: SharedPreferences.Editor,
        prefEditor: SharedPreferences.Editor,
    ) {
        //writes data synchronously
        //sharedPrefEditor.commit()
        //prefEditor.commit()

        //writes data asynchronously
        sharedPrefEditor.apply()
        prefEditor.apply()
    }
}