package com.catchmate.presentation.view.mypage

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.catchmate.domain.model.board.Board
import com.catchmate.presentation.R
import com.catchmate.presentation.databinding.ItemHomePostBinding
import com.catchmate.presentation.interaction.OnPostItemClickListener
import com.catchmate.presentation.util.DateUtils
import com.catchmate.presentation.util.ResourceUtil

class MyPostAdapter(
    private val context: Context,
    private val layoutInflater: LayoutInflater,
    private val onPostItemClickListener: OnPostItemClickListener,
) : RecyclerView.Adapter<MyPostAdapter.MyPostViewHolder>() {
    private var postList: MutableList<Board> = mutableListOf()

    fun updatePostList(newList: List<Board>) {
        postList = newList.toMutableList()
        notifyDataSetChanged()
    }

    inner class MyPostViewHolder(
        itemBinding: ItemHomePostBinding,
    ) : RecyclerView.ViewHolder(itemBinding.root) {
        val cvItemLayout: CardView
        val tvItemCount: TextView
        val tvItemDate: TextView
        val tvItemTime: TextView
        val tvItemPlace: TextView
        val tvItemTitle: TextView
        val ivItemHomeTeamBg: ImageView
        val ivItemAwayTeamBg: ImageView
        val ivItemHomeTeamLogo: ImageView
        val ivItemAwayTeamLogo: ImageView

        init {
            cvItemLayout = itemBinding.cvItemHomePost
            tvItemCount = itemBinding.tvItemHomePostMemberCount
            tvItemDate = itemBinding.tvItemHomePostDate
            tvItemTime = itemBinding.tvItemHomePostTime
            tvItemPlace = itemBinding.tvItemHomePostPlace
            tvItemTitle = itemBinding.tvItemHomePostTitle
            ivItemHomeTeamBg = itemBinding.ivItemHomePostHomeTeamBg
            ivItemAwayTeamBg = itemBinding.ivItemHomePostAwayTeamBg
            ivItemHomeTeamLogo = itemBinding.ivItemHomePostHomeTeamLogo
            ivItemAwayTeamLogo = itemBinding.ivItemHomePostAwayTeamLogo
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): MyPostViewHolder {
        val itemBinding = ItemHomePostBinding.inflate(layoutInflater)
        itemBinding.root.layoutParams =
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            )
        return MyPostViewHolder(itemBinding)
    }

    override fun getItemCount(): Int = postList.size

    override fun onBindViewHolder(
        holder: MyPostViewHolder,
        position: Int,
    ) {
        val board = postList[position]
        holder.apply {
            if (board.currentPerson == board.maxPerson) {
                tvItemCount.text = "${board.currentPerson}/${board.maxPerson} 마감"
                tvItemCount.setBackgroundResource(R.drawable.shape_all_rect_r12_grey100)
                tvItemCount.setTextColor(ContextCompat.getColor(context, R.color.grey500))
            } else {
                tvItemCount.text = "${board.currentPerson}/${board.maxPerson}"
                tvItemCount.setBackgroundResource(R.drawable.shape_all_rect_r12_brand50)
                tvItemCount.setTextColor(ContextCompat.getColor(context, R.color.brand500))
            }

            val dateTimePair = DateUtils.formatISODateTime(board.gameInfo.gameStartDate!!)
            tvItemDate.text = dateTimePair.first
            tvItemTime.text = dateTimePair.second
            tvItemPlace.text = board.gameInfo.location
            tvItemTitle.text = board.title

            val isCheerTeam = board.gameInfo.homeClubId == board.cheerClubId

            ResourceUtil.setTeamViewResources(
                board.gameInfo.homeClubId,
                isCheerTeam,
                ivItemHomeTeamBg,
                ivItemHomeTeamLogo,
                "home",
                context,
            )
            ResourceUtil.setTeamViewResources(
                board.gameInfo.awayClubId,
                !isCheerTeam,
                ivItemAwayTeamBg,
                ivItemAwayTeamLogo,
                "home",
                context,
            )

            cvItemLayout.setOnClickListener {
                onPostItemClickListener.onPostItemClicked(board.boardId)
            }
        }
    }
}
