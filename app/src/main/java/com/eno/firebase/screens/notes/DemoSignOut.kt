package com.eno.firebase.screens.notes

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

//Button(
//onClick = { viewModel.signOut() },
//colors = ButtonDefaults.buttonColors(
//containerColor = MaterialTheme.colorScheme.error
//),
//modifier = Modifier.fillMaxWidth()
//) {
//    Icon(
//        Icons.Default.ExitToApp,
//        contentDescription = null,
//        modifier = Modifier.padding(end = 8.dp)
//    )
//    Text("Sign Out")
//}
// In your MainActivity or main composable
//@Composable
//fun NotesApp() {
//    val navController = rememberNavController()
//
//    NavHost(
//        navController = navController,
//        startDestination = "notes_list"
//    ) {
//        composable("notes_list") {
//            NotesListScreen(
//                onNavigateToEditor = { noteId ->
//                    if (noteId != null) {
//                        navController.navigate("note_editor/$noteId")
//                    } else {
//                        navController.navigate("note_editor")
//                    }
////                }
//            )
//        }
//        composable(
//            "note_editor/{noteId}",
//            arguments = listOf(navArgument("noteId") { type = NavType.StringType })
//        ) { backStackEntry ->
//            val noteId = backStackEntry.arguments?.getString("noteId")
//            NoteEditorScreen(
//                noteId = noteId,
//                onNavigateBack = { navController.popBackStack() }
//            )
//        }
//
//        composable("note_editor") {
//            NoteEditorScreen(
//                noteId = null,
//                onNavigateBack = { navController.popBackStack() }
//            )
//        }
//    }