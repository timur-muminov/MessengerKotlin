package com.messengerkotlin.firebase_repository

import android.content.ContentResolver
import android.net.Uri
import com.messengerkotlin.core.enums.CommonStatus
import com.messengerkotlin.core.enums.Status
import com.messengerkotlin.models.MessageModel
import com.messengerkotlin.models.UserModel

class FirebaseRepository(currentUserId: String) {

    private val currentUserInfoHelper = CurrentUserRepository(currentUserId)
    private val otherUserInfoHelper = OtherUserRepository(currentUserId)
    private val userStatusHelper = UserStatusRepository(currentUserId)
    private val chatHelper = ChatRepository(currentUserId)


    fun getCurrentUserInfo(transferUserModel: (UserModel) -> Unit) {
        currentUserInfoHelper.getCurrentUserInfo { transferUserModel(it) }
    }

    fun getOtherUsersInfoFromChatsRegister(transferUserList: (List<UserModel>) -> Unit) {
        otherUserInfoHelper.getOtherUsersInfoFromChatsRegister { mapUsers ->
            transferUserList(ArrayList(mapUsers.values))
            for(user in mapUsers){
                chatHelper.getLastMessage(user.key){ messageModel ->
                    mapUsers[messageModel.senderId]?.lastMessage = messageModel.message
                    transferUserList(ArrayList(mapUsers.values))
                }
            }
        }
    }

    fun loadProfileImage(imageUri: Uri, contentResolver: ContentResolver){
        currentUserInfoHelper.loadProfileImage(imageUri, contentResolver)
    }

    fun changeUsername(username: String){
        currentUserInfoHelper.editUsername(username)
    }

    fun changeUserkey(username: String, responseStatus: (CommonStatus) -> Unit){
        currentUserInfoHelper.editUserKey(username, responseStatus)
    }

    fun findUser(userKey: String, transferUserId: (String?) -> Unit){
        otherUserInfoHelper.findOtherUser(userKey){ userId ->
            userId?.let { chatHelper.createChat(userId)}
            transferUserId(userId)
        }
    }

    fun getOtherUserInfo(userId: String, getUserModel: (UserModel?) -> Unit){
        otherUserInfoHelper.getOtherUserInfo(userId, getUserModel)
    }

    fun getMessagesFromChat(id: String, transferMessages: (ArrayList<MessageModel>) -> Unit){
        chatHelper.getMessagesFromChat(id, transferMessages)
    }

    fun findChatById(otherUserId: String, resultChatId: (String) -> Unit){
        chatHelper.findChatById(otherUserId, resultChatId)
    }

    fun setStatus(status: Status){
        userStatusHelper.setStatus(status)
    }

    fun getStatus(otherUserId: String, otherUserStatus: (Status) -> Unit){
        userStatusHelper.getStatus(otherUserId, otherUserStatus)
    }
}