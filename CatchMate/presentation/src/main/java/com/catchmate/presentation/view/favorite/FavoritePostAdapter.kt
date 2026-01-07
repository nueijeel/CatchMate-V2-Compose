package com.catchmate.presentation.view.favorite

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.ToggleButton
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.catchmate.domain.model.board.Board
import com.catchmate.presentation.R
import com.catchmate.presentation.databinding.ItemHomePostBinding
import com.catchmate.presentation.interaction.OnListItemAllRemovedListener
import com.catchmate.presentation.interaction.OnPostItemClickListener
import com.catchmate.presentation.interaction.OnPostItemToggleClickListener
import com.catchmate.presentation.util.DateUtils
import com.catchmate.presentation.util.ResourceUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavoritePostAdapter(
    private val context: Context,
    private val layoutInflater: LayoutInflater,
    private val onPostItemClickListener: OnPostItemClickListener,
    private val onPostItemToggleClickListener: OnPostItemToggleClickListener,
    private val onPostItemAllRemovedListener: OnListItemAllRemovedListener,
) : RecyclerView.Adapter<FavoritePostAdapter.FavoritePostViewHolder>() {
    private var likedList: MutableList<Board> = mutableListOf()

    fun updateLikedList(newList: List<Board>) {
        likedList = newList.toMutableList()
        notifyDataSetChanged()
    }

    fun removeUnlikedPost(position: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            likedList.removeAt(position)
            notifyItemRemoved(position)
            if (likedList.size == 0) {
                onPostItemAllRemovedListener.onListItemAllRemoved()
            }
        }
    }

    inner class FavoritePostViewHolder(
        itemBinding: ItemHomePostBinding,
    ) : RecyclerView.ViewHolder(itemBinding.root) {
        val cvItemLayout: CardView = itemBinding.cvItemHomePost
        val tvItemCount: TextView = itemBinding.tvItemHomePostMemberCount
        val tvItemDate: TextView = itemBinding.tvItemHomePostDate
        val tvItemTime: TextView = itemBinding.tvItemHomePostTime
        val tvItemPlace: TextView = itemBinding.tvItemHomePostPlace
        val tvItemTitle: TextView = itemBinding.tvItemHomePostTitle
        val ivItemHomeTeamBg: ImageView = itemBinding.ivItemHomePostHomeTeamBg
        val ivItemAwayTeamBg: ImageView = itemBinding.ivItemHomePostAwayTeamBg
        val ivItemHomeTeamLogo: ImageView = itemBinding.ivItemHomePostHomeTeamLogo
        val ivItemAwayTeamLogo: ImageView = itemBinding.ivItemHomePostAwayTeamLogo
        val tbItemPostFavorite: ToggleButton = itemBinding.btnItemHomePostFavorite

        init {
            tbItemPostFavorite.setOnCheckedChangeListener { _, _ ->
                val pos = absoluteAdapterPosition
                Log.i("Current Pos", pos.toString())
                onPostItemToggleClickListener.onPostItemToggleClicked(likedList[pos].boardId, pos)
            }

            cvItemLayout.setOnClickListener {
                val pos = absoluteAdapterPosition
                Log.i("Current Pos", pos.toString())
                onPostItemClickListener.onPostItemClicked(likedList[pos].boardId)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): FavoritePostViewHolder {
        val itemBinding = ItemHomePostBinding.inflate(layoutInflater)
        itemBinding.root.layoutParams =
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            )
        return FavoritePostViewHolder(itemBinding)
    }

    override fun getItemCount(): Int = likedList.size

    override fun onBindViewHolder(
        holder: FavoritePostViewHolder,
        position: Int,
    ) {
        val board = likedList[position]
        holder.apply {
            tbItemPostFavorite.isChecked = true
            tbItemPostFavorite.visibility = View.VISIBLE

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

            ResourceUtil
                .setTeamViewResources(
                    board.gameInfo.homeClubId,
                    isCheerTeam,
                    ivItemHomeTeamBg,
                    ivItemHomeTeamLogo,
                    "home",
                    context,
                )
            ResourceUtil
                .setTeamViewResources(
                    board.gameInfo.awayClubId,
                    !isCheerTeam,
                    ivItemAwayTeamBg,
                    ivItemAwayTeamLogo,
                    "home",
                    context,
                )
        }
    }
}
