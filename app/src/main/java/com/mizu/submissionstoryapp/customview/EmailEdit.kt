package com.mizu.submissionstoryapp.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.InputType
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.mizu.submissionstoryapp.R

class EmailEdit : AppCompatEditText, View.OnFocusChangeListener {

    private lateinit var emailIconImage: Drawable

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        hint = context.getString(R.string.input_email)
    }

    private fun init() {
        emailIconImage = ContextCompat.getDrawable(context, R.drawable.ic_email) as Drawable
        setButtonDrawables(startOfTheText = emailIconImage)
        inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        compoundDrawablePadding = resources.getDimensionPixelSize(R.dimen.drawable_spacing)
        typeface = Typeface.DEFAULT
        onFocusChangeListener = this
        addTextChangedListener {
            validateEmail()
        }
    }

    private fun setButtonDrawables(
        startOfTheText: Drawable? = null,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null
    ) {
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText
        )
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if (!hasFocus) {
            validateEmail()
        }
    }

    private fun validateEmail() {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        val email = text?.toString()?.trim()
        if (email.isNullOrEmpty() || !email.matches(emailPattern.toRegex())) {
            showError()
        } else {
            error = null
        }
    }

    private fun showError() {
        error = context.getString(R.string.email_error_message)
    }

}