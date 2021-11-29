package com.canonal.sharedpref

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.canonal.sharedpref.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

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

        //ENCRYPTED SHARED PREFERENCES
        //reading and writing is same as normal shared preferences
        //below code is for version 1.0.0 which supports API 23+
//        val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
//        val mainKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)
//        val encryptedSharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
//            "Encrypted Shared Preferences Sample",
//            mainKeyAlias,
//            applicationContext,
//            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
//            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
//        )

        //For version 1.1.0-alpha03 which supports API 21+
        val mainKey = MasterKey.Builder(applicationContext)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        val encryptedSharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
            applicationContext,
            "Encrypted Shared Preferences Sample",
            mainKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        //read data using object
        val usernameSharedPref = sharedPref.getString("username", "default value")
        val usernamePref = pref.getString("username", "default value")
        val usernameEncryptedSharedPref =
            encryptedSharedPreferences.getString("username", "default value")

        val cbStateSharedPref = sharedPref.getBoolean("cbState", false)
        val cbStatePref = pref.getBoolean("cbState", false)
        val cbStateEncryptedSharedPref = encryptedSharedPreferences.getBoolean("cbState", false)


        if (usernameSharedPref?.isEmpty() == false) {
            etUsername.setText(usernameSharedPref)
            cbRememberMe.isChecked = cbStateSharedPref
        }
        if (usernamePref?.isEmpty() == false) {
            etUsername.setText(usernamePref)
            cbRememberMe.isChecked = cbStatePref
        }
        if (usernameEncryptedSharedPref?.isEmpty() == false) {
            etUsername.setText(usernameEncryptedSharedPref)
            cbRememberMe.isChecked = cbStateEncryptedSharedPref
        }

        //write to shared preferences
        val sharedPrefEditor = sharedPref.edit()
        val prefEditor = pref.edit()
        val encryptedSharedPrefEditor = encryptedSharedPreferences.edit()

        btnLogin.setOnClickListener {
            if (cbRememberMe.isChecked) {
                //write data using editor
                saveOrClearSharedPreferences(
                    sharedPrefEditor,
                    prefEditor,
                    encryptedSharedPrefEditor,
                    etUsername.text.toString(),
                    true
                )
                applySharedPreferences(sharedPrefEditor, prefEditor, encryptedSharedPrefEditor)
            } else {
                saveOrClearSharedPreferences(
                    sharedPrefEditor,
                    prefEditor,
                    encryptedSharedPrefEditor,
                    "",
                    false
                )
                applySharedPreferences(sharedPrefEditor, prefEditor, encryptedSharedPrefEditor)
            }
        }

        val EXAMPLE_COUNTER = intPreferencesKey("example_counter")
        val exampleCounterFlow: Flow<Int> = dataStore.data.map { preferences ->
            //No type safety
            preferences[EXAMPLE_COUNTER] ?: 0

        }

        //incrementCounter(EXAMPLE_COUNTER)

    }

    suspend fun incrementCounter(EXAMPLE_COUNTER: Preferences.Key<Int>){
        dataStore.edit { settings ->
            //read
            val currentCounterValue = settings[EXAMPLE_COUNTER] ?: 0
            //write
            settings[EXAMPLE_COUNTER] = currentCounterValue + 1
        }
    }

    private fun saveOrClearSharedPreferences(
        sharedPrefEditor: SharedPreferences.Editor,
        prefEditor: SharedPreferences.Editor,
        encryptedSharedPrefEditor: SharedPreferences.Editor,
        username: String,
        cbState: Boolean
    ) {
        sharedPrefEditor.putString("username", username)
        prefEditor.putString("username", username)
        encryptedSharedPrefEditor.putString("username", username)

        sharedPrefEditor.putBoolean("cbState", cbState)
        prefEditor.putBoolean("cbState", cbState)
        encryptedSharedPrefEditor.putBoolean("cbState", cbState)
    }

    private fun applySharedPreferences(
        sharedPrefEditor: SharedPreferences.Editor,
        prefEditor: SharedPreferences.Editor,
        encryptedSharedPrefEditor: SharedPreferences.Editor,
    ) {
        //writes data synchronously
        //sharedPrefEditor.commit()
        //prefEditor.commit()

        //writes data asynchronously
        sharedPrefEditor.apply()
        prefEditor.apply()
        encryptedSharedPrefEditor.apply()
    }
}