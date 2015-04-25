package com.attribes.incommon;

public class ImageVO {

	int height;
	int width;
	String source;
	
	public ImageVO(int height,int width,String source){
		
		this.height = height;
		this.width = width;
		this.source = source;
	}
	
	public int getHeight() {
		
		return height;
	}
	
	public void setHeight(int height) {
		
		this.height = height;
	}
	
	public int getWidth() {
		
		return width;
	}
	
	public void setWidth(int width) {
		
		this.width = width;
	}
	
	public String getSource() {
		
		return source;
	}
	
	public void setSource(String source) {
		
		this.source = source;
	}
	
	
}
