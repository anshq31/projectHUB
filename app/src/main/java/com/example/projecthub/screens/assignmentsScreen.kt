
package com.example.projecthub.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ChangeCircle
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.currentCompositionErrors
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.projecthub.R
import com.example.projecthub.data.Assignment
import com.example.projecthub.data.Bid
import com.example.projecthub.data.UserProfileCache
import com.example.projecthub.data.chatChannel
import com.example.projecthub.usecases.CreateAssignmentFAB
import com.example.projecthub.usecases.MainAppBar
import com.example.projecthub.usecases.bottomNavigationBar
import com.example.projecthub.usecases.checkExistingBid
import com.example.projecthub.usecases.formatTimestamp
import com.example.projecthub.usecases.updateBidStatus
import com.example.projecthub.viewModel.authViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.TextButton
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.graphics.Brush
import com.example.projecthub.viewModel.ThemeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun assignmentsScreen(
    navController: NavHostController,
    authViewModel: authViewModel = viewModel(),
) {
    var isLoading by remember { mutableStateOf(true) }
    val themeViewModel: ThemeViewModel = viewModel()

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("My Assignments", "All Assignments")
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val assignmentsState = remember { mutableStateListOf<Assignment>() }
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    var assignmentToEdit by remember { mutableStateOf<Assignment?>(null) }

    LaunchedEffect(Unit) {
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

    Scaffold(
        topBar = {
            MainAppBar(title = "Assignments", navController = navController)
        },
        bottomBar = {
            bottomNavigationBar(navController = navController, currentRoute = "assignments_page")
        },
        floatingActionButton = {
            CreateAssignmentFAB(onClick = { showDialog = true })
        },
        floatingActionButtonPosition = FabPosition.Center,
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                // CHANGED: Using MaterialTheme colors for TabRow
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                tabs.forEachIndexed{ index, title ->
                    Tab(
                        text = {
                            Text(
                                title,
                                fontWeight = FontWeight.Bold,
                                // CHANGED: Using MaterialTheme colors for tab text
                                color = if (selectedTab == index)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        selected = selectedTab == index,
                        onClick = {
                            selectedTab = index
                        }
                    )
                }
            }

            when (selectedTab) {
                0 -> {
                    val myAssignments = if (currentUserId != null) {
                        assignmentsState.filter {
                            it.createdBy == currentUserId
                        }
                    }else emptyList()
                    AvailableAssignmentsList(myAssignments, isLoading, navController,
                        onEditAssignment = { assignment ->
                            assignmentToEdit = assignment
                            showDialog = true
                        }
                    )
                }
                1 -> {
                    val activeAssignments = assignmentsState.filter { it.status == "Active" }
                    AvailableAssignmentsList(
                        assignments = activeAssignments,
                        navController = navController,
                        onEditAssignment = { assignment ->
                            assignmentToEdit = assignment
                            showDialog = true
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
}

@Composable
fun AvailableAssignmentsList(assignments: List<Assignment>, isLoading: Boolean = false, navController: NavHostController, onEditAssignment: (Assignment) -> Unit = {}) {
    if(isLoading){
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            // CHANGED: Using theme primary color for loading indicator
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
    }else if (assignments.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            // CHANGED: Using theme onSurfaceVariant color for empty state text
            Text("No assignments available", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(assignments) { assignment ->
                AssignmentCard(
                    assignment = assignment,
                    navController = navController,
                    onEditAssignment = onEditAssignment
                )
            }
        }
    }
}

@Composable
fun AssignmentCard(assignment: Assignment, navController: NavHostController, onEditAssignment: (Assignment) -> Unit = {}) {
    val context = LocalContext.current
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    val isCreatedByCurrentUser = assignment.createdBy == currentUserId
    var showBidDialog by remember { mutableStateOf(false) }
    var showBidsListDialog by remember { mutableStateOf(false) }

    var posterName by remember { mutableStateOf(UserProfileCache.getUserName(assignment.createdBy)) }
    var posterPhotoId by remember { mutableStateOf(UserProfileCache.getProfilePhotoId(assignment.createdBy)) }

    var hasExistingBid by remember { mutableStateOf(false) }
    var existingBidData by remember { mutableStateOf<Bid?>(null) }

    var expanded by remember { mutableStateOf(false) }
    var selectedStatus by remember { mutableStateOf(assignment.status) }

    var showStatusDialog by remember { mutableStateOf(false) }
    var showRateDialog by remember { mutableStateOf(false) }

    if (showBidDialog) {
        PlaceBidDialog(
            assignmentId = assignment.id,
            budget = assignment.budget,
            existingBid = if (hasExistingBid) existingBidData else null,
            onDismiss = { showBidDialog = false },
            onBidPlaced = {
                showBidDialog = false
                Toast.makeText(
                    context,
                    if (hasExistingBid) "Bid updated successfully!" else "Bid placed successfully!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }

    if (showBidsListDialog) {
        BidsListDialog(
            assignmentId = assignment.id,
            navController = navController,
            onDismiss = { showBidsListDialog = false }
        )
    }

    if (showStatusDialog) {
        UpdateStatusDialog(
            currentStatus = assignment.status,
            onDismiss = { showStatusDialog = false },
            onStatusSelected = { newStatus ->
                Firebase.firestore.collection("assignments").document(assignment.id)
                    .update("status", newStatus)
                    .addOnSuccessListener {
                        Toast.makeText(
                            context,
                            "status updated to new Status ${newStatus}",
                            Toast.LENGTH_SHORT
                        ).show()
                        showStatusDialog = false
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Failed to update status", Toast.LENGTH_SHORT)
                            .show()
                        showStatusDialog = false
                    }
            }
        )
    }

    if (showRateDialog && assignment.acceptedBidderId != null) {
        RateUserDialog(
            userIdToRate = assignment.acceptedBidderId,
            onDismiss = { showRateDialog = false },
            onRatingSubmitted = { rating ->
                val db = FirebaseFirestore.getInstance()
                db.collection("assignments")
                    .document(assignment.id)
                    .update("bidderRating", rating)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Rating submitted successfully", Toast.LENGTH_SHORT).show()
                        showRateDialog = false
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Failed to submit rating", Toast.LENGTH_SHORT).show()
                    }
            }
        )
    }

    LaunchedEffect(assignment.createdBy) {
        if (posterName == "Unknown User") {
            FirebaseFirestore.getInstance().collection("users")
                .document(assignment.createdBy)
                .get()
                .addOnSuccessListener { document ->
                    posterName = document.getString("name") ?: "Unknown User"
                    document.getLong("profilePhotoId")?.toInt()?.let {
                        posterPhotoId = it
                    }
                }
        }
    }

    LaunchedEffect(currentUserId, assignment.id) {
        currentUserId?.let { userId ->
            checkExistingBid(assignment.id, userId) { hasBid, existingBid ->
                hasExistingBid = hasBid
                existingBidData = existingBid
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .padding(1.dp)
            // CHANGED: Using theme secondary color for border
            .background(MaterialTheme.colorScheme.secondary)
    ){
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                // CHANGED: Using theme surface color with alpha
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.98f)
            ),
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = posterPhotoId),
                        contentDescription = "Poster profile photo",
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .clickable {
                                navController.navigate("user_profile/${assignment.createdBy}")
                            }
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = posterName,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium,
                        // CHANGED: Using theme onSurface color for username
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.clickable {
                            navController.navigate("user_profile/${assignment.createdBy}")
                        }
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = selectedStatus,
                            style = MaterialTheme.typography.bodySmall,
                            // CHANGED: Using theme onSurfaceVariant color for status text
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                        if(assignment.createdBy == currentUserId) {
                            Icon(
                                Icons.Default.ArrowDropDown,
                                contentDescription = "Dropdown",
                                // CHANGED: Using theme secondary color for dropdown icon
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.clickable { expanded = true }
                            )
                        }
                    }

                    if (assignment.createdBy == currentUserId) {
                        DropdownMenu(
                            expanded = expanded,
                            offset = DpOffset(x = 210.dp, y = 0.dp),
                            onDismissRequest = { expanded = false }
                        ) {
                            val statuses = listOf("Active", "In Progress", "Completed")
                            statuses.forEach { status ->

                                DropdownMenuItem(
                                    text = { Text(status) },
                                    onClick = {
                                        expanded = false
                                        if (status == "Completed") {
                                            if (assignment.acceptedBidderId != null) {
                                                showRateDialog = true
                                                FirebaseFirestore.getInstance().collection("assignments")
                                                    .document(assignment.id)
                                                    .update(mapOf(
                                                        "status" to status,
                                                        "bidderRating" to null
                                                    ))
                                                    .addOnSuccessListener {
                                                        selectedStatus = status
                                                        Toast.makeText(
                                                            context,
                                                            "Status updated to $status",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                                    .addOnFailureListener {
                                                        Toast.makeText(
                                                            context,
                                                            "Failed to update status",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                            } else {
                                                FirebaseFirestore.getInstance().collection("assignments")
                                                    .document(assignment.id)
                                                    .update("status", status)
                                                    .addOnSuccessListener {
                                                        selectedStatus = status
                                                        Toast.makeText(
                                                            context,
                                                            "Status updated to $status",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                                    .addOnFailureListener {
                                                        Toast.makeText(
                                                            context,
                                                            "Failed to update status",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                            }
                                        } else {
                                            FirebaseFirestore.getInstance().collection("assignments")
                                                .document(assignment.id)
                                                .update("status", status)
                                                .addOnSuccessListener {
                                                    selectedStatus = status
                                                    Toast.makeText(
                                                        context,
                                                        "Status updated to $status",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                                .addOnFailureListener {
                                                    Toast.makeText(
                                                        context,
                                                        "Failed to update status",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

                Divider(
                    modifier = Modifier.padding(vertical = 4.dp),
                    // CHANGED: Using theme secondary color for divider
                    color = MaterialTheme.colorScheme.secondary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = assignment.subject,
                        style = MaterialTheme.typography.bodyMedium,
                        // CHANGED: Using theme primary color for subject
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = "Posted: ${formatTimestamp(assignment.timestamp)}",
                        style = MaterialTheme.typography.bodySmall,
                        // CHANGED: Using theme onSurfaceVariant color for timestamp
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = assignment.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    // CHANGED: Using theme onSurface color for title
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = assignment.description,
                    style = MaterialTheme.typography.bodyMedium,
                    // CHANGED: Using theme onSurfaceVariant color for description
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    InfoChip(Icons.Default.Timer, "Deadline: ${assignment.deadline}")
                    InfoChip(Icons.Default.CurrencyRupee, "₹${assignment.budget}")
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    if (isCreatedByCurrentUser) {
                        OutlinedButton(
                            onClick = { showBidsListDialog = true },
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.MonetizationOn,
                                    contentDescription = "View Bids",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("View Bids")
                            }
                        }

                        OutlinedButton(
                            onClick = { onEditAssignment(assignment) }
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = "Edit Assignment",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Manage")
                            }
                        }
                    } else {
                        Button(
                            onClick = { showBidDialog = true }
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    if (hasExistingBid) Icons.Default.Edit else Icons.Default.MonetizationOn,
                                    contentDescription = if (hasExistingBid) "Edit Bid" else "Place Bid",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (hasExistingBid) "Edit Bid" else "Place Bid")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BidsListDialog(assignmentId: String, navController: NavHostController, onDismiss: () -> Unit) {
    val context = LocalContext.current
    var bids by remember { mutableStateOf<List<Bid>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var refreshTrigger by remember { mutableStateOf(0) }

    fun loadBids() {
        isLoading = true
        FirebaseFirestore.getInstance().collection("bids")
            .whereEqualTo("assignmentId", assignmentId)
            .get()
            .addOnSuccessListener { documents ->
                bids = documents.toObjects(Bid::class.java)
                isLoading = false
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error loading bids: ${e.message}", Toast.LENGTH_SHORT).show()
                isLoading = false
            }
    }
    LaunchedEffect(assignmentId, refreshTrigger) {
        loadBids()
    }

    AlertDialog(
        // CHANGED: Using theme surface color for dialog background
        containerColor = MaterialTheme.colorScheme.surface.copy(0.90f),
        onDismissRequest = onDismiss,
        // CHANGED: Using theme onSurface color for dialog title
        title = { Text("Bids for Assignment", color = MaterialTheme.colorScheme.onSurface) },
        text = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                when {
                    isLoading -> {
                        // CHANGED: Using theme primary color for loading indicator
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    bids.isEmpty() -> {
                        // CHANGED: Using theme onSurfaceVariant color for empty state text
                        Text(
                            text = "No bids have been placed on this assignment yet.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    else -> {
                        LazyColumn {
                            items(bids) { bid ->
                                BidItem(bid = bid, navController = navController, onStatusChanged = {
                                    refreshTrigger++
                                })
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
fun BidItem(bid: Bid, navController: NavHostController, onStatusChanged: () -> Unit = {}) {
    val context = LocalContext.current
    var profilePhotoResId by remember { mutableStateOf(R.drawable.profilephoto1) }

    LaunchedEffect(bid.bidderId) {
        FirebaseFirestore.getInstance().collection("users")
            .document(bid.bidderId)
            .get()
            .addOnSuccessListener { document ->
                document.getLong("profilePhotoId")?.toInt()?.let {
                    profilePhotoResId = it
                }
            }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            // CHANGED: Using theme surface color with alpha for bid item
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.98f)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        painter = painterResource(id = profilePhotoResId),
                        contentDescription = "Bidder profile photo",
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .clickable {
                                navController.navigate("user_profile/${bid.bidderId}")
                            }
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        modifier = Modifier.clickable { navController.navigate("user_profile/${bid.bidderId}")},
                        text = bid.bidderName,
                        style = MaterialTheme.typography.titleMedium,
                        // CHANGED: Using theme onSurface color for bidder name
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    // CHANGED: Using theme-appropriate colors for bid status
                    color = when(bid.status) {
                        "accepted" -> MaterialTheme.colorScheme.primaryContainer
                        "rejected" -> MaterialTheme.colorScheme.errorContainer
                        else -> MaterialTheme.colorScheme.tertiaryContainer
                    },
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = bid.status.capitalize(),
                        style = MaterialTheme.typography.bodySmall,
                        // CHANGED: Using theme-appropriate colors for bid status text
                        color = when(bid.status) {
                            "accepted" -> MaterialTheme.colorScheme.onPrimaryContainer
                            "rejected" -> MaterialTheme.colorScheme.onErrorContainer
                            else -> MaterialTheme.colorScheme.onTertiaryContainer
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Bid Amount: ₹${bid.bidAmount}",
                style = MaterialTheme.typography.bodyLarge,
                // CHANGED: Using theme primary color for bid amount
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Submitted: ${formatTimestamp(bid.timestamp)}",
                style = MaterialTheme.typography.bodySmall,
                // CHANGED: Using theme onSurfaceVariant color for timestamp
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (bid.status == "pending") {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = {
                            updateBidStatus(bid.id, "rejected", context){
                                onStatusChanged()
                            }
                        },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        // CHANGED: Using theme error color for reject
                        Text("Reject", color = MaterialTheme.colorScheme.error)
                    }

                    Button(
                        onClick = {
                            updateBidStatus(bid.id, "accepted", context) {
                                onStatusChanged()
                            }
                        }
                    ) {
                        Text("Accept")
                    }
                }
            }
        }
    }
}

@Composable
fun InfoChip(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Surface(
        modifier = Modifier.padding(end = 4.dp),
        // CHANGED: Using theme surfaceVariant color for chip background
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.small
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                // CHANGED: Using theme onSurfaceVariant color for icon
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                // CHANGED: Using theme onSurfaceVariant color for text
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun UpdateStatusDialog(
    currentStatus: String,
    onDismiss: () -> Unit,
    onStatusSelected: (String) -> Unit
) {
    val statuses = listOf("Active", "In Progress", "Completed")
    var selectedStatus by remember { mutableStateOf(currentStatus) }

    AlertDialog(
        onDismissRequest = onDismiss,
        // CHANGED: Using theme surface color for dialog background
        containerColor = MaterialTheme.colorScheme.surface,
        confirmButton = {
            Button(onClick = { onStatusSelected(selectedStatus) }) {
                Text("Update")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                // CHANGED: Using theme onSurface color for cancel button
                Text("Cancel", color = MaterialTheme.colorScheme.onSurface)
            }
        },
        title = {
            // CHANGED: Using theme onSurface color for dialog title
            Text("Update Assignment Status", color = MaterialTheme.colorScheme.onSurface)
        },
        text = {
            Column {
                statuses.forEach { status ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedStatus = status }
                            .padding(8.dp)
                    ) {
                        RadioButton(
                            selected = selectedStatus == status,
                            onClick = { selectedStatus = status }
                        )
                        // CHANGED: Using theme onSurface color for status text
                        Text(text = status, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        }
    )
}