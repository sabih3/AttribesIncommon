package com.attribes.incommon.groups;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
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
public class ChooseInterestScreen extends ActionBarActivity implements SearchView.OnQueryTextListener{

    private ArrayList<InterestsModel.Response> interestList;
    private InterestLayout interestLayout;
    private Typeface font_ProxiLight;
    private static OnInterestSelectedListener onInterestSelectedListener;
    private SearchView searchView;


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
        setActionBarStyling();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_friends,menu);
        searchView = (SearchView) menu.findItem(R.id.friend_search).getActionView();

        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(this);

        int id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView textView = (TextView) searchView.findViewById(id);
        textView.setTextColor(Color.GRAY);
        textView.setHintTextColor(Color.GRAY);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);

    }

    @Override
    public boolean onQueryTextSubmit(String searchQuery) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String searchQuery) {

        ArrayList<InterestsModel.Response> filteredList=new ArrayList<>();

        if(!searchQuery.isEmpty()){

                for(InterestsModel.Response response : interestList){

                    if(response.toString().toLowerCase().startsWith(searchQuery)){
                        filteredList.add(response);
                        populateInterest(filteredList);
                    }
                }
            }

        else{
            populateInterest(interestList);
        }


        return false;
    }

    private void setActionBarStyling() {
        ActionBar bar = getSupportActionBar();


        bar.setBackgroundDrawable(getResources().getDrawable(
                R.drawable.ab_solid_background));
//		bar.setHomeAsUpIndicator(R.drawable.ic_ab_back_holo_light);
        bar.setIcon(R.drawable.logo);
        bar.setLogo(R.drawable.logo);
//		bar.setDisplayUseLogoEnabled(true);
//		bar.setDisplayShowHomeEnabled(true);
//
//		bar.setDisplayHomeAsUpEnabled(true);
        int actionBarTitleId = Resources.getSystem().getIdentifier(
                "action_bar_title", "id", "android");
        if (actionBarTitleId > 0) {
            TextView title = (TextView) findViewById(actionBarTitleId);
            if (title != null) {
                setCustomFont(title, Constants.FONT_PROXI_REGULAR);
                title.setTextSize(20);

                title.setTextColor(getResources().getColor(R.color.actionBar));

            }
        }

    }

    public void setCustomFont(TextView textView, String fontName){
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/" + fontName);
        if(textView!=null)
            textView.setTypeface(custom_font);
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