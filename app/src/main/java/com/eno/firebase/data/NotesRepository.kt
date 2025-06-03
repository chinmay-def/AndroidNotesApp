package com.eno.firebase.data

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObjects


class NotesRepository {
    private val db = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()

    fun getCurrentUserId(): String? = auth.currentUser?.uid

    // Create a new note
    suspend fun addNote(title: String, content: String, color: String = "#FFFFFF"): Result<String> {
        val userId = getCurrentUserId() ?: return Result.failure(Exception("User not authenticated"))

        return try {
            val note = Note(
                title = title,
                content = content,
                userId = userId,
                color = color
            )

            val docRef = db.collection("notes").add(note).await()

            // Update the note with its generated ID
            db.collection("notes").document(docRef.id)
                .update("id", docRef.id)
                .await()

            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Get all notes for current user
    suspend fun getUserNotes(): Result<List<Note>> {
        val userId = getCurrentUserId() ?: return Result.failure(Exception("User not authenticated"))

        return try {
            val snapshot = db.collection("notes")
                .whereEqualTo("userId", userId)
                .orderBy("updatedAt", Query.Direction.DESCENDING)
                .get()
                .await()

            val notes = snapshot.toObjects<Note>()
            Result.success(notes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Get single note by ID
    suspend fun getNoteById(noteId: String): Result<Note?> {
        return try {
            val document = db.collection("notes").document(noteId).get().await()
            val note = document.toObject(Note::class.java)

            // Verify note belongs to current user
            if (note?.userId != getCurrentUserId()) {
                return Result.failure(Exception("Access denied"))
            }

            Result.success(note)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Update existing note
    suspend fun updateNote(noteId: String, title: String, content: String, color: String): Result<Unit> {
        val userId = getCurrentUserId() ?: return Result.failure(Exception("User not authenticated"))

        return try {
            val updates = mapOf(
                "title" to title,
                "content" to content,
                "color" to color,
                "updatedAt" to System.currentTimeMillis()
            )

            db.collection("notes")
                .document(noteId)
                .update(updates)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Delete note
    suspend fun deleteNote(noteId: String): Result<Unit> {
        return try {
            // First verify the note belongs to current user
            val noteResult = getNoteById(noteId)
            if (noteResult.isFailure) {
                return Result.failure(noteResult.exceptionOrNull() ?: Exception("Note not found"))
            }

            db.collection("notes").document(noteId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Real-time listener for user's notes
    fun listenToUserNotes(onNotesChange: (List<Note>) -> Unit): ListenerRegistration? {
        val userId = getCurrentUserId() ?: return null

        return db.collection("notes")
            .whereEqualTo("userId", userId)
            .orderBy("updatedAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Handle error - you might want to pass this to UI
                    return@addSnapshotListener
                }

                val notes = snapshot?.toObjects<Note>() ?: emptyList()
                onNotesChange(notes)
            }
    }

    // Search notes by title or content
    suspend fun searchNotes(query: String): Result<List<Note>> {
        val userId = getCurrentUserId() ?: return Result.failure(Exception("User not authenticated"))

        return try {
            // Note: Firestore doesn't support full-text search natively
            // This is a simple title search - for better search, consider using Algolia
            val snapshot = db.collection("notes")
                .whereEqualTo("userId", userId)
                .orderBy("title")
                .startAt(query)
                .endAt(query + "\uf8ff")
                .get()
                .await()

            val notes = snapshot.toObjects<Note>()
            Result.success(notes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}