package com.example.projecthub.ui.presentation.homescreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.projecthub.data.Assignment
import com.example.projecthub.navigation.routes

object HomeScreenComponents {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun HomeTopAppBar(scrollBehavior: TopAppBarScrollBehavior, greeting: String, currentDate: String, onNotificationClick: () -> Unit, onSettingsClick: () -> Unit) {
        TopAppBar(
            title = {
                Column {
                    Text(
                        text = greeting,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = currentDate,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.9f)
                    )
                }
            },
            actions = {
                IconButton(onClick = onNotificationClick) {
                    Icon(Icons.Default.Notifications, contentDescription = "Notifications",
                        tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = onSettingsClick) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings",
                        tint = MaterialTheme.colorScheme.primary)
                }
            },
            scrollBehavior = scrollBehavior,
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.75f),
                titleContentColor = MaterialTheme.colorScheme.primary
            )
        )
    }

    @Composable
    fun StatsCard(posted: Int, active: Int, completed: Int) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(0.9f)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(1.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Your Statistics",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatItem(
                            icon = Icons.Default.Create,
                            label = "Posted",
                            value = posted.toString(),
                            color = MaterialTheme.colorScheme.primary
                        )
                        StatItem(
                            icon = Icons.Default.Assignment,
                            label = "Working On",
                            value = active.toString(),
                            color = MaterialTheme.colorScheme.secondary
                        )
                        StatItem(
                            icon = Icons.Default.CheckCircle,
                            label = "Completed",
                            value = completed.toString(),
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun StatItem(
        icon: ImageVector,
        label: String,
        value: String,
        color: Color
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = label, tint = color)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    @Composable
    fun MyAssignmentsSection(
        navController: NavHostController,
        assignments: List<Assignment>,
        isLoading: Boolean = false
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            SectionHeader(
                title = "My Assignments",
                onSeeAllClick = { navController.navigate(routes.assignmentsScreen.route) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            AssignmentsList(
                assignments = assignments,
                isLoading = isLoading,
                emptyMessage = "No active assignments"
            )
        }
    }

    @Composable
    fun AvailableAssignmentsSection(
        navController: NavHostController,
        assignments: List<Assignment>,
        isLoading: Boolean = false
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            SectionHeader(
                title = "Available Assignments",
                onSeeAllClick = { navController.navigate(routes.assignmentsScreen.route) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            AssignmentsList(
                assignments = assignments,
                isLoading = isLoading,
                emptyMessage = "No available assignments"
            )
        }
    }

    @Composable
    private fun SectionHeader(
        title: String,
        onSeeAllClick: () -> Unit
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            TextButton(onClick = onSeeAllClick) {
                Text(
                    "See All",
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                )
            }
        }
    }

    @Composable
    private fun AssignmentsList(
        assignments: List<Assignment>,
        isLoading: Boolean,
        emptyMessage: String
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            )
        ) {
            when {
                isLoading -> {
                    LoadingView()
                }
                assignments.isEmpty() -> {
                    EmptyView(message = emptyMessage)
                }
                else -> {
                    AssignmentsRow(assignments = assignments)
                }
            }
        }
    }

    @Composable
    private fun LoadingView() {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
    }

    @Composable
    private fun EmptyView(message: String) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                message,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    @Composable
    private fun AssignmentsRow(assignments: List<Assignment>) {
        androidx.compose.foundation.lazy.LazyRow(
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(assignments) { assignment ->
                AssignmentCard(assignment = assignment)
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AssignmentCard(assignment: Assignment) {
        Card(
            modifier = Modifier
                .width(280.dp)
                .padding(horizontal = 3.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(0.9f)
            ),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            onClick = {  }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(1.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
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

                        StatusBadge(status = "Active")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = assignment.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = assignment.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        InfoChip(Icons.Default.Timer, "Deadline: ${assignment.deadline}")
                        InfoChip(Icons.Default.CurrencyRupee, "${assignment.budget}")
                    }
                }
            }
        }
    }

    @Composable
    fun StatusBadge(status: String) {
        Surface(
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = status,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }

    @Composable
    fun InfoChip(icon: ImageVector, text: String) {
        Surface(
            modifier = Modifier.padding(end = 4.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
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
}