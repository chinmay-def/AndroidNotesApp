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


@Composable
fun AuthNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = if (authViewModel.uiState.value.isAuthenticated) "notes_list" else "login"
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
                    navController.navigate("notes_list") {
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
                    navController.navigate("note_list") {
                        popUpTo("signup") { inclusive = true }
                    }
                }
            )
        }

        composable("notes_list") {
            NotesListScreen(
                onNavigateToEditor = { noteId ->
                    if (noteId != null) {
                        navController.navigate("note_editor/$noteId")
                    } else {
                        navController.navigate("note_editor")
                    }
                }
            )
        }
        composable(
            "note_editor/{noteId}",
            arguments = listOf(navArgument("noteId") { type = NavType.StringType })
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId")
            NoteEditorScreen(
                noteId = noteId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("note_editor") {
            NoteEditorScreen(
                noteId = null,
                onNavigateBack = { navController.popBackStack() }
            )
        }



    }
}

//        composable("home") {
//            HomeScreen(
//                viewModel = authViewModel,
//                onNavigateToLogin = {
//                    navController.navigate("login") {
//                        popUpTo("home") { inclusive = true }
//                    }
//                }
//            )
//        }