package com.example.projecthub.ui.presentation.homescreen

import AppBackground7
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.projecthub.data.Assignment
import com.example.projecthub.data.UserProfileCache
import com.example.projecthub.navigation.routes
import com.example.projecthub.screens.CreateAssignmentDialog
import com.example.projecthub.utils.CreateAssignmentFAB
import com.example.projecthub.utils.bottomNavigationBar
import com.example.projecthub.utils.getCurrentDate
import com.example.projecthub.utils.getGreeting
import com.example.projecthub.viewModel.AuthState
import com.example.projecthub.viewModel.ThemeViewModel
import com.example.projecthub.viewModel.authViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun homePage(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    authViewModel: authViewModel = viewModel()
) {
    val authState = authViewModel.authState.observeAsState()
    var showAssignmentDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    var isLoading by remember { mutableStateOf(true) }
    val themeViewModel: ThemeViewModel = viewModel()
    var postedAssignments by remember { mutableStateOf(0) }
    var activeAssignments by remember { mutableStateOf(0) }
    var completedAssignments by remember { mutableStateOf(0) }
    var myAssignments by remember { mutableStateOf<List<Assignment>>(emptyList()) }
    var availableAssignments by remember { mutableStateOf<List<Assignment>>(emptyList()) }
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val db = FirebaseFirestore.getInstance()
    val currentDate = Calendar.getInstance().time

    LaunchedEffect(Unit) {
        UserProfileCache.preloadUserProfiles()
    }
    LaunchedEffect(userId) {
        isLoading = true
        if (userId != null) {
            var completedQueries = 0
            db.collection("assignments")
                .whereEqualTo("createdBy", userId)
                .get()
                .addOnSuccessListener { documents ->
                    val allUserAssignments = documents.toObjects(Assignment::class.java)
                    postedAssignments = allUserAssignments.size
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    myAssignments = allUserAssignments.filter { assignment ->
                        try {
                            val deadlineDate = dateFormat.parse(assignment.deadline)
                            deadlineDate != null && deadlineDate.after(currentDate)
                        } catch (e: Exception) {
                            true
                        }
                    }
                    activeAssignments = myAssignments.size
                    completedAssignments = allUserAssignments.size - activeAssignments
                    completedQueries++
                    if (completedQueries >= 2) isLoading = false
                }
                .addOnFailureListener {
                    completedQueries++
                    if (completedQueries >= 2) isLoading = false
                }
            db.collection("assignments")
                .whereNotEqualTo("createdBy", userId)
                .get()
                .addOnSuccessListener { documents ->
                    val allAvailableAssignments = documents.toObjects(Assignment::class.java)
                    val dateFormat = java.text.SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    availableAssignments = allAvailableAssignments.filter { assignment ->
                        try {
                            val deadlineDate = dateFormat.parse(assignment.deadline)
                            deadlineDate != null && deadlineDate.after(currentDate)
                        } catch (e: Exception) {
                            true
                        }
                    }
                    completedQueries++
                    if (completedQueries >= 2) isLoading = false
                }
                .addOnFailureListener {
                    completedQueries++
                    if (completedQueries >= 2) isLoading = false
                }
        } else {
            isLoading = false
        }
    }
    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Unauthenticated -> navController.navigate("login_page") {
                popUpTo("home_page") { inclusive = true }
            }
            else -> Unit
        }
    }
    Scaffold(
        topBar = {
            HomeScreenComponents.HomeTopAppBar(
                scrollBehavior = scrollBehavior,
                greeting = getGreeting(),
                currentDate = getCurrentDate(),
                onNotificationClick = { },
                onSettingsClick = { navController.navigate(routes.settingsScreen.route) }
            )
        },
        bottomBar = {
            bottomNavigationBar(navController = navController, currentRoute = "home_page")
        },
        floatingActionButton = {
            CreateAssignmentFAB(onClick = { showAssignmentDialog = true })
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { paddingValues ->
        if (showAssignmentDialog) {
            CreateAssignmentDialog(
                showDialog = showAssignmentDialog,
                onDismiss = { showAssignmentDialog = false },
                authViewModel = authViewModel,
                onAssignmentCreated = {
                    showAssignmentDialog = false
                }
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { Spacer(modifier = Modifier.height(8.dp)) }
                item {
                    HomeScreenComponents.StatsCard(
                        posted = postedAssignments,
                        active = activeAssignments,
                        completed = completedAssignments
                    )
                }
                item {
                    HomeScreenComponents.MyAssignmentsSection(
                        navController = navController,
                        assignments = myAssignments,
                        isLoading = isLoading
                    )
                }
                item {
                    HomeScreenComponents.AvailableAssignmentsSection(
                        navController = navController,
                        assignments = availableAssignments,
                        isLoading = isLoading
                    )
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}