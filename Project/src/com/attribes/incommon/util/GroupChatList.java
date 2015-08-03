package com.attribes.incommon.util;

import com.attribes.incommon.models.FriendAllResponse;

import java.util.ArrayList;

/**
 * Created by Sabih Ahmed on 17-Jun-15.
 */
public class GroupChatList {

    private static ArrayList<FriendAllResponse.Response> groupChatList;
    private static GroupChatList mInstance;
    private Boolean QBSessionFlag = false;

    private GroupChatList(){

    }

    public static GroupChatList getInstance(){
        if(mInstance == null){

            mInstance=new GroupChatList();
            groupChatList = new ArrayList<>();
        }

        return mInstance;
    }

    public ArrayList<FriendAllResponse.Response> getList(){

        return this.groupChatList;
    }

    public void addOccupant(FriendAllResponse.Response friend){
        this.groupChatList.add(friend);
    }

    public void removeOccupant(int position){

        this.groupChatList.remove(position);
    }

    public void removeOccupant(FriendAllResponse.Response friendObject){
        this.groupChatList.remove(friendObject);
    }


    public Boolean getQBSessionFlag() {
        return QBSessionFlag;
    }

    public void setQBSessionFlag(Boolean QBSessionFlag) {
        this.QBSessionFlag = QBSessionFlag;
    }
}
