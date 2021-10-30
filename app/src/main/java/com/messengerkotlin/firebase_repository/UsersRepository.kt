package com.messengerkotlin.firebase_repository

import android.content.ContentResolver
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.messengerkotlin.core.LoadingState
import com.messengerkotlin.core.enums.CommonStatus
import com.messengerkotlin.firebase_repository.extensions.onValueEvent
import com.messengerkotlin.models.UserModel

class UsersRepository {

    private val usersReference = FirebaseDatabase.getInstance().reference.child("Users")

    fun getUserLiveData(id: String): LiveData<LoadingState<UserModel?, Throwable>> {
        val userLiveData = MutableLiveData<LoadingState<UserModel?, Throwable>>(LoadingState.Loading())
        usersReference.child(id).onValueEvent(
            onDataChanged = {
                try {
                    userLiveData.postValue(LoadingState.Loaded(it.getValue(UserModel::class.java)))
                } catch (e: Throwable) {
                    userLiveData.postValue(LoadingState.Error(e))
                }
            },
            onCancelled = {
                userLiveData.postValue(LoadingState.Error(it.toException()))
            }
        )
        return userLiveData
    }

    fun editUsername(userId: String,username: String) {
        usersReference.child(userId).child("username").setValue(username)
    }

    fun editUserKey(userkey: String, responseStatus: (CommonStatus) -> Unit) {
        usersReference.onValueEvent(onDataChanged = { dataSnapshot ->
            var exist = false
            for (snapshot in dataSnapshot.children) {
                val userModel: UserModel? = snapshot.getValue(UserModel::class.java)
                if (userModel?.userkey != null && userModel.userkey == userkey) {
                    responseStatus(CommonStatus.ALREADY_EXIST)
                    exist = true
                    break
                }
            }
            if (!exist) {
                myUserReference.child("userkey").setValue(userkey)
                responseStatus(CommonStatus.SUCCESS)
            }
        })
    }

    fun loadProfileImage(imageUri: Uri, contentResolver: ContentResolver) {
        val name = System.currentTimeMillis().toString() + "." + getFileExtension(imageUri, contentResolver)
        val storageReference = FirebaseStorage.getInstance().getReference("Uploads").child(name)

        storageReference.putFile(imageUri).addOnCompleteListener{ task1 ->
            if(task1.isSuccessful){
                storageReference.downloadUrl.addOnCompleteListener{ task2 ->
                    if(task2.isSuccessful){
                        myUserReference.child("imageurl").setValue(task2.result.toString())
                    }
                }
            }
        }
    }

    private fun getFileExtension(imageURI: Uri, contentResolver: ContentResolver): String? {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(imageURI))
    }
}