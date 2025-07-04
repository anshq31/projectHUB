package com.example.projecthub.ui.presentation.authentication

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.projecthub.viewModel.AuthState
import com.example.projecthub.viewModel.ThemeViewModel
import com.example.projecthub.viewModel.authViewModel

@Composable
fun signupPage(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    authViewModel: authViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    val themeViewModel: ThemeViewModel = viewModel()
    var currentTab by remember { mutableStateOf(AuthMode.SIGNUP) }
    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current

    LaunchedEffect(authState.value) {
        when(authState.value){
            is AuthState.Authenticated -> navController.navigate("home_page")
            is AuthState.Error -> Toast.makeText(
                context,
                (authState.value as AuthState.Error).message,
                Toast.LENGTH_SHORT
            ).show()
            is AuthState.EmailVerificationSent -> {
                Toast.makeText(
                    context,
                    "Verification email has been sent",
                    Toast.LENGTH_SHORT
                ).show()
                navController.popBackStack()
                navController.navigate("login_page")
            }
            else -> Unit
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

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
                    title = "Set up your account",
                    subtitle = "Sign up to get started"
                )

                Spacer(modifier = Modifier.height(32.dp))
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                AuthComponents.AuthFormContainer {
                    AuthComponents.AuthModeSwitcher(
                        currentTab = currentTab,
                        onLoginClick = { navController.navigate("login_page") },
                        onRegisterClick = { currentTab = AuthMode.SIGNUP }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    AuthComponents.AuthFormContent(
                        mode = AuthMode.SIGNUP,
                        visible = currentTab == AuthMode.SIGNUP
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
                        Spacer(modifier = Modifier.height(24.dp))

                        AuthComponents.PasswordField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            placeholder = "Confirm Password",
                            isVisible = confirmPasswordVisible,
                            onVisibilityToggle = { confirmPasswordVisible = !confirmPasswordVisible }
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        AuthComponents.GradientButton(
                            text = "Create Account",
                            onClick = {
                                if (password == confirmPassword) {
                                    authViewModel.signup(email, password)
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Passwords do not match",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        )

                        AuthComponents.AuthStateHandler(
                            authState = authState.value,
                            onResendVerificationClick = { authViewModel.resendVerificationEmail() }
                        )

                        AuthComponents.AuthBottomMessage(
                            message = "Already have an account? ",
                            actionText = "Login",
                            onActionClick = { navController.navigate("login_page") }
                        )
                    }
                }
            }
        }
    }
}