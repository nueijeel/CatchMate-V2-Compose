package com.catchmate.presentation.view.notification

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.view.View
import androidx.core.content.ContextCompat
import com.catchmate.presentation.R
import kotlin.math.abs

class SwipeBackgroundHelper {
    companion object {
        private const val OFFSET_PX = 20
        private const val EXTRA_PADDING = 50

        @JvmStatic
        fun paintDrawCommandToStart(
            canvas: Canvas,
            viewItem: View,
            backgroundColor: Int,
            dX: Float,
        ) {
            val drawCommand = createDrawCommand(viewItem, dX, backgroundColor)
            paintDrawCommand(drawCommand, canvas, dX, viewItem)
        }

        private fun createDrawCommand(
            viewItem: View,
            dX: Float,
            backgroundColor: Int,
        ): DrawCommand {
            val context = viewItem.context
            val backgroundColor = getBackgroundColor(backgroundColor, dX, viewItem)
            return DrawCommand(context.getString(R.string.dialog_button_delete), backgroundColor)
        }

        private fun getBackgroundColor(
            color: Int,
            dX: Float,
            viewItem: View,
        ): Int {
            val context = viewItem.context
            val width = viewItem.width.toFloat()
            return when {
                abs(dX) < width / 6 -> ContextCompat.getColor(context, R.color.brand100)
                abs(dX) < width / 3 -> ContextCompat.getColor(context, R.color.brand200)
                abs(dX) < width / 2 -> ContextCompat.getColor(context, R.color.brand300)
                abs(dX) < width * 2 / 3 -> ContextCompat.getColor(context, R.color.brand400)
                abs(dX) < width * 5 / 6 -> ContextCompat.getColor(context, R.color.brand500)
                else -> ContextCompat.getColor(context, color)
            }
            // 순서대로 1, 2, 3, 4, 5, 6 단계
        }

        private fun paintDrawCommand(
            drawCommand: DrawCommand,
            canvas: Canvas,
            dX: Float,
            viewItem: View,
        ) {
            drawBackground(canvas, viewItem, dX, drawCommand.backgroundColor)
            drawText(canvas, viewItem, dX, drawCommand.text)
        }

        private fun drawText(
            canvas: Canvas,
            viewItem: View,
            dX: Float,
            text: String,
        ) {
            val paint =
                Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = ContextCompat.getColor(viewItem.context, R.color.grey0)
                    textSize = 40f
                    textAlign = Paint.Align.CENTER
                }

            // 기존 아이콘 위치 계산 방식 사용
            val topMargin = calculateTopMargin(paint, viewItem)
            val bounds = getStartContainerRectangle(viewItem, text.length * 20, topMargin, OFFSET_PX, dX)

            // 텍스트 위치를 기존 아이콘의 중앙에 배치
            val textX = (bounds.left + bounds.right) / 2f
            val textY = (bounds.top + bounds.bottom) / 2f - (paint.descent() + paint.ascent()) / 2

            canvas.drawText(text, textX, textY, paint)
        }

        private fun getStartContainerRectangle(
            viewItem: View,
            textWidth: Int,
            topMargin: Int,
            sideOffset: Int,
            dx: Float,
        ): Rect {
            val leftBound = viewItem.right + dx.toInt() + sideOffset + EXTRA_PADDING
            val rightBound = viewItem.right + dx.toInt() + textWidth + sideOffset + EXTRA_PADDING
            val topBound = viewItem.top + topMargin
            val bottomBound = viewItem.bottom - topMargin
            return Rect(leftBound, topBound, rightBound, bottomBound)
        }

        private fun calculateTopMargin(
            paint: Paint,
            viewItem: View,
        ): Int = (viewItem.height - paint.textSize.toInt()) / 2

        private fun drawBackground(
            canvas: Canvas,
            viewItem: View,
            dX: Float,
            color: Int,
        ) {
            val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
            backgroundPaint.color = color
            val backgroundRectangle = getBackGroundRectangle(viewItem, dX)
            canvas.drawRect(backgroundRectangle, backgroundPaint)
        }

        // 백그라운드를 그릴 사각형 정보 계산
        private fun getBackGroundRectangle(
            viewItem: View,
            dX: Float,
        ): RectF =
            if (dX < 0) {
                RectF(
                    viewItem.right.toFloat() + dX,
                    viewItem.top.toFloat(),
                    viewItem.right.toFloat(),
                    viewItem.bottom.toFloat(),
                )
            } else {
                RectF(
                    0f,
                    viewItem.top.toFloat(),
                    dX,
                    viewItem.bottom.toFloat(),
                )
            }
    }

    private class DrawCommand(
        val text: String,
        val backgroundColor: Int,
    )
}
