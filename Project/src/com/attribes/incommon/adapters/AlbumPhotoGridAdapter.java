package com.attribes.incommon.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.androidquery.AQuery;
import com.attribes.incommon.AlbumImageChooserScreen;
import com.attribes.incommon.ImageVO;
import com.attribes.incommon.R;
import com.squareup.picasso.Picasso;

public class AlbumPhotoGridAdapter extends BaseAdapter{

	Context context;
	ArrayList<ImageVO> albumImagesArray = new ArrayList<ImageVO>();
	ArrayList<String> googleImages ;
	boolean isFaceBookUserRegistered = false, isEditing;
	
	AQuery aq;
	public AlbumPhotoGridAdapter(Context context, ArrayList<ImageVO> albumImagesArray,boolean isFaceBookUserRegistered,boolean isEditing) {
		this.albumImagesArray = albumImagesArray;
		this.context = context;
		this.isEditing = isEditing;
		this.isFaceBookUserRegistered = isFaceBookUserRegistered;
	}

	public AlbumPhotoGridAdapter(Context context, ArrayList<String> albumImagesArray,boolean isEditing) {
		this.googleImages = albumImagesArray;
		this.context = context;
		this.isEditing = isEditing;
		this.isFaceBookUserRegistered = isFaceBookUserRegistered;
	}
	
	@Override
	public int getCount() {
		if(isFaceBookUserRegistered){
		return albumImagesArray.size();
		}
		else{
			return googleImages.size();
		}
	}

	@Override
	public Object getItem(int position) {
		if(isFaceBookUserRegistered){
		return albumImagesArray.get(position);
		}
		else{
			return googleImages.get(position);
		}
	}

	@Override
	public long getItemId(int position) {
		
		return position;
	}

	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		aq = new AQuery(context);
		//LayoutInflater inflater = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
		//view = inflater.inflate(R.layout.album_photo_item_grid, null);
		//ImageView photo=(ImageView) view.findViewById(R.id.albumPhotoGridItem_image);
		ImageView imageView;
		if(view==null){
			imageView=new ImageView(context);
		}
		else{
			imageView=(ImageView)view;
		}
		if(isFaceBookUserRegistered){
		Picasso.with(context).load(albumImagesArray.get(position).getSource())
		.placeholder(R.drawable.placeholder)
		.resize(300, 300)
		.centerCrop()
		.noFade()
		.into(imageView);
		}
		else{
			
			Picasso.with(context).load(googleImages.get(position))
			.placeholder(R.drawable.placeholder)
			.resize(300, 300)
			.centerCrop()
			.noFade()
			.into(imageView);
			
		}
		imageView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(isFaceBookUserRegistered){
				Intent intent = new Intent(context, AlbumImageChooserScreen.class);
				intent.putExtra("imageUri", albumImagesArray.get(position).getSource());
				if(isEditing){
					intent.putExtra("is_edit_mode", true);
				}
				context.startActivity(intent);
				}
				else{
					
					Intent intent = new Intent(context, AlbumImageChooserScreen.class);
					intent.putExtra("imageUri", googleImages.get(position));
					if(isEditing){
						intent.putExtra("is_edit_mode", true);
					}
					context.startActivity(intent);
				}
				
				
				
			}
		});
		return imageView;
	}

}
