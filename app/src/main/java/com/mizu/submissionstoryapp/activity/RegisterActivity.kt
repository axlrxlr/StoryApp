package com.mizu.submissionstoryapp.activity

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.mizu.submissionstoryapp.MainActivity
import com.mizu.submissionstoryapp.R
import com.mizu.submissionstoryapp.activity.viewmodel.RegisterViewModel
import com.mizu.submissionstoryapp.api.RegisterResponse
import com.mizu.submissionstoryapp.databinding.ActivityRegisterBinding

@Suppress("DEPRECATION")
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var nameInput: String
    private lateinit var emailInput: String
    private lateinit var passwordInput: String
    private lateinit var passwordConfirm: String

    private val viewModel by viewModels<RegisterViewModel>()

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        binding.etEmailRegister.clearFocus()
        binding.etUsernameRegister.clearFocus()
        binding.etPasswordRegister.clearFocus()
        binding.etPasswordConfirm.clearFocus()

        if (binding.etEmailRegister.text.toString().isNotEmpty() || binding.etUsernameRegister.text.toString().isNotEmpty() ||
            binding.etPasswordRegister.text.toString().isNotEmpty() || binding.etPasswordConfirm.text.toString().isNotEmpty()) {
            AlertDialog.Builder(ContextThemeWrapper(this, R.style.CustomAlertDialogStyle))
                .setTitle(R.string.regis_cancel)
                .setMessage(R.string.regis_cancel_desc)
                .setPositiveButton(R.string.btn_yes) { _, _ ->
                    val backToMain = Intent(this@RegisterActivity, MainActivity::class.java)
                    startActivity(backToMain)
                    finish()
                }
                .setNegativeButton(R.string.btn_no, null)
                .show()
        }else {
            val backToMain = Intent(this@RegisterActivity, MainActivity::class.java)
            startActivity(backToMain)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()



        binding.btnSubmitRegister.setOnClickListener {

            binding.etEmailRegister.clearFocus()
            binding.etUsernameRegister.clearFocus()
            binding.etPasswordRegister.clearFocus()
            binding.etPasswordConfirm.clearFocus()

            nameInput = binding.etUsernameRegister.text.toString()
            emailInput = binding.etEmailRegister.text.toString()
            passwordInput = binding.etPasswordRegister.text.toString()
            passwordConfirm = binding.etPasswordConfirm.text.toString()

            if (binding.etEmailRegister.error == null && binding.etUsernameRegister.error == null &&
                binding.etPasswordRegister.error == null && binding.etPasswordConfirm.error == null &&
                nameInput.isNotEmpty() && emailInput.isNotEmpty() && passwordInput.isNotEmpty() && passwordConfirm.isNotEmpty() &&
                passwordInput == passwordConfirm){

                viewModel.postRegister(nameInput, emailInput, passwordInput)

            }else{
                if(passwordInput != passwordConfirm){
                    AlertDialog.Builder(ContextThemeWrapper(this, R.style.CustomAlertDialogStyle))
                        .setTitle("Error")
                        .setMessage(R.string.pass_mismatch)
                        .setPositiveButton(R.string.btn_ok) { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                }else{
                    AlertDialog.Builder(ContextThemeWrapper(this, R.style.CustomAlertDialogStyle))
                        .setTitle("Error")
                        .setMessage(R.string.register_error)
                        .setPositiveButton(R.string.btn_ok) { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                }


            }
        }

        viewModel.registerResponse.observe(this){
            if(it){
                AlertDialog.Builder(ContextThemeWrapper(this, R.style.CustomAlertDialogStyle))
                    .setTitle("Error")
                    .setMessage(R.string.email_taken)
                    .setPositiveButton(R.string.btn_ok) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
        }

        binding.lbLoginLink.setOnClickListener{
            if (binding.etEmailRegister.text.toString().isNotEmpty() || binding.etUsernameRegister.text.toString().isNotEmpty() ||
                binding.etPasswordRegister.text.toString().isNotEmpty() || binding.etPasswordConfirm.text.toString().isNotEmpty()) {
                AlertDialog.Builder(ContextThemeWrapper(this, R.style.CustomAlertDialogStyle))
                    .setTitle(R.string.regis_cancel)
                    .setMessage(R.string.regis_cancel_desc)
                    .setPositiveButton(R.string.btn_yes) { _, _ ->
                        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    .setNegativeButton(R.string.btn_no, null)
                    .show()
            }else {
                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        startAnimation()


        viewModel.returnRegister.observe(this){
            registerSubmit(it)
        }

        viewModel.isLoading.observe(this){
            showLoading(it)
        }

    }

    private fun startAnimation() {
        val title = ObjectAnimator.ofFloat(binding.lbRegister, View.ALPHA, 1f).setDuration(100)
        val desc = ObjectAnimator.ofFloat(binding.lbDescRegister, View.ALPHA, 1f).setDuration(100)
        val email = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(binding.lbEmailRegister, View.ALPHA, 1f).setDuration(100),
                ObjectAnimator.ofFloat(binding.etEmailRegister, View.ALPHA, 1f).setDuration(100)
            )
        }
        val name =  AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(binding.lbUsernameRegister, View.ALPHA, 1f).setDuration(100),
                ObjectAnimator.ofFloat(binding.etUsernameRegister, View.ALPHA, 1f).setDuration(100)
            )
        }
        val password =  AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(binding.lbPasswordRegister, View.ALPHA, 1f).setDuration(100),
                ObjectAnimator.ofFloat(binding.etPasswordRegister, View.ALPHA, 1f).setDuration(100)
            )
        }
        val confirmPassword =  AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(binding.lbPasswordConfirm, View.ALPHA, 1f).setDuration(100),
                ObjectAnimator.ofFloat(binding.etPasswordConfirm, View.ALPHA, 1f).setDuration(100)
            )
        }

        val link = ObjectAnimator.ofFloat(binding.lbLoginLink, View.ALPHA, 1f).setDuration(100)
        val button = ObjectAnimator.ofFloat(binding.btnSubmitRegister, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(title, desc, email, name, password, confirmPassword, link, button)
            start()
        }

    }

    private fun registerSubmit(it: RegisterResponse) {
        val showLogin = Intent(this@RegisterActivity, LoginActivity::class.java)
        showLogin.putExtra(LoginActivity.REGISTER_SUCCESS, it.error)
        when (it.error){
            true -> Toast.makeText(this, "User registration invalid.", Toast.LENGTH_SHORT).show()
            false -> {
                startActivity(showLogin)
                finish()
            }
        }

    }

    private fun showLoading(it: Boolean) {
        if (it) {
            binding.btnSubmitRegister.setBackgroundColor(resources.getColor(R.color.light_grey))
            binding.pbLoading.visibility = View.VISIBLE
        }else{
            binding.btnSubmitRegister.setBackgroundColor(resources.getColor(R.color.black))
            binding.pbLoading.visibility = View.GONE
        }
    }

}