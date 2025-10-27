package com.udb.controlgastos.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ExpenseRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun getCurrentUserId(): String? = auth.currentUser?.uid

    private fun getExpensesCollection() = firestore.collection("expenses")

    // Nuevo gasto
    suspend fun addExpense(expense: Expense): Result<String> {
        return try {
            val userId = getCurrentUserId() ?: return Result.failure(Exception("Usuario no autenticado"))
            val expenseWithUser = expense.copy(userId = userId)
            val docRef = getExpensesCollection().add(expenseWithUser).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Gastos
    fun getUserExpenses(): Flow<List<Expense>> = callbackFlow {
        val userId = getCurrentUserId()
        if (userId == null) {
            close(Exception("Usuario no autenticado"))
            return@callbackFlow
        }

        val listener = getExpensesCollection()
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val expenses = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Expense::class.java)?.copy(id = doc.id)
                }?.sortedByDescending { it.date.toDate() } ?: emptyList()

                trySend(expenses)
            }

        awaitClose { listener.remove() }
    }

    // Actualizar un gasto
    suspend fun updateExpense(expense: Expense): Result<Unit> {
        return try {
            getExpensesCollection()
                .document(expense.id)
                .set(expense)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Eliminar un gasto
    suspend fun deleteExpense(expenseId: String): Result<Unit> {
        return try {
            getExpensesCollection()
                .document(expenseId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // gastos del mes actual
    suspend fun getMonthlyTotal(year: Int, month: Int): Double {
        return try {
            val userId = getCurrentUserId() ?: return 0.0

            val snapshot = getExpensesCollection()
                .whereEqualTo("userId", userId)
                .get()
                .await()

            val calendar = java.util.Calendar.getInstance()
            snapshot.documents.mapNotNull {
                it.toObject(Expense::class.java)
            }.filter { expense ->
                calendar.time = expense.date.toDate()
                val expenseMonth = calendar.get(java.util.Calendar.MONTH) + 1
                val expenseYear = calendar.get(java.util.Calendar.YEAR)
                expenseMonth == month && expenseYear == year
            }.sumOf { it.amount }

        } catch (e: Exception) {
            0.0
        }
    }
}