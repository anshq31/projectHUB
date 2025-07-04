package com.example.projecthub.ui.presentation.chat

import ChatWallpaperBackground
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.projecthub.data.message
import com.example.projecthub.utils.sendMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.projecthub.utils.listenForMessages
import com.example.projecthub.viewModel.ThemeViewModel
import com.google.firebase.firestore.ListenerRegistration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavHostController,
    chatChannelId: String
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val messageListState = rememberLazyListState()

    var messages by remember { mutableStateOf<List<message>>(emptyList()) }
    var messageText by remember { mutableStateOf("") }
    val scrollState = rememberLazyListState()
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    var otherUserName by remember { mutableStateOf("Chat") }
    var otherUserId by remember { mutableStateOf("") }
    var otherUserPhotoId by remember { mutableStateOf(com.example.projecthub.R.drawable.profilephoto1) }
    var isLoading by remember { mutableStateOf(true) }
    val themeViewModel: ThemeViewModel = viewModel()

    DisposableEffect(chatChannelId) {
        var listener: ListenerRegistration? = null

        val db = FirebaseFirestore.getInstance()
        try {
            db.collection("chatChannels")
                .document(chatChannelId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val user1Id = document.getString("user1Id") ?: ""
                        val user2Id = document.getString("user2Id") ?: ""

                        otherUserId = if (currentUserId == user1Id) user2Id else user1Id

                        if (otherUserId.isNotEmpty()) {
                            db.collection("users")
                                .document(otherUserId)
                                .get()
                                .addOnSuccessListener { userDoc ->
                                    otherUserName = userDoc.getString("name") ?: "Chat"
                                    userDoc.getLong("profilePhotoId")?.toInt()?.let {
                                        otherUserPhotoId = it
                                    }
                                }
                                .addOnFailureListener { e ->
                                    Log.e("ChatScreen", "Error fetching user data: ${e.message}")
                                }
                        }
                    } else {
                        Log.e("ChatScreen", "Chat document doesn't exist")
                    }
                    isLoading = false
                }
                .addOnFailureListener { e ->
                    Log.e("ChatScreen", "Error loading chat: ${e.message}")
                    isLoading = false
                }

            listener = listenForMessages(chatChannelId) { fetchedMessages ->
                messages = fetchedMessages
            }
        } catch (e: Exception) {
            Log.e("ChatScreen", "Error in DisposableEffect: ${e.message}", e)
            isLoading = false
        }

        onDispose {
            try {
                listener?.remove()
            } catch (e: Exception) {
                Log.e("ChatScreen", "Error removing listener: ${e.message}", e)
            }
        }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            scrollState.animateScrollToItem(messages.size - 1)
        }
    }

    fun handleSendMessage() {
        if (messageText.isNotBlank()) {
            sendMessage(
                chatChannelId = chatChannelId,
                messageText = messageText,
                senderId = currentUserId
            )
            messageText = ""
        }
    }

    Scaffold(
        topBar = {
            MessageScreenComponents.ChatTopBar(
                isLoading = isLoading,
                otherUserName = otherUserName,
                otherUserPhotoId = otherUserPhotoId,
                otherUserId = otherUserId,
                navController = navController
            )
        }
    ) { innerPadding ->
        if (isLoading) {
            Box(modifier = Modifier.padding(innerPadding)) {
                MessageScreenComponents.LoadingIndicator()
                ChatWallpaperBackground(themeViewModel = themeViewModel)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    ChatWallpaperBackground(themeViewModel = themeViewModel)

                    if (messages.isEmpty()) {
                        MessageScreenComponents.NoMessagesPlaceholder()
                    } else {
                        LazyColumn(
                            state = scrollState,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            items(messages) { message ->
                                MessageScreenComponents.MessageBubble(message = message)
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }

                MessageScreenComponents.MessageInputBar(
                    messageText = messageText,
                    onMessageTextChange = { messageText = it },
                    onSendMessage = ::handleSendMessage
                )
            }
        }
    }
}