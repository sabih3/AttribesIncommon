package com.attribes.incommon;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.attribes.incommon.adapters.MessageAdapter;
import com.attribes.incommon.util.Constants;
import com.attribes.incommon.util.CustomPreferences;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.request.QBPagedRequestBuilder;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

public class MessageFragment extends Fragment {

	private String content = "";
	private QBChatService chatService;
	private View view;
	private ArrayList<Integer> userIds = new ArrayList<Integer>();
	private TextView messagesCount;
	private int count;
	private QBDialog dialogs;
	ArrayList<QBDialog> filteredDialogs;
	ArrayList<QBDialog> qbDialogs;
	ArrayList<QBUser> qbUser;
	private static String keyPrefsForMessages = "messages";
	private static final String keyPrefsForCount = "count";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_messages, null);
		messagesCount = (TextView) view
				.findViewById(R.id.messageFragment_countText);

		messagesCount.setTypeface(setCustomFont(Constants.FONT_PROXI_REGULAR));
		messagesCount.setTextColor(getResources().getColor(R.color.black_font));
		count = 0;

		if (savedInstanceState != null) {
			CustomPreferences customPrefs = new CustomPreferences(getActivity());
			qbDialogs= customPrefs
					.getObjectListFromPrefs(keyPrefsForMessages,QBDialog.class);
			if (qbDialogs != null) {
				userIds = customPrefs.getObjectListFromPrefs("userids", Integer.class);
				
				qbUser = customPrefs.getObjectListFromPrefs("qbuser", QBUser.class);
				ChatHandler.getInstance().setDialogUsers(qbUser);
				setCount(savedInstanceState.getInt(keyPrefsForCount));
				setList(qbDialogs);
			}

		}

		else {
			getAllMessages();
		}

		return view;
	}

	public Typeface setCustomFont(String fontName) {
		Typeface custom_font = Typeface.createFromAsset(getActivity()
				.getAssets(), "fonts/" + fontName);

		return custom_font;
	}

	private void getAllMessages() {
		getChatDialogsPrivate(getActivity());

	}

	private void getChatDialogsPrivate(Context context) {

		if (!QBChatService.isInitialized()) {
			QBChatService.init(context);
			chatService = QBChatService.getInstance();

		}

		QBRequestGetBuilder requestBuilder = new QBRequestGetBuilder();
		requestBuilder.setPagesLimit(100);
		requestBuilder.sortDesc("last_message_date_sent");

		QBChatService.getChatDialogs(QBDialogType.PRIVATE, requestBuilder,
				new QBEntityCallbackImpl<ArrayList<QBDialog>>() {
					@Override
					public void onSuccess(ArrayList<QBDialog> dialogs,
							Bundle args) {
						qbDialogs = dialogs;
						userIds = fetchUserIds(dialogs);
						fetchUsersByIds(userIds, dialogs);
						setCount(count);

					}

					@Override
					public void onError(List<String> errors) {

					}
				});

	}

	private void setCount(int count) {
		if (count == 1) {
			messagesCount.setTextColor(getResources().getColor(
					R.color.burnt_orange));
			messagesCount.setText(count + " new message");
		}

		else if (count > 1) {
			messagesCount.setTextColor(getResources().getColor(
					R.color.burnt_orange));
			messagesCount.setText(count + " new messages");
		}

		else {
			messagesCount.setTextColor(getResources().getColor(
					R.color.black_font));
			messagesCount.setText("No new messages");
		}

	}

	private void fetchUsersByIds(List<Integer> userIds,
			final ArrayList<QBDialog> dialogs) {
		QBPagedRequestBuilder requestBuilder = new QBPagedRequestBuilder();
		requestBuilder.setPage(1);
		requestBuilder.setPerPage(userIds.size());
		requestBuilder.addParameter("sort_desc", "last_message_date_sent");
		//
		QBUsers.getUsersByIDs(userIds, requestBuilder,
				new QBEntityCallbackImpl<ArrayList<QBUser>>() {
					@Override
					public void onSuccess(ArrayList<QBUser> users, Bundle params) {

						// Save users
						//
						qbUser = users;
						ChatHandler.getInstance().setDialogUsers(users);

						// build list view
						//

						setList(dialogs);
					}

					@Override
					public void onError(List<String> errors) {
						AlertDialog.Builder dialog = new AlertDialog.Builder(
								getActivity());
						dialog.setMessage("get occupants errors: " + errors)
								.create().show();
					}

				});

	}

	private ArrayList<Integer> fetchUserIds(ArrayList<QBDialog> dialogs) {
		ArrayList<Integer> userIDs = new ArrayList<Integer>();
		for (QBDialog dialog : dialogs) {
			if (dialog.getUnreadMessageCount() != 0) {
				count++;
			}
			userIDs.addAll(dialog.getOccupants());
		}
		return userIDs;

	}

	private void setList(ArrayList<QBDialog> dialogs) {
		int size = dialogs.size();
		filteredDialogs = new ArrayList<QBDialog>();
		for (int i = 0; i < size; i++) {
			if (!(dialogs.get(i).getLastMessage() == null)) {

				filteredDialogs.add(dialogs.get(i));

			}

		}
		ListView messagesList = (ListView) view
				.findViewById(R.id.fragment_messages_list);
		ListAdapter messagesListAdapter = new MessageAdapter(getActivity(),
				filteredDialogs);
		messagesList.setAdapter(messagesListAdapter);

	}

	public static MessageFragment newInstance() {

		MessageFragment messageFragment = new MessageFragment();

		return messageFragment;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		saveInstance(outState);
		super.onSaveInstanceState(outState);
	}

	private void saveInstance(Bundle outState) {
		CustomPreferences customPrefs = new CustomPreferences(getActivity());
		customPrefs.putObjectListInPrefs(qbDialogs, keyPrefsForMessages);
		customPrefs.putObjectListInPrefs(userIds, "userids");
		customPrefs.putObjectListInPrefs(qbUser, "qbuser");
		outState.putInt(keyPrefsForCount, count);
	}

}
