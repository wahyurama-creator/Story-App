package com.way.storyapp.presentation.ui.component

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.way.storyapp.R

class CustomEditText : AppCompatEditText, View.OnTouchListener {

    private lateinit var clearButton: Drawable

    constructor(context: Context) : super(context) {
        setupView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setupView()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        setupView()
    }

    override fun onTouch(viewParam: View?, motionParam: MotionEvent): Boolean {
        if (compoundDrawables[2] != null) {
            val clearButtonStart: Float
            val clearButtonEnd: Float

            var isClearButtonClick = false
            if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                clearButtonEnd = (clearButton.intrinsicWidth + paddingStart).toFloat()
                when {
                    motionParam.x < clearButtonEnd -> isClearButtonClick = true
                }
            } else {
                clearButtonStart = (width - paddingEnd - clearButton.intrinsicWidth).toFloat()
                when {
                    motionParam.x > clearButtonStart -> isClearButtonClick = true
                }
            }

            if (isClearButtonClick) {
                when (motionParam.action) {
                    MotionEvent.ACTION_DOWN -> {
                        clearButton = ContextCompat.getDrawable(
                            context,
                            R.drawable.ic_clear
                        ) as Drawable
                        showClearButton()
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        clearButton = ContextCompat.getDrawable(
                            context,
                            R.drawable.ic_clear
                        ) as Drawable
                        when {
                            text != null -> text?.clear()
                        }
                        hideButton()
                        return true
                    }
                    else -> return false
                }
            }
        } else {
            return false
        }
        return false
    }

    private fun setupView() {
        clearButton = ContextCompat.getDrawable(context, R.drawable.ic_clear) as Drawable

        setButtonDrawables(startOfTheText = setImageStart())
        setOnTouchListener(this)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                handleOnTextChange(s)
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
    }

    private fun setButtonDrawables(
        startOfTheText: Drawable? = setImageStart(),
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

    private fun handleOnTextChange(s: CharSequence?) {
        // 33 for input type Email, 129 for input type Password, 97 for input type Person
        when (inputType) {
            33 -> {
                if (s.toString().isNotEmpty()) {
                    showClearButton()
                    if (!isValidEmail(s.toString())) error =
                        context.getString(R.string.email_err_format)
                } else {
                    hideButton()
                }
            }
            129 -> {
                if (s.toString().length <= 6 && s.toString().isNotEmpty()) {
                    error = context.getString(R.string.password_err_length)
                } else {
                    hideButton()
                }
            }
            97 -> {
                if (s.toString().isEmpty()) {
                    hideButton()
                } else {
                    showClearButton()
                }
            }
        }
    }

    private fun setImageStart(): Drawable? {
        val emailImage = ContextCompat.getDrawable(context, R.drawable.ic_mail) as Drawable
        val passwordImage = ContextCompat.getDrawable(context, R.drawable.ic_password) as Drawable
        val personImage = ContextCompat.getDrawable(context, R.drawable.ic_profile) as Drawable
        return when (inputType) {
            33 -> emailImage
            129 -> passwordImage
            97 -> personImage
            else -> null
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun showClearButton() {
        setButtonDrawables(endOfTheText = clearButton)
    }

    private fun hideButton() {
        setButtonDrawables()
    }
}