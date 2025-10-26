package com.udb.controlgastos.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.Timestamp
import com.udb.controlgastos.data.Expense
import com.udb.controlgastos.viewmodel.ExpenseState
import com.udb.controlgastos.viewmodel.ExpenseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditExpenseScreen(
    expense: Expense,
    onNavigateBack: () -> Unit,
    viewModel: ExpenseViewModel = viewModel()
) {
    var name by remember { mutableStateOf(expense.name) }
    var amount by remember { mutableStateOf(expense.amount.toString()) }
    var category by remember { mutableStateOf(expense.category) }
    var description by remember { mutableStateOf(expense.description) }
    var expandedCategory by remember { mutableStateOf(false) }

    var nameError by remember { mutableStateOf<String?>(null) }
    var amountError by remember { mutableStateOf<String?>(null) }
    var categoryError by remember { mutableStateOf<String?>(null) }

    val expenseState by viewModel.expenseState.collectAsState()

    // Categorías
    val categories = listOf(
        "Alimentación",
        "Transporte",
        "Entretenimiento",
        "Salud",
        "Educación",
        "Vivienda",
        "Servicios",
        "Compras",
        "Otros"
    )

    LaunchedEffect(expenseState) {
        if (expenseState is ExpenseState.Success) {
            onNavigateBack()
            viewModel.resetExpenseState()
        }
    }

    // Validaciones
    fun validateFields(): Boolean {
        var isValid = true

        if (name.isBlank()) {
            nameError = "El nombre es obligatorio"
            isValid = false
        } else if (name.length < 3) {
            nameError = "El nombre debe tener al menos 3 caracteres"
            isValid = false
        } else {
            nameError = null
        }

        val amountValue = amount.toDoubleOrNull()
        if (amount.isBlank()) {
            amountError = "El monto es obligatorio"
            isValid = false
        } else if (amountValue == null) {
            amountError = "Ingresa un monto válido"
            isValid = false
        } else if (amountValue <= 0) {
            amountError = "El monto debe ser mayor a 0"
            isValid = false
        } else if (amountValue > 999999.99) {
            amountError = "El monto es demasiado grande"
            isValid = false
        } else {
            amountError = null
        }

        if (category.isBlank()) {
            categoryError = "Selecciona una categoría"
            isValid = false
        } else {
            categoryError = null
        }

        return isValid
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Gasto") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Nombre
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    if (nameError != null) nameError = null
                },
                label = { Text("Nombre del gasto *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = nameError != null,
                supportingText = {
                    if (nameError != null) {
                        Text(
                            text = nameError!!,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )

            // Monto
            OutlinedTextField(
                value = amount,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d{0,2}\$"))) {
                        amount = newValue
                        if (amountError != null) amountError = null
                    }
                },
                label = { Text("Monto *") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                prefix = { Text("$") },
                isError = amountError != null,
                supportingText = {
                    if (amountError != null) {
                        Text(
                            text = amountError!!,
                            color = MaterialTheme.colorScheme.error
                        )
                    } else {
                        Text("Ejemplo: 25.50")
                    }
                },
                placeholder = { Text("0.00") }
            )

            // Categoría
            ExposedDropdownMenuBox(
                expanded = expandedCategory,
                onExpandedChange = { expandedCategory = !expandedCategory }
            ) {
                OutlinedTextField(
                    value = category,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoría *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory) },
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                        .fillMaxWidth(),
                    isError = categoryError != null,
                    supportingText = {
                        if (categoryError != null) {
                            Text(
                                text = categoryError!!,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )

                ExposedDropdownMenu(
                    expanded = expandedCategory,
                    onDismissRequest = { expandedCategory = false }
                ) {
                    categories.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat) },
                            onClick = {
                                category = cat
                                expandedCategory = false
                                if (categoryError != null) categoryError = null
                            }
                        )
                    }
                }
            }

            // Descripción
            OutlinedTextField(
                value = description,
                onValueChange = {
                    if (it.length <= 200) description = it
                },
                label = { Text("Descripción (opcional)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 4,
                supportingText = {
                    Text("${description.length}/200 caracteres")
                }
            )

            Text(
                text = "* Campos obligatorios",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.weight(1f))

            // Actualizar
            Button(
                onClick = {
                    if (validateFields()) {
                        val updatedExpense = expense.copy(
                            name = name.trim(),
                            amount = amount.toDouble(),
                            category = category,
                            description = description.trim()
                        )
                        viewModel.updateExpense(updatedExpense)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = expenseState !is ExpenseState.Loading
            ) {
                if (expenseState is ExpenseState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Actualizar Gasto")
                }
            }

            // Errores
            if (expenseState is ExpenseState.Error) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = (expenseState as ExpenseState.Error).message,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}