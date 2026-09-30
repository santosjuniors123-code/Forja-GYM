package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.ExerciseEntity
import com.example.ui.components.EmptyState
import com.example.ui.components.ForjaCard
import com.example.ui.navigation.Screen
import com.example.ui.theme.ForgeBlack
import com.example.ui.theme.ForgeBorder
import com.example.ui.theme.ForgeCard
import com.example.ui.theme.ForgeOrange
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.viewmodel.ForjaViewModel

@Composable
fun ExerciseLibraryScreen(
    viewModel: ForjaViewModel,
    onNavigate: (Screen) -> Unit
) {
    val allExercises by viewModel.allExercises.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedMuscle by remember { mutableStateOf("Todos") }
    var selectedEquipment by remember { mutableStateOf("Todos") }
    var onlyFavorites by remember { mutableStateOf(false) }

    val muscleGroups = listOf(
        "Todos", "Peito", "Costas", "Ombros", "Bíceps", "Tríceps",
        "Quadríceps", "Posterior", "Glúteos", "Panturrilhas", "Abdômen", "Cardio"
    )

    val equipments = listOf("Todos", "Barra", "Halteres", "Máquina", "Cabo", "Peso Corporal")

    val filteredExercises = allExercises.filter { exercise ->
        val matchesSearch = exercise.name.contains(searchQuery, ignoreCase = true) ||
                exercise.muscleGroup.contains(searchQuery, ignoreCase = true)
        val matchesMuscle = selectedMuscle == "Todos" || exercise.muscleGroup.equals(selectedMuscle, ignoreCase = true)
        val matchesEquipment = selectedEquipment == "Todos" || exercise.equipment.contains(selectedEquipment, ignoreCase = true)
        val matchesFav = !onlyFavorites || exercise.isFavorite
        matchesSearch && matchesMuscle && matchesEquipment && matchesFav
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ForgeBlack)
            .padding(horizontal = 16.dp)
            .testTag("exercise_library_screen")
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "BIBLIOTECA DE EXERCÍCIOS",
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = TextPrimaryDark,
                letterSpacing = 0.5.sp
            )
            Text(
                text = "${filteredExercises.size} exercícios encontrados",
                fontSize = 12.sp,
                color = TextSecondaryDark
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Buscar exercício por nome ou grupo") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = ForgeOrange) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Limpar", tint = TextSecondaryDark)
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("exercise_search_bar"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ForgeOrange,
                    unfocusedBorderColor = ForgeBorder,
                    focusedTextColor = TextPrimaryDark,
                    unfocusedTextColor = TextPrimaryDark,
                    cursorColor = ForgeOrange
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Muscle Group Filter Chips Row
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(muscleGroups) { muscle ->
                    val isSelected = selectedMuscle == muscle
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedMuscle = muscle },
                        label = { Text(muscle, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ForgeOrange,
                            selectedLabelColor = Color.White,
                            containerColor = ForgeCard,
                            labelColor = TextSecondaryDark
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Equipment & Favorites Filter Row
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Favorite Toggle Chip
                item {
                    FilterChip(
                        selected = onlyFavorites,
                        onClick = { onlyFavorites = !onlyFavorites },
                        leadingIcon = {
                            Icon(
                                imageVector = if (onlyFavorites) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = if (onlyFavorites) Color.White else ForgeOrange
                            )
                        },
                        label = { Text("Favoritos", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFB91C1C),
                            selectedLabelColor = Color.White,
                            containerColor = ForgeCard,
                            labelColor = TextSecondaryDark
                        )
                    )
                }

                items(equipments) { eq ->
                    val isSelected = selectedEquipment == eq
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedEquipment = eq },
                        label = { Text(eq, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF26180E),
                            selectedLabelColor = ForgeOrange,
                            containerColor = ForgeCard,
                            labelColor = TextSecondaryDark
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

        if (filteredExercises.isEmpty()) {
            item {
                EmptyState(
                    title = "Nenhum exercício encontrado",
                    description = "Tente alterar os termos de busca ou filtros de grupos musculares."
                )
            }
        } else {
            items(filteredExercises) { exercise ->
                ExerciseListItem(
                    exercise = exercise,
                    onItemClick = { viewModel.selectExercise(exercise) },
                    onToggleFavorite = { viewModel.toggleFavorite(exercise.id, exercise.isFavorite) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ExerciseListItem(
    exercise: ExerciseEntity,
    onItemClick: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    ForjaCard(
        backgroundColor = ForgeCard,
        borderColor = ForgeBorder,
        onClick = onItemClick,
        testTag = "exercise_item_${exercise.id}"
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF26180E)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.FitnessCenter,
                        contentDescription = null,
                        tint = ForgeOrange,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = exercise.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark
                    )
                    Text(
                        text = "${exercise.muscleGroup} • ${exercise.equipment} • ${exercise.level}",
                        fontSize = 12.sp,
                        color = TextSecondaryDark
                    )
                }
            }

            IconButton(onClick = onToggleFavorite) {
                Icon(
                    imageVector = if (exercise.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorito",
                    tint = if (exercise.isFavorite) Color(0xFFEF4444) else TextSecondaryDark
                )
            }
        }
    }
}
