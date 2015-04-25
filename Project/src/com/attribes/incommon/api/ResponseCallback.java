package com.attribes.incommon.api;

/**
 * Created with IntelliJ IDEA.
 * User: amateen
 * Date: 5/29/13
 * Time: 6:33 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ResponseCallback<T> {

    public void onSuccess(T data);

    public void onFailure(String errorMessage, ErrorType errorType);

    public static enum ErrorType{

        NETWORK_COMMUNICATION_ERROR,
        APPLICATION_EXCEPTION,
        UNKNOWN_ERROR;
    }

}
