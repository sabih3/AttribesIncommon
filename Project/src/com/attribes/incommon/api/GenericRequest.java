package com.attribes.incommon.api;

import java.net.HttpURLConnection;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import com.attribes.incommon.api.transport.NetworkAction;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

public abstract class GenericRequest implements NetworkAction {

    /**
     * Using raw type here to avoid generic  problem,
     * though raw types are bad practices,because compiler does not ensure the type safety but we are already putting
     * type safety in code that extends this class.
     */
    protected ResponseCallback responseCallback;
    protected String imagePath;
    private static final String TAG = "GenericRequest";
    private RequestMethod requestMethod = RequestMethod.GET;        //GET request is default

    public<T extends ResponseCallback> void setResponseCallback(T responseCallback) {
        this.responseCallback = responseCallback;
    }

    /**
     * Override this method if token is NOT required by the request.
     *
     * @return
     */
    public boolean isTokenRequired() {
        return true;
    }

    /**
     * All child classes which have a different base url that to described in WebClient.java will override this function.
     *
     * @return BaseUrl
     */
    public String getBaseUrl() {

        return null;
    }

    abstract public String getEndPoint();

    abstract public List<NameValuePair> getUrlParamMap();

    abstract public String toJson();

    protected void setRequestToPost() {
        requestMethod = RequestMethod.POST;
    }
    protected void setRequestToMultiPart() {
        requestMethod = RequestMethod.MULTI_PART;
    }

    protected void setRequestToGet() {
        requestMethod = RequestMethod.GET;
    }

    public RequestMethod getRequestMethod() {
        return requestMethod;
    }

//    public NameValuePair getTokenParam() {
//        NameValuePair value = new BasicNameValuePair(WebserviceElement.AUTH.getKeyName(), Session.INSTANCE.getAuthToken());
//        return value;
//    }

    public String getUrl() {

        String url = getEndPoint();
        List<NameValuePair> urlParamMap = getUrlParamMap();
//        if (isTokenRequired()) {
//            urlParamMap.add(getTokenParam());
//        }
        String encodedUrl = "";
        if (urlParamMap != null) {
            encodedUrl = URLEncodedUtils.format(urlParamMap, WebClient.CHARSET);
        }
        url = (encodedUrl == null || encodedUrl.equals("")) ? url : (url + "?" + encodedUrl);
        return url;
    }

    public <T> void notifyCallbackSuccess(T data){
        responseCallback.onSuccess(data);
    }

    public void notifyCallbackFailure(String message, ResponseCallback.ErrorType errorType){
        responseCallback.onFailure(message,errorType);
    }

    /**
     * This method will call onSuccess and onFailure methods depending on
     * HTTP status code and JSON reply status code.
     *
     * @param statusCode
     * @param data
     */
    @Override
    public <T> void onResponse(int statusCode, T data) {


        if (statusCode == HttpURLConnection.HTTP_OK) {
            //special check to prevent data being null and being passed to corresponding requests
            if (data == null){
                notifyCallbackFailure("Error", ResponseCallback.ErrorType.UNKNOWN_ERROR);

            }
            else if (data instanceof String) {
                String strData = (String) data;
                if (isJson(strData)) {
                    JsonObject object = new JsonParser().parse(strData).getAsJsonObject();
                    Gson gson = new GsonBuilder().create();


                    if (statusCode == 200){
                        notifyCallbackSuccess(data);
                    } else {

                        notifyCallbackFailure("Error", ResponseCallback.ErrorType.NETWORK_COMMUNICATION_ERROR);
                    }
                } else { //data is not json so it has to be an image/file currently.
                    notifyCallbackSuccess(data);
                }

            } else {
                notifyCallbackSuccess(data);
            }
        } else {
            notifyCallbackFailure("Error", ResponseCallback.ErrorType.NETWORK_COMMUNICATION_ERROR);
        }
    }

    private boolean isJson(String data) {
        try {
            new JsonParser().parse(data);
        } catch (JsonParseException e) {
            return false;
        }

        return true;

    }

    /**
     * the function will be called after this request has been dispatched to the executor service.
     */
    public void onDispatched(){

    }

    /**
     * Usually it will be required except for login request and image request.
     * @return
     */
    public boolean isCsrfTokenRequired(){
        return true;
    }

    public String getImageUploader(){
        return imagePath;
    }
    public void setImageUploader(String imagePath){
        this.imagePath = imagePath;
    }

    public static enum RequestMethod {
        POST("post"), GET("get"), MULTI_PART("multipart");
        private String methodName;

        private RequestMethod(String methodName) {
            this.methodName = methodName;
        }

        public String getMethodName() {
            return methodName;
        }
    }
}
