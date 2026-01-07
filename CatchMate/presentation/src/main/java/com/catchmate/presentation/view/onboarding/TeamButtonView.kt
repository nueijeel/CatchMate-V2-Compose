package com.catchmate.presentation.view.onboarding

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.catchmate.presentation.R
import com.catchmate.presentation.databinding.ViewTeamButtonBinding

class TeamButtonView(
    context: Context,
    attrs: AttributeSet,
) : ConstraintLayout(context, attrs) {
    val binding: ViewTeamButtonBinding by lazy {
        ViewTeamButtonBinding.inflate(
            LayoutInflater.from(context),
            this,
            true,
        )
    }

    init {
        initView()
        getAttrs(attrs)
    }

    private fun initView() {
        binding.tvTeamButton.setTextColor(ContextCompat.getColor(context, R.color.grey700))
        binding.toggleTeamButton.setOnCheckedChangeListener { _, isChecked ->
            val textColor =
                if (isChecked) {
                    ContextCompat.getColor(context, R.color.brand500)
                } else {
                    ContextCompat.getColor(context, R.color.grey700)
                }
            binding.tvTeamButton.setTextColor(textColor)
        }
    }

    private fun getAttrs(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.TeamButtonView)
        setTypeArray(typedArray)
    }

    private fun setTypeArray(typedArray: TypedArray) {
        val teamName = typedArray.getText(R.styleable.TeamButtonView_teamNameText)
        val teamToggleBg = typedArray.getResourceId(R.styleable.TeamButtonView_teamToggleBg, 0)
        val teamLogoImage = typedArray.getResourceId(R.styleable.TeamButtonView_teamLogoImage, 0)

        binding.tvTeamButton.text = teamName
        if (teamToggleBg != 0) {
            binding.toggleTeamButton.setBackgroundResource(teamToggleBg)
        }

        if (teamLogoImage != 0) {
            binding.ivTeamButton.setImageResource(teamLogoImage)
        }

        typedArray.recycle()
    }
}
