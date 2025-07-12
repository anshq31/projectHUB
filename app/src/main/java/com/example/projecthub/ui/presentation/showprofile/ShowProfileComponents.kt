package com.example.projecthub.ui.presentation.showprofile

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

object ShowProfileComponents {

    @Composable
    fun LoadingIndicator() {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary
            )
        }
    }

    @SuppressLint("DefaultLocale")
    @Composable
    fun ProfileHeader(
        name: String,
        photoId: Int,
        averageRating: Double = 0.0,
        ratingCount: Int = 0
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    )
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = photoId),
                    contentDescription = "Profile Photo",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ){
                if(ratingCount > 0){
                    val fullStars = averageRating.toInt()
                    val hasHalfStar = averageRating - fullStars >= 0.5

                    repeat(fullStars) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Star",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    if (hasHalfStar) {
                        Icon(
                            imageVector = Icons.Default.StarHalf,
                            contentDescription = "Half Star",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    val remainingStars = 5 - fullStars - (if (hasHalfStar) 1 else 0)
                    repeat(remainingStars) {
                        Icon(
                            imageVector = Icons.Default.StarBorder,
                            contentDescription = "Empty Star",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = String.format("%.1f", averageRating) + " (${ratingCount})",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }else{
                    Text(
                        text = "Not rated yet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    @Composable
    fun ProfileCard(
        content: @Composable () -> Unit
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 8.dp, shape = RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                // Original color
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                // Alternative: containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                content()
            }
        }
    }

    @Composable
    fun InfoSection(title: String, icon: ImageVector, content: String?) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        if (!content.isNullOrBlank()) {
            Text(
                text = content,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(start = 32.dp, top = 8.dp, bottom = 8.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    @Composable
    fun ProfileDetailRow(label: String, value: String, icon: ImageVector) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 32.dp, top = 12.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }

    @Composable
    fun ProfileDivider() {
        Divider(
            modifier = Modifier.padding(vertical = 16.dp),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
        )
    }

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    fun SkillsGrid(skills: List<String>) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 32.dp, top = 8.dp)
        ) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                skills.forEach { skill ->
                    SuggestionChip(
                        onClick = { },
                        label = {
                            Text(
                                skill,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        )
                    )
                }
            }
        }
    }

    @Composable
    fun EditProfileButton(onClick: () -> Unit) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .shadow(8.dp, RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent
            ),
            contentPadding = PaddingValues(0.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        MaterialTheme.colorScheme.primary,
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Edit Profile",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}