package com.example.myexample.view

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.example.myexample.databinding.ActivityRegisterBinding
import com.example.myexample.R
import com.example.myexample.data.RegisterBody
import com.example.myexample.data.ValidateEmailBody
import com.example.myexample.repository.AuthRepository
import com.example.myexample.utilis.APIService
import com.example.myexample.utilis.VibrateView1
import com.example.myexample.view_model.RegisterActivityViewModel
import com.example.myexample.view_model.RegisterActivityViewModelFactory

class Register : AppCompatActivity(), View.OnClickListener, View.OnFocusChangeListener,
    View.OnKeyListener, TextWatcher {

    private lateinit var mBinding: ActivityRegisterBinding
    private lateinit var mViewModel: RegisterActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityRegisterBinding.inflate(LayoutInflater.from(this))
        setContentView(mBinding.root)
        mBinding.TextFullName.onFocusChangeListener = this
        mBinding.TextEmail.onFocusChangeListener = this
        mBinding.TextPhoneNumber.onFocusChangeListener = this
        mBinding.TextAge.onFocusChangeListener = this
        mBinding.TextProfession.onFocusChangeListener = this
        mBinding.TextFathersName.onFocusChangeListener = this
        mBinding.TextFathersEmail.onFocusChangeListener = this
        mBinding.TextFathersPhoneNumber.onFocusChangeListener = this
        mBinding.TextMothersName.onFocusChangeListener = this
        mBinding.TextMothersEmail.onFocusChangeListener = this
        mBinding.TextMothersPhoneNumber.onFocusChangeListener = this
        mBinding.TextGuardianName.onFocusChangeListener = this
        mBinding.TextGuardianEmail.onFocusChangeListener = this
        mBinding.TextGuardianPhoneNumber.onFocusChangeListener = this
        mBinding.TextAddress.onFocusChangeListener = this
        mBinding.TextCityVillage.onFocusChangeListener = this
        mBinding.TextPostOffice.onFocusChangeListener = this
        mBinding.TextDistrict.onFocusChangeListener = this
        mBinding.TextState.onFocusChangeListener = this
        mBinding.TextCountry.onFocusChangeListener = this
        mBinding.TextPinCode.onFocusChangeListener = this
        mBinding.TextPassword.onFocusChangeListener = this
        mBinding.TextConfirmPassword.onFocusChangeListener = this
        mBinding.TextConfirmPassword.setOnKeyListener(this)
        mBinding.TextConfirmPassword.addTextChangedListener(this)
        mBinding.buttonRegister.setOnClickListener(this)
        mViewModel = ViewModelProvider(
            this,
            RegisterActivityViewModelFactory(AuthRepository(APIService.getService()), application)
        ).get(RegisterActivityViewModel::class.java)
        setupObservers()

    }

    private fun setupObservers() {
        mViewModel.getIsLoading().observe(this) {
            mBinding.progressBar.isVisible = it
        }

        mViewModel.getIsUniqueEmail().observe(this) {
            if (validateEmail(shouldUpdateView = false)) {
                if (it) {
                    mBinding.Emailil.apply {
                        if (isErrorEnabled) isErrorEnabled = false
                        setStartIconDrawable(R.drawable.check_circle_24)
                        setStartIconTintList(ColorStateList.valueOf(Color.GREEN))
                    }
                } else {
                    mBinding.Emailil.apply {
                        if (startIconDrawable != null) startIconDrawable = null
                        isErrorEnabled = true
                        error = "This email is already used"
                    }
                }

            }
        }

        mViewModel.getIsUniqueEmail().observe(this) {
            if (validateEmail(shouldUpdateView = false)) {
                if (it) {
                    mBinding.FatherEmailil.apply {
                        if (isErrorEnabled) isErrorEnabled = false
                        setStartIconDrawable(R.drawable.check_circle_24)
                        setStartIconTintList(ColorStateList.valueOf(Color.GREEN))
                    }
                } else {
                    mBinding.FatherEmailil.apply {
                        if (startIconDrawable != null) startIconDrawable = null
                        isErrorEnabled = true
                        error = "This email is already used"
                    }
                }

            }
        }

        mViewModel.getIsUniqueEmail().observe(this) {
            if (validateEmail(shouldUpdateView = false)) {
                if (it) {
                    mBinding.MotherEmailil.apply {
                        if (isErrorEnabled) isErrorEnabled = false
                        setStartIconDrawable(R.drawable.check_circle_24)
                        setStartIconTintList(ColorStateList.valueOf(Color.GREEN))
                    }
                } else {
                    mBinding.MotherEmailil.apply {
                        if (startIconDrawable != null) startIconDrawable = null
                        isErrorEnabled = true
                        error = "This email is already used"
                    }
                }

            }
        }


        mViewModel.getErrorMessage().observe(this) {
            val formErrorKeys = arrayOf(
                "fullName",
                "email",
                "phn_no",
                "age",
                "profession",
                "f_name",
                "f_email",
                "f_phn_no",
                "m_name",
                "m_email",
                "m_phn_no",
                "g_name",
                "g_email",
                "g_phn_no",
                "address",
                "city_or_village",
                "post_office",
                "district",
                "state",
                "country",
                "Pin",
                "password"
            )
            val message = StringBuilder()
            it.map { entry ->
                if (formErrorKeys.contains(entry.key)) {
                    when (entry.key) {
                        "fullName" -> {
                            mBinding.FullNameil.apply {
                                isErrorEnabled = true
                                error = entry.value
                            }
                        }
                        "email" -> {
                            mBinding.Emailil.apply {
                                isErrorEnabled = true
                                error = entry.value
                            }

                        }
                        "phn_no" -> {
                            mBinding.PhoneNumberil.apply {
                                isErrorEnabled = true
                                error = entry.value
                            }

                        }
                        "age" -> {
                            mBinding.Ageil.apply {
                                isErrorEnabled = true
                                error = entry.value
                            }

                        }
                        "profession" -> {
                            mBinding.Professionil.apply {
                                isErrorEnabled = true
                                error = entry.value
                            }

                        }
                        "f_name" -> {
                            mBinding.FatherNameil.apply {
                                isErrorEnabled = true
                                error = entry.value
                            }

                        }
                        "f_email" -> {
                            mBinding.FatherEmailil.apply {
                                isErrorEnabled = true
                                error = entry.value
                            }

                        }
                        "f_phn_no" -> {
                            mBinding.FatherPhoneNumberil.apply {
                                isErrorEnabled = true
                                error = entry.value
                            }

                        }
                        "m_name" -> {
                            mBinding.MotherNameil.apply {
                                isErrorEnabled = true
                                error = entry.value
                            }

                        }
                        "m_email" -> {
                            mBinding.MotherEmailil.apply {
                                isErrorEnabled = true
                                error = entry.value
                            }

                        }
                        "m_phn_no" -> {
                            mBinding.MotherPhoneNumberil.apply {
                                isErrorEnabled = true
                                error = entry.value
                            }

                        }
                        "g_name" -> {
                            mBinding.GuardianNameil.apply {
                                isErrorEnabled = true
                                error = entry.value
                            }

                        }
                        "g_email" -> {
                            mBinding.GuardianEmailil.apply {
                                isErrorEnabled = true
                                error = entry.value
                            }

                        }
                        "g_phn_no" -> {
                            mBinding.GuardianPhoneNumberil.apply {
                                isErrorEnabled = true
                                error = entry.value
                            }

                        }
                        "address" -> {
                            mBinding.Addressil.apply {
                                isErrorEnabled = true
                                error = entry.value
                            }

                        }
                        "city_or_village" -> {
                            mBinding.CityVillageil.apply {
                                isErrorEnabled = true
                                error = entry.value
                            }

                        }
                        "post_office" -> {
                            mBinding.PostOfficeil.apply {
                                isErrorEnabled = true
                                error = entry.value
                            }

                        }
                        "district" -> {
                            mBinding.Districtil.apply {
                                isErrorEnabled = true
                                error = entry.value
                            }

                        }
                        "state" -> {
                            mBinding.Stateil.apply {
                                isErrorEnabled = true
                                error = entry.value
                            }

                        }
                        "country" -> {
                            mBinding.Countryil.apply {
                                isErrorEnabled = true
                                error = entry.value
                            }

                        }
                        "Pin" -> {
                            mBinding.PinCodeil.apply {
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

    private fun validateFullName(shouldVibrateView: Boolean = true): Boolean {
        var errormessage: String? = null
        val value: String = mBinding.TextFullName.text.toString()
        if (value.isEmpty()) {
            errormessage = "Full Name field is required"
        }

        if (errormessage != null) {
            mBinding.FullNameil.apply {
                isErrorEnabled = true
                error = errormessage
                if (shouldVibrateView) VibrateView1.vibrate(this@Register, this)
            }
        }
        return errormessage == null
    }

    private fun validateEmail(
        shouldUpdateView: Boolean = true,
        shouldVibrateView: Boolean = true
    ): Boolean {
        var errormessage: String? = null
        val value: String = mBinding.TextEmail.text.toString()
        if (value.isEmpty()) {
            errormessage = "Email address field is required"
        } else if (!Patterns.EMAIL_ADDRESS.matcher(value).matches()) {
            errormessage = "Email address is invalid"
        }

        if (errormessage != null && shouldUpdateView) {
            mBinding.Emailil.apply {
                isErrorEnabled = true
                error = errormessage
                if (shouldVibrateView) VibrateView1.vibrate(this@Register, this)
            }
        }

        return errormessage == null
    }

    private fun validatePhoneNumber(shouldVibrateView: Boolean = true): Boolean {
        var errormessage: String? = null
        val value: String = mBinding.TextPhoneNumber.text.toString()
        if (value.isEmpty()) {
            errormessage = "Phone Number field is required"
        } else if (!Patterns.PHONE.matcher(value).matches()) {
            errormessage = "Phone Number is invalid"
        }

        if (errormessage != null) {
            mBinding.PhoneNumberil.apply {
                isErrorEnabled = true
                error = errormessage
                if (shouldVibrateView) VibrateView1.vibrate(this@Register, this)
            }
        }
        return errormessage == null
    }

    private fun validateAge(shouldVibrateView: Boolean = true): Boolean {
        var errormessage: String? = null
        val value: String = mBinding.TextAge.text.toString()
        val age: Int? = value.toIntOrNull()
        if (value.isEmpty()) {
            errormessage = "Age field is required"
        } else if (age == null || age < 0 || age > 120) {
            errormessage = "Age must be a valid number"
        }

        if (errormessage != null) {
            mBinding.Ageil.apply {
                isErrorEnabled = true
                error = errormessage
                if (shouldVibrateView) VibrateView1.vibrate(this@Register, this)
            }
        }
        return errormessage == null
    }

    private fun validateProfession(shouldVibrateView: Boolean = true): Boolean {
        var errormessage: String? = null
        val value: String = mBinding.TextProfession.text.toString()
        if (value.isEmpty()) {
            errormessage = "Profession field is required"
        }

        if (errormessage != null) {
            mBinding.Professionil.apply {
                isErrorEnabled = true
                error = errormessage
                if (shouldVibrateView) VibrateView1.vibrate(this@Register, this)
            }
        }
        return errormessage == null
    }

    private fun validateFatherName(shouldVibrateView: Boolean = true): Boolean {
        var errormessage: String? = null
        val value: String = mBinding.TextFathersName.text.toString()
        if (value.isEmpty()) {
            errormessage = "Father's Name field is required"
        }

        if (errormessage != null) {
            mBinding.FatherNameil.apply {
                isErrorEnabled = true
                error = errormessage
                if (shouldVibrateView) VibrateView1.vibrate(this@Register, this)
            }
        }
        return errormessage == null
    }

    private fun validateFatherEmail(
        shouldUpdateView: Boolean = true,
        shouldVibrateView: Boolean = true
    ): Boolean {
        var errormessage: String? = null
        val value: String = mBinding.TextFathersEmail.text.toString()
        if (value.isEmpty()) {
            errormessage = "Father's Email field is required"
        } else if (!Patterns.EMAIL_ADDRESS.matcher(value).matches()) {
            errormessage = "Father's Email address is invalid"
        }

        if (errormessage != null && shouldUpdateView) {
            mBinding.FatherEmailil.apply {
                isErrorEnabled = true
                error = errormessage
                if (shouldVibrateView) VibrateView1.vibrate(this@Register, this)
            }
        }
        return errormessage == null
    }

    private fun validateFatherPhoneNumber(shouldVibrateView: Boolean = true): Boolean {
        var errormessage: String? = null
        val value: String = mBinding.TextFathersPhoneNumber.text.toString()
        if (value.isEmpty()) {
            errormessage = "Father's Phone Number field is required"
        } else if (!Patterns.PHONE.matcher(value).matches()) {
            errormessage = "Father's Phone Number is invalid"
        }

        if (errormessage != null) {
            mBinding.FatherPhoneNumberil.apply {
                isErrorEnabled = true
                error = errormessage
                if (shouldVibrateView) VibrateView1.vibrate(this@Register, this)
            }
        }
        return errormessage == null
    }

    private fun validateMotherName(shouldVibrateView: Boolean = true): Boolean {
        var errormessage: String? = null
        val value: String = mBinding.TextMothersName.text.toString()
        if (value.isEmpty()) {
            errormessage = "Mother's Name field is required"
        }

        if (errormessage != null) {
            mBinding.MotherNameil.apply {
                isErrorEnabled = true
                error = errormessage
                if (shouldVibrateView) VibrateView1.vibrate(this@Register, this)
            }
        }
        return errormessage == null
    }

    private fun validateMotherEmail(
        shouldUpdateView: Boolean = true,
        shouldVibrateView: Boolean = true
    ): Boolean {
        var errormessage: String? = null
        val value: String = mBinding.TextMothersEmail.text.toString()
        if (value.isEmpty()) {
            errormessage = "Mother's Email field is required"
        } else if (!Patterns.EMAIL_ADDRESS.matcher(value).matches()) {
            errormessage = "Mother's Email address is invalid"
        }

        if (errormessage != null && shouldUpdateView) {
            mBinding.MotherEmailil.apply {
                isErrorEnabled = true
                error = errormessage
                if (shouldVibrateView) VibrateView1.vibrate(this@Register, this)
            }
        }
        return errormessage == null
    }

    private fun validateMotherPhoneNumber(shouldVibrateView: Boolean = true): Boolean {
        var errormessage: String? = null
        val value: String = mBinding.TextMothersPhoneNumber.text.toString()
        if (value.isEmpty()) {
            errormessage = "Mother's Phone Number field is required"
        } else if (!Patterns.PHONE.matcher(value).matches()) {
            errormessage = "Mother's Phone Number is invalid"
        }

        if (errormessage != null) {
            mBinding.MotherPhoneNumberil.apply {
                isErrorEnabled = true
                error = errormessage
                if (shouldVibrateView) VibrateView1.vibrate(this@Register, this)
            }
        }
        return errormessage == null
    }

    private fun validateGuardianName(shouldVibrateView: Boolean = true): Boolean {
        var errormessage: String? = null
        val value: String = mBinding.TextGuardianName.text.toString()
        if (value.isEmpty()) {
            errormessage = "Guardian Name field is required"
        }

        if (errormessage != null) {
            mBinding.GuardianNameil.apply {
                isErrorEnabled = true
                error = errormessage
                if (shouldVibrateView) VibrateView1.vibrate(this@Register, this)
            }
        }
        return errormessage == null
    }

    private fun validateGuardianEmail(shouldVibrateView: Boolean = true): Boolean {
        var errormessage: String? = null
        val value: String = mBinding.TextGuardianEmail.text.toString()
        if (value.isEmpty()) {
            errormessage = "Guardian Email field is required"
        } else if (!Patterns.EMAIL_ADDRESS.matcher(value).matches()) {
            errormessage = "Guardian Email address is invalid"
        }

        if (errormessage != null) {
            mBinding.GuardianEmailil.apply {
                isErrorEnabled = true
                error = errormessage
                if (shouldVibrateView) VibrateView1.vibrate(this@Register, this)
            }
        }
        return errormessage == null
    }

    private fun validateGuardianPhoneNumber(shouldVibrateView: Boolean = true): Boolean {
        var errormessage: String? = null
        val value: String = mBinding.TextGuardianPhoneNumber.text.toString()
        if (value.isEmpty()) {
            errormessage = "Guardian Phone Number field is required"
        } else if (!Patterns.PHONE.matcher(value).matches()) {
            errormessage = "Guardian Phone Number is invalid"
        }

        if (errormessage != null) {
            mBinding.GuardianPhoneNumberil.apply {
                isErrorEnabled = true
                error = errormessage
                if (shouldVibrateView) VibrateView1.vibrate(this@Register, this)
            }
        }
        return errormessage == null
    }

    private fun validateAddress(shouldVibrateView: Boolean = true): Boolean {
        var errormessage: String? = null
        val value: String = mBinding.TextAddress.text.toString()
        if (value.isEmpty()) {
            errormessage = "Address field is required"
        }

        if (errormessage != null) {
            mBinding.Addressil.apply {
                isErrorEnabled = true
                error = errormessage
                if (shouldVibrateView) VibrateView1.vibrate(this@Register, this)
            }
        }
        return errormessage == null
    }

    private fun validateCityOrVillage(shouldVibrateView: Boolean = true): Boolean {
        var errormessage: String? = null
        val value: String = mBinding.TextCityVillage.text.toString()
        if (value.isEmpty()) {
            errormessage = "City/Village field is required"
        }

        if (errormessage != null) {
            mBinding.CityVillageil.apply {
                isErrorEnabled = true
                error = errormessage
                if (shouldVibrateView) VibrateView1.vibrate(this@Register, this)
            }
        }
        return errormessage == null
    }

    private fun validatePostOffice(shouldVibrateView: Boolean = true): Boolean {
        var errormessage: String? = null
        val value: String = mBinding.TextPostOffice.text.toString()
        if (value.isEmpty()) {
            errormessage = "Post Office field is required"
        }

        if (errormessage != null) {
            mBinding.PostOfficeil.apply {
                isErrorEnabled = true
                error = errormessage
                if (shouldVibrateView) VibrateView1.vibrate(this@Register, this)
            }
        }
        return errormessage == null
    }

    private fun validateDistrict(shouldVibrateView: Boolean = true): Boolean {
        var errormessage: String? = null
        val value: String = mBinding.TextDistrict.text.toString()
        if (value.isEmpty()) {
            errormessage = "District field is required"
        }

        if (errormessage != null) {
            mBinding.Districtil.apply {
                isErrorEnabled = true
                error = errormessage
                if (shouldVibrateView) VibrateView1.vibrate(this@Register, this)
            }
        }
        return errormessage == null
    }

    private fun validateState(shouldVibrateView: Boolean = true): Boolean {
        var errormessage: String? = null
        val value: String = mBinding.TextState.text.toString()
        if (value.isEmpty()) {
            errormessage = "State field is required"
        }

        if (errormessage != null) {
            mBinding.Stateil.apply {
                isErrorEnabled = true
                error = errormessage
                if (shouldVibrateView) VibrateView1.vibrate(this@Register, this)
            }
        }
        return errormessage == null
    }

    private fun validateCountry(shouldVibrateView: Boolean = true): Boolean {
        var errormessage: String? = null
        val value: String = mBinding.TextCountry.text.toString()
        if (value.isEmpty()) {
            errormessage = "Country field is required"
        }

        if (errormessage != null) {
            mBinding.Countryil.apply {
                isErrorEnabled = true
                error = errormessage
                if (shouldVibrateView) VibrateView1.vibrate(this@Register, this)
            }
        }
        return errormessage == null
    }

    private fun validatePinCode(shouldVibrateView: Boolean = true): Boolean {
        var errormessage: String? = null
        val value: String = mBinding.TextPinCode.text.toString()
        val pinCode: Int? = value.toIntOrNull()
        if (value.isEmpty()) {
            errormessage = "Pin Code field is required"
        } else if (pinCode == null || value.length != 6) {
            errormessage = "Pin Code must be a valid 6-digit number"
        }

        if (errormessage != null) {
            mBinding.PinCodeil.apply {
                isErrorEnabled = true
                error = errormessage
                if (shouldVibrateView) VibrateView1.vibrate(this@Register, this)
            }
        }
        return errormessage == null
    }

    private fun validatePassword(
        shouldUpdateView: Boolean = true,
        shouldVibrateView: Boolean = true
    ): Boolean {
        var errormessage: String? = null
        val value: String = mBinding.TextPassword.text.toString()
        if (value.isEmpty()) {
            errormessage = "Password field is required"
        } else if (value.length < 8) {
            errormessage = "Password must be atleast 8 characters long"
        }

        if (errormessage != null && shouldUpdateView) {
            mBinding.Passwordil.apply {
                isErrorEnabled = true
                error = errormessage
                if (shouldVibrateView) VibrateView1.vibrate(this@Register, this)
            }
        }
        return errormessage == null
    }

    private fun validateConfirmPassword(
        shouldUpdateView: Boolean = true,
        shouldVibrateView: Boolean = true
    ): Boolean {
        var errormessage: String? = null
        val value: String = mBinding.TextConfirmPassword.text.toString()
        if (value.isEmpty()) {
            errormessage = "Confirm Password field is required"
        } else if (value.length < 8) {
            errormessage = "Confirm Password must be atleast 8 characters long"
        }

        if (errormessage != null && shouldUpdateView) {
            mBinding.ConfirmPasswordil.apply {
                isErrorEnabled = true
                error = errormessage
                if (shouldVibrateView) VibrateView1.vibrate(this@Register, this)
            }
        }
        return errormessage == null
    }

    private fun validatePasswordAndConfirmPassword(
        shouldUpdateView: Boolean = true,
        shouldVibrateView: Boolean = true
    ): Boolean {
        var errormessage: String? = null
        val password = mBinding.TextPassword.text.toString()
        val confirmpassword = mBinding.TextConfirmPassword.text.toString()
        if (password != confirmpassword) {
            errormessage = "Confirm password doesn't match with password"
        }

        if (errormessage != null && shouldUpdateView) {
            mBinding.ConfirmPasswordil.apply {
                isErrorEnabled = true
                error = errormessage
                if (shouldVibrateView) VibrateView1.vibrate(this@Register, this)
            }
        }
        return errormessage == null
    }


    override fun onClick(view: View?) {
        if (view != null && view.id == R.id.buttonRegister)
            onSubmit()
    }

    override fun onFocusChange(view: View?, hasFocus: Boolean) {
        if (view != null) {
            when (view.id) {
                R.id.TextFullName -> {
                    if (hasFocus) {
                        if (mBinding.FullNameil.isErrorEnabled) {
                            mBinding.FullNameil.isErrorEnabled = false
                        }
                    } else {
                        validateFullName()
                    }
                }
                R.id.TextEmail -> {
                    if (hasFocus) {
                        if (mBinding.Emailil.isErrorEnabled) {
                            mBinding.Emailil.isErrorEnabled = false
                        }
                    } else {
                        if (validateEmail()) {
                            mViewModel.validateEmailAddress(ValidateEmailBody(mBinding.TextEmail.text!!.toString()))
                        }
                    }
                }
                R.id.TextPhoneNumber -> {
                    if (hasFocus) {
                        if (mBinding.PhoneNumberil.isErrorEnabled) {
                            mBinding.PhoneNumberil.isErrorEnabled = false
                        }
                    } else {
                        validatePhoneNumber()
                    }
                }
                R.id.TextAge -> {
                    if (hasFocus) {
                        if (mBinding.Ageil.isErrorEnabled) {
                            mBinding.Ageil.isErrorEnabled = false
                        }
                    } else {
                        validateAge()
                    }
                }
                R.id.TextProfession -> {
                    if (hasFocus) {
                        if (mBinding.Professionil.isErrorEnabled) {
                            mBinding.Professionil.isErrorEnabled = false
                        }
                    } else {
                        validateProfession()
                    }
                }
                R.id.TextFathersName -> {
                    if (hasFocus) {
                        if (mBinding.FatherNameil.isErrorEnabled) {
                            mBinding.FatherNameil.isErrorEnabled = false
                        }
                    } else {
                        validateFatherName()
                    }
                }
                R.id.TextFathersEmail -> {
                    if (hasFocus) {
                        if (mBinding.FatherEmailil.isErrorEnabled) {
                            mBinding.FatherEmailil.isErrorEnabled = false
                        }
                    } else {
                        if (validateFatherEmail()) {
                            mViewModel.validateEmailAddress(ValidateEmailBody(mBinding.TextFathersEmail.text!!.toString()))
                        }
                    }
                }
                R.id.TextFathersPhoneNumber -> {
                    if (hasFocus) {
                        if (mBinding.FatherPhoneNumberil.isErrorEnabled) {
                            mBinding.FatherPhoneNumberil.isErrorEnabled = false
                        }
                    } else {
                        validateFatherPhoneNumber()
                    }
                }
                R.id.TextMothersName -> {
                    if (hasFocus) {
                        if (mBinding.MotherNameil.isErrorEnabled) {
                            mBinding.MotherEmailil.isErrorEnabled = false
                        }
                    } else {
                        validateMotherName()
                    }

                }
                R.id.TextMothersEmail -> {
                    if (hasFocus) {
                        if (mBinding.MotherEmailil.isErrorEnabled) {
                            mBinding.MotherEmailil.isErrorEnabled = false
                        }
                    } else {
                        if (validateMotherEmail()) {
                            mViewModel.validateEmailAddress(ValidateEmailBody(mBinding.TextMothersEmail.text!!.toString()))
                        }
                    }

                }
                R.id.TextMothersPhoneNumber -> {
                    if (hasFocus) {
                        if (mBinding.MotherPhoneNumberil.isErrorEnabled) {
                            mBinding.MotherPhoneNumberil.isErrorEnabled = false
                        }
                    } else {
                        validateMotherPhoneNumber()
                    }

                }
                R.id.TextGuardianName -> {
                    if (hasFocus) {
                        if (mBinding.GuardianNameil.isErrorEnabled) {
                            mBinding.GuardianNameil.isErrorEnabled = false
                        }
                    } else {
                        validateGuardianName()
                    }

                }
                R.id.TextGuardianEmail -> {
                    if (hasFocus) {
                        if (mBinding.GuardianEmailil.isErrorEnabled) {
                            mBinding.GuardianEmailil.isErrorEnabled = false
                        }
                    } else {
                        if (validateGuardianEmail()) {
                            mViewModel.validateEmailAddress(ValidateEmailBody(mBinding.TextGuardianEmail.text!!.toString()))
                        }
                    }

                }
                R.id.TextGuardianPhoneNumber -> {
                    if (hasFocus) {
                        if (mBinding.GuardianPhoneNumberil.isErrorEnabled) {
                            mBinding.GuardianPhoneNumberil.isErrorEnabled = false
                        }
                    } else {
                        validateGuardianPhoneNumber()
                    }

                }
                R.id.TextAddress -> {
                    if (hasFocus) {
                        if (mBinding.Addressil.isErrorEnabled) {
                            mBinding.Addressil.isErrorEnabled = false
                        }
                    } else {
                        validateAddress()
                    }

                }
                R.id.TextCityVillage -> {
                    if (hasFocus) {
                        if (mBinding.CityVillageil.isErrorEnabled) {
                            mBinding.CityVillageil.isErrorEnabled = false
                        }
                    } else {
                        validateCityOrVillage()
                    }

                }
                R.id.TextPostOffice -> {
                    if (hasFocus) {
                        if (mBinding.PostOfficeil.isErrorEnabled) {
                            mBinding.PostOfficeil.isErrorEnabled = false
                        }
                    } else {
                        validatePostOffice()
                    }

                }
                R.id.TextDistrict -> {
                    if (hasFocus) {
                        if (mBinding.Districtil.isErrorEnabled) {
                            mBinding.Districtil.isErrorEnabled = false
                        }
                    } else {
                        validateDistrict()
                    }

                }
                R.id.TextState -> {
                    if (hasFocus) {
                        if (mBinding.Stateil.isErrorEnabled) {
                            mBinding.Stateil.isErrorEnabled = false
                        }
                    } else {
                        validateState()
                    }

                }
                R.id.TextCountry -> {
                    if (hasFocus) {
                        if (mBinding.Countryil.isErrorEnabled) {
                            mBinding.Countryil.isErrorEnabled = false
                        }
                    } else {
                        validateCountry()
                    }

                }
                R.id.TextPinCode -> {
                    if (hasFocus) {
                        if (mBinding.PinCodeil.isErrorEnabled) {
                            mBinding.PinCodeil.isErrorEnabled = false
                        }
                    } else {
                        validatePinCode()
                    }

                }
                R.id.TextPassword -> {
                    if (hasFocus) {
                        if (mBinding.Passwordil.isErrorEnabled) {
                            mBinding.Passwordil.isErrorEnabled = false
                        }
                    } else {
                        if (validatePassword() && mBinding.TextConfirmPassword.text!!.isNotEmpty() && validateConfirmPassword() &&
                            validatePasswordAndConfirmPassword()
                        ) {
                            if (mBinding.ConfirmPasswordil.isErrorEnabled) {
                                mBinding.ConfirmPasswordil.isErrorEnabled = false
                            }
                            mBinding.ConfirmPasswordil.apply {
                                setStartIconDrawable(R.drawable.check_circle_24)
                                setStartIconTintList(ColorStateList.valueOf(Color.GREEN))
                            }
                        }
                    }
                }
                R.id.TextConfirmPassword -> {
                    if (hasFocus) {
                        if (mBinding.ConfirmPasswordil.isErrorEnabled) {
                            mBinding.ConfirmPasswordil.isErrorEnabled = false
                        }
                    } else {
                        if (validateConfirmPassword() && validatePassword() && validatePasswordAndConfirmPassword()) {
                            if (mBinding.Passwordil.isErrorEnabled) {
                                mBinding.Passwordil.isErrorEnabled = false
                            }
                            mBinding.ConfirmPasswordil.apply {
                                setStartIconDrawable(R.drawable.check_circle_24)
                                setStartIconTintList(ColorStateList.valueOf(Color.GREEN))
                            }
                        }
                    }

                }

            }
        }
    }

    override fun onKey(view: View?, keyCode: Int, keyEvent: KeyEvent?): Boolean {
        if (KeyEvent.KEYCODE_ENTER == keyCode && keyEvent!!.action == KeyEvent.ACTION_DOWN) {
            // do registration
            onSubmit()
        }
        return false
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        if (validatePassword(shouldUpdateView = false) && validateConfirmPassword(shouldUpdateView = false) && validatePasswordAndConfirmPassword(
                shouldUpdateView = false
            )
        ) {
            mBinding.ConfirmPasswordil.apply {
                if (isErrorEnabled) isErrorEnabled = false
                setStartIconDrawable(R.drawable.check_circle_24)
                setStartIconTintList(ColorStateList.valueOf(Color.GREEN))
            }
        } else {
            if (mBinding.ConfirmPasswordil.startIconDrawable != null)
                mBinding.ConfirmPasswordil.startIconDrawable = null
        }

    }

    override fun afterTextChanged(p0: Editable?) {}


    private fun onSubmit() {
        if (validate()) {
            //make api request
            mViewModel.registerUser(
                RegisterBody(
                    mBinding.TextFullName.text!!.toString(),
                    mBinding.TextEmail.text!!.toString(),
                    mBinding.TextPhoneNumber.text!!.toString().toLong(),
                    mBinding.TextAge.text!!.toString().toInt(),
                    mBinding.TextProfession.text!!.toString(),
                    mBinding.TextFathersName.text!!.toString(),
                    mBinding.TextFathersEmail.text!!.toString(),
                    mBinding.TextFathersPhoneNumber.text!!.toString().toLong(),
                    mBinding.TextMothersName.text!!.toString(),
                    mBinding.TextMothersEmail.text!!.toString(),
                    mBinding.TextMothersPhoneNumber.text!!.toString().toLong(),
                    mBinding.TextGuardianName.text!!.toString(),
                    mBinding.TextGuardianEmail.text!!.toString(),
                    mBinding.TextGuardianPhoneNumber.text!!.toString().toLong(),
                    mBinding.TextAddress.text!!.toString(),
                    mBinding.TextCityVillage.text!!.toString(),
                    mBinding.TextPostOffice.text!!.toString(),
                    mBinding.TextDistrict.text!!.toString(),
                    mBinding.TextState.text!!.toString(),
                    mBinding.TextCountry.text!!.toString(),
                    mBinding.TextPinCode.text!!.toString().toInt(),
                    mBinding.TextPassword.text!!.toString()
                )
            )
        }

    }

    private fun validate(): Boolean {
        var isValid = true

        if (!validateFullName(shouldVibrateView = false)) isValid = false
        if (!validateEmail(shouldVibrateView = false)) isValid = false
        if (!validatePhoneNumber(shouldVibrateView = false)) isValid = false
        if (!validateAge(shouldVibrateView = false)) isValid = false
        if (!validateProfession(shouldVibrateView = false)) isValid = false
        if (!validateFatherName(shouldVibrateView = false)) isValid = false
        if (!validateFatherEmail(shouldVibrateView = false)) isValid = false
        if (!validateFatherPhoneNumber(shouldVibrateView = false)) isValid = false
        if (!validateMotherName(shouldVibrateView = false)) isValid = false
        if (!validateMotherEmail(shouldVibrateView = false)) isValid = false
        if (!validateMotherPhoneNumber(shouldVibrateView = false)) isValid = false
        if (!validateGuardianName(shouldVibrateView = false)) isValid = false
        if (!validateGuardianEmail(shouldVibrateView = false)) isValid = false
        if (!validateGuardianPhoneNumber(shouldVibrateView = false)) isValid = false
        if (!validateAddress(shouldVibrateView = false)) isValid = false
        if (!validateCityOrVillage(shouldVibrateView = false)) isValid = false
        if (!validatePostOffice(shouldVibrateView = false)) isValid = false
        if (!validateDistrict(shouldVibrateView = false)) isValid = false
        if (!validateState(shouldVibrateView = false)) isValid = false
        if (!validateCountry(shouldVibrateView = false)) isValid = false
        if (!validatePinCode(shouldVibrateView = false)) isValid = false
        if (!validatePassword(shouldVibrateView = false)) isValid = false
        if (!validateConfirmPassword(shouldVibrateView = false)) isValid = false
        if (isValid && !validatePasswordAndConfirmPassword(shouldVibrateView = false)) isValid =
            false

        if (!isValid) VibrateView1.vibrate(this, mBinding.cardView)
        return isValid
    }
}