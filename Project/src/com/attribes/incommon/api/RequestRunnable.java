package com.attribes.incommon.api;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: amateen
 * Date: 5/29/13
 * Time: 6:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class RequestRunnable<T extends GenericRequest> implements Runnable {

    private T req;


    public RequestRunnable(T req) {
          this.req = req;


    }



    @Override
    public void run() {
        try {

            WebClient.getInstance().doRequest(req);


        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
