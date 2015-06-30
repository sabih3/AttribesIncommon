package com.attribes.incommon.groups;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.attribes.incommon.R;
import com.attribes.incommon.models.InterestsModel;
import com.attribes.incommon.network.RestClient;
import com.attribes.incommon.util.Constants;
import com.attribes.incommon.util.UserDevicePreferences;
import com.attribes.incommon.views.InterestLayout;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.util.ArrayList;

/**
 * Created by Sabih Ahmed on 20-Jun-15.
 */
public class ChooseInterestScreen extends Activity {

    private ArrayList<InterestsModel.Response> interestList;
    private InterestLayout interestLayout;
    private Typeface font_ProxiLight;
    private static OnInterestSelectedListener onInterestSelectedListener;



    public ChooseInterestScreen() {

    }

    public void setOnInterestSelectedListener(OnInterestSelectedListener onInterestSelectedListener){

        this.onInterestSelectedListener=onInterestSelectedListener;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.groups_choose_interest);
        initContents();
        getInterests();
    }

    private void initContents() {
        interestLayout= (InterestLayout) findViewById(R.id.chooseInterest_list);
        font_ProxiLight=Typeface.createFromAsset(getAssets(),
                "fonts/"+Constants.FONT_PROXI_LIGHT);
    }

    private void getInterests() {
        RestClient.getAdapter().getAllInterests(Constants.AUTHORIZATION, UserDevicePreferences.getInstance().getSmToken(),
                new Callback<InterestsModel>() {
                    @Override
                    public void success(InterestsModel interestsModel, Response response) {
                        interestList = interestsModel.response;
                        populateInterest(interestList);

                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {

                    }
                });
    }

    private void populateInterest(ArrayList<InterestsModel.Response> interestList) {

        LayoutInflater inflater = getLayoutInflater();
        interestLayout.setVisibility(View.VISIBLE);
        interestLayout.removeAllViews();

        for (int i = 0; i < interestList.size(); i++) {

            TextView interest = (TextView) inflater.inflate(
                    R.layout.interest_txt_item, null);
            interest.setText(interestList.get(i).title);


            interest.setTextSize(14);


            interest.setTypeface(font_ProxiLight);
            interest.setTextSize(18);
            interest.setOnClickListener(new ChooseInterestClick());
            interestLayout.addView(interest);
        }

    }


    private void toggleTextColor(TextView tx) {
        if (tx.isSelected())
            tx.setTextColor(Color.WHITE);
        else
            tx.setTextColor(Color.BLACK);
    }

    private class ChooseInterestClick implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            TextView tv = (TextView) view;
            tv.setSelected(!tv.isSelected());
            toggleTextColor(tv);
            GroupInterest.GROUP_INTEREST = tv.getText().toString();
            onInterestSelectedListener.OnInterestSelected();
            showGroupSetupScreen();
        }

        private void showGroupSetupScreen() {

            finish();
        }
    }
}