package com.wolkowiczmateusz.horizontalcardview

import android.content.ClipData
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class CardsHorizontalAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val cards = ArrayList<Card>()
    private lateinit var context: Context

    var itemSelected: (card: Card, position: Int) -> Unit = { _, _ -> }
    var plusItemSelected: () -> Unit = {}
    var deleteItemSelected: (card: Card) -> Unit = {}
    var longClickPressListener: (card: Card) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View
        val inflater = LayoutInflater.from(parent.context)
        context = parent.context
        return when (viewType) {
            PLUS_TYPE_LEFT -> {
                view = inflater.inflate(R.layout.it_debit_card_plus_left, parent, false)
                PlusViewHolder(view)
            }
            PLUS_TYPE -> {
                view = inflater.inflate(R.layout.it_debit_card_plus, parent, false)
                PlusViewHolder(view)
            }
            else -> {
                view = inflater.inflate(R.layout.it_card_item, parent, false)
                CardViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val card = cards[position]
        if (getItemViewType(position) == CARD_TYPE) {
            val viewHolder = holder as CardViewHolder
            viewHolder.tvCardNumbers.text = card.cardNumber?.takeLast(4)
            if (card.selected) {
                viewHolder.ivCardBackground.setImageDrawable(context.getDrawable(R.drawable.ic_card_background))
            } else {
                viewHolder.ivCardBackground.setImageDrawable(context.getDrawable(R.drawable.ic_card_background_alpha))
            }
            viewHolder.itemView.setOnClickListener {
                itemSelected.invoke(card, position)
            }
            viewHolder.tvCardNumbers.setOnClickListener {
                deleteItemSelected.invoke(card)
            }
            viewHolder.itemView.setOnLongClickListener {
                longClickPressListener.invoke(card)
                val clipData = ClipData.newPlainText("CardNumber", card.id)
                val shadowBuilder: View.DragShadowBuilder = View.DragShadowBuilder(it)
                it.startDragAndDrop(clipData, shadowBuilder, it, 0)
            }
        } else {
            val viewHolder = holder as PlusViewHolder
            viewHolder.itemView.setOnClickListener {
                plusItemSelected.invoke()
            }
        }
    }

    fun setData(cardVo: List<Card>) {
        val cardsWithPlus = cardVo.toMutableList()
        cardsWithPlus.add(Card())
        this.cards.clear()
        this.cards.addAll(cardsWithPlus)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return cards.size
    }

    fun getItemByPosition(position: Int): Card {
        return cards[position]
    }

    fun isFirstItemSelected(position: Int): Boolean {
        return position == 0
    }

    fun makeViewSelected(position: Int) {
        cards.forEach {
            it.selected = false
        }
        val item = cards[position]
        item.selected = true
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == cards.size - 1) {
            convertPlusType()
        } else {
            CARD_TYPE
        }
    }

    private fun convertPlusType(): Int {
        return if (cards.size == 2) {
            PLUS_TYPE_LEFT
        } else {
            PLUS_TYPE
        }
    }

    fun getItemPositionByData(cardVo: Card?): Int {
        return cards.indexOfFirst { it.id == cardVo?.id }
    }

    fun getItemPositionById(cardId: String?): Card? {
        return cards.firstOrNull { it.id == cardId }
    }

    internal inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvCardNumbers: TextView = itemView.findViewById(R.id.tvCardNumber)
        var ivCardBackground: ImageView = itemView.findViewById(R.id.ivCardBackground)
    }

    internal inner class PlusViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    companion object {
        const val CARD_TYPE = 1
        const val PLUS_TYPE_LEFT = 2
        const val PLUS_TYPE = 3
    }
}