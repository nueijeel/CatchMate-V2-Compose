package com.catchmate.presentation.view.mypage

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.catchmate.domain.model.enroll.ReceivedEnrollInfo
import com.catchmate.presentation.R
import com.catchmate.presentation.databinding.ItemProfileBinding
import com.catchmate.presentation.util.AgeUtils
import com.catchmate.presentation.util.ClubUtils
import com.catchmate.presentation.util.GenderUtils
import com.catchmate.presentation.util.ResourceUtil.convertTeamColor
import de.hdodenhof.circleimageview.CircleImageView

class ReceivedJoinProfileAdapter(
    private val context: Context,
    private val layoutInflater: LayoutInflater,
) : RecyclerView.Adapter<ReceivedJoinProfileAdapter.ReceivedJoinProfileViewHolder>() {
    private var enrollInfoList: MutableList<ReceivedEnrollInfo> = mutableListOf()

    fun updateEnrollInfoList(newList: List<ReceivedEnrollInfo>) {
        enrollInfoList = newList.toMutableList()
        notifyDataSetChanged()
    }

    inner class ReceivedJoinProfileViewHolder(
        itemBinding: ItemProfileBinding,
    ) : RecyclerView.ViewHolder(itemBinding.root) {
        val cvProfile: CardView
        val tvProfileNewBadge: TextView
        val ivProfileImage: CircleImageView
        val tvProfileNickname: TextView
        val tvProfileTeam: TextView
        val tvProfileCheerStyle: TextView
        val tvProfileGender: TextView
        val tvProfileAge: TextView

        init {
            cvProfile = itemBinding.cvProfile
            tvProfileNewBadge = itemBinding.tvProfileItemNewBadge
            ivProfileImage = itemBinding.ivProfileItemImage
            tvProfileNickname = itemBinding.tvProfileItemNickname
            tvProfileTeam = itemBinding.tvProfileItemTeamBadge
            tvProfileCheerStyle = itemBinding.tvProfileItemCheerStyleBadge
            tvProfileGender = itemBinding.tvProfileItemGenderBadge
            tvProfileAge = itemBinding.tvProfileItemAgeBadge
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ReceivedJoinProfileViewHolder {
        val itemBinding = ItemProfileBinding.inflate(layoutInflater)
        itemBinding.root.layoutParams =
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            )
        return ReceivedJoinProfileViewHolder(itemBinding)
    }

    override fun getItemCount(): Int = enrollInfoList.size

    override fun onBindViewHolder(
        holder: ReceivedJoinProfileViewHolder,
        position: Int,
    ) {
        val info = enrollInfoList[position]
        holder.apply {
            if (info.new) {
                tvProfileNewBadge.visibility = View.VISIBLE
            } else {
                tvProfileNewBadge.visibility = View.INVISIBLE
            }
            Glide
                .with(context)
                .load(info.userInfo.profileImageUrl)
                .error(R.drawable.vec_all_default_profile)
                .into(ivProfileImage)
            tvProfileNickname.text = info.userInfo.nickName
            tvProfileTeam.text = ClubUtils.convertClubIdToName(info.userInfo.favoriteClub.id)

            DrawableCompat
                .setTint(
                    tvProfileTeam.background,
                    convertTeamColor(
                        context,
                        info.userInfo.favoriteClub.id,
                        true,
                        "receivedJoinProfileAdapter",
                    ),
                )

            if (info.userInfo.watchStyle != null) {
                tvProfileCheerStyle.visibility = View.VISIBLE
                tvProfileCheerStyle.text = info.userInfo.watchStyle
            } else {
                tvProfileCheerStyle.visibility = View.GONE
            }
            tvProfileGender.text = GenderUtils.convertBoardGender(context, info.userInfo.gender)
            tvProfileAge.text = AgeUtils.convertBirthDateToAge(info.userInfo.birthDate)
        }
    }
}
