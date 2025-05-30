package com.eno.firebase.screens.notes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditorScreen(
    noteId: String? = null, // null for new note
    viewModel: NotesViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf("#FFFFFF") }
    var showColorPicker by remember { mutableStateOf(false) }

    val isLoading by viewModel.isLoading
    val selectedNote by viewModel.selectedNote

    // Load existing note if editing
    LaunchedEffect(noteId) {
        if (noteId != null) {
            viewModel.loadNote(noteId)
        }
    }

    // Update UI when note is loaded
    LaunchedEffect(selectedNote) {
        selectedNote?.let { note ->
            title = note.title
            content = note.content
            selectedColor = note.color
        }
    }

    // Save note function
    fun saveNote() {
        if (noteId != null) {
            // Update existing note
            viewModel.updateNote(noteId, title, content, selectedColor)
        } else {
            // Create new note
            viewModel.createNote(title, content, selectedColor)
        }
        onNavigateBack()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(android.graphics.Color.parseColor(selectedColor)))
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(if (noteId != null) "Edit Note" else "New Note")
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(onClick = { showColorPicker = !showColorPicker }) {
                    Icon(Icons.Default.Palette, contentDescription = "Colors")
                }
                IconButton(
                    onClick = { saveNote() },
                    enabled = !isLoading && (title.isNotBlank() || content.isNotBlank())
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(Icons.Default.Check, contentDescription = "Save")
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(android.graphics.Color.parseColor(selectedColor))
            )
        )

        // Color Picker
        if (showColorPicker) {
            ColorPicker(
                selectedColor = selectedColor,
                onColorSelected = { color ->
                    selectedColor = color
                    showColorPicker = false
                }
            )
        }

        // Note Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Title Field
            BasicTextField(
                value = title,
                onValueChange = { title = it },
                textStyle = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                decorationBox = { innerTextField ->
                    if (title.isEmpty()) {
                        Text(
                            text = "Title",
                            style = TextStyle(
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        )
                    }
                    innerTextField()
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Content Field
            BasicTextField(
                value = content,
                onValueChange = { content = it },
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 24.sp
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                decorationBox = { innerTextField ->
                    if (content.isEmpty()) {
                        Text(
                            text = "Start writing...",
                            style = TextStyle(
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        )
                    }
                    innerTextField()
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun ColorPicker(
    selectedColor: String,
    onColorSelected: (String) -> Unit
) {
    val colors = listOf(
        "#FFFFFF", // White
        "#FFEB3B", // Yellow
        "#FFC107", // Amber
        "#FF9800", // Orange
        "#FF5722", // Deep Orange
        "#F44336", // Red
        "#E91E63", // Pink
        "#9C27B0", // Purple
        "#673AB7", // Deep Purple
        "#3F51B5", // Indigo
        "#2196F3", // Blue
        "#03A9F4", // Light Blue
        "#00BCD4", // Cyan
        "#009688", // Teal
        "#4CAF50", // Green
        "#8BC34A", // Light Green
        "#CDDC39", // Lime
        "#607D8B"  // Blue Grey
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Choose Color",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(colors) { color ->
                    ColorCircle(
                        color = color,
                        isSelected = color == selectedColor,
                        onClick = { onColorSelected(color) }
                    )
                }
            }
        }
    }
}

@Composable
fun ColorCircle(
    color: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(Color(android.graphics.Color.parseColor(color)))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                Icons.Default.Check,
                contentDescription = "Selected",
                tint = if (color == "#FFFFFF") Color.Black else Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }

    // Add border for white color to make it visible
    if (color == "#FFFFFF") {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.Transparent)
                .clickable { onClick() }
        ) {
            // Border effect can be added here if needed
        }
    }
}