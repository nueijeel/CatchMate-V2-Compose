package com.catchmate.presentation.view.notification

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.catchmate.domain.model.notification.NotificationInfo
import com.catchmate.presentation.R
import com.catchmate.presentation.databinding.ItemNotificationBinding
import com.catchmate.presentation.interaction.OnItemSwipeListener
import com.catchmate.presentation.interaction.OnListItemAllRemovedListener
import com.catchmate.presentation.interaction.OnNotificationItemClickListener
import com.catchmate.presentation.util.DateUtils
import de.hdodenhof.circleimageview.CircleImageView

class NotificationAdapter(
    private val context: Context,
    private val layoutInflater: LayoutInflater,
    private val itemClickListener: OnNotificationItemClickListener,
    private val itemSwipeListener: OnItemSwipeListener,
    private val itemAllRemovedListener: OnListItemAllRemovedListener,
) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {
    private var notificationList: MutableList<NotificationInfo> = mutableListOf()

    fun updateNotificationList(newList: List<NotificationInfo>) {
        notificationList = newList.toMutableList()
        notifyDataSetChanged()
    }

    fun updateSelectedNotification(pos: Int) {
        notificationList[pos].read = true
        notifyItemChanged(pos)
    }

    fun removeItem(pos: Int) {
        Log.i("알림 어댑터", "remove Item : $pos")
        notificationList.removeAt(pos)
        notifyItemRemoved(pos)
        if (notificationList.size == 0) itemAllRemovedListener.onListItemAllRemoved()
    }

    fun swipeItem(
        pos: Int,
        notificationId: Long,
    ) {
        itemSwipeListener.onNotificationItemSwipe(pos, notificationId)
    }

    inner class ViewHolder(
        itemBinding: ItemNotificationBinding,
    ) : RecyclerView.ViewHolder(itemBinding.root) {
        val layoutNotification: ConstraintLayout
        val ivNotificationItemProfile: CircleImageView
        val tvNotificationItemTitle: TextView
        val tvNotificationItemDate: TextView
        val tvNotificationItemTime: TextView
        val tvNotificationItemPlace: TextView

        init {
            layoutNotification = itemBinding.layoutNotification
            ivNotificationItemProfile = itemBinding.ivNotificationItemProfile
            tvNotificationItemTitle = itemBinding.tvNotificationItemTitle
            tvNotificationItemDate = itemBinding.tvNotificationItemDate
            tvNotificationItemTime = itemBinding.tvNotificationItemTime
            tvNotificationItemPlace = itemBinding.tvNotificationItemPlace

            itemBinding.root.setOnClickListener {
                val pos = absoluteAdapterPosition
                val currentNotice = notificationList[pos]
                if (currentNotice.boardInfo == null) {
                    itemClickListener.onNotificationItemClick(
                        currentNotice.notificationId,
                        pos,
                        null,
                        null,
                        currentNotice.inquiryInfo?.inquiryId,
                    )
                } else {
                    val chatRoomId = currentNotice.boardInfo?.chatRoomId
                    itemClickListener.onNotificationItemClick(
                        currentNotice.notificationId,
                        pos,
                        currentNotice.acceptStatus,
                        if (chatRoomId == -1L) {
                            null
                        } else {
                            chatRoomId
                        },
                        null,
                    )
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val itemBinding = ItemNotificationBinding.inflate(layoutInflater)
        itemBinding.root.layoutParams =
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            )
        return ViewHolder(itemBinding)
    }

    override fun getItemCount(): Int = notificationList.size

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        val info = notificationList[position]
        if (info.boardInfo == null) {
            Glide
                .with(context)
                .load(R.drawable.ic_notification_samsung_device)
                .into(holder.ivNotificationItemProfile)
            val (date, _) = info.createdAt.split("T")
            holder.tvNotificationItemDate.text = date.replace("-", ".")
        } else {
            Glide
                .with(context)
                .load(info.senderProfileImageUrl)
                .into(holder.ivNotificationItemProfile)
            val dateTime: Pair<String, String> = DateUtils.formatISODateTimeToDateTime(info.boardInfo!!.gameInfo.gameStartDate!!)
            holder.tvNotificationItemDate.text = dateTime.first + " | "
            holder.tvNotificationItemTime.text = dateTime.second + " | "
            holder.tvNotificationItemPlace.text = info.boardInfo!!.gameInfo.location
        }
        holder.tvNotificationItemTitle.text = info.body
        if (info.read) {
            holder.layoutNotification.setBackgroundColor(ContextCompat.getColor(context, R.color.grey50))
            holder.tvNotificationItemTitle.setTextColor(ContextCompat.getColor(context, R.color.grey500))
        } else {
            holder.layoutNotification.setBackgroundColor(ContextCompat.getColor(context, R.color.grey0))
            holder.tvNotificationItemTitle.setTextColor(ContextCompat.getColor(context, R.color.grey800))
        }
    }
}
