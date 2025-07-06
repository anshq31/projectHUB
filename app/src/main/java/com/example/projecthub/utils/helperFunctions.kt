package com.example.projecthub.utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.projecthub.navigation.routes
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.style.TextAlign
import com.example.projecthub.data.Assignment
import com.example.projecthub.data.Bid
import com.example.projecthub.data.chatChannel
import com.example.projecthub.data.message
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun bottomNavigationBar(navController: NavHostController, currentRoute: String) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home",tint = MaterialTheme.colorScheme.secondary) },
            label = { Text("Home") },
            selected = currentRoute == "home_page",
            onClick = {
                if (currentRoute != "home_page") {
                    navController.navigate("home_page") {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }
            }
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.Assignment, contentDescription = "Assignments",tint = MaterialTheme.colorScheme.secondary) },
            label = { Text("Assignments") },
            selected = currentRoute == "assignments_page",
            onClick = {
                if (currentRoute != "assignments") {
                    navController.navigate(routes.assignmentsScreen.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }

            }
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.Email, contentDescription = "Messages",tint = MaterialTheme.colorScheme.secondary) },
            label = { Text("Messages") },
            selected = currentRoute == "messages_list",
            onClick = {
                if (currentRoute != "messages_list") {
                    navController.navigate(routes.messagesListScreen.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }
            }
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile",tint = MaterialTheme.colorScheme.secondary) },
            label = { Text("Profile") },
            selected = currentRoute == "profile",
            onClick = {
                if (currentRoute != "profile") {
                    navController.navigate(routes.profilePage.route) {

                    }
                }
            }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppBar(title: String, navController: NavHostController) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
            titleContentColor = MaterialTheme.colorScheme.primary
        ),
        actions = {
            IconButton(onClick = { /* Notifications */ }) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
            IconButton(onClick = { navController.navigate(routes.settingsScreen.route) }) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
        }
    )
}

