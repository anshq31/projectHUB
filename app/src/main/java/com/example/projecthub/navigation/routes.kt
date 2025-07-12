package com.example.projecthub.navigation

 sealed class routes(val route : String) {
    object loginPage : routes("login_page")
    object signupPage : routes("signup_page")
    object homePage : routes("home_page")
    object profileSetupPage : routes("profile_setup_page")
    object onBoardingPage : routes("onBoarding_page")
    object profilePage : routes("profile_page")
    object settingsScreen : routes("settings_page")
    object changePasswordScreen : routes("change_password_page")
    object editProfileScreen : routes("edit_profile_page")
    object createAssignmentScreen : routes("create_assignment_page")
    object assignmentsScreen : routes("assignments_page")
    object assignmentDetailScreen : routes("assignment_details/{assignmentId}")
    object userProfileScreen : routes("user_profile/{userId}")

    object messagesListScreen : routes("messages_list")
    object chatScreen : routes("chat_screen/{chatChannelId}")

    object emailVerificationScreen : routes("email_verification_screen")
}