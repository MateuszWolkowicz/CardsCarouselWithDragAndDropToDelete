package com.wolkowiczmateusz.horizontalcardview

import android.content.ClipData
import android.graphics.Color
import android.os.Bundle
import android.view.DragEvent
import android.view.View
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity() {

    private val list = mutableListOf(
        Card(id = "8", cardNumber = "58958598594")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupRecyclerView()
        dragAndDropListener()
    }

    private fun dragAndDropListener() {
        ivDelete.setOnDragListener { _, event ->
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    true
                }
                DragEvent.ACTION_DRAG_ENTERED -> {
                    true
                }
                DragEvent.ACTION_DRAG_LOCATION -> {
                    true
                }
                DragEvent.ACTION_DRAG_EXITED -> {
                    true
                }
                DragEvent.ACTION_DROP -> {
                    val clipData: ClipData = event.clipData
                    val cardId = clipData.getItemAt(0).text
                    val card = rvCard.getItemPositionById(cardId.toString())
                    Toast.makeText(this, "Card deleted : ${card?.cardNumber}", LENGTH_SHORT).show()
                    list.removeIf { it.id == cardId }
                    rvCard.setData(list)
                    true
                }
                DragEvent.ACTION_DRAG_ENDED -> {
                    for (i in 0 until container.childCount) {
                        val v = container.getChildAt(i)
                        if (v != ivDelete) {
                            v.alpha = 1f
                        }
                    }
                    ivDelete.alpha = 1f
                    container.setBackgroundColor(Color.WHITE)
                    ivDelete.visibility = View.GONE
                    true
                }
                else -> {
                    true
                }
            }
        }
    }

    private fun setupRecyclerView() {
        rvCard.plusItemSelected = {
            addNewCardToList()
        }
        rvCard.itemSelected = {
            tvSelectedCard.text = it.cardNumber
        }
        rvCard.setData(list)
        rvCard.longClickPressListener = { _ ->
            container.setBackgroundColor(Color.GRAY)
            for (i in 0 until container.childCount) {
                val v = container.getChildAt(i)
                if (v != ivDelete) {
                    v.alpha = 0.5f
                }
            }
            ivDelete.alpha = 1f
            ivDelete.visibility = View.VISIBLE
        }
    }

    private fun addNewCardToList() {
        val newList = mutableListOf<Card>()
        val newId = UUID.randomUUID()
        val randomCardNumber = (1000..9999).random()
        list.forEach {
            it.selected = false
            newList.add(it)
        }
        newList.add(
            Card(
                id = newId.toString(),
                selected = true,
                cardNumber = "5895859$randomCardNumber"
            )
        )
        list.clear()
        list.addAll(newList)
        rvCard.setData(list)
    }
}
