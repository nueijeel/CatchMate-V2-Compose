package com.catchmate.presentation.view.mypage

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.catchmate.domain.model.enroll.ReceivedEnrollInfo
import com.catchmate.presentation.R
import com.catchmate.presentation.databinding.ItemReceivedEnrollBinding
import com.catchmate.presentation.interaction.OnReceivedEnrollResultSelectedListener
import com.catchmate.presentation.util.AgeUtils.convertBirthDateToAge
import com.catchmate.presentation.util.ClubUtils.convertClubIdToName
import com.catchmate.presentation.util.DateUtils.formatDateTimeToEnrollDateTime
import com.catchmate.presentation.util.GenderUtils.convertBoardGender
import com.catchmate.presentation.util.ResourceUtil.convertTeamColor
import de.hdodenhof.circleimageview.CircleImageView

class ReceivedEnrollAdapter(
    private val context: Context,
    private val layoutInflater: LayoutInflater,
    private val onReceivedEnrollResultSelectedListener: OnReceivedEnrollResultSelectedListener,
) : RecyclerView.Adapter<ReceivedEnrollAdapter.ReceivedEnrollViewHolder>() {
    private var enrollList: MutableList<ReceivedEnrollInfo> = mutableListOf()

    fun updateEnrollList(newList: List<ReceivedEnrollInfo>) {
        enrollList = newList.toMutableList()
        notifyDataSetChanged()
    }

    inner class ReceivedEnrollViewHolder(
        itemBinding: ItemReceivedEnrollBinding,
    ) : RecyclerView.ViewHolder(itemBinding.root) {
        val ivEnrollUserProfile: CircleImageView = itemBinding.ivItemReceivedEnrollProfile
        val tvEnrollUserNickname: TextView = itemBinding.tvItemReceivedEnrollNickname
        val tvEnrollUserCheerTeam: TextView = itemBinding.tvItemReceivedEnrollTeamBadge
        val tvEnrollUserWatchStyle: TextView = itemBinding.tvItemReceivedEnrollWatchStyle
        val tvEnrollUserGender: TextView = itemBinding.tvItemReceivedEnrollGenderBadge
        val tvEnrollUserAge: TextView = itemBinding.tvItemReceivedEnrollAgeBadge
        val tvEnrollSavedDateTime: TextView = itemBinding.tvItemReceivedEnrollSavedDateTime
        val edtEnrollDescription: EditText = itemBinding.edtItemReceivedEnrollDescription
        val tvEnrollReject: TextView = itemBinding.tvItemReceivedEnrollReject
        val tvEnrollAccept: TextView = itemBinding.tvItemReceivedEnrollAccept
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ReceivedEnrollViewHolder {
        val itemBinding = ItemReceivedEnrollBinding.inflate(layoutInflater)
        itemBinding.root.layoutParams =
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            )
        return ReceivedEnrollViewHolder(itemBinding)
    }

    override fun getItemCount(): Int = enrollList.size

    override fun onBindViewHolder(
        holder: ReceivedEnrollViewHolder,
        position: Int,
    ) {
        val info = enrollList[position]
        holder.apply {
            Glide
                .with(context)
                .load(info.userInfo.profileImageUrl)
                .error(R.drawable.vec_all_default_profile)
                .into(ivEnrollUserProfile)

            tvEnrollUserNickname.text = info.userInfo.nickName
            tvEnrollUserCheerTeam.text = convertClubIdToName(info.userInfo.favoriteClub.id)

            DrawableCompat
                .setTint(
                    tvEnrollUserCheerTeam.background,
                    convertTeamColor(
                        context,
                        info.userInfo.favoriteClub.id,
                        true,
                        "receivedEnrollAdapter",
                    ),
                )

            if (info.userInfo.watchStyle != null) {
                tvEnrollUserWatchStyle.visibility = View.VISIBLE
                tvEnrollUserWatchStyle.text = info.userInfo.watchStyle
            } else {
                tvEnrollUserWatchStyle.visibility = View.GONE
            }

            tvEnrollUserGender.text = convertBoardGender(context, info.userInfo.gender)
            tvEnrollUserAge.text = convertBirthDateToAge(info.userInfo.birthDate)
            tvEnrollSavedDateTime.text = formatDateTimeToEnrollDateTime(info.requestDate)
            edtEnrollDescription.setText(info.description)

            tvEnrollReject.setOnClickListener {
                onReceivedEnrollResultSelectedListener.onReceivedEnrollRejected(info.enrollId)
            }
            tvEnrollAccept.setOnClickListener {
                onReceivedEnrollResultSelectedListener.onReceivedEnrollAccepted(info.enrollId)
            }
        }
    }
}
