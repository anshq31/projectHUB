package com.example.projecthub.ui.presentation.authentication


import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.projecthub.viewModel.AuthState
import com.example.projecthub.viewModel.authViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun EmailVerificationScreen(
    navController: NavHostController,
    authViewModel: authViewModel,
    onContinueClick: () -> Unit
) {

    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current
    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.EmailVerified -> {
                Toast.makeText(context, "Email verified!", Toast.LENGTH_SHORT).show()

                val user = FirebaseAuth.getInstance().currentUser
                if (user != null) {
                    authViewModel.saveFcmTokenToFirestore(user.uid)
                }

                navController.navigate("onBoarding_page")
            }
            is AuthState.Error -> {
                Toast.makeText(context, (authState.value as AuthState.Error).message, Toast.LENGTH_SHORT).show()
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
                .align(Alignment.Center)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondary)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Verified",
                    tint = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier
                        .size(56.dp)
                        .align(Alignment.Center)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Click on the link sent on email to verify",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                ),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(vertical = 8.dp),
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = onContinueClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = "Continue",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}