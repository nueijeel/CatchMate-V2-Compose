package com.catchmate.presentation.util

import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

object AgeUtils {
    // 10-50대면 숫자만, 전연령이면 0을 반환
    fun convertPostAge(age: String): String =
        if (age != "전연령") {
            age
                .replace(
                    Regex("[^0-9]"),
                    "",
                )
        } else {
            "0"
        }

    fun convertBirthDateToAge(birthDate: String): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val birth = LocalDate.parse(birthDate, formatter)

        val currentDate = LocalDate.now()

        val age = Period.between(birth, currentDate).years

        return when (age) {
            in 10..19 -> "10대"
            in 20..29 -> "20대"
            in 30..39 -> "30대"
            in 40..49 -> "40대"
            in 50..59 -> "50대"
            in 60..69 -> "60대"
            in 70..79 -> "70대"
            in 80..89 -> "80대"
            in 90..99 -> "90대"
            else -> "아동"
        }
    }

    fun convertAgeStringToList(age: String): List<String> = age.split(",").map { it }
}
