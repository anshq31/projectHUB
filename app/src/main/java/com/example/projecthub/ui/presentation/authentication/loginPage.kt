package com.example.projecthub.ui.presentation.authentication

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.projecthub.ui.presentation.authentication.AuthComponents.AuthFormContainer
import com.example.projecthub.viewModel.AuthState
import com.example.projecthub.viewModel.ThemeViewModel
import com.example.projecthub.viewModel.authViewModel


enum class AuthMode {
    LOGIN, SIGNUP
}
@Composable
fun loginPage(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    authViewModel: authViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(false) }
    val themeViewModel: ThemeViewModel = viewModel()
    var currentTab by remember { mutableStateOf(AuthMode.LOGIN) }
    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val savedEmail = authViewModel.getSavedEmail()
        if (savedEmail.isNotEmpty()) {
            email = savedEmail
            rememberMe = true
        }
    }

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Authenticated -> navController.navigate("home_page") {
                popUpTo("profile_setup_page") { inclusive = true }
            }
            is AuthState.FirstTimeUser -> navController.navigate("onBoarding_page") {
                popUpTo("login_page") { inclusive = true }
            }
            is AuthState.ProfileSetupRequired -> navController.navigate("profile_setup_page") {
                popUpTo("onBoarding_page") { inclusive = true }
            }
            is AuthState.Error -> Toast.makeText(
                context, (authState.value as AuthState.Error).message,
                Toast.LENGTH_SHORT
            ).show()
            else -> Unit
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AuthComponents.AuthScreenBackground(themeViewModel = themeViewModel)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Spacer(modifier = Modifier.height(24.dp))

                AuthComponents.HeaderText(
                    title = "Welcome Back",
                    subtitle = "Log in to continue using your account"
                )
                Spacer(modifier = Modifier.height(32.dp))
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                AuthFormContainer {
                    AuthComponents.AuthModeSwitcher(
                        currentTab = currentTab,
                        onLoginClick = { currentTab = AuthMode.LOGIN },
                        onRegisterClick = { navController.navigate("signup_page") }
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    AuthComponents.AuthFormContent(
                        mode = AuthMode.LOGIN,
                        visible = currentTab == AuthMode.LOGIN
                    ) {
                        AuthComponents.EmailField(
                            value = email,
                            onValueChange = { email = it }
                        )
                        AuthComponents.PasswordField(
                            value = password,
                            onValueChange = { password = it },
                            isVisible = passwordVisible,
                            onVisibilityToggle = { passwordVisible = !passwordVisible }
                        )
                        AuthComponents.RememberMeRow(
                            checked = rememberMe,
                            onCheckedChange = { rememberMe = it },
                            onForgotPasswordClick = { authViewModel.resetPassword(email) }
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        AuthComponents.GradientButton(
                            text = "Login",
                            onClick = { authViewModel.login(email, password, rememberMe) }
                        )
                        AuthComponents.AuthStateHandler(
                            authState = authState.value,
                            onResendVerificationClick = { authViewModel.resendVerificationEmail() }
                        )
                        AuthComponents.AuthBottomMessage(
                            message = "Don't have an account? ",
                            actionText = "Sign Up",
                            onActionClick = { navController.navigate("signup_page") }
                        )
                    }
                }
            }
        }
    }
}