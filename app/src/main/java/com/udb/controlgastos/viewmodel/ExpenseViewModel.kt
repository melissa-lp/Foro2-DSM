package com.udb.controlgastos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udb.controlgastos.data.Expense
import com.udb.controlgastos.data.ExpenseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.util.Calendar

class ExpenseViewModel : ViewModel() {
    private val repository = ExpenseRepository()

    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
    val expenses: StateFlow<List<Expense>> = _expenses

    private val _expenseState = MutableStateFlow<ExpenseState>(ExpenseState.Idle)
    val expenseState: StateFlow<ExpenseState> = _expenseState

    private val _monthlyTotal = MutableStateFlow(0.0)
    val monthlyTotal: StateFlow<Double> = _monthlyTotal

    init {
        loadExpenses()
        loadMonthlyTotal()
    }

    private fun loadExpenses() {
        viewModelScope.launch {
            repository.getUserExpenses()
                .catch { e ->
                    _expenseState.value = ExpenseState.Error(e.message ?: "Error al cargar gastos")
                }
                .collect { expensesList ->
                    _expenses.value = expensesList
                }
        }
    }

    fun addExpense(expense: Expense) {
        viewModelScope.launch {
            _expenseState.value = ExpenseState.Loading
            val result = repository.addExpense(expense)
            _expenseState.value = if (result.isSuccess) {
                loadMonthlyTotal()
                ExpenseState.Success("Gasto agregado exitosamente")
            } else {
                ExpenseState.Error(result.exceptionOrNull()?.message ?: "Error al agregar gasto")
            }
        }
    }

    fun updateExpense(expense: Expense) {
        viewModelScope.launch {
            _expenseState.value = ExpenseState.Loading
            val result = repository.updateExpense(expense)
            _expenseState.value = if (result.isSuccess) {
                loadMonthlyTotal()
                ExpenseState.Success("Gasto actualizado exitosamente")
            } else {
                ExpenseState.Error(result.exceptionOrNull()?.message ?: "Error al actualizar gasto")
            }
        }
    }

    fun deleteExpense(expenseId: String) {
        viewModelScope.launch {
            _expenseState.value = ExpenseState.Loading
            val result = repository.deleteExpense(expenseId)
            _expenseState.value = if (result.isSuccess) {
                loadMonthlyTotal()
                ExpenseState.Success("Gasto eliminado exitosamente")
            } else {
                ExpenseState.Error(result.exceptionOrNull()?.message ?: "Error al eliminar gasto")
            }
        }
    }

    private fun loadMonthlyTotal() {
        viewModelScope.launch {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH) + 1
            _monthlyTotal.value = repository.getMonthlyTotal(year, month)
        }
    }

    fun resetExpenseState() {
        _expenseState.value = ExpenseState.Idle
    }
}

sealed class ExpenseState {
    object Idle : ExpenseState()
    object Loading : ExpenseState()
    data class Success(val message: String) : ExpenseState()
    data class Error(val message: String) : ExpenseState()
}