package com.catchmate.presentation.view.chatting

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.catchmate.domain.model.chatting.ChatRoomInfo
import com.catchmate.presentation.R
import com.catchmate.presentation.databinding.ItemChattingBinding
import com.catchmate.presentation.interaction.OnChattingRoomSelectedListener
import com.catchmate.presentation.interaction.OnItemSwipeListener
import com.catchmate.presentation.interaction.OnListItemAllRemovedListener
import com.catchmate.presentation.util.DateUtils.formatLastChatTime
import com.catchmate.presentation.util.ResourceUtil.convertTeamColor
import com.catchmate.presentation.util.ResourceUtil.convertTeamLogo

class ChattingRoomListAdapter(
    private val onChattingRoomSelectedListener: OnChattingRoomSelectedListener,
    private val itemSwipeListener: OnItemSwipeListener,
    private val onListItemAllRemovedListener: OnListItemAllRemovedListener,
) : ListAdapter<ChatRoomInfo, ChattingRoomListAdapter.ChattingRoomListViewHolder>(diffUtil) {
    fun swipeItem(
        position: Int,
        swipedItemId: Long,
    ) {
        itemSwipeListener.onNotificationItemSwipe(position, swipedItemId)
    }

    fun removeItem(position: Int) {
        val mutableList = currentList.toMutableList()
        if (position >= 0 && position < mutableList.size) {
            mutableList.removeAt(position)
            submitList(mutableList)
            if (mutableList.size == 0) {
                onListItemAllRemovedListener.onListItemAllRemoved()
            }
        }
    }

    inner class ChattingRoomListViewHolder(
        private val binding: ItemChattingBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(chatRoomInfo: ChatRoomInfo) {
            binding.apply {
                root.setOnClickListener {
                    onChattingRoomSelectedListener.onChattingRoomSelected(chatRoomInfo.chatRoomId, chatRoomInfo.isNewChatRoom)
                }
                // 채팅방 이미지가 변경된 적 없는 경우 chatRoomImage에 cheerTeamId가 String으로 담겨옴
                // 해당 변수를 int로 변환할 때 예외 처리를 통해 변경된 적 있을 경우의 imageUrl을 imageView에 표시
                try {
                    val clubId = chatRoomInfo.chatRoomImage.toInt()
                    val logoResource = convertTeamLogo(clubId)
                    ivChattingItemLogo.visibility = View.VISIBLE
                    Glide
                        .with(root.context)
                        .load(logoResource)
                        .into(ivChattingItemLogo)
                    DrawableCompat
                        .setTint(
                            ivChattingItemBg.drawable,
                            convertTeamColor(
                                root.context,
                                clubId,
                                true,
                                "chattingHome",
                            ),
                        )
                } catch (e: Exception) {
                    ivChattingItemLogo.visibility = View.GONE
                    Glide
                        .with(root.context)
                        .load(chatRoomInfo.chatRoomImage)
                        .into(ivChattingItemBg)
                }

                tvChattingItemTitle.text = chatRoomInfo.boardInfo.title
                if (chatRoomInfo.isNewChatRoom) {
                    tvChattingItemNew.visibility = View.VISIBLE
                    tvChattingItemPeopleCount.visibility = View.GONE
                } else {
                    tvChattingItemNew.visibility = View.GONE
                    tvChattingItemPeopleCount.visibility = View.VISIBLE
                    tvChattingItemPeopleCount.text = chatRoomInfo.participantCount.toString()
                }

                if (chatRoomInfo.lastMessageAt == null && chatRoomInfo.lastMessageContent == null) {
                    tvChattingItemLastChat.text = root.context.getString(R.string.chatting_start_message)
                    tvChattingItemTime.text = "방금"
                    tvChattingItemUnreadMessageCount.visibility = View.GONE
                } else {
                    tvChattingItemLastChat.text = chatRoomInfo.lastMessageContent
                    tvChattingItemTime.text = formatLastChatTime(chatRoomInfo.lastMessageAt!!)
                    if (chatRoomInfo.unreadMessageCount == 0) {
                        tvChattingItemUnreadMessageCount.visibility = View.GONE
                    } else {
                        tvChattingItemUnreadMessageCount.visibility = View.VISIBLE
                        tvChattingItemUnreadMessageCount.text = chatRoomInfo.unreadMessageCount.toString()
                    }
                }
            }
        }
    }

    companion object {
        val diffUtil =
            object : DiffUtil.ItemCallback<ChatRoomInfo>() {
                override fun areItemsTheSame(
                    oldItem: ChatRoomInfo,
                    newItem: ChatRoomInfo,
                ): Boolean = oldItem == newItem

                override fun areContentsTheSame(
                    oldItem: ChatRoomInfo,
                    newItem: ChatRoomInfo,
                ): Boolean = oldItem == newItem
            }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ChattingRoomListViewHolder =
        ChattingRoomListViewHolder(
            ItemChattingBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            ),
        )

    override fun onBindViewHolder(
        holder: ChattingRoomListViewHolder,
        position: Int,
    ) {
        holder.bind(currentList[position])
    }
}
