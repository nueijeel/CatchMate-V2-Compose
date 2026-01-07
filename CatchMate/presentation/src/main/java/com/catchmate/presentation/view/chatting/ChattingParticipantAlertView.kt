package com.catchmate.presentation.view.chatting

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.catchmate.presentation.R
import com.catchmate.presentation.databinding.ViewChattingParticipantAlertBinding

class ChattingParticipantAlertView(
    context: Context,
    attrs: AttributeSet,
) : ConstraintLayout(context, attrs) {
    private val binding: ViewChattingParticipantAlertBinding by lazy {
        ViewChattingParticipantAlertBinding.inflate(
            LayoutInflater.from(context),
            this,
            true,
        )
    }

    init {
        getAttrs(attrs)
    }

    private fun getAttrs(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ChattingParticipantAlertView)
        setTypedArray(typedArray)
    }

    private fun setTypedArray(typedArray: TypedArray) {
        val text = typedArray.getText(R.styleable.ChattingParticipantAlertView_chattingParticipantAlertText)

        binding.tvChattingParticipantAlert.text = text
    }
}
