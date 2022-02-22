package com.messengerkotlin.firebase_repository

import android.content.ContentResolver
import android.net.Uri
import android.webkit.MimeTypeMap
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.messengerkotlin.core.EventResponse
import com.messengerkotlin.core.enums.CommonStatus
import com.messengerkotlin.core.utils.FBNames
import com.messengerkotlin.firebase_repository.extensions.onSingleEvent
import com.messengerkotlin.firebase_repository.extensions.onValueEventFlow
import com.messengerkotlin.models.UserModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class CurrentUserRepository(private val ioDispatcher: CoroutineDispatcher) {

    private val usersReference = FirebaseDatabase.getInstance().reference.child(FBNames.users)
    private val userKeysReference = FirebaseDatabase.getInstance().reference.child(FBNames.userKeys)

    suspend fun getCurrentUser(currentUserId: String): Flow<UserModel> =
        withContext(ioDispatcher) {
            usersReference.child(currentUserId).onValueEventFlow()
                .filter { it is EventResponse.Changed }
                .map { (it as EventResponse.Changed).snapshot.getValue(UserModel::class.java)!! }
        }

    suspend fun editUsername(currentUserId: String, username: String) = withContext(ioDispatcher){
        usersReference.child(currentUserId).child("username").setValue(username)
    }

    suspend fun editUserKey(
        currentUserId: String,
        userkey: String
    ): CommonStatus = withContext(ioDispatcher){
        when (val response: EventResponse = userKeysReference.child(userkey).onSingleEvent()) {
            is EventResponse.Cancelled -> throw IllegalStateException()
            is EventResponse.Changed -> {
                return@withContext if (response.snapshot.value == null){
                    userKeysReference.child(userkey).setValue(currentUserId)
                    usersReference.child(currentUserId).child("userkey").setValue(userkey)
                    CommonStatus.SUCCESS
                } else {
                    CommonStatus.ALREADY_EXIST
                }
            }
        }
    }


    suspend fun loadProfileImage(currentUserId: String, imageUri: Uri, contentResolver: ContentResolver) {
        withContext(ioDispatcher) {
            val name = System.currentTimeMillis().toString() + "." + getFileExtension(
                imageUri,
                contentResolver
            )
            val storageReference = FirebaseStorage.getInstance().getReference("Uploads").child(name)
            storageReference.putFile(imageUri).addOnCompleteListener { task1 ->
                if (task1.isSuccessful) {
                    storageReference.downloadUrl.addOnCompleteListener { task2 ->
                        if (task2.isSuccessful) {
                            usersReference.child(currentUserId).child("imageurl").setValue(task2.result.toString())
                        }
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