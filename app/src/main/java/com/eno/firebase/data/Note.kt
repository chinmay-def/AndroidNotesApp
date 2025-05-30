package com.eno.firebase.data

data class Note(
    val id: String = "",
    val title: String = "",
    val content: String = "",
    val userId: String = "", // Links to authenticated user
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val color: String = "#FFFFFF" // For note colors
)
