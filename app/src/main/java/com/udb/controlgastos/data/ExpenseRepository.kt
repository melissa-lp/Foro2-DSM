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

    // Gastos del usuario actual
    fun getUserExpenses(): Flow<List<Expense>> = callbackFlow {
        val userId = getCurrentUserId()
        if (userId == null) {
            close(Exception("Usuario no autenticado"))
            return@callbackFlow
        }

        val listener = getExpensesCollection()
            .whereEqualTo("userId", userId)
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val expenses = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Expense::class.java)?.copy(id = doc.id)
                } ?: emptyList()

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

    // Obtener total de gastos del mes actual
    suspend fun getMonthlyTotal(year: Int, month: Int): Double {
        return try {
            val userId = getCurrentUserId() ?: return 0.0

            val calendar = java.util.Calendar.getInstance()
            calendar.set(year, month - 1, 1, 0, 0, 0)
            val startOfMonth = com.google.firebase.Timestamp(calendar.time)

            calendar.set(year, month - 1, calendar.getActualMaximum(java.util.Calendar.DAY_OF_MONTH), 23, 59, 59)
            val endOfMonth = com.google.firebase.Timestamp(calendar.time)

            val snapshot = getExpensesCollection()
                .whereEqualTo("userId", userId)
                .whereGreaterThanOrEqualTo("date", startOfMonth)
                .whereLessThanOrEqualTo("date", endOfMonth)
                .get()
                .await()

            snapshot.documents.mapNotNull {
                it.toObject(Expense::class.java)?.amount
            }.sum()
        } catch (e: Exception) {
            0.0
        }
    }
}