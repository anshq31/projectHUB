package com.example.projecthub.ui.presentation.assignments

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.projecthub.data.Assignment
import com.example.projecthub.screens.CreateAssignmentDialog
import com.example.projecthub.screens.RateUserDialog
import com.example.projecthub.utils.CreateAssignmentFAB
import com.example.projecthub.utils.MainAppBar
import com.example.projecthub.utils.bottomNavigationBar
import com.example.projecthub.utils.markAssignmentCompleted
import com.example.projecthub.viewModel.ThemeViewModel
import com.example.projecthub.viewModel.authViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun assignmentsScreen(
    navController: NavHostController,
    authViewModel: authViewModel = viewModel(),
    assignmentId: String? = null
) {
    var isLoading by remember { mutableStateOf(true) }
    val themeViewModel: ThemeViewModel = viewModel()
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("My Assignments", "All Assignments")
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    var selectedAssignment by remember { mutableStateOf<Assignment?>(null) }
    var showBidDialog by remember { mutableStateOf(false) }
    var showRatingDialog by remember { mutableStateOf(false) }
    var bidderToRate by remember { mutableStateOf("") }
    var bidderName by remember { mutableStateOf("") }

    val assignmentsState = remember { mutableStateListOf<Assignment>() }
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    var assignmentToEdit by remember { mutableStateOf<Assignment?>(null) }

    LaunchedEffect(assignmentId) {
        if (assignmentId != null && assignmentId.isNotBlank()) {
            isLoading = true
            FirebaseFirestore.getInstance().collection("assignments")
                .document(assignmentId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val loadedAssignment = document.toObject(Assignment::class.java)
                        selectedAssignment = loadedAssignment?.copy(id = document.id)
                    } else {
                        Toast.makeText(context, "Assignment not found", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    }
                    isLoading = false
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Error loading assignment: ${e.message}", Toast.LENGTH_SHORT).show()
                    isLoading = false
                    navController.popBackStack()
                }
        }
    }

    LaunchedEffect(Unit) {
        if (assignmentId == null) {
            isLoading = true
            Firebase.firestore.collection("assignments")
                .get()
                .addOnSuccessListener { result ->
                    assignmentsState.clear()
                    val assignments = result.documents.mapNotNull { doc ->
                        val assignment = doc.toObject(Assignment::class.java)
                        assignment?.copy(id = doc.id)
                    }
                    assignmentsState.addAll(assignments)
                    isLoading = false
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Failed to fetch assignments", Toast.LENGTH_SHORT).show()
                    isLoading = false
                }
        }
    }

    fun getBidderName(bidderId: String, callback: (String) -> Unit) {
        FirebaseFirestore.getInstance().collection("users").document(bidderId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val name = document.getString("name") ?: "Unknown User"
                    callback(name)
                } else {
                    callback("Unknown User")
                }
            }
            .addOnFailureListener {
                callback("Unknown User")
            }
    }

    Scaffold(
        topBar = {
            MainAppBar(
                title = if (selectedAssignment != null) "Assignment Details" else "Assignments",
                navController = navController
            )
        },
        bottomBar = {
            bottomNavigationBar(
                navController = navController,
                currentRoute = if (selectedAssignment != null) "assignment_details" else "assignments_page"
            )
        },
        floatingActionButton = {
            if (selectedAssignment == null) {
                CreateAssignmentFAB(onClick = { showDialog = true })
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
    ) { innerPadding ->

        if (isLoading) {
            AssignmentComponents.LoadingIndicator()
        } else if (selectedAssignment != null) {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                AssignmentComponents.AssignmentDetailCard(selectedAssignment!!)

                Spacer(modifier = Modifier.height(16.dp))

                if (currentUserId != selectedAssignment!!.createdBy) {
                    Button(
                        onClick = { showBidDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Place Bid")
                    }
                }

                if (currentUserId == selectedAssignment!!.createdBy && selectedAssignment!!.status != "completed") {
                    Button(
                        onClick = {
                            val acceptedBidderId = selectedAssignment!!.acceptedBidderId
                            if (acceptedBidderId != null && acceptedBidderId.isNotEmpty()) {
                                getBidderName(acceptedBidderId) { name ->
                                    bidderToRate = acceptedBidderId
                                    bidderName = name
                                    showRatingDialog = true
                                }
                            } else {
                                markAssignmentCompleted(selectedAssignment!!.id ?: "") { assignmentId, acceptedBidderId ->
                                    getBidderName(acceptedBidderId) { name ->
                                        bidderToRate = acceptedBidderId
                                        bidderName = name
                                        showRatingDialog = true
                                    }
                                }
                                Toast.makeText(context, "Assignment marked as completed", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        Text("Mark as Completed")
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier.padding(innerPadding)
            ) {
                AssignmentComponents.TabRow(
                    selectedTabIndex = selectedTab,
                    tabs = tabs,
                    onTabSelected = { selectedTab = it }
                )

                when (selectedTab) {
                    0 -> {
                        val myAssignments = if (currentUserId != null) {
                            assignmentsState.filter { it.createdBy == currentUserId }
                        } else emptyList()
                        AssignmentComponents.AvailableAssignmentsList(
                            assignments = myAssignments,
                            isLoading = false,
                            navController = navController,
                            onEditAssignment = { assignment ->
                                assignmentToEdit = assignment
                                showDialog = true
                            },
                            onAssignmentClick = { assignment ->
                                selectedAssignment = assignment
                            }
                        )
                    }
                    1 -> {
                        val activeAssignments = assignmentsState.filter { it.status == "Active" }
                        AssignmentComponents.AvailableAssignmentsList(
                            assignments = activeAssignments,
                            navController = navController,
                            onEditAssignment = { assignment ->
                                assignmentToEdit = assignment
                                showDialog = true
                            },
                            onAssignmentClick = { assignment ->
                                selectedAssignment = assignment
                            }
                        )
                    }
                }
            }
        }

        if (showDialog) {
            CreateAssignmentDialog(
                showDialog = showDialog,
                onDismiss = {
                    showDialog = false
                    assignmentToEdit = null
                },
                authViewModel = authViewModel,
                existingAssignment = assignmentToEdit,
                onAssignmentCreated = {
                    showDialog = false
                    assignmentToEdit = null
                    Toast.makeText(
                        context,
                        if (assignmentToEdit != null) "Assignment updated successfully!" else "Assignment created successfully!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
        }

        if (showBidDialog && selectedAssignment != null) {
            AssignmentComponents.PlaceBidDialog(
                assignmentId = selectedAssignment!!.id,
                onDismiss = { showBidDialog = false },
                onBidPlaced = {
                    showBidDialog = false
                    Toast.makeText(context, "Bid placed successfully!", Toast.LENGTH_SHORT).show()
                },
                budget = selectedAssignment!!.budget
            )
        }

        if (showRatingDialog && bidderToRate.isNotEmpty()) {
            RateUserDialog(
                userIdToRate = bidderToRate,
                onDismiss = { showRatingDialog = false },
                onRatingSubmitted = { rating ->
                    showRatingDialog = false
                    Toast.makeText(context, "Rating submitted successfully", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}