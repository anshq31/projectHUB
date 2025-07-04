package com.example.projecthub.utils

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore


fun markAssignmentCompleted(assignmentId: String, onShowRatingDialog: (String, String) -> Unit) {
    val db = FirebaseFirestore.getInstance()

    db.collection("assignments").document(assignmentId)
        .get()
        .addOnSuccessListener { document ->
            if (document.exists()) {
                val acceptedBidderId = document.getString("acceptedBidderId")
                if (acceptedBidderId != null) {
                    onShowRatingDialog(assignmentId, acceptedBidderId)
                } else {
                    db.collection("assignments").document(assignmentId)
                        .update(
                            mapOf(
                                "status" to "completed",
                                "completedOn" to Timestamp.now()
                            )
                        )
                }
            }
        }
}