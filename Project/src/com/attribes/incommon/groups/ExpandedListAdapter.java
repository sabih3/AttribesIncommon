package com.attribes.incommon.groups;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ListAdapter;
import com.attribes.incommon.R;
import com.devsmart.android.ui.HorizontalListView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Sabih Ahmed on 22-Jul-15.
 */
public class ExpandedListAdapter extends BaseExpandableListAdapter {


    private final Context context;
    private HashMap<String, ArrayList<String>> childData;
    private ArrayList<String> headerList;
    private HorizontalListAdapter imageAdapter;

    public ExpandedListAdapter(Context context, ArrayList <String> headerList,
                               HashMap<String,ArrayList<String>> childData ){

        this.context = context;
        this.headerList = headerList;
        this.childData = childData;
    }

    @Override
    public int getGroupCount() {
        return this.headerList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.childData.get(this.headerList.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.headerList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.childData.get(this.headerList.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int position) {
        return position;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.expandable_row_header,null);
        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        imageAdapter=new HorizontalListAdapter(context, childData.get(""));


        if(convertView==null){
            LayoutInflater inflater= (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.expandable_row,null);
            HorizontalListView horizontalList= (HorizontalListView)convertView.findViewById(R.id.createGroup_list);

            horizontalList.setAdapter(imageAdapter);
        }


        return convertView;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }
}
