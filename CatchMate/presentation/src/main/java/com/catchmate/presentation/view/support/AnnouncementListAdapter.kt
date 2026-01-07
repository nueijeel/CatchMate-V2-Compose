package com.catchmate.presentation.view.support

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.catchmate.domain.model.support.NoticeInfo
import com.catchmate.presentation.databinding.ItemAnnouncementBinding
import com.catchmate.presentation.interaction.OnAnnouncementItemClickListener
import com.catchmate.presentation.util.DateUtils.formatInquiryAnsweredDate

class AnnouncementListAdapter(
    private val onAnnouncementItemClickListener: OnAnnouncementItemClickListener,
) : ListAdapter<NoticeInfo, AnnouncementListAdapter.AnnouncementViewHolder>(diffUtil) {
    inner class AnnouncementViewHolder(
        private val binding: ItemAnnouncementBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: NoticeInfo) {
            binding.tvTitleItemAnnouncement.text = data.title
            binding.tvTeamAndDateInfoAnnouncement.text = "${data.userInfo.nickName} | ${formatInquiryAnsweredDate(data.updatedAt)}"
            binding.cvItemAnnouncement.setOnClickListener {
                onAnnouncementItemClickListener.onAnnouncementItemClick(data.noticeId)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): AnnouncementViewHolder =
        AnnouncementViewHolder(
            ItemAnnouncementBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            ),
        )

    override fun onBindViewHolder(
        holder: AnnouncementViewHolder,
        position: Int,
    ) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil =
            object : DiffUtil.ItemCallback<NoticeInfo>() {
                override fun areItemsTheSame(
                    oldItem: NoticeInfo,
                    newItem: NoticeInfo,
                ): Boolean = oldItem == newItem

                override fun areContentsTheSame(
                    oldItem: NoticeInfo,
                    newItem: NoticeInfo,
                ): Boolean = oldItem == newItem
            }
    }
}
