package com.catchmate.presentation.util

import java.text.SimpleDateFormat
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField
import java.util.Date
import java.util.Locale

object DateUtils {
    fun formatBirthDate(inputDate: String): String {
        val year = inputDate.substring(0..1)
        val month = inputDate.substring(2..3)
        val day = inputDate.substring(4..5)

        return if (year.toInt() in 0..50) {
            "20$year-$month-$day"
        } else {
            "19$year-$month-$day"
        }
    }

    // 게시글 등록 시 bottom sheet 통해 선택된 날짜, 시간 값 포맷
    fun formatGameDateTime(
        date: String,
        time: String,
    ): String = "$date $time:00"

    // 게시글 수정 시 받아온 날짜+시간 값 화면에 표시되는 형식으로 포맷
    fun formatGameDateTimeEditBoard(dateTime: String): String {
        val (date, time) = dateTime.split("T")
        val newTime = time.substringBefore(".")
        return "$date $newTime"
    }

    // 날짜+시간 값 한 번에 표시되도록 포맷(readPost, addPost)
    fun formatPlayDate(dateTime: String): String {
        val (date, time) = dateTime.split(" ", "T")

        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDate: Date = inputDateFormat.parse(date)
        val outputDateFormat = SimpleDateFormat("M월 d일 E요일", Locale.getDefault())

        val formattedTime = time.substring(0, 5)

        return outputDateFormat.format(formattedDate) + " | " + formattedTime
    }

    fun formatInquiryAnsweredDate(dateTime: String): String {
        val (date, time) = dateTime.split("T")

        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDate: Date = inputDateFormat.parse(date)
        val outputDateFormat = SimpleDateFormat("yyyy년 M월 d일", Locale.getDefault())

        return outputDateFormat.format(formattedDate)
    }

    // 날짜+시간 값 분리해서 원하는 형태로 포맷 후 Pair로 반환 - 날짜:M월 d일 E요일
    fun formatISODateTime(dateTime: String): Pair<String, String> {
        val (date, time) = dateTime.split("T")

        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd")

        val formattedDate = inputDateFormat.parse(date)

        val outputDateFormat = SimpleDateFormat("M월 d일 E요일", Locale.KOREAN)

        return Pair(outputDateFormat.format(formattedDate), time.substring(0, 5))
    }

    // home date filter 포맷하는 함수
    fun formatDateToFilterDate(date: String): String {
        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd")
        val formattedDate = inputDateFormat.parse(date)
        val outputDateFormat = SimpleDateFormat("MM.dd E", Locale.KOREAN)
        return outputDateFormat.format(formattedDate)
    }

    // 날짜+시간 값 분리해서 원하는 형태로 포맷 후 Pair로 반환 - 날짜:MM.dd
    fun formatISODateTimeToDateTime(dateTime: String): Pair<String, String> {
        val (date, time) = dateTime.split("T")

        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd")

        val formattedDate = inputDateFormat.parse(date)

        val outputDateFormat = SimpleDateFormat("MM.dd", Locale.KOREAN)

        return Pair(outputDateFormat.format(formattedDate), time.substring(0, 5))
    }

    // 받은 신청 화면에서 직관 신청 등록된 시간 표시하는 포맷 함수
    fun formatDateTimeToEnrollDateTime(dateTime: String): String {
        val (date, time) = dateTime.split("T")
        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd")
        val formattedDate = inputDateFormat.parse(date)
        val outputDataFormat = SimpleDateFormat("M월 d일", Locale.KOREAN)
        return outputDataFormat.format(formattedDate) + " " + time.substring(0, 5)
    }

    // 마지막 채팅 시간 포맷하는 함수
    fun formatLastChatTime(dateTime: String): String {
        val formatter =
            DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
                .optionalStart() // 소수점 이하가 있을 수도 있고 없을 수도 있음
                .appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true) // 최소 0자리, 최대 6자리
                .optionalEnd()
                .toFormatter()
        val parsedTime =
            LocalDateTime
                .parse(dateTime, formatter)
                .atZone(ZoneId.of("Asia/Seoul"))
                .toInstant()
        val now = Instant.now()
        val duration = Duration.between(parsedTime, now)

        val minutes = duration.toMinutes()
        val hours = duration.toHours()
        val days = duration.toDays()

        return when {
            minutes < 1 -> {
                "방금"
            }

            minutes < 60 -> {
                "${minutes}분 전"
            }

            hours < 24 -> {
                "${hours}시간 전"
            }

            days < 7 -> {
                "${days}일 전"
            }

            else -> {
                val dateTime = LocalDateTime.ofInstant(parsedTime, ZoneId.of("Asia/Seoul"))
                val formatter = DateTimeFormatter.ofPattern("M월 d일")
                dateTime.format(formatter)
            }
        }
    }

    // 채팅 전송 시간 포맷하는 함수
    fun formatChatSendTime(dateTime: String): String {
        val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        val parsedTime =
            ZonedDateTime
                .parse(dateTime, formatter)
                .withZoneSameInstant(ZoneId.of("Asia/Seoul")) // 시스템 시간대로 변환

        val outputFormatter = DateTimeFormatter.ofPattern("a h:mm") // "오전 4:55" 형식
        return parsedTime.format(outputFormatter)
    }

    // 채팅 수신 시 날짜값 포맷하는 함수
    fun getCurrentTimeFormatted(): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
        return ZonedDateTime.now().format(formatter)
    }

    fun checkIsFinishedGame(dateTime: String): Boolean {
        val gameDate = Instant.parse("${dateTime}Z")
        val now = Instant.now()
        return gameDate.isBefore(now)
    }
}
