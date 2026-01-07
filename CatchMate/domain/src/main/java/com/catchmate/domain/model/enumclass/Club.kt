package com.catchmate.domain.model.enumclass

// name 프로퍼티가 enum 기본 속성이라 생성자 명으로 지정할 수 없음 name -> teamName 대체
enum class Club(
    val id: Int,
    val teamName: String,
    val homeStadium: String,
    val region: String,
) {
    KIA(1, "타이거즈", "기아 챔피언스 필드", "광주"),
    SAMSUNG(2, "라이온즈", "대구 삼성 라이온즈 파크", "대구"),
    LG(3, "트윈스", "잠실 야구장", "잠실"),
    DOOSAN(4, "베어스", "잠실 야구장", "잠실"),
    KT(5, "위즈", "수원KT위즈파크", "수원"),
    SSG(6, "랜더스", "인천 SSG랜더스필드", "인천"),
    LOTTE(7, "자이언츠", "사직 야구장", "부산"),
    HANWHA(8, "이글스", "한화생명 이글스 파크", "대전"),
    NC(9, "다이노스", "창원NC파크", "창원"),
    KIWOOM(10, "히어로즈", "고척 스카이돔", "고척"),
    PACIFIST(11, "평화주의자", "평화주의자", "평화주의자"),
    BEGINNER(12, "야알못", "야알못", "야알못"),
}
