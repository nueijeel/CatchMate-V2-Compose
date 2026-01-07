package com.catchmate.presentation.view.mypage

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import com.catchmate.presentation.R
import com.catchmate.presentation.databinding.ViewMyPageUserProfileBinding

class MyPageUserProfileView(
    context: Context,
    attrs: AttributeSet,
) : ConstraintLayout(context, attrs) {
    val binding: ViewMyPageUserProfileBinding by lazy {
        ViewMyPageUserProfileBinding.inflate(
            LayoutInflater.from(context),
            this,
            true,
        )
    }

    init {
        getAttrs(attrs)
    }

    private fun getAttrs(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.MyPageUserProfileView)
        setTypedArray(typedArray)
    }

    private fun setTypedArray(typedArray: TypedArray) {
        val profileImage = typedArray.getResourceId(R.styleable.MyPageUserProfileView_myPageUserProfileImage, 0)
        val nickname = typedArray.getText(R.styleable.MyPageUserProfileView_myPageUserProfileNicknameText)
        val teamBadgeColor = typedArray.getColor(R.styleable.MyPageUserProfileView_myPageUserProfileTeamBadgeColor, 0)
        val team = typedArray.getText(R.styleable.MyPageUserProfileView_myPageUserProfileTeamBadgeText)
        val cheerStyle = typedArray.getText(R.styleable.MyPageUserProfileView_myPageUserProfileCheerStyleBadgeText)
        val gender = typedArray.getText(R.styleable.MyPageUserProfileView_myPageUserProfileGenderBadgeText)
        val age = typedArray.getText(R.styleable.MyPageUserProfileView_myPageUserProfileAgeBadgeText)

        binding.apply {
            ivMyPageUserProfile.setImageResource(profileImage)
            tvMyPageUserProfileNickname.text = nickname
            ViewCompat.setBackgroundTintList(tvMyPageUserProfileTeamBadge, ColorStateList.valueOf(teamBadgeColor))
            tvMyPageUserProfileTeamBadge.text = team
            tvMyPageUserProfileCheerStyleBadge.text = cheerStyle
            tvMyPageUserProfileGenderBadge.text = gender
            tvMyPageUserProfileAgeBadge.text = age
        }
    }
}
