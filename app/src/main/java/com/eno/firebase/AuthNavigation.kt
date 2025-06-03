package com.eno.firebase


import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.eno.firebase.screens.auth.AuthViewModel
import com.eno.firebase.screens.auth.HomeScreen
import com.eno.firebase.screens.auth.LoginScreen
import com.eno.firebase.screens.auth.SignUpScreen
import com.eno.firebase.screens.notes.NoteEditorScreen
import com.eno.firebase.screens.notes.NotesListScreen
import com.eno.firebase.screens.notes.NotesViewModel


@Composable
fun AuthNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val notesViewModel: NotesViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = if (authViewModel.uiState.value.isAuthenticated) "home" else "login"
    ) {
        composable("login") {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToSignUp = {
                    navController.navigate("signup") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("signup") {
            SignUpScreen(
                viewModel = authViewModel,
                onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo("signup") { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate("home") {
                        popUpTo("signup") { inclusive = true }
                    }
                }
            )
        }
        composable("home") {
            NotesListScreen(
                viewModel = notesViewModel,
                onNavigateToEditor = { noteId ->
                    navController.navigate("editor/${noteId ?: "new"}")
                }
            )
        }
        composable(
            route = "editor/{noteId}",
            arguments = listOf(navArgument("noteId") { defaultValue = "new" })
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId")
            NoteEditorScreen(
                noteId = if (noteId == "new") null else noteId,
                viewModel = notesViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

    }
}



//composable("notes_list") {
//    NotesListScreen(
//        onNavigateToEditor = { noteId ->
//            if (noteId != null) {
//                navController.navigate("note_editor/$noteId")
//            } else {
//                navController.navigate("note_editor")
//            }
//        }
//    )
//}
//composable(
//"note_editor/{noteId}",
//arguments = listOf(navArgument("noteId") { type = NavType.StringType })
//) { backStackEntry ->
//    val noteId = backStackEntry.arguments?.getString("noteId")
//    NoteEditorScreen(
//        noteId = noteId,
//        onNavigateBack = { navController.popBackStack() }
//    )
//}
//
//composable("note_editor") {
//    NoteEditorScreen(
//        noteId = null,
//        onNavigateBack = { navController.popBackStack() }
//    )
//}