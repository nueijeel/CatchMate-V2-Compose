package com.catchmate.presentation.view.chatting

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.catchmate.domain.model.chatting.ChatMessageInfo
import com.catchmate.domain.model.enumclass.ChatMessageType
import com.catchmate.domain.model.user.GetUserProfileResponse
import com.catchmate.presentation.R
import com.catchmate.presentation.databinding.ItemReceivedChatBinding
import com.catchmate.presentation.databinding.ItemSendChatBinding
import com.catchmate.presentation.databinding.ViewChattingDateBinding
import com.catchmate.presentation.databinding.ViewChattingParticipantAlertBinding
import com.catchmate.presentation.util.DateUtils.formatChatSendTime

class ChatListAdapter(
    private val userId: Long,
    private val chattingCrewList: List<GetUserProfileResponse>,
) : ListAdapter<ChatMessageInfo, RecyclerView.ViewHolder>(diffUtil) {
    inner class SendChatViewHolder(
        private val binding: ItemSendChatBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(chatMessageInfo: ChatMessageInfo) {
            binding.tvSendChatMessage.text = chatMessageInfo.content
            binding.tvSendChatTime.text = formatChatSendTime(chatMessageInfo.id?.date!!)
        }
    }

    inner class ReceivedChatViewHolder(
        private val binding: ItemReceivedChatBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(chatMessageInfo: ChatMessageInfo) {
            // 현재 메시지가 리스트의 마지막인지 확인
            val isLastMessage = absoluteAdapterPosition == currentList.lastIndex
            // 현재 메시지와 다음 메시지의 senderId가 같은지 비교
            val isSameSenderAsNext =
                absoluteAdapterPosition < currentList.lastIndex &&
                    currentList[absoluteAdapterPosition + 1].senderId == chatMessageInfo.senderId

            if (isLastMessage || !isSameSenderAsNext) {
                // 마지막 메시지(서버에서 주는 데이터를 역순으로 스크롤하게 출력했기 때문에 마지막 메시지인지 판단)이거나 다음 메시지와 보낸 사람이 다르면 프로필과 닉네임 표시
                binding.ivReceivedChatProfile.visibility = View.VISIBLE
                binding.tvReceivedChatNickname.visibility = View.VISIBLE
                val currentUserInfo =
                    chattingCrewList.firstOrNull { it.userId == chatMessageInfo.senderId }
                binding.tvReceivedChatNickname.text = currentUserInfo?.nickName ?: "알수없음"
                Glide
                    .with(binding.root)
                    .load(currentUserInfo?.profileImageUrl ?: R.drawable.vec_all_default_profile)
                    .error(R.drawable.vec_all_default_profile)
                    .into(binding.ivReceivedChatProfile)
            } else {
                // 연속된 메시지라면 프로필과 닉네임 뷰 숨기기
                binding.ivReceivedChatProfile.visibility = View.GONE
                binding.tvReceivedChatNickname.visibility = View.GONE
            }
            binding.tvReceivedChatMessage.text = chatMessageInfo.content
            binding.tvReceivedChatTime.text = formatChatSendTime(chatMessageInfo.id?.date!!)
        }
    }

    inner class ChattingDateViewHolder(
        private val binding: ViewChattingDateBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(chatMessageInfo: ChatMessageInfo) {
            binding.tvChattingDate.text = chatMessageInfo.content
        }
    }

    inner class ChattingParticipantViewHolder(
        private val binding: ViewChattingParticipantAlertBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            chatMessageInfo: ChatMessageInfo,
            viewType: Int,
        ) {
            val nickname =
                chattingCrewList.firstOrNull { it.userId == chatMessageInfo.senderId }?.nickName ?: "알수없음"
            val message =
                if (viewType == ENTER) {
                    "$nickname 님이 채팅에 참여했어요"
                } else {
                    "$nickname 님이 나갔어요"
                }
            binding.tvChattingParticipantAlert.text = message
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder =
        when (viewType) {
            MY_CHAT -> {
                SendChatViewHolder(
                    ItemSendChatBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false,
                    ),
                )
            }

            OTHER_CHAT -> {
                ReceivedChatViewHolder(
                    ItemReceivedChatBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false,
                    ),
                )
            }

            DATE -> {
                ChattingDateViewHolder(
                    ViewChattingDateBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false,
                    ),
                )
            }

            else -> {
                ChattingParticipantViewHolder(
                    ViewChattingParticipantAlertBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false,
                    ),
                )
            }
        }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        when (getItemViewType(position)) {
            MY_CHAT -> (holder as SendChatViewHolder).bind(currentList[position])
            OTHER_CHAT -> (holder as ReceivedChatViewHolder).bind(currentList[position])
            DATE -> (holder as ChattingDateViewHolder).bind(currentList[position])
            ENTER, LEAVE -> (holder as ChattingParticipantViewHolder).bind(currentList[position], getItemViewType(position))
        }
    }

    override fun getItemViewType(position: Int): Int =
        when (currentList[position].messageType) {
            ChatMessageType.TALK.name -> {
                if (currentList[position].senderId == userId) {
                    MY_CHAT
                } else {
                    OTHER_CHAT
                }
            }

            ChatMessageType.DATE.name -> {
                DATE
            }

            ChatMessageType.ENTER.name -> {
                ENTER
            }

            else -> {
                LEAVE
            }
        }

    companion object {
        val diffUtil =
            object : DiffUtil.ItemCallback<ChatMessageInfo>() {
                override fun areItemsTheSame(
                    oldItem: ChatMessageInfo,
                    newItem: ChatMessageInfo,
                ): Boolean = oldItem == newItem

                override fun areContentsTheSame(
                    oldItem: ChatMessageInfo,
                    newItem: ChatMessageInfo,
                ): Boolean = oldItem == newItem
            }

        private const val MY_CHAT = 1
        private const val OTHER_CHAT = 2
        private const val DATE = 3
        private const val ENTER = 4
        private const val LEAVE = 5
    }
}
