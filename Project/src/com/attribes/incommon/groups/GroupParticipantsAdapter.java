package com.attribes.incommon.groups;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.attribes.incommon.R;
import com.attribes.incommon.models.FriendAllResponse;
import com.attribes.incommon.util.UserDevicePreferences;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.quickblox.chat.model.QBDialog;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Sabih Ahmed on 02-Jul-15.
 */
public class GroupParticipantsAdapter extends BaseAdapter {

    private final ArrayList<Integer> occupantIDs;
    private ArrayList<FriendAllResponse.Response> friendList;
    private ArrayList<Integer> participantAddList;
    private ArrayList<Integer> participantRemoveList;
    private GroupParticipantChangeListener groupParticipantChangeListener;
    private Context mContext;
    private QBDialog qbDialog;

    public void setGroupParticipantChangeListener(GroupParticipantChangeListener groupParticipantChangeListener){
        this.groupParticipantChangeListener = groupParticipantChangeListener;
    }

    public GroupParticipantsAdapter(Context context, ArrayList<FriendAllResponse.Response> friendList,
                                    ArrayList<Integer> occupants,
                                    QBDialog qbDialog) {
        this.mContext = context;
        this.friendList=friendList;
        this.occupantIDs=occupants;
        this.qbDialog = qbDialog;
        participantAddList = new ArrayList<>();
        participantRemoveList = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return friendList.size();
    }

    @Override
    public Object getItem(int position) {
        return friendList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        ViewHolder viewHolder;
        if(convertView == null){

            viewHolder=new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.group_detail_list_row, null);
            viewHolder = createViewHolder(convertView);
            setParticipantLogic(position,viewHolder);
            viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {


                @Override
                public void onClick(View view) {

                    boolean checked = ((CheckBox) view).isChecked();

                    if (checked == true) {
                        participantAddList.add(Integer.parseInt(friendList.get(position).qb_id));
                    }

                    else {

                            if(participantAddList.contains(Integer.parseInt(friendList.get(position).qb_id))){
                                Integer integerObject = Integer.parseInt(friendList.get(position).qb_id);
                                participantAddList.remove(integerObject);
                                ((CheckBox) view).setChecked(false);
                            }

                            participantRemoveList.add(Integer.parseInt(friendList.get(position).qb_id));

//                        else{
//                            //you are not owner of this group
//                            //notify user that he /she is not owner of this groups
//                            Toast.makeText(mContext,"Oops ! It seems you are not an owner of this group",Toast.LENGTH_SHORT).show();
//                            ((CheckBox) view).setChecked(true);
//                        }

                    }
                    groupParticipantChangeListener.OnParticipantChange(participantAddList, participantRemoveList);
                }
            });

            convertView.setTag(viewHolder);
        }

        else{
            viewHolder = (ViewHolder) convertView.getTag();

        }

        Picasso.with(mContext).load(friendList.get(position).image_uri).into(viewHolder.image);
        viewHolder.name.setText(friendList.get(position).name);



        return convertView;
    }

    private void setParticipantLogic(int position, ViewHolder viewHolder) {
        for(Integer id:occupantIDs){
            if(friendList.get(position).qb_id.equals(Integer.toString(id))){

                viewHolder.checkBox.setChecked(true);
                if(!qbDialog.getUserId().equals(Integer.parseInt(UserDevicePreferences.getInstance().getQbUserId()))){
                    //current user is not owner of this group
                    //it cant remove already added members, thats why checkbox is set to be disabled
                    viewHolder.checkBox.setEnabled(false);
                }
                return;
            }


        }

    }

    private ViewHolder createViewHolder(View view) {
        ViewHolder viewHolder =new ViewHolder();
        viewHolder.image = (CircularImageView) view.findViewById(R.id.group_detail_list_row_image);
        viewHolder.name = (TextView) view.findViewById(R.id.group_detail_list_row_name);
        viewHolder.checkBox =(CheckBox)view.findViewById(R.id.group_detail_list_row_checkBox);
        return viewHolder;
    }

    public static class ViewHolder{

        private CircularImageView image;
        private TextView name;
        private TextView status;
        private CheckBox checkBox;
    }


}
