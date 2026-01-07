package com.catchmate.presentation.view.mypage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.catchmate.domain.model.user.GetUserProfileResponse
import com.catchmate.presentation.R
import com.catchmate.presentation.databinding.ItemChattingParticipantBinding
import com.catchmate.presentation.interaction.OnBlockedUserSelectedListener

class BlockedUserListAdapter(
    private val onBlockedUserSelectedListener: OnBlockedUserSelectedListener,
) : ListAdapter<GetUserProfileResponse, BlockedUserListAdapter.BlockedUserViewHolder>(diffUtil) {
    inner class BlockedUserViewHolder(
        private val binding: ItemChattingParticipantBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(userInfo: GetUserProfileResponse) {
            binding.apply {
                tvChattingParticipantKickOut.visibility = View.VISIBLE
                tvChattingParticipantKickOut.text = binding.root.context.getString(R.string.mypage_setting_unblock)
                tvChattingParticipantKickOut.setOnClickListener {
                    onBlockedUserSelectedListener.onBlockedUserSelected(
                        absoluteAdapterPosition,
                        userInfo.userId,
                        userInfo.nickName,
                    )
                }
                Glide
                    .with(binding.root)
                    .load(userInfo.profileImageUrl)
                    .error(R.drawable.vec_all_default_profile)
                    .into(ivChattingParticipant)
                tvChattingParticipantNickname.text = userInfo.nickName
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): BlockedUserViewHolder =
        BlockedUserViewHolder(
            ItemChattingParticipantBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            ),
        )

    override fun onBindViewHolder(
        holder: BlockedUserViewHolder,
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
                ): Boolean = oldItem.userId == newItem.userId

                override fun areContentsTheSame(
                    oldItem: GetUserProfileResponse,
                    newItem: GetUserProfileResponse,
                ): Boolean = oldItem.userId == newItem.userId
            }
    }
}
