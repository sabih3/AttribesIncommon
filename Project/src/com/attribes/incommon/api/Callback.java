package com.attribes.incommon.api;

public interface Callback<T> {
	public void onSuccess(T a);
	public void onFailure(String errorMesg);
}
