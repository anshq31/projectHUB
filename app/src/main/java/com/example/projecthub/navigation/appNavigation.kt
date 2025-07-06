package com.example.projecthub.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.projecthub.ui.presentation.settings.ChangePasswordScreen
import com.example.projecthub.ui.presentation.chat.ChatScreen
import com.example.projecthub.ui.presentation.profilesetupscreens.EditProfileScreen
import com.example.projecthub.ui.presentation.chat.MessageListScreen
import com.example.projecthub.screens.OnBoardingScreen
import com.example.projecthub.screens.ProfileSetupScreen
import com.example.projecthub.ui.presentation.showprofile.userProfileScreen
import com.example.projecthub.ui.presentation.assignments.assignmentsScreen
import com.example.projecthub.screens.createAssignmentScreen
import com.example.projecthub.ui.presentation.authentication.EmailVerificationScreen
import com.example.projecthub.ui.presentation.homescreen.homePage
import com.example.projecthub.ui.presentation.authentication.loginPage
import com.example.projecthub.ui.presentation.showprofile.profileScreen
import com.example.projecthub.ui.presentation.settings.settingsScreen
import com.example.projecthub.ui.presentation.authentication.signupPage
import com.example.projecthub.viewModel.AuthState
import com.example.projecthub.viewModel.ThemeViewModel
import com.example.projecthub.viewModel.authViewModel

@Composable
fun appNavigation(modifier: Modifier, authViewModel: authViewModel, themeViewModel: ThemeViewModel) {
    val navController = rememberNavController()
    val authState by authViewModel.authState.observeAsState()

    NavHost(navController = navController, startDestination = "splash_screen", builder = {

        composable("splash_screen") {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            LaunchedEffect(authState) {
                when (authState) {
                    is AuthState.Authenticated ->
                        navController.navigate(routes.homePage.route) {
                            popUpTo("splash_screen") { inclusive = true }
                        }
                    is AuthState.FirstTimeUser ->
                        navController.navigate(routes.onBoardingPage.route) {
                            popUpTo("splash_screen") { inclusive = true }
                        }
                    is AuthState.ProfileSetupRequired ->
                        navController.navigate(routes.profileSetupPage.route) {
                            popUpTo("splash_screen") { inclusive = true }
                        }
                    is AuthState.Unauthenticated ->
                        navController.navigate(routes.loginPage.route) {
                            popUpTo("splash_screen") { inclusive = true }
                        }
                    else -> {}
                }
            }
        }
        composable("login_page") {
            loginPage(Modifier, navController, authViewModel)
        }
        composable("signup_page") {
            signupPage(Modifier, navController, authViewModel)
        }
        composable(routes.homePage.route) {
            homePage(Modifier, navController, authViewModel)
        }
        composable(routes.profileSetupPage.route) {
            ProfileSetupScreen(navController, authViewModel)
        }
        composable(routes.onBoardingPage.route) {
            OnBoardingScreen(navController, authViewModel)
        }
        composable(routes.profilePage.route) {
            profileScreen(navController, authViewModel)
        }
        composable(routes.settingsScreen.route) {
            settingsScreen(navController, authViewModel, themeViewModel)
        }
        composable(routes.changePasswordScreen.route) {
            ChangePasswordScreen(navController, authViewModel)
        }
        composable(routes.editProfileScreen.route) {
            EditProfileScreen(navController, authViewModel)
        }
        composable(routes.createAssignmentScreen.route) {
            createAssignmentScreen(navController, authViewModel)
        }

        composable(routes.assignmentsScreen.route) {
            assignmentsScreen(navController, authViewModel)
        }

        composable(
            route = "${routes.assignmentDetailScreen.route}/{assignmentId}",
            arguments = listOf(navArgument("assignmentId") { type = NavType.StringType })
        ) { backStackEntry ->
            val assignmentId = backStackEntry.arguments?.getString("assignmentId") ?: ""
            assignmentsScreen(
                navController = navController,
                authViewModel = authViewModel,
                assignmentId = assignmentId
            )
        }

        composable(
            "edit_assignment/{assignmentId}",
            arguments = listOf(navArgument("assignmentId") { type = NavType.StringType })
        ) { backStackEntry ->
            val assignmentId = backStackEntry.arguments?.getString("assignmentId")
            createAssignmentScreen(
                navController = navController,
                authViewModel = authViewModel,
                assignmentId = assignmentId
            )
        }

        composable(
            route = routes.userProfileScreen.route,
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            userProfileScreen(navController = navController, userId = userId)
        }

        composable(routes.messagesListScreen.route) {
            MessageListScreen(navController, authViewModel)
        }

        composable(
            route = routes.chatScreen.route,
            arguments = listOf(navArgument("chatChannelId") { type = NavType.StringType })
        ) { backStackEntry ->
            val chatChannelId = backStackEntry.arguments?.getString("chatChannelId") ?: ""
            ChatScreen(navController, chatChannelId)
        }

        composable(routes.emailVerificationScreen.route) {
            EmailVerificationScreen(
                navController = navController,
                authViewModel = authViewModel,
                onContinueClick = { authViewModel.isEmailVerified() }
            )
        }

    })
}