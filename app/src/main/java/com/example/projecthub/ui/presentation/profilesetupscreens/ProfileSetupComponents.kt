package com.example.projecthub.ui.presentation.profilesetupscreens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import com.example.projecthub.R
import com.example.projecthub.ui.theme.SilverGray

object ProfileSetupComponents {

    @Composable
    fun GradientBackground(content: @Composable () -> Unit) {
        val gradientColors = listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
            MaterialTheme.colorScheme.background
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = gradientColors
                    )
                )
                .padding(16.dp)
        ) {
            content()
        }
    }

    @Composable
    fun ProfileCard(content: @Composable () -> Unit) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 8.dp, shape = RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                content()
            }
        }
    }

    @Composable
    fun ProfilePhotoSelection(
        showDialog: MutableState<Boolean>,
        selectedPhotoId: Int,
        onPhotoSelected: (Int) -> Unit
    ) {
        val profilePhotos = listOf(
            R.drawable.profilephoto1,
            R.drawable.profilephoto2,
            R.drawable.profilephoto3,
            R.drawable.profilephoto4,
            R.drawable.profilephoto5,
            R.drawable.profilephoto6,
            R.drawable.profilephoto7,
            R.drawable.profilephoto8,
            R.drawable.profilephoto9
        )

        if (showDialog.value) {
            PhotoSelectionDialog(
                photos = profilePhotos,
                selectedPhotoId = selectedPhotoId,
                onPhotoSelected = { photo ->
                    onPhotoSelected(photo)
                    showDialog.value = false
                },
                onDismiss = { showDialog.value = false }
            )
        }
    }

    @Composable
    fun PhotoSelectionDialog(
        photos: List<Int>,
        selectedPhotoId: Int,
        onPhotoSelected: (Int) -> Unit,
        onDismiss: () -> Unit
    ) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(text = "Select a Profile Photo")
            },
            text = {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    items(photos.size) { index ->
                        val photo = photos[index]
                        Image(
                            painter = painterResource(id = photo),
                            contentDescription = "Profile Option ${index + 1}",
                            modifier = Modifier
                                .size(80.dp)
                                .padding(8.dp)
                                .clip(CircleShape)
                                .border(
                                    width = 2.dp,
                                    color = if (photo == selectedPhotoId)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        Color.LightGray,
                                    shape = CircleShape
                                )
                                .clickable {
                                    onPhotoSelected(photo)
                                }
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = onDismiss) {
                    Text("Close")
                }
            }
        )
    }

    @Composable
    fun ProfilePhotoSelector(
        selectedPhotoId: Int,
        onEditClick: () -> Unit
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                        shape = CircleShape
                    )
                    .background(SilverGray.copy(alpha = 0.5f))
                    .clickable { },
                contentAlignment = Alignment.Center
            ) {
                if (selectedPhotoId != 0) {
                    Image(
                        painter = painterResource(id = selectedPhotoId),
                        contentDescription = "Profile Photo",
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Add profile photo",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .align(Alignment.BottomEnd)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.surface,
                        shape = CircleShape
                    )
                    .clickable { onEditClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit profile photo",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }

    @Composable
    fun PhotoChangeText() {
        Text(
            text = "Add Photo",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
        )
    }

    @Composable
    fun InputField(
        value: String,
        onValueChange: (String) -> Unit,
        placeholder: String,
        leadingIcon: ImageVector,
        contentDescription: String,
        modifier: Modifier = Modifier.fillMaxWidth()
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .shadow(4.dp, RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(12.dp),
            tonalElevation = 1.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = { Text(placeholder) },
                leadingIcon = { Icon(leadingIcon, contentDescription) },
                modifier = modifier.animateContentSize(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    }

    @Composable
    fun BioField(
        bio: String,
        onBioChange: (String) -> Unit,
        wordCount: Int,
        maxBioWords: Int,
        exceedsLimit: Boolean
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .shadow(4.dp, RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(12.dp),
            tonalElevation = 1.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            OutlinedTextField(
                value = bio,
                onValueChange = onBioChange,
                placeholder = {
                    Row {
                        Text("Bio")
                        Text(
                            text = "$wordCount/$maxBioWords words",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (exceedsLimit) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(start = 8.dp)
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
                    .height(120.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (exceedsLimit) Color.Red else MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                    unfocusedBorderColor = if (exceedsLimit) Color.Red else Color.Transparent,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun SemesterDropdown(
        semester: String,
        isExpanded: Boolean,
        onExpandedChange: (Boolean) -> Unit,
        onSemesterSelected: (String) -> Unit
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .shadow(4.dp, RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(12.dp),
            tonalElevation = 1.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            val semesterOptions = listOf("1", "2", "3", "4", "5", "6", "7", "8")

            ExposedDropdownMenuBox(
                expanded = isExpanded,
                onExpandedChange = onExpandedChange,
            ) {
                OutlinedTextField(
                    value = semester,
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text("Current Semester*") },
                    leadingIcon = { Icon(Icons.Default.DateRange, "Semester") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedContainerColor = MaterialTheme.colorScheme.surface
                    )
                )

                ExposedDropdownMenu(
                    expanded = isExpanded,
                    onDismissRequest = { onExpandedChange(false) },
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
                        .width(120.dp)
                ) {
                    semesterOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                onSemesterSelected(option)
                                onExpandedChange(false)
                            }
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun SectionTitle(title: String) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )
    }

    @Composable
    fun SkillsInputRow(
        skill: String,
        onSkillChange: (String) -> Unit,
        onAddSkill: () -> Unit,
        isAddEnabled: Boolean
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .shadow(4.dp, RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(12.dp),
            tonalElevation = 1.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = skill,
                    onValueChange = onSkillChange,
                    placeholder = { Text("Add Skill") },
                    leadingIcon = { Icon(Icons.Default.Code, "Skills") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedContainerColor = MaterialTheme.colorScheme.surface
                    )
                )

                Button(
                    onClick = onAddSkill,
                    modifier = Modifier
                        .padding(end = 8.dp, top = 8.dp),
                    enabled = isAddEnabled
                ) {
                    Text("Add")
                }
            }
        }
    }

    @Composable
    fun SkillsChipRow(
        skills: List<String>,
        onSkillRemove: (Int) -> Unit = {}
    ) {
        if (skills.isNotEmpty()) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(skills.size) { index ->
                    val currentSkill = skills[index]
                    SuggestionChip(
                        onClick = { },
                        label = { Text(currentSkill) },
                        icon = {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Remove skill",
                                modifier = Modifier
                                    .size(16.dp)
                                    .clickable { onSkillRemove(index) }
                            )
                        }
                    )
                }
            }
        }
    }

    @Composable
    fun GradientButton(
        text: String,
        onClick: () -> Unit,
        enabled: Boolean
    ) {
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
            contentPadding = PaddingValues(0.dp),
            enabled = enabled
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.tertiary
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}