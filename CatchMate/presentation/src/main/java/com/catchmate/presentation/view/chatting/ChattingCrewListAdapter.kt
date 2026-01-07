package com.catchmate.presentation.view.chatting

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.catchmate.domain.model.user.GetUserProfileResponse
import com.catchmate.presentation.databinding.ItemChattingParticipantBinding
import com.catchmate.presentation.interaction.OnKickOutClickListener

class ChattingCrewListAdapter(
    private val loginUserId: Long,
    private val writerId: Long,
    private val pageType: String,
    private val onKickOutClickListener: OnKickOutClickListener? = null,
) : ListAdapter<GetUserProfileResponse, ChattingCrewListAdapter.CrewViewHolder>(diffUtil) {
    inner class CrewViewHolder(
        private val binding: ItemChattingParticipantBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(profile: GetUserProfileResponse) {
            binding.apply {
                Glide
                    .with(root)
                    .load(profile.profileImageUrl)
                    .into(ivChattingParticipant)
                tvChattingParticipantNickname.text = profile.nickName

                val currentUserId = profile.userId

                if (currentUserId == loginUserId) {
                    tvChattingParticipantMeBadge.visibility = View.VISIBLE
                } else {
                    tvChattingParticipantMeBadge.visibility = View.GONE
                }

                if (currentUserId == writerId) {
                    ivChattingParticipantLeaderBadge.visibility = View.VISIBLE
                } else {
                    ivChattingParticipantLeaderBadge.visibility = View.GONE
                }

                if (pageType == "chattingSetting") {
                    tvChattingParticipantKickOut.setOnClickListener {
                        onKickOutClickListener?.onKickOutClicked(profile.userId)
                    }
                    // 방장 아닌 사람만 내보내기 버튼 표시
                    if (profile.userId != writerId) {
                        tvChattingParticipantKickOut.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CrewViewHolder =
        CrewViewHolder(
            ItemChattingParticipantBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            ),
        )

    override fun onBindViewHolder(
        holder: CrewViewHolder,
        position: Int,
    ) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil =
            object : DiffUtil.ItemCallback<GetUserProfileResponse>() {
                override fun areItemsTheSame(
                    oldItem: GetUserProfileResponse,
                    newItem: GetUserProfileResponse,
                ): Boolean = oldItem == newItem

                override fun areContentsTheSame(
                    oldItem: GetUserProfileResponse,
                    newItem: GetUserProfileResponse,
                ): Boolean = oldItem == newItem
            }
    }
}
