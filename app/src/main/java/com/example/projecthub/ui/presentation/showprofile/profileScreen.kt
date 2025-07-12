package com.example.projecthub.ui.presentation.showprofile

import AppBackground7
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.projecthub.R
import com.example.projecthub.data.UserProfile
import com.example.projecthub.navigation.routes
import com.example.projecthub.screens.CreateAssignmentDialog
import com.example.projecthub.utils.CreateAssignmentFAB
import com.example.projecthub.utils.MainAppBar
import com.example.projecthub.utils.bottomNavigationBar
import com.example.projecthub.viewModel.ThemeViewModel
import com.example.projecthub.viewModel.authViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun profileScreen(navController: NavHostController,authViewModel: authViewModel) {
    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    var showDialog by remember { mutableStateOf(false) }

    var userProfile by remember { mutableStateOf<UserProfile?>(null) }

    LaunchedEffect(true) {
        if (userId != null) {
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val name = document.getString("name") ?: ""
                        val bio = document.getString("bio") ?: ""
                        val collegeName = document.getString("collegeName") ?: ""
                        val semester = document.getString("semester") ?: ""
                        val collegeLocation = document.getString("collegeLocation") ?: ""
                        val skills = document.get("skills") as? List<String> ?: emptyList()
                        val profilePhotoId = document.getLong("profilePhotoId")?.toInt()
                            ?: R.drawable.profilephoto1
                        val ratingSum = document.getDouble("ratingSum") ?: 0.0
                        val ratingCount = document.getLong("ratingCount")?.toInt() ?: 0
                        val averageRating = document.getDouble("averageRating") ?: 0.0
                        val skillsList = document.get("skills") as? List<String> ?: emptyList()

                        userProfile = UserProfile(
                            name = name,
                            bio = bio,
                            collegeName = collegeName,
                            semester = semester,
                            collegeLocation = collegeLocation,
                            skills = skillsList,
                            profilePhotoId = profilePhotoId,
                            ratingSum = ratingSum,
                            ratingCount = ratingCount,
                            averageRating = averageRating
                        )
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Error loading profile", Toast.LENGTH_SHORT).show()
                }
        }
    }
    if (userProfile == null) {
        ShowProfileComponents.LoadingIndicator()
    } else {
        ProfileScreenContent(navController = navController, userProfile = userProfile!!,authViewModel = authViewModel)
    }
}

@Composable
fun ProfileScreenContent(navController: NavHostController, userProfile: UserProfile,authViewModel: authViewModel) {

    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val themeViewModel: ThemeViewModel = viewModel()

    Scaffold(
        topBar = {
            MainAppBar(title = "Profile",navController = navController)
        },
        bottomBar = {
            bottomNavigationBar(navController = navController, currentRoute = "profile")
        },
        floatingActionButton = {
            CreateAssignmentFAB(onClick = { showDialog = true })
        },
        floatingActionButtonPosition = FabPosition.Center,
    ) { paddingValues ->
        if (showDialog) {
            CreateAssignmentDialog(
                showDialog = showDialog,
                onDismiss = { showDialog = false },
                authViewModel = authViewModel,
                onAssignmentCreated = {
                    showDialog = false
                    Toast.makeText(
                        context,
                        "Assignment created successfully!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                ShowProfileComponents.ProfileHeader(
                    name = userProfile.name,
                    photoId = userProfile.profilePhotoId,
                    averageRating = userProfile.averageRating,
                    ratingCount = userProfile.ratingCount
                )

                Spacer(modifier = Modifier.height(16.dp))

                ShowProfileComponents.ProfileCard {
                    ShowProfileComponents.InfoSection(
                        title = "About",
                        icon = Icons.Default.Person,
                        content = userProfile.bio
                    )

                    ShowProfileComponents.ProfileDivider()

                    ShowProfileComponents.InfoSection(
                        title = "Education",
                        icon = Icons.Default.School,
                        content = null
                    )

                    ShowProfileComponents.ProfileDetailRow(
                        label = "College",
                        value = userProfile.collegeName,
                        icon = Icons.Default.AccountBalance
                    )

                    ShowProfileComponents.ProfileDetailRow(
                        label = "Semester",
                        value = userProfile.semester,
                        icon = Icons.Default.DateRange
                    )

                    ShowProfileComponents.ProfileDetailRow(
                        label = "Location",
                        value = userProfile.collegeLocation,
                        icon = Icons.Default.LocationOn
                    )

                    ShowProfileComponents.ProfileDivider()

                    ShowProfileComponents.InfoSection(
                        title = "Skills",
                        icon = Icons.Default.Code,
                        content = null
                    )

                    ShowProfileComponents.SkillsGrid(skills = userProfile.skills)

                    Spacer(modifier = Modifier.height(24.dp))

                    ShowProfileComponents.EditProfileButton {
                        navController.navigate(routes.editProfileScreen.route)
                    }
                }
            }
        }
    }
}