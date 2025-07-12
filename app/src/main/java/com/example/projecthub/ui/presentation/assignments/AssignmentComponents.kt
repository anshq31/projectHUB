package com.example.projecthub.ui.presentation.assignments

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.projecthub.data.Assignment
import com.example.projecthub.data.Bid
import com.example.projecthub.data.UserProfileCache
import com.example.projecthub.screens.RateUserDialog
import com.example.projecthub.utils.checkExistingBid
import com.example.projecthub.utils.formatTimestamp
import com.example.projecthub.utils.updateBidStatus
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object AssignmentComponents {

    @Composable
    fun LoadingIndicator() {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary
            )
        }
    }

    @Composable
    fun EmptyAssignmentsMessage() {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                "No assignments available",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    @Composable
    fun AvailableAssignmentsList(
        assignments: List<Assignment>,
        isLoading: Boolean = false,
        navController: NavHostController,
        onEditAssignment: (Assignment) -> Unit = {},
        onAssignmentClick: (Assignment) -> Unit = {},
        onDeleteAssignment: (Assignment) -> Unit = {}
    ) {
        if (isLoading) {
            LoadingIndicator()
        } else if (assignments.isEmpty()) {
            EmptyAssignmentsMessage()
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(assignments) { assignment ->
                    AssignmentCard(
                        assignment = assignment,
                        navController = navController,
                        onEditAssignment = onEditAssignment,
                        onAssignmentClick = onAssignmentClick,
                        onDeleteAssignment = onDeleteAssignment
                    )
                }
            }
        }
    }

    @Composable
    fun AssignmentCard(
        assignment: Assignment,
        navController: NavHostController,
        onEditAssignment: (Assignment) -> Unit = {},
        onAssignmentClick: (Assignment) -> Unit = {},
        onDeleteAssignment: (Assignment) -> Unit = {},
    ) {
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

        var showRateDialog by remember { mutableStateOf(false) }

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
                .clip(RoundedCornerShape(16.dp))
                .padding(1.dp)
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAssignmentClick(assignment) },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                ),
                shape = RoundedCornerShape(16.dp),
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
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.clickable {
                                navController.navigate("user_profile/${assignment.createdBy}")
                            }
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = selectedStatus,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                            if (assignment.createdBy == currentUserId) {
                                Icon(
                                    Icons.Default.ArrowDropDown,
                                    contentDescription = "Dropdown",
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
                                            FirebaseFirestore.getInstance()
                                                .collection("assignments")
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
                                    )
                                }
                            }
                        }
                    }

                    Divider(
                        modifier = Modifier.padding(vertical = 4.dp),
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
                            color = MaterialTheme.colorScheme.primary
                        )

                        Text(
                            text = "Posted: ${formatTimestamp(assignment.timestamp)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = assignment.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = assignment.description,
                        style = MaterialTheme.typography.bodyMedium,
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
                            Spacer(modifier = Modifier.width(36.dp))

                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .border(
                                        width = 2.dp,
                                        color = MaterialTheme.colorScheme.error,
                                        shape = CircleShape
                                    )
                                    .align(Alignment.CenterVertically)
                            ) {
                                IconButton(
                                    onClick = { onDeleteAssignment(assignment) },
                                    modifier = Modifier
                                        .fillMaxSize()
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete Assignment",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(24.dp)
                                    )
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
                            Toast.makeText(
                                context,
                                "Rating submitted successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            showRateDialog = false
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Failed to submit rating", Toast.LENGTH_SHORT)
                                .show()
                        }
                }
            )
        }
    }

    @Composable
    fun AssignmentDetailCard(assignment: Assignment) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = assignment.subject,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = "Active",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = assignment.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Posted: ${formatTimestamp(assignment.timestamp)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Description",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = assignment.description,
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    InfoChip(Icons.Default.Timer, "Deadline: ${assignment.deadline}")
                    InfoChip(Icons.Default.CurrencyRupee, "₹${assignment.budget}")
                }
            }
        }
    }

    @Composable
    fun TabRow(
        selectedTabIndex: Int,
        tabs: List<String>,
        onTabSelected: (Int) -> Unit
    ) {
        androidx.compose.material3.TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    text = {
                        Text(
                            title,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedTabIndex == index)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    selected = selectedTabIndex == index,
                    onClick = { onTabSelected(index) }
                )
            }
        }
    }

    @Composable
    fun BidsListDialog(
        assignmentId: String,
        navController: NavHostController,
        onDismiss: () -> Unit
    ) {
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
                    Toast.makeText(context, "Error loading bids: ${e.message}", Toast.LENGTH_SHORT)
                        .show()
                    isLoading = false
                }
        }

        LaunchedEffect(assignmentId, refreshTrigger) {
            loadBids()
        }

        AlertDialog(
            containerColor = MaterialTheme.colorScheme.surface.copy(0.90f),
            onDismissRequest = onDismiss,
            title = {
                Text(
                    "Bids for Assignment",
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    when {
                        isLoading -> {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }

                        bids.isEmpty() -> {
                            Text(
                                text = "No bids have been placed on this assignment yet.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }

                        else -> {
                            LazyColumn {
                                items(bids) { bid ->
                                    BidItem(
                                        bid = bid,
                                        navController = navController,
                                        onStatusChanged = { refreshTrigger++ }
                                    )
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
    fun BidItem(
        bid: Bid,
        navController: NavHostController,
        onStatusChanged: () -> Unit = {}
    ) {
        val context = LocalContext.current
        var profilePhotoResId by remember { mutableStateOf(com.example.projecthub.R.drawable.profilephoto1) }

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
                            modifier = Modifier.clickable { navController.navigate("user_profile/${bid.bidderId}") },
                            text = bid.bidderName,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    BidStatus(status = bid.status)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Bid Amount: ₹${bid.bidAmount}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Submitted: ${formatTimestamp(bid.timestamp)}",
                    style = MaterialTheme.typography.bodySmall,
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
                                updateBidStatus(bid.id, "rejected", context) {
                                    onStatusChanged()
                                }
                            },
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(
                                "Reject",
                                color = MaterialTheme.colorScheme.error
                            )
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
    fun BidStatus(status: String) {
        Surface(
            color = when (status) {
                "accepted" -> MaterialTheme.colorScheme.primaryContainer
                "rejected" -> MaterialTheme.colorScheme.errorContainer
                else -> MaterialTheme.colorScheme.tertiaryContainer
            },
            shape = RoundedCornerShape(4.dp)
        ) {
            Text(
                text = status.capitalize(),
                style = MaterialTheme.typography.bodySmall,
                color = when (status) {
                    "accepted" -> MaterialTheme.colorScheme.onPrimaryContainer
                    "rejected" -> MaterialTheme.colorScheme.onErrorContainer
                    else -> MaterialTheme.colorScheme.onTertiaryContainer
                },
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }

    @Composable
    fun InfoChip(icon: ImageVector, text: String) {
        Surface(
            modifier = Modifier.padding(end = 4.dp),
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
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun PlaceBidDialog(
        assignmentId: String,
        onDismiss: () -> Unit,
        onBidPlaced: () -> Unit,
        budget: Int,
        existingBid: Bid? = null
    ) {
        val context = LocalContext.current
        var bidAmount by remember { mutableStateOf(existingBid?.bidAmount?.toString() ?: "") }
        var isSubmitting by remember { mutableStateOf(false) }
        val currentUser = FirebaseAuth.getInstance().currentUser

        val showDatePicker = remember { mutableStateOf(false) }
        val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        var enterDate by remember { mutableStateOf("") }

        if (showDatePicker.value) {
            val currentMillis = System.currentTimeMillis()
            val datePickerState = rememberDatePickerState(initialSelectedDateMillis = currentMillis)

            DatePickerDialog(
                onDismissRequest = { showDatePicker.value = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let {
                            enterDate = dateFormatter.format(Date(it))
                        }
                        showDatePicker.value = false
                    }) {
                        Text("Confirm")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker.value = false }) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        val db = FirebaseFirestore.getInstance()
        var userName by remember { mutableStateOf("") }

        LaunchedEffect(currentUser?.uid) {
            currentUser?.uid?.let { userId ->
                db.collection("users").document(userId).get()
                    .addOnSuccessListener { document ->
                        userName = document.getString("name") ?: ""
                    }
            }
        }

        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(if (existingBid != null) "Edit Your Bid" else "Place a Bid") },
            text = {
                Column {
                    Text(
                        text = "Enter your bid amount:",
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = bidAmount,
                        onValueChange = { bidAmount = it },
                        label = { Text("Bid Amount (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = enterDate,
                        onValueChange = { },
                        label = { Text("Deadline*") },
                        leadingIcon = { Icon(Icons.Default.DateRange, "Deadline") },
                        trailingIcon = {
                            IconButton(onClick = { showDatePicker.value = true }) {
                                Icon(Icons.Default.CalendarToday, "Select date")
                            }
                        },
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (bidAmount.isNotBlank() && currentUser != null && enterDate.isNotBlank()) {
                            val amount = bidAmount.toIntOrNull()
                            if (amount != null && amount > 0 && amount <= budget) {
                                isSubmitting = true

                                if (existingBid != null) {
                                    db.collection("bids").document(existingBid.id)
                                        .update("bidAmount", amount)
                                        .addOnSuccessListener {
                                            isSubmitting = false
                                            onBidPlaced()
                                        }
                                        .addOnFailureListener { e ->
                                            isSubmitting = false
                                            Toast.makeText(
                                                context,
                                                "Error updating bid: ${e.message}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                } else {
                                    val newBid = Bid(
                                        id = db.collection("bids").document().id,
                                        assignmentId = assignmentId,
                                        bidderId = currentUser.uid,
                                        bidderName = userName,
                                        bidAmount = amount,
                                        enterCompletionDate = enterDate,
                                        timestamp = Timestamp.now()
                                    )

                                    db.collection("bids").document(newBid.id)
                                        .set(newBid)
                                        .addOnSuccessListener {
                                            isSubmitting = false
                                            onBidPlaced()
                                        }
                                        .addOnFailureListener { e ->
                                            isSubmitting = false
                                            Toast.makeText(
                                                context,
                                                "Error placing bid: ${e.message}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    "Please enter a valid bid amount (greater than ₹0 and not more than ₹$budget)",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    },
                    enabled = !isSubmitting && bidAmount.isNotBlank() && enterDate.isNotBlank()
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(if (existingBid != null) "Update Bid" else "Submit Bid")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        )
    }
}