package com.attribes.incommon.groups;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.attribes.incommon.R;
import com.devsmart.android.ui.HorizontalListView;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Sabih Ahmed on 27-Jun-15.
 */
public class HorizontalListAdapter extends BaseAdapter{

    private Context mContext;
    private ArrayList<String> imageUrlList;
    public HorizontalListAdapter(Context context, ArrayList<String> imageUrlList){

        this.mContext=context;
        this.imageUrlList=imageUrlList;

    }
    @Override
    public int getCount() {
        return imageUrlList.size();
    }

    @Override
    public Object getItem(int position) {
        return imageUrlList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        LayoutInflater inflater= (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        convertView = inflater.inflate(R.layout.create_group_list_item,null);


        CircularImageView imageView= (CircularImageView) convertView.findViewById(R.id.createGroup_image);
        Picasso.with(mContext).load(imageUrlList.get(position)).placeholder(R.drawable.human_place_holder)
                .into(imageView);

        return convertView;
    }
}
