package com.dolgantsev.androindfirstproject.view.rv_adapters

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class TopSpaceItemDecoration(private val spaceHeight: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.top = spaceHeight
        outRect.bottom = spaceHeight
        outRect.left = spaceHeight
        outRect.right = spaceHeight
    }
}