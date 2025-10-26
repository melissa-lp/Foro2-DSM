package com.udb.controlgastos.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Expense(
    @DocumentId
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val amount: Double = 0.0,
    val category: String = "",
    val date: Timestamp = Timestamp.now(),
    val description: String = ""
) {
    constructor() : this("", "", "", 0.0, "", Timestamp.now(), "")
}