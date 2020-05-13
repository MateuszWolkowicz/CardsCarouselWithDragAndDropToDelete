package com.wolkowiczmateusz.horizontalcardview

data class Card(
    val id: String? = null,
    var cardNumber: String? = null,
    var status: String? = null,
    var expDate: String? = null,
    var selected: Boolean = false
)
