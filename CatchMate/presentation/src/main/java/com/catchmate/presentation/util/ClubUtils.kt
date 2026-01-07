package com.catchmate.presentation.util

import com.catchmate.domain.model.enumclass.Club

object ClubUtils {
    fun convertClubNameToId(clubName: String): Int =
        when (clubName) {
            "타이거즈" -> Club.KIA.id
            "라이온즈" -> Club.SAMSUNG.id
            "트윈스" -> Club.LG.id
            "베어스" -> Club.DOOSAN.id
            "위즈" -> Club.KT.id
            "랜더스" -> Club.SSG.id
            "자이언츠" -> Club.LOTTE.id
            "이글스" -> Club.HANWHA.id
            "다이노스" -> Club.NC.id
            "히어로즈" -> Club.KIWOOM.id
            "평화주의자" -> Club.PACIFIST.id
            else -> Club.BEGINNER.id
        }

    fun convertClubIdToName(clubId: Int): String =
        when (clubId) {
            Club.KIA.id -> Club.KIA.teamName
            Club.SAMSUNG.id -> Club.SAMSUNG.teamName
            Club.LG.id -> Club.LG.teamName
            Club.DOOSAN.id -> Club.DOOSAN.teamName
            Club.KT.id -> Club.KT.teamName
            Club.SSG.id -> Club.SSG.teamName
            Club.LOTTE.id -> Club.LOTTE.teamName
            Club.HANWHA.id -> Club.HANWHA.teamName
            Club.NC.id -> Club.NC.teamName
            Club.KIWOOM.id -> Club.KIWOOM.teamName
            Club.PACIFIST.id -> Club.PACIFIST.teamName
            else -> Club.BEGINNER.teamName
        }

    fun convertClubIdListToNameList(cludIdList: Array<Int>): MutableList<String> {
        val newList: MutableList<String> = mutableListOf()
        cludIdList.forEach { id ->
            when (id) {
                Club.KIA.id -> newList.add(Club.KIA.teamName)
                Club.SAMSUNG.id -> newList.add(Club.SAMSUNG.teamName)
                Club.LG.id -> newList.add(Club.LG.teamName)
                Club.DOOSAN.id -> newList.add(Club.DOOSAN.teamName)
                Club.KT.id -> newList.add(Club.KT.teamName)
                Club.SSG.id -> newList.add(Club.SSG.teamName)
                Club.LOTTE.id -> newList.add(Club.LOTTE.teamName)
                Club.HANWHA.id -> newList.add(Club.HANWHA.teamName)
                Club.NC.id -> newList.add(Club.NC.teamName)
                else -> newList.add(Club.KIWOOM.teamName)
            }
        }
        return newList
    }
}
