package com.example.mystory.ui.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.example.mystory.R

class PasswordEdText : AppCompatEditText {
    private lateinit var iconForm : Drawable
    private var length = 0

    constructor(context: Context) : super(context){
        init()
    }

    constructor(context: Context, attrs : AttributeSet) : super(context, attrs){
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr : Int) : super(context, attrs, defStyleAttr){
        init()
    }

    private fun init() {
        iconForm = ContextCompat.getDrawable(context, R.drawable.key) as Drawable
        setIconForm(startOfDrawableText = iconForm)
        addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                length = s.length
                error = if(length < 8 && s.isNotEmpty()){
                    context.getString(R.string.password_error)
                } else null
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
    }

    private fun setIconForm(
        startOfDrawableText : Drawable? = null,
        topOfDrawableText : Drawable? = null,
        endOfDrawableText : Drawable? = null,
        bottomOfDrawableText: Drawable? = null
    ) {
        setCompoundDrawablesWithIntrinsicBounds(
            startOfDrawableText,
            topOfDrawableText,
            endOfDrawableText,
            bottomOfDrawableText
        )
    }



    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        hint = context.getString(R.string.password)
        maxLines = 1
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
        transformationMethod = PasswordTransformationMethod.getInstance()
        setSelection(length)
    }
}