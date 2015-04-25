package com.attribes.incommon;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class AlbumImageChooserScreen extends BaseActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_album_chooser_screen);
		
		setActionBarStyling();
		
		final String imageUri= getIntent().getStringExtra("imageUri");
		ImageView image=(ImageView)findViewById(R.id.albumChooserScreen_image);
		Picasso.with(context).load(imageUri).placeholder(R.drawable.placeholder).into(image);
		
		Button chooseButton = (Button)findViewById(R.id.albumChooserScreen_chooseButton);
		chooseButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				
				setUserImageUri(imageUri);
				Intent intent = new Intent(getApplicationContext(), CreateProfileScreen.class);
				if(getIntent().getBooleanExtra("is_edit_mode", false)){
					intent.putExtra("is_edit_mode", true);
				}
				startActivity(intent);
				
			}
		});
		
		Button cancelButton=(Button)findViewById(R.id.albumChooserScreen_cancelButton);
		cancelButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
				
			}
		});
		
	}
}
