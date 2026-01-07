package com.catchmate.presentation.util

import android.content.Context
import com.catchmate.presentation.R

object GenderUtils {
    fun convertPostGender(gender: String): String =
        when (gender) {
            "여성" -> "F"
            "남성" -> "M"
            else -> "N"
        }

    fun convertBoardGender(
        context: Context,
        gender: String,
    ): String =
        when (gender) {
            "F" -> context.getString(R.string.female)
            "M" -> context.getString(R.string.male)
            "N" -> context.getString(R.string.regardless_of_gender)
            else -> ""
        }
}
