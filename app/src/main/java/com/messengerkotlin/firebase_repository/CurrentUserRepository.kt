package com.messengerkotlin.firebase_repository

import android.content.ContentResolver
import android.net.Uri
import android.webkit.MimeTypeMap
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.messengerkotlin.core.EventResponse
import com.messengerkotlin.core.enums.CommonStatus
import com.messengerkotlin.firebase_repository.extensions.onSingleEvent
import com.messengerkotlin.firebase_repository.extensions.onValueEventFlow
import com.messengerkotlin.models.UserModel
import kotlinx.coroutines.flow.collect
import java.lang.IllegalStateException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class CurrentUserRepository {

    private val usersReference = FirebaseDatabase.getInstance().reference.child("Users")

    suspend fun getCurrentUser(currentUserId: String, callback: (UserModel?) -> Unit) {
        usersReference.child(currentUserId).onValueEventFlow().collect {
            when (it) {
                is EventResponse.Cancelled -> throw IllegalStateException()
                is EventResponse.Changed -> callback(it.snapshot.getValue(UserModel::class.java))
            }
        }
    }

    fun editUsername(currentUserId: String, username: String) {
        usersReference.child(currentUserId).child("username").setValue(username)
    }

    suspend fun editUserKey(
        userkey: String
    ): CommonStatus {
        when (val response: EventResponse = usersReference.onSingleEvent()) {
            is EventResponse.Cancelled -> throw IllegalStateException()
            is EventResponse.Changed -> {
                response.snapshot.children
                    .forEach { dataSnapshot ->
                        val userModel = dataSnapshot.getValue(UserModel::class.java)
                        if (userModel?.userkey != null && userModel.userkey == userkey) {
                            return CommonStatus.ALREADY_EXIST
                        }
                    }
                return CommonStatus.SUCCESS
            }
        }
    }


    fun loadProfileImage(currentUserId: String, imageUri: Uri, contentResolver: ContentResolver) {
        val name = System.currentTimeMillis().toString() + "." + getFileExtension(
            imageUri,
            contentResolver
        )
        val storageReference = FirebaseStorage.getInstance().getReference("Uploads").child(name)

        storageReference.putFile(imageUri).addOnCompleteListener { task1 ->
            if (task1.isSuccessful) {
                storageReference.downloadUrl.addOnCompleteListener { task2 ->
                    if (task2.isSuccessful) {
                        usersReference.child(currentUserId).child("imageurl")
                            .setValue(task2.result.toString())
                    }
                }
            }
        }
    }

    private fun getFileExtension(imageURI: Uri, contentResolver: ContentResolver): String? {
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(contentResolver.getType(imageURI))
    }
}