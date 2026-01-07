package com.catchmate.presentation.view.onboarding

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.catchmate.presentation.R
import com.catchmate.presentation.databinding.ViewCheerStyleButtonBinding

class CheerStyleButtonView(
    context: Context,
    attrs: AttributeSet,
) : ConstraintLayout(context, attrs) {
    val binding: ViewCheerStyleButtonBinding by lazy {
        ViewCheerStyleButtonBinding.inflate(
            LayoutInflater.from(context),
            this,
            true,
        )
    }

    init {
        getAttrs(attrs)
    }

    private fun getAttrs(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CheerStyleButtonView)
        setTypedArray(typedArray)
    }

    private fun setTypedArray(typedArray: TypedArray) {
        val cheerStyleToggleBg = typedArray.getResourceId(R.styleable.CheerStyleButtonView_cheerStyleToggleBg, -1)
        val cheerStyleNameText = typedArray.getText(R.styleable.CheerStyleButtonView_cheerStyleNameText)
        val cheerStyleExplainText = typedArray.getText(R.styleable.CheerStyleButtonView_cheerStyleExplainText)
        val cheerStyleImage = typedArray.getResourceId(R.styleable.CheerStyleButtonView_cheerStyleImage, -1)

        binding.tvCheerStyleName.text = cheerStyleNameText
        binding.tvCheerStyleExplain.text = cheerStyleExplainText
        binding.toggleCheerStyle.setBackgroundResource(cheerStyleToggleBg)
        binding.ivCheerStyle.setImageResource(cheerStyleImage)
        typedArray.recycle()
    }
}
