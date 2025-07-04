package com.example.projecthub.ui.presentation.profilesetupscreens

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.projecthub.R
import com.example.projecthub.data.UserProfile
import com.example.projecthub.ui.presentation.profilesetupscreens.ProfileSetupComponents.ProfilePhotoSelection
import com.example.projecthub.utils.MainAppBar
import com.example.projecthub.viewModel.ThemeViewModel
import com.example.projecthub.viewModel.authViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun EditProfileScreen(
    navController: NavHostController,
    authViewModel: authViewModel = viewModel()
) {
    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    var userProfile by remember { mutableStateOf<UserProfile?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    var name by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var collegeName by remember { mutableStateOf("") }
    var semester by remember { mutableStateOf("") }
    var collegeLocation by remember { mutableStateOf("") }
    var selectedPhotoId by remember { mutableStateOf(R.drawable.profilephoto1) }
    var showPhotoDialog by remember { mutableStateOf(false) }
    var semesterDropdownExpanded by remember { mutableStateOf(false) }
    val skills = remember { mutableStateListOf<String>() }
    var skill by remember { mutableStateOf("") }
    val themeViewModel: ThemeViewModel = viewModel()

    val maxBioWords = 50
    val maxSkills = 10

    val wordList = bio.trim().split("\\s+".toRegex()).filter { it.isNotEmpty() }
    val wordCount = wordList.size
    val exceedsLimit = wordCount > maxBioWords

    val isValid = name.isNotBlank() && collegeName.isNotBlank() &&
            semester.isNotBlank() && collegeLocation.isNotBlank()

    LaunchedEffect(true) {
        if (userId != null) {
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val fetchedName = document.getString("name") ?: ""
                        val fetchedBio = document.getString("bio") ?: ""
                        val fetchedCollegeName = document.getString("collegeName") ?: ""
                        val fetchedSemester = document.getString("semester") ?: ""
                        val fetchedCollegeLocation = document.getString("collegeLocation") ?: ""
                        val fetchedSkills = document.get("skills") as? List<String> ?: emptyList()
                        val fetchedPhotoId = document.getLong("profilePhotoId")?.toInt()
                            ?: R.drawable.profilephoto1

                        name = fetchedName
                        bio = fetchedBio
                        collegeName = fetchedCollegeName
                        semester = fetchedSemester
                        collegeLocation = fetchedCollegeLocation
                        selectedPhotoId = fetchedPhotoId
                        skills.clear()
                        skills.addAll(fetchedSkills)

                        userProfile = UserProfile(
                            fetchedName,
                            fetchedBio,
                            fetchedCollegeName,
                            fetchedSemester,
                            fetchedCollegeLocation,
                            fetchedSkills,
                            fetchedPhotoId
                        )
                        isLoading = false
                    } else {
                        Toast.makeText(context, "Profile not found", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Error loading profile: ${it.message}", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                }
        }
    }

    val handleBioChange: (String) -> Unit = { newText ->
        val newWordList = newText.trim().split("\\s+".toRegex()).filter { it.isNotEmpty() }
        if (newWordList.size <= maxBioWords || newText.length < bio.length) {
            bio = if (newWordList.size >= maxBioWords) newText.trimEnd() else newText
        }
    }

    val addSkill: () -> Unit = {
        if (skill.isNotBlank() && skills.size < maxSkills) {
            skills.add(skill)
            skill = ""
        } else if (skills.size >= maxSkills) {
            Toast.makeText(context, "Maximum 10 skills allowed", Toast.LENGTH_SHORT).show()
        }
    }

    val saveUpdates: () -> Unit = {
        val updatedProfile = hashMapOf(
            "profilePhotoId" to selectedPhotoId,
            "name" to name,
            "bio" to bio,
            "collegeName" to collegeName,
            "semester" to semester,
            "collegeLocation" to collegeLocation,
            "skills" to skills
        )

        if (userId != null) {
            db.collection("users").document(userId)
                .update(updatedProfile as Map<String, Any>)
                .addOnSuccessListener {
                    Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    Scaffold(
        topBar = {
            MainAppBar(title = "Edit Profile", navController = navController)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (userProfile != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    ProfileSetupComponents.ProfileCard {
                        ProfileSetupComponents.ProfilePhotoSelector(
                            selectedPhotoId = selectedPhotoId,
                            onEditClick = { showPhotoDialog = true }
                        )

                        if (showPhotoDialog) {
                            ProfilePhotoSelection(
                                showDialog = remember { mutableStateOf(showPhotoDialog) },
                                selectedPhotoId = selectedPhotoId,
                                onPhotoSelected = { photoId ->
                                    selectedPhotoId = photoId
                                    showPhotoDialog = false
                                }
                            )
                        }

                        Text(
                            text = "Change Photo",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
                        )

                        ProfileSetupComponents.InputField(
                            value = name,
                            onValueChange = { name = it },
                            placeholder = "Full Name*",
                            leadingIcon = Icons.Default.Person,
                            contentDescription = "Name"
                        )

                        ProfileSetupComponents.BioField(
                            bio = bio,
                            onBioChange = handleBioChange,
                            wordCount = wordCount,
                            maxBioWords = maxBioWords,
                            exceedsLimit = exceedsLimit
                        )

                        ProfileSetupComponents.InputField(
                            value = collegeName,
                            onValueChange = { collegeName = it },
                            placeholder = "College Name*",
                            leadingIcon = Icons.Default.School,
                            contentDescription = "College"
                        )

                        ProfileSetupComponents.SemesterDropdown(
                            semester = semester,
                            isExpanded = semesterDropdownExpanded,
                            onExpandedChange = { semesterDropdownExpanded = it },
                            onSemesterSelected = { semester = it }
                        )

                        ProfileSetupComponents.InputField(
                            value = collegeLocation,
                            onValueChange = { collegeLocation = it },
                            placeholder = "College Location*",
                            leadingIcon = Icons.Default.LocationOn,
                            contentDescription = "Location"
                        )

                        ProfileSetupComponents.SectionTitle("Skills")

                        ProfileSetupComponents.SkillsInputRow(
                            skill = skill,
                            onSkillChange = { skill = it },
                            onAddSkill = addSkill,
                            isAddEnabled = skill.isNotBlank() && skills.size < maxSkills
                        )

                        ProfileSetupComponents.SkillsChipRow(
                            skills = skills,
                            onSkillRemove = { index -> skills.removeAt(index) }
                        )

                        ProfileSetupComponents.GradientButton(
                            text = "Save Changes",
                            onClick = saveUpdates,
                            enabled = isValid
                        )
                    }
                }
            }
        }
    }
}