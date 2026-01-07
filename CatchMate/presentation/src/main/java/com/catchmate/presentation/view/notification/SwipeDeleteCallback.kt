package com.catchmate.presentation.view.notification

import android.graphics.Canvas
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.catchmate.domain.model.notification.NotificationInfo
import com.catchmate.presentation.R

class SwipeDeleteCallback(
    private val recyclerView: RecyclerView,
    private val data: MutableList<NotificationInfo>,
) : ItemTouchHelper.Callback() {
    var adapter: NotificationAdapter = (recyclerView.adapter as NotificationAdapter)

    // dragFlags 0으로 지정해서 드래그 비허용, swipeFlags LEFT로 지정해서 왼쪽 스와이프 허용
    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
    ): Int = makeMovementFlags(0, ItemTouchHelper.LEFT)

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder,
    ): Boolean = false

    override fun onSwiped(
        viewHolder: RecyclerView.ViewHolder,
        direction: Int,
    ) {
        // swipe 발생시킨 아이템 position
        val deletedPos = viewHolder.absoluteAdapterPosition
        val deletedItemId = data[deletedPos].notificationId

        if (direction == ItemTouchHelper.LEFT) {
            // adapter에서 아이템 제거
            adapter.swipeItem(deletedPos, deletedItemId)
        }
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean,
    ) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            val viewItem = viewHolder.itemView
            if (dX < 0) {
                SwipeBackgroundHelper.paintDrawCommandToStart(
                    c,
                    viewItem,
                    R.color.brand500,
                    dX,
                )
            }
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}
