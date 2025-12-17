package com.kkek.assistant.repository

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kkek.assistant.System.notification.AppNotification
import com.kkek.assistant.data.CallDetails
import com.kkek.assistant.data.model.Contact
import com.kkek.assistant.data.model.ScreenCapture
import com.kkek.assistant.domain.model.AiTool
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

object FirebaseRepository {

    private val RealTimeDB = Firebase.database.reference
    private val FirestoreDB = Firebase.firestore

    fun uploadAvailableCommands(tools: List<AiTool>) {
        // We create a simplified map structure for the DB
        val commandsMap = tools.associate { tool ->
            tool.name to mapOf(
                "description" to tool.description,
                "parameters" to tool.parameters.map { param ->
                    mapOf(
                        "name" to param.name,
                        "type" to param.type,
                        "description" to param.description,
                        "isRequired" to param.isRequired
                    )
                }
            )
        }

        RealTimeDB.child("commands").child("metadata").setValue(commandsMap)
            .addOnSuccessListener {
                Log.d("FirebaseRepository", "Uploaded available commands to DB")
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseRepository", "Failed to upload commands", e)
            }
    }


    fun uploadNotification(notification: AppNotification) {
        FirestoreDB.collection("notifications").add(notification)
            .addOnSuccessListener { documentReference ->
                Log.d("FirebaseRepository", "Notification uploaded successfully with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseRepository", "Failed to upload notification.", e)
            }
    }

    fun uploadCallDetails(callDetails: CallDetails) {
        RealTimeDB.child("calls").push().setValue(callDetails)
            .addOnSuccessListener {
                Log.d("FirebaseRepository", "Call details uploaded successfully.")
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseRepository", "Failed to upload call details.", e)
            }
    }

    suspend fun getContacts(): List<Contact> {
        Log.d("FirebaseDB", "Getting contacts from Firestore")
        val contacts = mutableListOf<Contact>()

        try {
            val querySnapshot = FirestoreDB.collection("contacts").get().await()

            for (document in querySnapshot.documents) {
                val name = document.getString("name") ?: ""
                val phone = document.getString("phone") ?: ""

                if (name.isNotEmpty() && phone.isNotEmpty()) {
                    contacts.add(Contact(name = name, phoneNumber = phone))
                }
            }
            Log.d("FirebaseDB", "Fetched contacts from Firestore: $contacts")

        } catch (e: Exception) {
            Log.e("FirebaseDB", "Failed to get contacts from Firestore", e)
        }

        Log.d("FirebaseDB", "Returning contacts: $contacts")
        return contacts
    }

    suspend fun uploadScreenContent(capture: ScreenCapture) {
        try {
            FirestoreDB.collection("screen_captures").add(capture).await()
            Log.d("FirebaseRepository", "Screen content uploaded successfully.")
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Failed to upload screen content.", e)
        }
    }
}
