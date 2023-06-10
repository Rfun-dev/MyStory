package com.example.mystory.ui.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.example.mystory.R

class EmailEdText : AppCompatEditText {
    private lateinit var iconForm : Drawable

    constructor(context : Context) : super(context){
        init()
    }

    constructor(context : Context, attrs : AttributeSet) : super(context, attrs){
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr : Int) : super(context, attrs, defStyleAttr){
        init()
    }

    private fun init(){
        iconForm = ContextCompat.getDrawable(context, R.drawable.email) as Drawable
        setIconForm(startOfDrawableText = iconForm)
        addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                error = if(s.isNotEmpty()){
                    if(!s.toString().matches(Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"))){
                        context.getString(R.string.email_error)
                    } else null
                }else null
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
    }

    private fun setIconForm(
        startOfDrawableText : Drawable? = null,
        topOfDrawableText : Drawable? = null,
        endOfDrawableText : Drawable? = null,
        bottomOfDrawableText : Drawable? = null
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
        hint = context.getString(R.string.email)
        isSingleLine = true
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }
}