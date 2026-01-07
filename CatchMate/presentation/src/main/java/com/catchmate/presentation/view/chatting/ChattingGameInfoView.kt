package com.catchmate.presentation.view.chatting

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.catchmate.presentation.R
import com.catchmate.presentation.databinding.ViewChattingGameInfoBinding
import com.catchmate.presentation.util.ResourceUtil.setTeamViewResources

class ChattingGameInfoView(
    context: Context,
    attrs: AttributeSet,
) : ConstraintLayout(context, attrs) {
    private val binding: ViewChattingGameInfoBinding by lazy {
        ViewChattingGameInfoBinding.inflate(
            LayoutInflater.from(context),
            this,
            true,
        )
    }

    init {
        getAttrs(attrs)
    }

    private fun getAttrs(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ChattingGameInfoView)
        setTypedArray(typedArray)
    }

    private fun setTypedArray(typedArray: TypedArray) {
        val date = typedArray.getText(R.styleable.ChattingGameInfoView_chattingGameInfoDateText)
        val time = typedArray.getText(R.styleable.ChattingGameInfoView_chattingGameInfoTimeText)
        val place = typedArray.getText(R.styleable.ChattingGameInfoView_chattingGameInfoPlaceText)
        val homeTeamImage = typedArray.getResourceId(R.styleable.ChattingGameInfoView_chattingGameInfoHomeTeamImage, 0)
        val awayTeamImage = typedArray.getResourceId(R.styleable.ChattingGameInfoView_chattingGameInfoAwayTeamImage, 0)

        binding.tvChattingGameInfoDate.text = date
        binding.tvChattingGameInfoTime.text = time
        binding.tvChattingGameInfoPlace.text = place

        binding.ivChattingGameInfoHomeTeam.setImageResource(homeTeamImage)
        binding.ivChattingGameInfoAwayTeam.setImageResource(awayTeamImage)

        typedArray.recycle()
    }

    fun setHomeTeamImageView(
        homeClubId: Int,
        isCheerTeam: Boolean,
    ) {
        setTeamViewResources(
            homeClubId,
            isCheerTeam,
            binding.ivChattingGameInfoHomeTeam,
            binding.ivChattingGameInfoHomeTeamLogo,
            "chattingRoom",
            context,
        )
    }

    fun setAwayTeamImageView(
        awayClubId: Int,
        isCheerTeam: Boolean,
    ) {
        setTeamViewResources(
            awayClubId,
            isCheerTeam,
            binding.ivChattingGameInfoAwayTeam,
            binding.ivChattingGameInfoAwayTeamLogo,
            "chattingRoom",
            context,
        )
    }

    fun setGameDateTextView(date: String) {
        binding.tvChattingGameInfoDate.text = date
    }

    fun setGameTimeTextView(time: String) {
        binding.tvChattingGameInfoTime.text = time
    }

    fun setGamePlaceTextView(place: String) {
        binding.tvChattingGameInfoPlace.text = place
    }
}
