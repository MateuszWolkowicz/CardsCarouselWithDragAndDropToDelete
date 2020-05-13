package com.wolkowiczmateusz.horizontalcardview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import kotlinx.android.synthetic.main.v_cards_horizontal.view.*
import kotlin.math.abs


class CardsHorizontalRecyclerView constructor(
    context: Context,
    attrs: AttributeSet
) : ConstraintLayout(context, attrs) {

    private var adapter = CardsHorizontalAdapter()
    private var startSnapHelper: SnapHelper = LinearSnapHelper()
    // StartSnapHelper - it should snap item to the start of the list. Not to the center as LinearSnapHelper
    //        private var startSnapHelper: SnapHelper = StartSnapHelper()
    var itemSelected: (card: Card) -> Unit = {}
    var plusItemSelected: () -> Unit = {}
    var deleteItemSelected: (card: Card) -> Unit = {}
    var longClickPressListener: (card: Card) -> Unit = {}

    init {
        inflate(R.layout.v_cards_horizontal, true)
        setupRecyclerView()
        registerAddCardClickListeners()
        registerCardClickListeners()
        registerCardDeleteListeners()
        registerLongClickPressListener()
    }

    private fun registerLongClickPressListener() {
        adapter.longClickPressListener = { card ->
            longClickPressListener.invoke(card)
        }
    }

    private fun registerCardClickListeners() {
        adapter.itemSelected = { _, position ->
            rvCardHorizontal.smoothScrollToPosition(position)
        }
    }

    private fun scrollToPosition(position: Int) {
        rvCardHorizontal.smoothScrollToPosition(position)
    }

    private fun registerAddCardClickListeners() {
        adapter.plusItemSelected = {
            plusItemSelected.invoke()
        }
    }

    private fun registerCardDeleteListeners() {
        adapter.deleteItemSelected = {
            deleteItemSelected.invoke(it)
        }
    }

    private fun setupRecyclerView() {
        rvCardHorizontal.adapter = adapter
        startSnapHelper.attachToRecyclerView(rvCardHorizontal)
    }

    fun setData(cardVo: List<Card>) {
        val linearLayoutManager: LinearLayoutManager = if (cardVo.size <= 1) {
            object : LinearLayoutManager(context, HORIZONTAL, false) {
                override fun canScrollHorizontally(): Boolean {
                    return false
                }
            }
        } else {
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }
        rvCardHorizontal.layoutManager = linearLayoutManager
        rvCardHorizontal.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            var prevPosition: Int? = null

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val centerView = startSnapHelper.findSnapView(linearLayoutManager)
                    centerView?.let {
                        var pos = linearLayoutManager.getPosition(centerView)
                        val maxPosition = linearLayoutManager.itemCount - 1
                        if (pos == maxPosition) {
                            pos = maxPosition - 1
                        }
                        if (pos != prevPosition) {
                            prevPosition = pos
                            setSelectedItem(pos)
                        }
                    }
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val firstPos = linearLayoutManager.findFirstVisibleItemPosition()
                val lastPos = linearLayoutManager.findLastVisibleItemPosition()
                val middle = abs(lastPos - firstPos) / 2 + firstPos
                if (middle >= 0) {
                    post { adapter.makeViewSelected(middle) }
                }
            }
        })
        adapter.setData(cardVo)
        val selectedCard = cardVo.find { it.selected }
        var position = 0
        if (selectedCard != null) {
            position = adapter.getItemPositionByData(selectedCard)
        }
        scrollToPosition(position)
        setSelectedItem(position)
    }


    fun setSelectedItem(position: Int) {
        if (position >= 0) {
            this.post { adapter.makeViewSelected(position) }
            val itemFromAdapter = adapter.getItemByPosition(position)
            itemSelected.invoke(itemFromAdapter)
        }
    }

    fun getItemPositionById(cardId: String?): Card? {
        return adapter.getItemPositionById(cardId)
    }

}

fun ViewGroup.inflate(layoutRes: Int, attachToRoot: Boolean = false) =
    context.inflate(layoutRes, this, attachToRoot)

fun Context.inflate(layoutRes: Int, root: ViewGroup? = null, attachToRoot: Boolean = false) =
    LayoutInflater.from(this).inflate(layoutRes, root, attachToRoot)