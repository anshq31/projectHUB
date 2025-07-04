package com.example.projecthub.screens

import AppBackground7
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.projecthub.R
import com.example.projecthub.ui.presentation.profilesetupscreens.ProfileSetupComponents
import com.example.projecthub.ui.presentation.profilesetupscreens.ProfileSetupComponents.ProfilePhotoSelection
import com.example.projecthub.viewModel.ThemeViewModel
import com.example.projecthub.viewModel.authViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun ProfileSetupScreen(navController: NavHostController, authViewModel: authViewModel = viewModel()) {
    var name by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var collegeName by remember { mutableStateOf("") }
    var semester by remember { mutableStateOf("") }
    var collegeLocation by remember { mutableStateOf("") }
    var skill by remember { mutableStateOf("") }

    val skills = remember { mutableStateListOf<String>() }
    var profilePhoto by remember { mutableStateOf<Uri?>(null) }
    val themeViewModel: ThemeViewModel = viewModel()

    var selectedPhotoId by remember { mutableStateOf(R.drawable.profilephoto1) }
    var showPhotoDialog by remember { mutableStateOf(false) }
    var semesterDropdownExpanded by remember { mutableStateOf(false) }

    val maxBioWords = 50
    val maxSkills = 10

    val context = LocalContext.current

    val db = FirebaseFirestore.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    val wordList = bio.trim().split("\\s+".toRegex()).filter { it.isNotEmpty() }
    val wordCount = wordList.size
    val exceedsLimit = wordCount > maxBioWords

    val isValid = name.isNotBlank() && collegeName.isNotBlank() &&
            semester.isNotBlank() && collegeLocation.isNotBlank()

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

    val saveProfile: () -> Unit = {
        val profile = hashMapOf(
            "profilePhoto" to selectedPhotoId,
            "name" to name,
            "bio" to bio,
            "collegeName" to collegeName,
            "semester" to semester,
            "collegeLocation" to collegeLocation,
            "skills" to skills
        )

        if (userId != null) {
            db.collection("users").document(userId)
                .set(profile)
                .addOnSuccessListener {
                    authViewModel.completeProfileSetup()
                    Toast.makeText(context, "Profile Saved Successfully!", Toast.LENGTH_SHORT).show()
                    navController.navigate("home_page")
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    ProfileSetupComponents.GradientBackground {

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
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

                ProfileSetupComponents.PhotoChangeText()

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

                ProfileSetupComponents.SkillsChipRow(skills = skills)

                ProfileSetupComponents.GradientButton(
                    text = "Save Profile",
                    onClick = saveProfile,
                    enabled = isValid
                )
            }
        }
    }
}