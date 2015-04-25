package com.attribes.incommon.api.transport;

/**
 *
 * User: amateen
 * Date: 5/29/13
 * Time: 1:48 PM
 * Network Action Interface, Activities has to implement this function if they want to get the network
 */
public interface NetworkAction {

       public <T> void onResponse(int statusCode, T data);



}
