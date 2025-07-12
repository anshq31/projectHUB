package com.example.projecthub.ui.presentation.chat

import AppBackground7
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.projecthub.R
import com.example.projecthub.data.chatChannel
import com.example.projecthub.navigation.routes
import com.example.projecthub.utils.MainAppBar
import com.example.projecthub.utils.NoChannelsMessage
import com.example.projecthub.utils.bottomNavigationBar
import com.example.projecthub.viewModel.ThemeViewModel
import com.example.projecthub.viewModel.authViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageListScreen(
    navController: NavHostController,
    authViewModel: authViewModel
) {
    var chats by remember { mutableStateOf<List<ChatWithUserDetails>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val themeViewModel: ThemeViewModel = viewModel()

    LaunchedEffect(key1 = true) {
        val db = FirebaseFirestore.getInstance()
        try {
            val query1 = db.collection("chatChannels")
                .whereEqualTo("user1Id", currentUserId)
                .get()
                .await()

            val query2 = db.collection("chatChannels")
                .whereEqualTo("user2Id", currentUserId)
                .get()
                .await()

            val processedChats = mutableListOf<ChatWithUserDetails>()

            for (document in query1.documents) {
                val channel = document.toObject(chatChannel::class.java)
                if (channel != null) {
                    val otherUserId = channel.user2Id

                    val userDoc = db.collection("users").document(otherUserId).get().await()
                    val userName = userDoc.getString("name") ?: "Unknown User"
                    val photoId = userDoc.getLong("profilePhotoId")?.toInt() ?: R.drawable.profilephoto1

                    processedChats.add(
                        ChatWithUserDetails(
                            channelId = document.id,
                            channel = channel.copy(channelId = document.id),
                            otherUserId = otherUserId,
                            otherUserName = userName,
                            otherUserPhotoId = photoId,
                            lastMessage = channel.lastMessageText
                        )
                    )
                }
            }

            for (document in query2.documents) {
                val channel = document.toObject(chatChannel::class.java)
                if (channel != null) {
                    val otherUserId = channel.user1Id

                    val userDoc = db.collection("users").document(otherUserId).get().await()
                    val userName = userDoc.getString("name") ?: "Unknown User"
                    val photoId = userDoc.getLong("profilePhotoId")?.toInt() ?: R.drawable.profilephoto1

                    processedChats.add(
                        ChatWithUserDetails(
                            channelId = document.id,
                            channel = channel.copy(channelId = document.id),
                            otherUserId = otherUserId,
                            otherUserName = userName,
                            otherUserPhotoId = photoId,
                            lastMessage = channel.lastMessageText
                        )
                    )
                }
            }

            chats = processedChats.sortedByDescending { it.channel.lastMessageTimestamp.seconds }
            isLoading = false
        } catch (e: Exception) {
            isLoading = false
        }
    }

    Scaffold(
        topBar = { MainAppBar(title = "Messages", navController = navController) },
        bottomBar = { bottomNavigationBar(navController = navController, currentRoute = "messages_list") }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues).background(MaterialTheme.colorScheme.background)) {

            if (isLoading) {
                MessageListComponents.LoadingIndicator()
            } else if (chats.isEmpty()) {
                NoChannelsMessage(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(16.dp)
                ) {
                    items(chats) { chatDetails ->
                        MessageListComponents.ChatItem(
                            chatDetails = chatDetails,
                            onClick = {
                                navController.navigate(routes.chatScreen.route.replace("{chatChannelId}", chatDetails.channelId))
                            }
                        )
                        MessageListComponents.ChatDivider()
                    }
                }
            }
        }
    }
}

data class ChatWithUserDetails(
    val channelId: String,
    val channel: chatChannel,
    val otherUserId: String,
    val otherUserName: String,
    val otherUserPhotoId: Int,
    val lastMessage: String
)