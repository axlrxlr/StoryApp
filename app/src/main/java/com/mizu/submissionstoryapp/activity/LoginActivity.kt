package com.mizu.submissionstoryapp.activity

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.mizu.submissionstoryapp.MainActivity
import com.mizu.submissionstoryapp.R
import com.mizu.submissionstoryapp.activity.viewmodel.LoginViewModel
import com.mizu.submissionstoryapp.activity.viewmodel.ViewModelFactory
import com.mizu.submissionstoryapp.databinding.ActivityLoginBinding
import com.mizu.submissionstoryapp.datastore.LoginPreferences

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "login")

@Suppress("DEPRECATION")
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var email: String
    private lateinit var password: String

    companion object{
        const val REGISTER_SUCCESS = "register_success"
    }


    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {

        binding.etEmailLogin.clearFocus()
        binding.etPasswordLogin.clearFocus()

        if (binding.etEmailLogin.text.toString().isNotEmpty() || binding.etPasswordLogin.text.toString().isNotEmpty()){
            AlertDialog.Builder(ContextThemeWrapper(this, R.style.CustomAlertDialogStyle))
                .setTitle(R.string.login_cancel)
                .setMessage(R.string.login_cancel_desc)
                .setPositiveButton(R.string.btn_yes) { _, _ ->
                    val backToMain = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(backToMain)
                    finish()
                }
                .setNegativeButton(R.string.btn_no, null)
                .show()
        }else {
            val backToMain = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(backToMain)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        val pref = LoginPreferences.getInstance(dataStore)
        val viewModel = ViewModelProvider(this, ViewModelFactory(pref))[LoginViewModel::class.java]

        val registerSuccess = intent.getBooleanExtra(REGISTER_SUCCESS, true)
        if (!registerSuccess) {
            AlertDialog.Builder(ContextThemeWrapper(this, R.style.CustomAlertDialogStyle))
                .setTitle(R.string.register_success)
                .setMessage(R.string.register_success_desc)
                .setPositiveButton(R.string.btn_ok) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        startAnimation()


        binding.btnSubmitLogin.setOnClickListener {

            binding.etEmailLogin.clearFocus()
            binding.etPasswordLogin.clearFocus()

            email = binding.etEmailLogin.text.toString()
            password = binding.etPasswordLogin.text.toString()


            if (binding.etEmailLogin.error != null || binding.etPasswordLogin.error != null || email.isEmpty() || password.isEmpty()) {
                AlertDialog.Builder(ContextThemeWrapper(this, R.style.CustomAlertDialogStyle))
                    .setTitle("Error")
                    .setMessage(R.string.login_error)
                    .setPositiveButton(R.string.btn_ok) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }else{
                viewModel.postLogin(email, password)
            }
        }

        binding.lbRegisterLink.setOnClickListener{
            if (binding.etEmailLogin.text.toString().isNotEmpty() || binding.etPasswordLogin.text.toString().isNotEmpty()){
                AlertDialog.Builder(ContextThemeWrapper(this, R.style.CustomAlertDialogStyle))
                    .setTitle(R.string.login_cancel)
                    .setMessage(R.string.login_cancel_desc)
                    .setPositiveButton(R.string.btn_yes) { _, _ ->
                        val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    .setNegativeButton(R.string.btn_no, null)
                    .show()
            }else {
                val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        viewModel.loginResponse.observe(this){
            if(it){
                AlertDialog.Builder(ContextThemeWrapper(this, R.style.CustomAlertDialogStyle))
                    .setTitle("Error")
                    .setMessage(R.string.user_not_found)
                    .setPositiveButton(R.string.btn_ok) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
        }

        viewModel.loginResult.observe(this){
            viewModel.saveSessionToken("Bearer " + it.token)
            val login = Intent(this@LoginActivity, StoryListActivity::class.java)
            login.putExtra(StoryListActivity.USER_TOKEN,"Bearer " + it.token)
            startActivity(login)
            finish()
        }

       viewModel.isLoading.observe(this){
            showLoading(it)
        }
    }

    private fun startAnimation() {

        val title = ObjectAnimator.ofFloat(binding.lbLogin, View.ALPHA, 1f).setDuration(250)
        val desc = ObjectAnimator.ofFloat(binding.lbDescLogin, View.ALPHA, 1f).setDuration(250)
        val email = AnimatorSet().apply {
            playTogether(
            ObjectAnimator.ofFloat(binding.lbEmailLogin, View.ALPHA, 1f).setDuration(250),
            ObjectAnimator.ofFloat(binding.etEmailLogin, View.ALPHA, 1f).setDuration(250)
            )
        }

        val password =  AnimatorSet().apply {
            playTogether(
            ObjectAnimator.ofFloat(binding.lbPasswordLogin, View.ALPHA, 1f).setDuration(250),
            ObjectAnimator.ofFloat(binding.etPasswordLogin, View.ALPHA, 1f).setDuration(250)
            )
        }

        val link = ObjectAnimator.ofFloat(binding.lbRegisterLink, View.ALPHA, 1f).setDuration(250)
        val button = ObjectAnimator.ofFloat(binding.btnSubmitLogin, View.ALPHA, 1f).setDuration(250)

        AnimatorSet().apply {
            playSequentially(title, desc, email, password, link, button)
            start()
        }

    }

    private fun showLoading(it: Boolean) {
        if (it) {
            binding.btnSubmitLogin.setBackgroundColor(resources.getColor(R.color.light_grey))
            binding.pbLoading.visibility = View.VISIBLE
        }else{
            binding.btnSubmitLogin.setBackgroundColor(resources.getColor(R.color.black))
            binding.pbLoading.visibility = View.GONE
        }
    }
}