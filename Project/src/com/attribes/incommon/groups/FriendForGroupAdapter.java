package com.attribes.incommon.groups;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Service;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.attribes.incommon.R;
import com.attribes.incommon.interfaces.OnOpponentSelectedListener;
import com.attribes.incommon.models.FriendAllResponse.Response;
import com.attribes.incommon.util.GroupChatList;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

public class FriendForGroupAdapter extends BaseAdapter{

	private Activity mActivity;
	private ArrayList<Response> friendList;

    private OnOpponentSelectedListener opponentSelectionListener;

    public void setOnOpponentSelectedListener(OnOpponentSelectedListener opponentSelectionListener){

        this.opponentSelectionListener = opponentSelectionListener;
    }
	
	public FriendForGroupAdapter(Activity activity, ArrayList<Response> friendList) {
		this.mActivity = activity;
		this.friendList = friendList;
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
	public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
		if(convertView == null ){
			LayoutInflater inflater=(LayoutInflater) mActivity.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.group_friend_row_item, null);
			
			viewHolder = new ViewHolder();
			viewHolder.imageView = (CircularImageView) convertView.findViewById(R.id.group_friend_image);
			viewHolder.textView = (TextView) convertView.findViewById(R.id.group_friend_name);
		    viewHolder.checkBox = (CheckBox)convertView.findViewById(R.id.group_friend_checkBox);

            viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                    if (compoundButton.isChecked()) {

                        GroupChatList.getInstance().addOccupant(friendList.get(position));
                        friendList.get(position).setSelected(true);
                        opponentSelectionListener.OnOpponentAdded(position);

                    } else {

                        friendList.get(position).setSelected(false);
                        GroupChatList.getInstance().removeOccupant(friendList.get(position));
                        opponentSelectionListener.OnOpponentRemoved(position);
                    }
                }
            });


            convertView.setTag(viewHolder);


		} else {
            viewHolder= (ViewHolder) convertView.getTag();
            viewHolder.checkBox.setChecked(friendList.get(position).isSelected());

		}


        Picasso.with(mActivity).load(friendList.get(position).image_uri).into(viewHolder.imageView);
        viewHolder.textView.setText(friendList.get(position).name);


		return convertView;
	}
	
	private static class ViewHolder{
		CircularImageView imageView;
		TextView textView;
        CheckBox checkBox;
		
	}

}
