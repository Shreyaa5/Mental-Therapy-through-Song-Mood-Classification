package com.example.myexample.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.example.myexample.databinding.ActivityLoginBinding
import com.example.myexample.R
import com.example.myexample.data.LoginBody
import com.example.myexample.repository.AuthRepository
import com.example.myexample.utilis.APIService
import com.example.myexample.utilis.VibrateView1
import com.example.myexample.view_model.LoginActivityViewModel
import com.example.myexample.view_model.LoginActivityViewModelFactory
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity(), View.OnClickListener, View.OnFocusChangeListener, View.OnKeyListener {

    private lateinit var mBinding: ActivityLoginBinding
    private lateinit var mViewModel: LoginActivityViewModel
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityLoginBinding.inflate(LayoutInflater.from(this))
        setContentView(mBinding.root)
        supportActionBar?.hide()

        // Initialize Firebase Auth
        auth = Firebase.auth

        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        mBinding.buttonLogin.setOnClickListener(this)
        mBinding.buttonLoginWithGoogle.setOnClickListener(this)
        mBinding.buttonRegister.setOnClickListener(this)
        mBinding.TextEmail.onFocusChangeListener = this
        mBinding.TextPassword.onFocusChangeListener = this
        mBinding.TextPassword.setOnKeyListener(this)
        mViewModel = ViewModelProvider(this, LoginActivityViewModelFactory(AuthRepository(APIService.getService()), application)).get(LoginActivityViewModel::class.java)

        setupObservers()
    }

    private fun signIn() {
        Log.d("LoginActivity", "Starting Google Sign-In Intent")
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    private val googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        Log.d("LoginActivity", "Google Sign-In result received")
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(Exception::class.java)
                Log.d("LoginActivity", "Google Sign-In successful")
                firebaseAuthWithGoogle(account)
            } catch (e: Exception) {
                Log.e("LoginActivity", "Google Sign-In failed", e)
                Toast.makeText(this, "Google Sign-In failed.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Log.d("LoginActivity", "Google Sign-In canceled")
            Toast.makeText(this, "Google Sign-In canceled.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        Log.d("LoginActivity", "Authenticating with Firebase")
        val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Log.d("LoginActivity", "Authentication Successful")
                Toast.makeText(this, "Authentication Successful.", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            } else {
                Log.e("LoginActivity", "Authentication Failed", task.exception)
                Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupObservers() {
        mViewModel.getIsLoading().observe(this) {
            mBinding.progressBar.isVisible = it
        }

        mViewModel.getErrorMessage().observe(this) {
            val formErrorKeys = arrayOf(
                "fullName", "email", "phn_no", "age", "profession", "f_name", "f_email",
                "f_phn_no", "m_name", "m_email", "m_phn_no", "g_name", "g_email", "g_phn_no",
                "address", "city_or_village", "post_office", "district", "state", "country", "Pin", "password"
            )
            val message = StringBuilder()
            it.map { entry ->
                if (formErrorKeys.contains(entry.key)) {
                    when (entry.key) {
                        "email" -> {
                            mBinding.Emailil.apply {
                                isErrorEnabled = true
                                error = entry.value
                            }
                        }
                        "password" -> {
                            mBinding.Passwordil.apply {
                                isErrorEnabled = true
                                error = entry.value
                            }
                        }
                    }
                } else {
                    message.append(entry.value).append("\n")
                }

                if (message.isNotEmpty()) {
                    AlertDialog.Builder(this)
                        .setIcon(R.drawable._info_24)
                        .setTitle("INFORMATION")
                        .setMessage(message)
                        .setPositiveButton("OK") { dialog, _ -> dialog!!.dismiss() }
                        .show()
                }
            }
        }

        mViewModel.getUser().observe(this) {
            if (it != null) {
                startActivity(Intent(this, HomeActivity::class.java))
            }
        }
    }

    private fun validateEmail(shouldUpdateView: Boolean = true, shouldVibrateView: Boolean = true): Boolean {
        var errorMessage: String? = null
        val value: String = mBinding.TextEmail.text.toString()
        if (value.isEmpty()) {
            errorMessage = "Email address field is required"
        } else if (!Patterns.EMAIL_ADDRESS.matcher(value).matches()) {
            errorMessage = "Email address is invalid"
        }

        if (errorMessage != null && shouldUpdateView) {
            mBinding.Emailil.apply {
                isErrorEnabled = true
                error = errorMessage
                if (shouldVibrateView) VibrateView1.vibrate(this@LoginActivity, this)
            }
        }

        return errorMessage == null
    }

    private fun validatePassword(shouldUpdateView: Boolean = true, shouldVibrateView: Boolean = true): Boolean {
        var errorMessage: String? = null
        val value: String = mBinding.TextPassword.text.toString()
        if (value.isEmpty()) {
            errorMessage = "Password field is required"
        } else if (value.length < 8) {
            errorMessage = "Password must be at least 8 characters long"
        }

        if (errorMessage != null && shouldUpdateView) {
            mBinding.Passwordil.apply {
                isErrorEnabled = true
                error = errorMessage
                if (shouldVibrateView) VibrateView1.vibrate(this@LoginActivity, this)
            }
        }
        return errorMessage == null
    }

    private fun validate(): Boolean {
        var isValid = true
        if (!validateEmail(shouldVibrateView = false)) isValid = false
        if (!validatePassword(shouldVibrateView = false)) isValid = false
        if (!isValid) VibrateView1.vibrate(this, mBinding.cardView)
        return isValid
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.buttonLogin -> {
                submitForm()
            }
            R.id.buttonRegister -> {
                startActivity(Intent(this, Register::class.java))
            }
            R.id.buttonLoginWithGoogle -> {
                signIn()
            }
        }
    }

    override fun onFocusChange(view: View?, hasFocus: Boolean) {
        when (view?.id) {
            R.id.TextEmail -> {
                if (hasFocus) {
                    if (mBinding.Emailil.isErrorEnabled) {
                        mBinding.Emailil.isErrorEnabled = false
                    }
                } else {
                    validateEmail()
                }
            }
            R.id.TextPassword -> {
                if (hasFocus) {
                    if (mBinding.Passwordil.isErrorEnabled) {
                        mBinding.Passwordil.isErrorEnabled = false
                    }
                } else {
                    validatePassword()
                }
            }
        }
    }

    private fun submitForm() {
        if (validate()) {
            mViewModel.loginUser(LoginBody(mBinding.TextEmail.text!!.toString(), mBinding.TextPassword.text!!.toString()))
        }
    }

    override fun onKey(view: View?, keyCode: Int, keyEvent: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent?.action == KeyEvent.ACTION_UP) {
            submitForm()
        }
        return false
    }
}
