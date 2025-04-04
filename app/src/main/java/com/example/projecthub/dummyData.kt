package com.example.projecthub

import com.example.projecthub.models.user

object dummyData {
    val dummyUser = user(
        userId = "12345",
        name = "John Doe",
        bio = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
        collegeName = "XYZ University",
        semester = "3rd",
        collegeLocation = "New York, USA",
        skills = listOf("Kotlin", "Java", "Android Development"),
        profilePhotoId = R.drawable.profilephoto1
    )

}