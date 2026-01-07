package com.catchmate.presentation.util

import android.content.Context
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.catchmate.domain.model.enumclass.Club
import com.catchmate.presentation.R

object ResourceUtil {
    fun convertTeamLogo(clubId: Int): Int =
        when (clubId) {
            Club.NC.id -> R.drawable.vec_all_nc_dinos_logo
            Club.SAMSUNG.id -> R.drawable.vec_all_samsung_lions_logo
            Club.SSG.id -> R.drawable.vec_all_ssg_landers_logo
            Club.DOOSAN.id -> R.drawable.vec_all_doosan_bears_logo
            Club.KT.id -> R.drawable.vec_all_kt_wiz_logo
            Club.HANWHA.id -> R.drawable.vec_all_hanwha_eagles_logo
            Club.LOTTE.id -> R.drawable.vec_all_lotte_giants_logo
            Club.KIA.id -> R.drawable.vec_all_kia_tigers_logo
            Club.LG.id -> R.drawable.vec_all_lg_twins_logo
            else -> R.drawable.vec_all_kiwoom_heroes_logo
        }

    fun convertTeamColor(
        context: Context,
        clubId: Int,
        isCheerTeam: Boolean,
        currentPage: String,
    ): Int =
        if (isCheerTeam) {
            when (clubId) {
                Club.NC.id -> ContextCompat.getColor(context, R.color.nc_dinos)
                Club.SAMSUNG.id -> ContextCompat.getColor(context, R.color.samsung_lions)
                Club.SSG.id -> ContextCompat.getColor(context, R.color.ssg_landers)
                Club.DOOSAN.id -> ContextCompat.getColor(context, R.color.doosan_bears)
                Club.KT.id -> ContextCompat.getColor(context, R.color.kt_wiz)
                Club.HANWHA.id -> ContextCompat.getColor(context, R.color.hanwha_eagles)
                Club.LOTTE.id -> ContextCompat.getColor(context, R.color.lotte_giants)
                Club.KIA.id -> ContextCompat.getColor(context, R.color.kia_tigers)
                Club.LG.id -> ContextCompat.getColor(context, R.color.lg_twins)
                Club.KIWOOM.id -> ContextCompat.getColor(context, R.color.kiwoom_heroes)
                else -> ContextCompat.getColor(context, R.color.brand500)
            }
        } else {
            if (currentPage == "home") {
                ContextCompat.getColor(context, R.color.grey50)
            } else {
                ContextCompat.getColor(context, R.color.grey0)
            }
        }

    private fun setTeamLogoOpacity(
        imageView: ImageView,
        isCheerTeam: Boolean,
    ) {
        if (!isCheerTeam) {
            imageView.alpha = 0.6f
        } else {
            imageView.alpha = 1.0f
        }
    }

    fun setTeamViewResources(
        clubId: Int,
        isCheerTeam: Boolean,
        backgroundView: ImageView,
        logoView: ImageView,
        currentPage: String,
        context: Context,
    ) {
        // 로고 설정
        logoView.setImageResource(convertTeamLogo(clubId))
        setTeamLogoOpacity(logoView, isCheerTeam)

        // 배경색 설정
        val drawable = backgroundView.background.mutate()
        DrawableCompat.setTint(drawable, convertTeamColor(context, clubId, isCheerTeam, currentPage))
    }
}
