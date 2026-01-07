package com.catchmate.presentation.view.mypage

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.catchmate.domain.model.enroll.AllReceivedEnrollInfoResponse
import com.catchmate.presentation.databinding.ItemReceivedJoinBinding
import com.catchmate.presentation.interaction.OnReceivedEnrollClickListener
import com.catchmate.presentation.util.DateUtils

class ReceivedJoinAdapter(
    private val layoutInflater: LayoutInflater,
    private val onReceivedEnrollClickListener: OnReceivedEnrollClickListener,
) : RecyclerView.Adapter<ReceivedJoinAdapter.ReceivedJoinViewHolder>() {
    private var list: MutableList<AllReceivedEnrollInfoResponse> = mutableListOf()

    fun updateList(newList: List<AllReceivedEnrollInfoResponse>) {
        list = newList.toMutableList()
        notifyDataSetChanged()
    }

    inner class ReceivedJoinViewHolder(
        itemBinding: ItemReceivedJoinBinding,
    ) : RecyclerView.ViewHolder(itemBinding.root) {
        val layoutBoardInfo: ConstraintLayout
        val tvGameDate: TextView
        val tvGameTime: TextView
        val tvGamePlace: TextView
        val tvBoardTitle: TextView
        val imgbtnMove: ImageButton
        val rvProfile: RecyclerView

        init {
            layoutBoardInfo = itemBinding.layoutItemReceivedJoinBoardInfo
            tvGameDate = itemBinding.tvReceivedJoinItemDate
            tvGameTime = itemBinding.tvReceivedJoinItemTime
            tvGamePlace = itemBinding.tvReceivedJoinItemPlace
            tvBoardTitle = itemBinding.tvReceivedJoinItemTitle
            imgbtnMove = itemBinding.imgbtnReceivedJoinItemMove
            rvProfile = itemBinding.rvReceivedJoinItemProfile

            rvProfile.apply {
                adapter = ReceivedJoinProfileAdapter(context, layoutInflater)
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ReceivedJoinViewHolder {
        val itemBinding = ItemReceivedJoinBinding.inflate(layoutInflater)
        itemBinding.root.layoutParams =
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            )
        return ReceivedJoinViewHolder(itemBinding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(
        holder: ReceivedJoinViewHolder,
        position: Int,
    ) {
        val boardInfo = list[position].boardInfo
        val enrollReceivedInfoList = list[position].enrollReceiveInfoList

        holder.apply {
            val dateTime = DateUtils.formatISODateTime(boardInfo.gameInfo.gameStartDate!!)
            tvGameDate.text = dateTime.first
            tvGameTime.text = dateTime.second
            tvGamePlace.text = boardInfo.gameInfo.location
            tvBoardTitle.text = boardInfo.title

            val adapter = rvProfile.adapter as ReceivedJoinProfileAdapter
            adapter.updateEnrollInfoList(enrollReceivedInfoList)

            layoutBoardInfo.setOnClickListener {
                onReceivedEnrollClickListener.onReceivedEnrollClick(boardInfo.boardId)
            }
        }
    }
}
