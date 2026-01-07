package com.catchmate.presentation.view.post

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.catchmate.presentation.R
import com.catchmate.presentation.databinding.ViewTeamToggleCheckButtonBinding

class TeamToggleCheckButtonView(
    context: Context,
    attrs: AttributeSet,
) : ConstraintLayout(context, attrs) {
    val binding: ViewTeamToggleCheckButtonBinding by lazy {
        ViewTeamToggleCheckButtonBinding.inflate(
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
        binding.toggleTeamToggleCheckButton.setOnCheckedChangeListener { _, isChecked ->
            binding.cbTeamToggleCheckButton.isChecked = isChecked
        }
        binding.cbTeamToggleCheckButton.setOnCheckedChangeListener { _, isChecked ->
            binding.toggleTeamToggleCheckButton.isChecked = isChecked
        }
    }

    private fun getAttrs(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.TeamToggleCheckButtonView)
        setTypeArray(typedArray)
    }

    private fun setTypeArray(typedArray: TypedArray) {
        val toggleBg = typedArray.getResourceId(R.styleable.TeamToggleCheckButtonView_teamToggleCheckButtonToggleBg, 0)
        val teamLogo = typedArray.getResourceId(R.styleable.TeamToggleCheckButtonView_teamToggleCheckButtonLogoImage, 0)
        val teamName = typedArray.getText(R.styleable.TeamToggleCheckButtonView_teamToggleCheckButtonTeamNameText)

        binding.toggleTeamToggleCheckButton.setBackgroundResource(toggleBg)
        binding.ivTeamToggleCheckButton.setImageResource(teamLogo)
        binding.tvTeamToggleCheckButton.text = teamName

        typedArray.recycle()
    }
}
