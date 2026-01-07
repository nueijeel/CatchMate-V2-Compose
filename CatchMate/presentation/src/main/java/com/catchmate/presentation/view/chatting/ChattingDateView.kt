package com.catchmate.presentation.view.chatting

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.catchmate.presentation.R
import com.catchmate.presentation.databinding.ViewChattingDateBinding

class ChattingDateView(
    context: Context,
    attrs: AttributeSet,
) : ConstraintLayout(context, attrs) {
    private val binding: ViewChattingDateBinding by lazy {
        ViewChattingDateBinding.inflate(
            LayoutInflater.from(context),
            this,
            true,
        )
    }

    init {
        getAttrs(attrs)
    }

    private fun getAttrs(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ChattingDateView)
        setTypedArray(typedArray)
    }

    private fun setTypedArray(typedArray: TypedArray) {
        val date = typedArray.getText(R.styleable.ChattingDateView_chattingDateText)
        binding.tvChattingDate.text = date
        typedArray.recycle()
    }
}
