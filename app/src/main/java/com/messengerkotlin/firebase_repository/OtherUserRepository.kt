package com.messengerkotlin.firebase_repository

import com.google.firebase.database.FirebaseDatabase
import com.messengerkotlin.firebase_repository.extensions.onSingleEvent
import com.messengerkotlin.firebase_repository.extensions.onValueEvent
import com.messengerkotlin.models.ChatInfoModel
import com.messengerkotlin.models.UserModel

class OtherUserRepository(private val currentUserId: String) {

    private val rootReference = FirebaseDatabase.getInstance().reference

    fun getOtherUsersInfoFromChatsRegister(transferUserList: (HashMap<String, UserModel>) -> Unit){
        val users: HashMap<String, UserModel> = HashMap()
        rootReference.child("UserChatsRegister").child(currentUserId)
            .onValueEvent(onDataChanged = { dataSnapshot ->
                var userCount: Long = 0
                for (snapshot in dataSnapshot.children) {
                    val chatInfoModel = snapshot.getValue(ChatInfoModel::class.java)
                    getOtherUserInfo(chatInfoModel!!.otherUserId) { userModel ->
                        users[userModel.id] = userModel
                        if(userCount == snapshot.childrenCount){
                            transferUserList(users)
                            userCount--
                        }
                    }
                    userCount++
                }
            })
    }

    fun getOtherUserInfo(userId: String, getUserModel: (UserModel) -> Unit) {
        rootReference.child("Users").child(userId).onSingleEvent(onDataChanged = { dataSnapshot ->
            val userModel = dataSnapshot.getValue(UserModel::class.java)!!
            getUserModel(userModel)
        })
    }

    fun findOtherUser(userKey: String, transferUserId: (String?) -> Unit) {
        rootReference.child("Users").onSingleEvent(onDataChanged = { dataSnapshot ->
            for(snapshot in dataSnapshot.children) {
                val userModel = dataSnapshot.getValue(UserModel::class.java)
                if(userModel?.userkey != userKey){
                    transferUserId(userModel?.id)
                }
            }
            transferUserId(null)
        })
    }
}