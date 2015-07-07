package com.attribes.incommon.groups;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.androidquery.AQuery;
import com.attribes.incommon.BaseActivity;
import com.attribes.incommon.DrawerScreen;
import com.attribes.incommon.R;
import com.attribes.incommon.adapters.MessageAdapter;
import com.attribes.incommon.util.Constants;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.request.QBRequestGetBuilder;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class GroupMainScreen extends DrawerScreen implements ListView.OnItemClickListener, OnGroupDialogDeleted{
	
	private AQuery mAquery;
    private ArrayList<QBDialog> dialogList;
    private ListView listDialogs;
    private TextView newGroupText;
    private ProgressBar progressBar;
    private GroupDialogAdapter adapter;
    private DrawerLayout mDrawer;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        ViewGroup content=(ViewGroup) findViewById(R.id.frame_container);
        getLayoutInflater().inflate(R.layout.activity_group_main, content, true);

        mDrawer = (DrawerLayout) findViewById(R.id.connect_drawer_layout);
        mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
		//setContentView(R.layout.activity_group_main);

		initContents();
        setActionBarStyling();

	}

	private void initContents() {
		mAquery = new AQuery(this);


        listDialogs = (ListView) findViewById(R.id.groupMainScreen_list);
        newGroupText = (TextView)findViewById(R.id.groupMainScreen_greetingText);
        newGroupText.setTypeface(setCustomFont(Constants.FONT_PROXI_LIGHT));
		mAquery.id(R.id.groupMainScreen_greetingText).typeface(setCustomFont(Constants.FONT_PROXI_REGULAR));

        GroupChatScreen groupChat=new GroupChatScreen();
        groupChat.setOnGroupDialogDeleted(this);
        getDialogs();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.group_main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		
		int id=item.getItemId();
		
		if(id == R.id.action_createGroup){
			Intent intent=new Intent(this,GroupCreateScreen.class);
			startActivity(intent);
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}


    private void getDialogs(){
        progressBar =(ProgressBar)findViewById(R.id.groupMainScreen_progress);
        progressBar.setVisibility(View.VISIBLE);
        QBRequestGetBuilder requestBuilder = new QBRequestGetBuilder();
        requestBuilder.setPagesLimit(100);
        requestBuilder.sortDesc("last_message_date_sent");

        QBChatService.getChatDialogs(QBDialogType.GROUP, requestBuilder,
                new QBEntityCallbackImpl<ArrayList<QBDialog>>() {
                    @Override
                    public void onSuccess(ArrayList<QBDialog> dialogs,
                                          Bundle args) {

                        dialogList = dialogs;
                        updateUI(dialogList);
                    }

                    @Override
                    public void onError(List<String> errors) {

                    }
                });

    }

    private void updateUI(ArrayList<QBDialog> dialogList) {
        initializeDrawer();
        getActionBar().setDisplayHomeAsUpEnabled(true);
        if(dialogList.isEmpty()){
            newGroupText.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            listDialogs.setVisibility(View.GONE);

        }

        else{
            progressBar.setVisibility(View.GONE);
            adapter=new GroupDialogAdapter(this,dialogList);
            listDialogs.setAdapter(adapter);
            newGroupText.setVisibility(View.GONE);
            listDialogs.setOnItemClickListener(this);

        }
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

        showGroupChatScreen(dialogList.get(position));
    }

    private void showGroupChatScreen(QBDialog qbDialog) {

        Intent intent = new Intent(this,GroupChatScreen.class);

        intent.putExtra(Constants.EXTRA_QBDIALOG,qbDialog);
        startActivity(intent);
        overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_in);
    }

    @Override
    public void OnGroupDialogDelete() {
        getDialogs();
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        initContents();
//    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initContents();
    }
}
