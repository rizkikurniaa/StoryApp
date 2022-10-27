package com.kikulabs.storyapp.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.kikulabs.storyapp.R
import com.kikulabs.storyapp.data.model.UserModel
import com.kikulabs.storyapp.data.model.UserPreference
import com.kikulabs.storyapp.databinding.ActivityLoginBinding
import com.kikulabs.storyapp.ui.main.MainActivity
import com.kikulabs.storyapp.ui.register.RegisterActivity
import com.kikulabs.storyapp.utils.ViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class LoginActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initListener()
        initViewModel()
        playAnimation()

    }

    private fun initView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun initListener() {
        binding.tvRegisterNow.setOnClickListener(this)
        binding.btnLogin.setOnClickListener(this)
    }

    private fun initViewModel() {
        loginViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[LoginViewModel::class.java]

        loginViewModel.isLoading.observe(this) { showLoading(it) }
    }

    private fun playAnimation() {
        //fade in
        val logo = ObjectAnimator.ofFloat(binding.imageView, View.ALPHA, 1f).setDuration(500)
        val title = ObjectAnimator.ofFloat(binding.tvTitle, View.ALPHA, 1f).setDuration(500)
        val email = ObjectAnimator.ofFloat(binding.tilEmail, View.ALPHA, 1f).setDuration(500)
        val password = ObjectAnimator.ofFloat(binding.tilPassword, View.ALPHA, 1f).setDuration(500)
        val login = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(500)
        val register = ObjectAnimator.ofFloat(binding.llRegister, View.ALPHA, 1f).setDuration(500)

        //play it sequentially
        AnimatorSet().apply {
            playSequentially(logo, title, email, password, login, register)
            start()
        }
    }

    private fun login() {
        val email = binding.edLoginEmail.text.toString().trim()
        val password = binding.edLoginPassword.text.toString().trim()
        when {
            email.isEmpty() -> {
                binding.tilEmail.error = getString(R.string.insert_email)
            }
            password.isEmpty() -> {
                binding.tilPassword.error = getString(R.string.insert_password)
            }
            else -> {
                loginViewModel.loginUser(email, password)
                loginViewModel.userLogin.observe(this) {
                    binding.progressBar.visibility = View.VISIBLE
                    if (it != null) {
                        AlertDialog.Builder(this).apply {
                            setTitle(getString(R.string.login_success))
                            setMessage("${getString(R.string.welcome)}, ${it.name}!")
                            setPositiveButton(getString(R.string.next)) { _, _ ->
                                val intent = Intent(context, MainActivity::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent,
                                    ActivityOptionsCompat.makeSceneTransitionAnimation(this@LoginActivity as Activity)
                                        .toBundle()
                                )
                                finish()
                            }
                            create()
                            show()
                        }
                        loginViewModel.saveUser(UserModel(it.userId, it.name, it.token, true))
                    }

                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_register_now -> {
                val intent = Intent(this, RegisterActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
            R.id.btn_login -> {
                login()
                val ims = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                ims.hideSoftInputFromWindow(v.windowToken, 0)
            }
        }
    }
}