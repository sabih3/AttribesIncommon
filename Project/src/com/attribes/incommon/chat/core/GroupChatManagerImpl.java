package com.attribes.incommon.chat.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import com.attribes.incommon.ChatScreen;
import com.attribes.incommon.groups.GroupDialogUpdateListener;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBRequestUpdateBuilder;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.DiscussionHistory;

import android.util.Log;
import android.widget.Toast;

import com.attribes.incommon.groups.GroupChatScreen;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBGroupChat;
import com.quickblox.chat.QBGroupChatManager;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBMessageListenerImpl;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBEntityCallbackImpl;

public class GroupChatManagerImpl extends QBMessageListenerImpl<QBGroupChat> implements ChatManager{


    private GroupChatScreen groupChatActivity;
    private static QBGroupChatManager groupChatManager;
    private static QBGroupChat groupChat;
    private ChatScreen chatScreen;
    private Context mContext;
    private QBDialog updatedDialog;
    private GroupDialogUpdateListener groupDialogUpdateListener;

    public GroupChatManagerImpl(GroupChatScreen groupChatActivity){
        this.groupChatActivity = groupChatActivity;
        if(!QBChatService.isInitialized()){
            QBChatService.init(groupChatActivity);
        }
        groupChatManager = QBChatService.getInstance().getGroupChatManager();
    }

    public GroupChatManagerImpl(Context context){
        this.mContext =context;
        if(!QBChatService.isInitialized()){
            QBChatService.init(mContext);
        }
        groupChatManager = QBChatService.getInstance().getGroupChatManager();
    }

    public GroupChatManagerImpl(ChatScreen chatActivity){
        this.chatScreen = chatActivity;
        groupChatManager = QBChatService.getInstance().getGroupChatManager();
    }

    public void setGroupDialogUpdateListener(GroupDialogUpdateListener groupDialogUpdateListener){

        this.groupDialogUpdateListener = groupDialogUpdateListener;
    }

    public void joinGroupChat(QBDialog dialog,QBEntityCallback callback){
        groupChatManager = QBChatService.getInstance().getGroupChatManager();
        groupChat = groupChatManager.createGroupChat(dialog.getRoomJid());
        join(groupChat, callback);
    }

    public void join(QBGroupChat groupChat, QBEntityCallback callback) {
        DiscussionHistory history = new DiscussionHistory();
        history.setMaxStanzas(0);


        groupChat.join(history, new QBEntityCallbackImpl() {


            @Override
            public void onSuccess() {

                groupChat.addMessageListener(GroupChatManagerImpl.this);

                groupChatActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callback.onSuccess();

                        Toast.makeText(groupChatActivity, "Join successful", Toast.LENGTH_LONG).show();
                    }
                });

            }

            @Override
            public void onError(List list) {

                groupChatActivity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        callback.onError(list);
                    }
                });
            }
        });
    }

    @Override
    public void sendMessage(QBChatMessage message) throws XMPPException, SmackException.NotConnectedException {

        if (groupChat != null) {
            try {
                groupChat.sendMessage(message);
            } catch (SmackException.NotConnectedException nce){
                nce.printStackTrace();
            } catch (IllegalStateException e){
                e.printStackTrace();

                Toast.makeText(groupChatActivity, "You are still joining a group chat, please white a bit", Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(groupChatActivity, "Join unsuccessful", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void release() throws XMPPException {

        if(groupChat !=null){
            try {
                groupChat.leave();
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }

            groupChat.removeMessageListener(this);
        }
    }

    @Override
    public void processMessage(QBGroupChat groupChat, QBChatMessage message) {
        if(!message.getSenderId().equals(message.getRecipientId())){
            groupChatActivity.showMessage(message);
        }

    }

    public void updateGroupChatOccupants(QBDialog dialog, ArrayList<Integer> participantAddList,
                                         ArrayList<Integer> participantRemoveList,QBEntityCallbackImpl callback){

        ArrayList<String> addParticipantStringList = new ArrayList<>();
        ArrayList<String> removeParticipantStringList = new ArrayList<>();

        String addParticipants = "";
        String removeParticipants = "";
        QBRequestUpdateBuilder requestBuilder = new QBRequestUpdateBuilder();

        if(participantRemoveList.size() >= 1){

            for(Integer i : participantRemoveList){
                removeParticipantStringList.add(Integer.toString(i));
            }

            removeParticipants = TextUtils.join(",",removeParticipantStringList);
            requestBuilder.pullAll("occupants_ids", removeParticipants);


            groupChatManager.updateDialog(dialog, requestBuilder, new QBEntityCallback<QBDialog>() {

                @Override
                public void onSuccess(QBDialog dialog, Bundle bundle) {
                    updatedDialog = dialog;
                    groupDialogUpdateListener.OnGroupDialogUpdated(dialog);
                    //callback.onSuccess();
                }

                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(List<String> list) {

                }
            });
        }
        if(participantAddList.size() >= 1) {


            for(Integer i : participantAddList){
                addParticipantStringList.add(Integer.toString(i));
            }

            addParticipants= TextUtils.join(",",addParticipantStringList);
            requestBuilder.push("occupants_ids",addParticipants);



            groupChatManager.updateDialog(dialog, requestBuilder, new QBEntityCallback<QBDialog>() {

                @Override
                public void onSuccess(QBDialog dialog, Bundle bundle) {
                    updatedDialog = dialog;
                    groupDialogUpdateListener.OnGroupDialogUpdated(dialog);
                    //callback.onSuccess();
                }

                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(List<String> list) {

                }
            });

        }






    }

    public void updateGroupChatDialog(QBDialog dialog, String newGroupName, QBEntityCallbackImpl callback){
        QBRequestUpdateBuilder requestBuilder = new QBRequestUpdateBuilder();

        requestBuilder.addParameter("name",newGroupName);


        groupChatManager.updateDialog(dialog, requestBuilder, new QBEntityCallbackImpl<QBDialog>() {

            @Override
            public void onSuccess(QBDialog dialog, Bundle bundle) {
                callback.onSuccess();
            }

            @Override
            public void onError(List<String> list) {

            }
        });
    }

    public void deleteDialog(String dialogId, QBEntityCallbackImpl callback){
        groupChatManager.deleteDialog(dialogId, new QBEntityCallback<Void>() {

            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {

            }

            @Override
            public void onSuccess() {
            callback.onSuccess();
            }

            @Override
            public void onError(List<String> list) {

            }
        });

    }


}
