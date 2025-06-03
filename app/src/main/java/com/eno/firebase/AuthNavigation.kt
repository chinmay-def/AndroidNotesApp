package com.eno.firebase


import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.eno.firebase.screens.auth.AuthViewModel
import com.eno.firebase.screens.auth.LoginScreen
import com.eno.firebase.screens.auth.SignUpScreen
import com.eno.firebase.screens.notes.NoteEditorScreen
import com.eno.firebase.screens.notes.NotesListScreen
import com.eno.firebase.screens.notes.NotesViewModel


@Composable
fun AuthNavigation(
    authViewModel: AuthViewModel,
    notesViewModel: NotesViewModel
) {
    val navController = rememberNavController()
    val isAuthenticated = authViewModel.uiState.value.isAuthenticated

    LaunchedEffect(isAuthenticated) {
        if (!isAuthenticated) {
            navController.navigate("login") {
                popUpTo(0) { inclusive = true } // Clears entire back stack
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = "login"
            //if (authViewModel.uiState.value.isAuthenticated) "home" else "login"
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
                },
                notesViewModel = notesViewModel
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
                }, navController = navController
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