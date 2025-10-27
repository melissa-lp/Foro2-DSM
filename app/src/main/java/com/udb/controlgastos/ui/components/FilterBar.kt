package com.udb.controlgastos.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBar(
    selectedCategory: String,
    selectedMonth: Int,
    selectedYear: Int,
    onCategoryChange: (String) -> Unit,
    onMonthYearChange: (Int, Int) -> Unit,
    onClearFilters: () -> Unit
) {
    var expandedCategory by remember { mutableStateOf(false) }
    var expandedMonthYear by remember { mutableStateOf(false) }

    val categories = listOf(
        "Todas",
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

    // Generar lista de meses y años
    val months = listOf(
        "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
        "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
    )

    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val years = (currentYear - 2..currentYear).toList()

    Card(
        colors = CardDefaults.cardColors(
            containerColor = com.udb.controlgastos.ui.theme.Card
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    Icons.Default.FilterList,
                    contentDescription = "Filtros",
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Filtros",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.weight(1f))
                if (selectedCategory != "Todas" || selectedMonth != Calendar.getInstance().get(Calendar.MONTH) + 1) {
                    TextButton(onClick = onClearFilters) {
                        Text("Limpiar")
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Filtro de Categoría
                ExposedDropdownMenuBox(
                    expanded = expandedCategory,
                    onExpandedChange = { expandedCategory = !expandedCategory },
                    modifier = Modifier.weight(1f),

                ) {
                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Categoría") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory) },
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = expandedCategory,
                        onDismissRequest = { expandedCategory = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category) },
                                onClick = {
                                    onCategoryChange(category)
                                    expandedCategory = false
                                }
                            )
                        }
                    }
                }

                // Filtro de Mes/Año
                ExposedDropdownMenuBox(
                    expanded = expandedMonthYear,
                    onExpandedChange = { expandedMonthYear = !expandedMonthYear },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = "${months[selectedMonth - 1]} $selectedYear",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Mes/Año") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedMonthYear) },
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = expandedMonthYear,
                        modifier = Modifier.background(Color.White),
                        onDismissRequest = { expandedMonthYear = false }
                    ) {
                        years.reversed().forEach { year ->
                            Text(
                                text = year.toString(),
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                            months.forEachIndexed { index, month ->
                                DropdownMenuItem(
                                    text = { Text(month) },
                                    onClick = {
                                        onMonthYearChange(index + 1, year)
                                        expandedMonthYear = false
                                    }
                                )
                            }
                            if (year != years.first()) {
                                HorizontalDivider()
                            }
                        }
                    }
                }
            }
        }
    }
}