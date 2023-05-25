package com.mizu.submissionstoryapp

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.ContextThemeWrapper
import android.view.View
import androidx.core.view.doOnPreDraw
import com.mizu.submissionstoryapp.activity.LoginActivity
import com.mizu.submissionstoryapp.activity.RegisterActivity
import com.mizu.submissionstoryapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        AlertDialog.Builder(ContextThemeWrapper(this, R.style.CustomAlertDialogStyle))
            .setTitle(R.string.exit_app)
            .setMessage(R.string.exit_validation)
            .setPositiveButton(R.string.btn_ok) { _, _ ->
                finish()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        supportPostponeEnterTransition()

        binding.ivLogo.doOnPreDraw {
            supportStartPostponedEnterTransition()
        }
        playAnimation()

        binding.btnLogin.setOnClickListener{
            val loginIntent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(loginIntent)
            finish()
        }

        binding.btnRegister.setOnClickListener{
            val registerIntent = Intent(this@MainActivity, RegisterActivity::class.java)
            startActivity(registerIntent)
            finish()
        }

        binding.btnLocalization.setOnClickListener{
            AlertDialog.Builder(ContextThemeWrapper(this, R.style.CustomAlertDialogStyle))
                .setTitle(R.string.device_setting)
                .setMessage(R.string.device_setting_validation)
                .setPositiveButton(R.string.btn_continue) { _, _ ->
                    startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                }
                .setNegativeButton(R.string.btn_cancel, null)
                .show()

        }
    }

    private fun playAnimation() {

        ObjectAnimator.ofFloat(binding.ivLogo, View.TRANSLATION_Y, -20f, 20f).apply {
            duration = 4500
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        ObjectAnimator.ofFloat(binding.ivLogoShadow, View.SCALE_X, 0.8f, 1f).apply {
            duration = 4500
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        ObjectAnimator.ofFloat(binding.ivLogoShadow, View.SCALE_Y, 0.4f, 0.5f).apply {
            duration = 4500
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        ObjectAnimator.ofFloat(binding.ivLogoShadow, View.ALPHA, 0.1f, 0.3f).apply {
            duration = 4500
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.tvTitle, View.ALPHA, 1f).setDuration(500)
        val desc = ObjectAnimator.ofFloat(binding.tvAppDesc, View.ALPHA, 1f).setDuration(500)
        val btnLogin = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(500)
        val btnRegister = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(500)

        val together = AnimatorSet().apply {
            playTogether(btnLogin, btnRegister)
        }

        AnimatorSet().apply {
            playSequentially(title, desc, together)
            start()
        }


    }


}