@Composable
fun bubbleBackground(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.TopEnd)
                .offset(x = 50.dp, y = (-30).dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.35f))
        )

        Box(
            modifier = Modifier
                .size(100.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-30).dp, y = 30.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.39f))
        )

        Box(
            modifier = Modifier
                .size(150.dp)
                .align(Alignment.CenterStart)
                .offset(x = (-70).dp, y = (-100).dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.40f))
        )

        Box(
            modifier = Modifier
                .size(130.dp)
                .align(Alignment.CenterEnd)
                .offset(x = (70).dp, y = (40).dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.38f))
        )
    }
}
@Composable
fun CreateAssignmentFAB(onClick: () -> Unit) {
    Box(
        modifier = Modifier.offset(y = 35.dp)
    ) {
        FloatingActionButton(
            onClick = onClick,
            containerColor = MaterialTheme.colorScheme.primary,
            shape = CircleShape
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "Create Assignment",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

fun formatTimestamp(timestamp: Timestamp): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return sdf.format(timestamp.toDate())
}
fun formatTimeStamp(date: Date): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return sdf.format(date)
}

fun getCurrentDate(): String {
    val dateFormat = SimpleDateFormat("EEEE, dd MMM yyyy", Locale.getDefault())
    return dateFormat.format(Date())
}

fun getGreeting(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when {
        hour < 12 -> "Good Morning"
        hour < 17 -> "Good Afternoon"
        else -> "Good Evening"
    }
}
@Composable
fun NoChannelsMessage(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "No conversations yet",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "When you accept a bid, a chat will be created here",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
fun updateBidStatus(
    bidId: String,
    status: String,
    context: Context,
    onSuccess: () -> Unit = {}
) {
    val db = FirebaseFirestore.getInstance()
    val bidRef = db.collection("bids").document(bidId)

    bidRef.update("status", status)
        .addOnSuccessListener {
            Toast.makeText(context, "Bid $status", Toast.LENGTH_SHORT).show()

            if (status == "accepted") {
                bidRef.get()
                    .addOnSuccessListener { document ->
                        val bid = document.toObject(Bid::class.java)

                        bid?.let {
                            val assignmentRef = db.collection("assignments").document(it.assignmentId)

                            assignmentRef.update(
                                mapOf(
                                    "acceptedBidderId" to it.bidderId,
                                    "acceptedBidAmount" to it.bidAmount,
                                    "acceptedBidderName" to it.bidderName,
                                    "status" to "in_progress",
                                    "expectedCompletionDate" to it.enterCompletionDate
                                )
                            ).addOnSuccessListener { _ ->
                                db.collection("bids")
                                    .whereEqualTo("assignmentId", it.assignmentId)
                                    .whereNotEqualTo("id", bidId)
                                    .get()
                                    .addOnSuccessListener { otherBids ->
                                        val batch = db.batch()
                                        for (bidDoc in otherBids.documents) {
                                            batch.update(bidDoc.reference, "status", "rejected")
                                        }
                                        batch.commit()
                                    }

                                assignmentRef.get()
                                    .addOnSuccessListener { assignmentDoc ->
                                        val assignment = assignmentDoc.toObject(Assignment::class.java)

                                        assignment?.let { a ->
                                            val posterId = a.createdBy

                                            createOrGetChannel(
                                                otherUserId = it.bidderId
                                            ) { chatChannelId ->
                                                Toast.makeText(context, "Chat created: $chatChannelId", Toast.LENGTH_SHORT).show()
                                                onSuccess()
                                            }
                                        }
                                    }
                            }
                        }
                    }
            } else {
                onSuccess()
            }
        }
        .addOnFailureListener { e ->
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
}
fun createOrGetChannel(
    otherUserId: String,
    onChannelCreated: (String) -> Unit,
) {
    val db = FirebaseFirestore.getInstance()
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val chatChannelsRef = db.collection("chatChannels")

    val query1 = chatChannelsRef
        .whereEqualTo("user1Id", currentUserId)
        .whereEqualTo("user2Id", otherUserId)

    val query2 = chatChannelsRef
        .whereEqualTo("user1Id", otherUserId)
        .whereEqualTo("user2Id", currentUserId)

    query1.get().addOnSuccessListener { querySnapshot ->
        if (!querySnapshot.isEmpty) {
            onChannelCreated(querySnapshot.documents[0].id)
        } else {
            query2.get().addOnSuccessListener { querySnapshot2 ->
                if (!querySnapshot2.isEmpty) {
                    onChannelCreated(querySnapshot2.documents[0].id)
                } else {
                    val newChannel = chatChannel(
                        user1Id = currentUserId,
                        user2Id = otherUserId
                    )

                    chatChannelsRef.add(newChannel)
                        .addOnSuccessListener { documentRef ->
                            onChannelCreated(documentRef.id)
                        }
                }
            }
        }
    }
}
fun sendMessage(
    chatChannelId: String,
    messageText: String,
    senderId: String,
    onComplete: () -> Unit = {}
) {
    val db = FirebaseFirestore.getInstance()
    val messageRef = db.collection("chatChannels").document(chatChannelId).collection("messages").document()
    val message = message(
        messageId = messageRef.id,
        senderId = senderId,
        text = messageText
    )

    messageRef.set(message)
        .addOnSuccessListener {
            db.collection("chatChannels").document(chatChannelId)
                .update(
                    mapOf(
                        "lastMessageText" to messageText,
                        "lastMessageTimestamp" to Timestamp.now()
                    )
                )
                .addOnSuccessListener {
                    onComplete()
                }
        }
        .addOnFailureListener { e ->
            Log.e("SendMessage", "Error sending message: ${e.message}")
        }
}



fun listenForMessages(
    chatChannelId: String,
    onMessageReceived: (List<message>) -> Unit
): ListenerRegistration {
    val db = FirebaseFirestore.getInstance()

    return db.collection("chatChannels")
        .document(chatChannelId)
        .collection("messages")
        .orderBy("timestamp", Query.Direction.ASCENDING) // Fix casing here
        .addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("Firestore", "Listen failed: ${error.message}")
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val messages = snapshot.documents.mapNotNull { it.toObject(message::class.java) }
                onMessageReceived(messages)
            }
        }
}


fun checkExistingBid(
    assignmentId: String,
    userId: String,
    onResult: (Boolean, Bid?) -> Unit
) {
    FirebaseFirestore.getInstance()
        .collection("bids")
        .whereEqualTo("assignmentId", assignmentId)
        .whereEqualTo("bidderId", userId)
        .get()
        .addOnSuccessListener { documents ->
            if (documents.isEmpty) {
                onResult(false, null)
            } else {
                val existingBid = documents.documents[0].toObject(Bid::class.java)
                onResult(true, existingBid)
            }
        }
        .addOnFailureListener {
            onResult(false, null)
        }
}



fun markAssignmentCompleted(assignmentId: String, onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit = {}) {
    val db = FirebaseFirestore.getInstance()
    db.collection("assignments")
        .document(assignmentId)
        .update("status", "completed")
        .addOnSuccessListener {
            onSuccess()
        }
        .addOnFailureListener {
            onFailure(it)
        }
}



fun deleteAssignment(
    context: Context,
    assignment: Assignment,
    onComplete: (Boolean) -> Unit = {}
) {
    val db = FirebaseFirestore.getInstance()
    // Delete all bids for this assignment
    db.collection("bids").whereEqualTo("assignmentId", assignment.id)
        .get()
        .addOnSuccessListener { bidsSnapshot ->
            val batch = db.batch()
            for (bidDoc in bidsSnapshot.documents) {
                batch.delete(bidDoc.reference)
            }
            // Delete the assignment itself
            db.collection("assignments").document(assignment.id)
                .delete()
                .addOnSuccessListener {
                    Toast.makeText(context, "Assignment deleted", Toast.LENGTH_SHORT).show()
                    batch.commit()
                    onComplete(true)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Failed to delete assignment: ${e.message}", Toast.LENGTH_SHORT).show()
                    onComplete(false)
                }
        }
        .addOnFailureListener { e ->
            Toast.makeText(context, "Failed to delete bids: ${e.message}", Toast.LENGTH_SHORT).show()
            onComplete(false)
        }
}