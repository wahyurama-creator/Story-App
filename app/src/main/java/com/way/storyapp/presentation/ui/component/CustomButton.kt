package com.way.storyapp.presentation.ui.component

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.way.storyapp.R

class CustomButton : AppCompatButton {

    private lateinit var enabledBackground: Drawable
    private lateinit var disabledBackground: Drawable

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

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        background = if (isEnabled) enabledBackground else disabledBackground
    }

    private fun setupView() {
        enabledBackground =
            ContextCompat.getDrawable(context, R.drawable.bg_button_enabled) as Drawable
        disabledBackground =
            ContextCompat.getDrawable(context, R.drawable.bg_button_disabled) as Drawable
    }
}