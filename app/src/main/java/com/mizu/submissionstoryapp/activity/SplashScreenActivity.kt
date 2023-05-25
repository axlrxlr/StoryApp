package com.mizu.submissionstoryapp.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.mizu.submissionstoryapp.MainActivity
import com.mizu.submissionstoryapp.activity.viewmodel.LoginViewModel
import com.mizu.submissionstoryapp.activity.viewmodel.ViewModelFactory
import com.mizu.submissionstoryapp.databinding.ActivitySplashScreenBinding
import com.mizu.submissionstoryapp.datastore.LoginPreferences

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "login")

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding
    private var isLogged: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hide the action bar
        supportActionBar?.hide()

        // Initialize the LoginPreferences
        val pref = LoginPreferences.getInstance(dataStore)

        // Initialize the LoginViewModel
        val loginViewModel = ViewModelProvider(this, ViewModelFactory(pref))[LoginViewModel::class.java]

        // Create the intents for the next activities
        val showList = Intent(this@SplashScreenActivity, StoryListActivity::class.java)
        val login = Intent(this@SplashScreenActivity, MainActivity::class.java)

        // Observe the session token LiveData and check if the user is logged in
        loginViewModel.getSessionToken().observe(this) { sessionToken ->
            isLogged = sessionToken.isNotEmpty()
            if (isLogged) {
                showList.putExtra(StoryListActivity.USER_TOKEN, sessionToken)
            }
        }

        // Start the logo animation
        binding.ivSplashLogo.alpha = 0f
        binding.ivSplashLogo.animate().setDuration(2000).alpha(1f).withEndAction {
            // Check if the user is logged in and start the appropriate activity
            if (isLogged) {
                startActivity(showList)
            } else {
                startActivity(login)
            }
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
    }
}