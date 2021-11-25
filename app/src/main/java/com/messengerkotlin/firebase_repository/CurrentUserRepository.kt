package com.messengerkotlin.firebase_repository

import android.content.ContentResolver
import android.net.Uri
import android.webkit.MimeTypeMap
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.messengerkotlin.core.enums.CommonStatus
import com.messengerkotlin.firebase_repository.extensions.onValueEvent
import com.messengerkotlin.models.UserModel
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class CurrentUserRepository {

    private val usersReference = FirebaseDatabase.getInstance().reference.child("Users")

    suspend fun getCurrentUser(currentUserId: String): UserModel? =
        suspendCoroutine { continuation ->
            usersReference.child(currentUserId).onValueEvent(
                onDataChanged = {
                    continuation.resume(it.getValue(UserModel::class.java))
                }
            )
        }

    fun editUsername(currentUserId: String, username: String) {
        usersReference.child(currentUserId).child("username").setValue(username)
    }

    fun editUserKey(
        currentUserId: String,
        userkey: String,
        callbackStatus: (CommonStatus) -> Unit
    ) {
        usersReference.onValueEvent(onDataChanged = { dataSnapshot ->
            var exist = false
            for (snapshot in dataSnapshot.children) {
                val userModel: UserModel? = snapshot.getValue(UserModel::class.java)
                if (userModel?.userkey != null && userModel.userkey == userkey) {
                    callbackStatus(CommonStatus.ALREADY_EXIST)
                    exist = true
                    break
                }
            }
            if (!exist) {
                usersReference.child(currentUserId).child("userkey").setValue(userkey)
                callbackStatus(CommonStatus.SUCCESS)
            }
        })
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