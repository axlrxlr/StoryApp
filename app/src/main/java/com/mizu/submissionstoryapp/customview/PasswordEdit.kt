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

class PasswordEdit: AppCompatEditText, View.OnFocusChangeListener
{

    private lateinit var lockIconImage: Drawable

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

        hint = context.getString(R.string.input_pass)
    }


    private fun init() {

        lockIconImage = ContextCompat.getDrawable(context, R.drawable.ic_lock) as Drawable
        setButtonDrawables(startOfTheText = lockIconImage)
        inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        compoundDrawablePadding = resources.getDimensionPixelSize(R.dimen.drawable_spacing)
        typeface = Typeface.DEFAULT
        onFocusChangeListener = this
        addTextChangedListener {
            validatePassword()
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
            validatePassword()
        }
    }

    private fun validatePassword() {
        if ((text?.length ?: 0) < 8) {
            showError()
        } else {
            error = null
        }
    }

    private fun showError() {
        error = context.getString(R.string.password_error_message)
    }


}