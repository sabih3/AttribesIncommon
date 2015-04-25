package com.attribes.incommon.api;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.util.EntityUtils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class WebClient {

    public static final String BASE_URL_DEV_SERVER = "https://dev-app.kabuto-dev.com";
    public static final String BASE_URL_TEST_SERVER = "https://test-app.kabuto-dev.com";
    public static final String BASE_URL_PRODUCTION_SERVER = "https://app.kabuto.com";
    public static final String BASE_URL_STAGING_SERVER = "https://staging2-app.kabuto.com";
    public static final String CHARSET = "UTF-8";
    private static WebClient singleton = null;
    private SERVER_TYPE mServerType = SERVER_TYPE.DEV; //default type if not set

    private String TAG = "WebClient";

    private WebClient() {
       /*  if ( Session.getInstance().sessionExists()){
             //read from session on new reload
             mServerType = Session.getInstance().getmEnvironmentType();
         }*/
    }

    public static WebClient getInstance() {
        if (singleton == null) {
            singleton = new WebClient();
        }
        return singleton;
    }

    public void setServerType(SERVER_TYPE type) {
        this.mServerType = type;
        //Session.getInstance().setEnvironmentType(mServerType);
    }

    public SERVER_TYPE getServerType(){
        return mServerType;
    }

    private HttpClient sslClient(HttpClient client) {
        try {
            X509TrustManager tm = new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };
            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(null, new TrustManager[]{tm}, null);
            SSLSocketFactory ssf = new MySSLSocketFactory(ctx);
            ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            ClientConnectionManager ccm = client.getConnectionManager();

            SchemeRegistry sr = ccm.getSchemeRegistry();
            sr.register(new Scheme("https", ssf, 443));
            return new DefaultHttpClient(ccm, client.getParams());
        } catch (Exception ex) {
            return null;
        }
    }

    public void doRequest(GenericRequest request) throws IOException {
        if ( request.getRequestMethod() == GenericRequest.RequestMethod.GET) {
            get(request);
        }else if ( request.getRequestMethod() == GenericRequest.RequestMethod.POST){
            post(request);
        }

    }


    /**
     * @param -       url to hit
     * @param request - Request to hit
     * @param -       null if this is the login request, otherwise auth_request is
     *                required
     * @throws java.io.IOException
     * @throws org.apache.http.client.ClientProtocolException
     */
    private void post(GenericRequest request)
            throws IOException {
        BasicHttpParams connectionParams = new BasicHttpParams();
        //HttpConnectionParams.setConnectionTimeout(connectionParams,ApplicationConstants.NETWORK_REQUEST_CONNECTION_TIME_OUT_MILLI_SECOND);
        //HttpConnectionParams.setSoTimeout(connectionParams,ApplicationConstants.NETWORK_REQUEST_SOCKET_TIME_OUT_MILLI_SECOND);
        //ConnManagerParams.setTimeout(connectionParams, ApplicationConstants.NETWORK_REQUEST_CONNECTION_TIME_OUT_MILLI_SECOND);
        HttpClient client = sslClient(new DefaultHttpClient(connectionParams));
        String baseUrl = request.getBaseUrl();

        String url = baseUrl == null? mServerType.getUrl(): baseUrl;
        String boundary = "*****";
        // merge base url + request specific url

        HttpPost post = new HttpPost(url + request.getUrl());
        post.setEntity(new StringEntity(request.toJson(), CHARSET));

        /*
        String fileName = request.getImageUploader();
        if (fileName != null)
        {
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();

            File fileToUpload = new File(fileName);
            FileBody fileBody = new FileBody(fileToUpload);

            builder.addBinaryBody("files[]", fileToUpload, ContentType.create("image/jpeg"), fileName);

            post.setEntity(builder.build());


        }
        else
        {
               post.setEntity(new StringEntity(request.toJson(), CHARSET));
        }    */


        Log.d(TAG, url + request.getUrl());
        Log.d(TAG, request.toJson());
        HttpResponse res = null;
        ResponseCode responseCode = null;
        try{

            res = client.execute(post);

        }
        catch (ConnectTimeoutException e){
            Log.e(TAG,"post(): " + e.getMessage());
            responseCode = ResponseCode.ConnectionTimeout;
        }
        catch(SocketTimeoutException e){
            Log.e(TAG,"post(): " + e.getMessage());
            responseCode = ResponseCode.SocketTimeout;
        }
        catch(UnknownHostException e){
            Log.e(TAG,"post(): " + e.getMessage());
            responseCode = ResponseCode.UnknownHost;
        }
        catch(IOException e){
            Log.e(TAG, "post(): " + e.getMessage());
            responseCode = ResponseCode.NetworkFailure;
        }

        if (responseCode != null){
            request.notifyCallbackFailure(responseCode.name(), ResponseCallback.ErrorType.NETWORK_COMMUNICATION_ERROR);
            return;
        }

        if (res == null){
            Log.e(TAG,"post(): " + "Null response received.");
            request.notifyCallbackFailure(ResponseCode.NetworkFailure.name(), ResponseCallback.ErrorType.NETWORK_COMMUNICATION_ERROR );
            return;
        }
        Header contentTypeHeader = res.getFirstHeader("Content-Type");
        if (contentTypeHeader == null){
            Log.e(TAG,"post(): " + "Content-Type is null.");
            request.notifyCallbackFailure(ResponseCode.NetworkFailure.name(), ResponseCallback.ErrorType.NETWORK_COMMUNICATION_ERROR );
            return;
        }

        String contentType = contentTypeHeader.getValue();
        if (contentType.contains("image")){
            HttpEntity entity = res.getEntity();
            Bitmap image = BitmapFactory.decodeStream(
                    entity.getContent());
            request.onResponse(res.getStatusLine().getStatusCode(),image);
        }
        else{
            /*if (Session.getInstance().getWebCookie().getCookieStore() == null){
                DefaultHttpClient defaultHttpClient = (DefaultHttpClient)client;
                Session.getInstance().getWebCookie().setCookieStore(defaultHttpClient.getCookieStore());
                Session.getInstance().setXCsrfToken(Session.getInstance().getWebCookie().getCookieValueForKey(WebserviceElement.WEB_COOKIE_XCSRF_TOKEN.getKeyName()));
            }*/
            String response = EntityUtils.toString(res.getEntity());

            Log.e(TAG, response);

            request.onResponse(res.getStatusLine().getStatusCode(),response);
        }


    }


    private void get(GenericRequest request) throws IOException {

        BasicHttpParams connectionParams = new BasicHttpParams();
       // HttpConnectionParams.setConnectionTimeout(connectionParams, ApplicationConstants.NETWORK_REQUEST_CONNECTION_TIME_OUT_MILLI_SECOND);
        //HttpConnectionParams.setSoTimeout(connectionParams, ApplicationConstants.NETWORK_REQUEST_SOCKET_TIME_OUT_MILLI_SECOND);
        //ConnManagerParams.setTimeout(connectionParams, ApplicationConstants.NETWORK_REQUEST_CONNECTION_TIME_OUT_MILLI_SECOND);
        HttpClient client = sslClient(new DefaultHttpClient(connectionParams));

        String baseUrl = request.getBaseUrl();
        String url = baseUrl == null? mServerType.getUrl() + request.getUrl(): baseUrl;
        HttpGet get = new HttpGet(url);
        /*if (request.isCsrfTokenRequired()){
            get.setHeader(WebserviceElement.XCSRF_TOKEN.getKeyName(),Session.getInstance().getXCsrfToken());
            ((DefaultHttpClient)client).setCookieStore(Session.getInstance().getWebCookie().getCookieStore());
        } */

        Log.d(TAG,url);
        HttpResponse res = null;
        ResponseCode responseCode = null;
        try{

            res = client.execute(get);

        }
        catch (ConnectTimeoutException e){
            Log.e(TAG,"get(): " + e.getMessage());
            responseCode = ResponseCode.ConnectionTimeout;
        }
        catch(SocketTimeoutException e){
            Log.e(TAG,"get(): " + e.getMessage());
            responseCode = ResponseCode.SocketTimeout;
        }
        catch(UnknownHostException e){
            Log.e(TAG,"get(): " + e.getMessage());
            responseCode = ResponseCode.UnknownHost;
        }
        catch(IOException e){
            Log.e(TAG, "get(): " + e.getMessage());
            responseCode = ResponseCode.NetworkFailure;
        }

        if (responseCode != null){
            request.notifyCallbackFailure(responseCode.name(), ResponseCallback.ErrorType.NETWORK_COMMUNICATION_ERROR);
            return;
        }

        if (res == null){
            Log.e(TAG,"get(): " + "Null response received.");
            request.notifyCallbackFailure(ResponseCode.NetworkFailure.name(), ResponseCallback.ErrorType.NETWORK_COMMUNICATION_ERROR );
            return;
        }
        Header contentTypeHeader = res.getFirstHeader("Content-Type");

        if (contentTypeHeader == null){
            Log.e(TAG,"get(): " + "Content-Type is null.");
            request.notifyCallbackFailure(ResponseCode.NetworkFailure.name(), ResponseCallback.ErrorType.NETWORK_COMMUNICATION_ERROR );
            return;
        }
        String contentType = contentTypeHeader.getValue();

        //if content is not json and not html then request.onResponse(); this is for image and documents
        if (!contentType.contains("json")/*&& !contentType.contains("html")*/) {//assumption: server will never send html according to iOS team
            HttpEntity entity = res.getEntity();
            request.onResponse(res.getStatusLine().getStatusCode(),entity.getContent());
        }
        else{
          /*  if (Session.getInstance().getWebCookie().getCookieStore() == null){
                DefaultHttpClient defaultHttpClient = (DefaultHttpClient)client;
                Session.getInstance().getWebCookie().setCookieStore(defaultHttpClient.getCookieStore());
                Session.getInstance().setXCsrfToken(Session.getInstance().getWebCookie().getCookieValueForKey(WebserviceElement.WEB_COOKIE_XCSRF_TOKEN.getKeyName()));
            } */
            String response = EntityUtils.toString(res.getEntity());

            Log.e(TAG, response);

            request.onResponse(res.getStatusLine().getStatusCode(),response);
        }
    }

    public static enum SERVER_TYPE {
        TEST("test", BASE_URL_TEST_SERVER),
        DEV("dev", BASE_URL_DEV_SERVER),
        STAGING("staging",BASE_URL_STAGING_SERVER),
        PRODUCTION("production", BASE_URL_PRODUCTION_SERVER);
        private String url;
        private String type;

        private SERVER_TYPE(String type, String url) {
            this.type = type;
            this.url = url;
        }

        public String getUrl() {
            return this.url;
        }

        public String getType() {
            return this.type;
        }
    }

    public class MySSLSocketFactory extends SSLSocketFactory {
        SSLContext sslContext = SSLContext.getInstance("TLS");

        public MySSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
            super(truststore);

            TrustManager tm = new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };

            sslContext.init(null, new TrustManager[]{tm}, null);
        }

        public MySSLSocketFactory(SSLContext context) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException {
            super(null);
            sslContext = context;
        }

        @Override
        public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
            return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
        }

        @Override
        public Socket createSocket() throws IOException {
            return sslContext.getSocketFactory().createSocket();
        }
    }


}

