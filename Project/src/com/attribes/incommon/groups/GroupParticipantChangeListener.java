package com.attribes.incommon.groups;

import com.quickblox.users.model.QBUser;

import java.util.ArrayList;

/**
 * Created by Sabih Ahmed on 03-Jul-15.
 */
public interface GroupParticipantChangeListener {

    void OnParticipantChange(ArrayList<Integer> participantAddList, ArrayList<Integer> participantRemoveList,
                             QBUser participantAddedNames, QBUser participantRemovedNames);
}
