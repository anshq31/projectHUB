package com.example.projecthub.ui.presentation.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.projecthub.data.message
import java.text.SimpleDateFormat
import java.util.Locale
import com.google.firebase.auth.FirebaseAuth

object MessageScreenComponents {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ChatTopBar(
        isLoading: Boolean,
        otherUserName: String,
        otherUserPhotoId: Int,
        otherUserId: String,
        navController: NavHostController
    ) {
        val isDarkTheme = isSystemInDarkTheme()

        TopAppBar(
            title = {
                if (isLoading) {
                    LoadingTopBarContent()
                } else {
                    TopBarUserInfo(
                        otherUserName = otherUserName,
                        otherUserPhotoId = otherUserPhotoId,
                        otherUserId = otherUserId,
                        navController = navController
                    )
                }
            },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = if (isDarkTheme)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = if (isDarkTheme)
                    MaterialTheme.colorScheme.onPrimary
                else
                    MaterialTheme.colorScheme.onPrimaryContainer,
                navigationIconContentColor = if (isDarkTheme)
                    MaterialTheme.colorScheme.onPrimary
                else
                    MaterialTheme.colorScheme.onPrimaryContainer
            )
        )
    }

    @Composable
    fun LoadingTopBarContent() {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant
                    )
            )
            Spacer(modifier = Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .height(24.dp)
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        RoundedCornerShape(4.dp)
                    )
            )
        }
    }

    @Composable
    fun TopBarUserInfo(
        otherUserName: String,
        otherUserPhotoId: Int,
        otherUserId: String,
        navController: NavHostController
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = otherUserPhotoId),
                contentDescription = "Profile photo",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable {
                        if (otherUserId.isNotEmpty()) {
                            navController.navigate("user_profile/${otherUserId}")
                        }
                    }
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = otherUserName,
                modifier = Modifier.clickable {
                    if (otherUserId.isNotEmpty()) {
                        navController.navigate("user_profile/${otherUserId}")
                    }
                },
                style = MaterialTheme.typography.titleMedium
            )
        }
    }

    @Composable
    fun LoadingIndicator() {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    MaterialTheme.colorScheme.surface
                ),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary
            )
        }
    }

    @Composable
    fun NoMessagesPlaceholder() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "No messages yet",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                "Send a message to start the conversation",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    @Composable
    fun MessageInputBar(
        messageText: String,
        onMessageTextChange: (String) -> Unit,
        onSendMessage: () -> Unit
    ) {
        val isDarkTheme = isSystemInDarkTheme()

        Surface(
            color = if (isDarkTheme)
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)
            else
                MaterialTheme.colorScheme.surfaceVariant,
            tonalElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = messageText,
                    onValueChange = onMessageTextChange,
                    placeholder = {
                        Text(
                            "Type your message....",
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    singleLine = false,
                    maxLines = 4
                )

                Spacer(modifier = Modifier.width(8.dp))

                FloatingActionButton(
                    onClick = onSendMessage,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send")
                }
            }
        }
    }

    @Composable
    fun MessageBubble(message: message) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        val isCurrentUser = message.senderId == currentUserId
        val alignment = if (isCurrentUser) Alignment.CenterEnd else Alignment.CenterStart

        val bubbleColor = if (isCurrentUser)
            MaterialTheme.colorScheme.primary
        else
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)

        val textColor = if (isCurrentUser)
            MaterialTheme.colorScheme.onPrimary
        else
            MaterialTheme.colorScheme.onSecondaryContainer

        val timeString = SimpleDateFormat("hh:mm a", Locale.getDefault())
            .format(message.timestamp.toDate())

        val isLikelySingleLine = message.text.length < 30

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp),
            contentAlignment = alignment
        ) {
            Surface(
                color = bubbleColor,
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (isCurrentUser) 16.dp else 4.dp,
                    bottomEnd = if (isCurrentUser) 4.dp else 16.dp
                ),
                modifier = Modifier
                    .widthIn(max = 280.dp)
                    .wrapContentWidth(),
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    if (isLikelySingleLine) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = message.text,
                                color = textColor,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.weight(1f, fill = false)
                            )

                            Text(
                                text = timeString,
                                style = MaterialTheme.typography.labelSmall,
                                color = textColor.copy(alpha = 0.7f)
                            )
                        }
                    } else {
                        Text(
                            text = message.text,
                            color = textColor,
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Text(
                            text = timeString,
                            style = MaterialTheme.typography.labelSmall,
                            color = textColor.copy(alpha = 0.7f),
                            modifier = Modifier
                                .align(Alignment.End)
                                .padding(top = 4.dp)
                        )
                    }
                }
            }
        }
    }
}