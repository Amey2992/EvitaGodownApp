package com.infosolutions.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;
import com.infosolutions.utils.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.Policy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import khangtran.preferenceshelper.PreferencesHelper;

import static com.infosolutions.network.Constants.GODOWN_CODE;
import static com.infosolutions.network.Constants.MODULE_KEY;
import static com.infosolutions.network.Constants.PRODUCT_ID;


public class VolleySingleton {

    private final String TAG = VolleySingleton.class.getSimpleName();
    private static VolleySingleton instance;
    private RequestQueue requestQueue;
    private ImageLoader imageLoader;
    private ArrayList<ResponseListener> mResponseListener;
    private HashMap<CallType, ResponseListener> mResponseListenerMap = new HashMap<CallType, ResponseListener>();


    public enum CallType {

        AGENCY_NAME,
        USER_LOGIN,
        USER_COMMERCIAL_LOGIN,
        SYNC_LOCAL_DATA,
        REPORT_DOMESTIC,
        REPORT_COMMERCIAL,
        REPORT_TRUCK_RECEIVED,
        REPORT_TRUCK_SEND,
        REPORT_TV_DETAILS,
        COMMERCIAL_REPORT_STOCK,
        COMMERCIAL_REPORT_CONSUMER,
        STOCK_REPORT,
        OWNER_REPORT,
        GET_AVAILABLE_CYL,
        UPDATE_LOCAL_DATA,
        COMMERCIAL_DELIVERY_COUNT,
        CONSUMER_DETAILS,
        COMMERCIAL_CONSUMER_DETAILS,
        GET_STOCKS,
        POST_STOCKS,
        ERV_PURCHASE,
        SYNC_OTHERS,
        POST_COMMERCIAL_CONSUMER,
        POST_COMMERCIAL_SALE,
        COMMERCIAL_SAVE_CONSUMER_DELIVERY

    }


    private VolleySingleton(Context context) {

        requestQueue = Volley.newRequestQueue(context);
        imageLoader = new ImageLoader(requestQueue,

                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap> cache = new LruCache<String, Bitmap>(20);

                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });

        mResponseListener = new ArrayList<ResponseListener>();
    }


    public static VolleySingleton getInstance(Context context) {

        if (instance == null) {
            instance = new VolleySingleton(context);
        }
        return instance;
    }
    public void setImageLoader(ImageLoader imageLoader) {this.imageLoader = imageLoader;}
    public ImageLoader getImageLoader() {
        return imageLoader;
    }
    public void addResponseListener(CallType type, ResponseListener listener) {mResponseListenerMap.put(type, listener);}

    public void removeResponseListener(ResponseListener listener) {
        // TODO
    }
    private void notifySuccessListener(CallType type, String response) {
        mResponseListenerMap.get(type).onSuccess(type, response);
    }
    private void notifyFailureListener(CallType type, VolleyError error) {
        mResponseListenerMap.get(type).onFailure(type, error);
    }
    public RequestQueue getRequestQueue() {
        return requestQueue;
    }
    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }
    public void cancelRequest(){
        if (requestQueue != null) {
            requestQueue.cancelAll(TAG);
        }
    }


    public void networkRequestAPI(final CallType type, final String url, final String userId, final String password ) {
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (mResponseListener != null)
                            Log.d(TAG, "in onResponse ==> " + response);
                        notifySuccessListener(type, response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // handle error response
                if (mResponseListener != null)
                    Log.e("Error: ", "Response Is Failed");
                notifyFailureListener(type, error);
            }
        }) {

            @Override
            public byte[] getBody() {
                Map<String, String> params = new HashMap<String, String>();

                Log.d("module", Constants.LOGIN_API_VALUE);
                Log.d("userId", userId);
                Log.d("password", password);

                params.put(MODULE_KEY, Constants.LOGIN_API_VALUE);
                params.put(Constants.LOGIN_USERID_KEY, userId);
                params.put(Constants.LOGIN_PASSWORD_KEY, password);
               // params.put(Constants.LOGIN_USERTYPE_KEY, "Android");

                if (params != null && params.size() > 0) {
                    return encodeParameters(params, getParamsEncoding());
                }
                return null;
            }

            private byte[] encodeParameters(Map<String, String> params, String paramsEncoding)
            {
                StringBuilder encodedParams = new StringBuilder();
                try {
                    for (Map.Entry<String, String> entry : params.entrySet()) {
                        encodedParams.append(URLEncoder.encode(entry.getKey(), paramsEncoding));
                        encodedParams.append('=');
                        encodedParams.append(URLEncoder.encode(entry.getValue(), paramsEncoding));
                        encodedParams.append('&');
                    }
                    return encodedParams.toString().getBytes(paramsEncoding);
                } catch (UnsupportedEncodingException uee) {
                    throw new RuntimeException("Encoding not supported: "
                            + paramsEncoding, uee);
                }
            }

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset="
                        + getParamsEncoding();
            }
        };
        addToRequestQueue(request);
    }


    /**
     * Check Login validation for Login
     * */
    public void new_apiCallLoginValidation(final CallType type, final String url,
                                       final String userId, final String password, final String Android, final boolean isCommercialDeliveryman) {

        JSONObject jsonObject = new JSONObject();
        JSONObject jsonObject1 = new JSONObject();
        try {
            jsonObject.put(MODULE_KEY,  Constants.LOGIN_API_VALUE);
            jsonObject.put(Constants.LOGIN_USERID_KEY, userId);
            jsonObject.put(Constants.LOGIN_PASSWORD_KEY, password);
            jsonObject.put(Constants.LOGIN_USERTYPE_KEY, Android);
            if(isCommercialDeliveryman) {
                jsonObject.put(Constants.COMMERCIAL_LOGIN, isCommercialDeliveryman);
            }

            jsonObject1.put("_objLogin",jsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("login:",jsonObject1.toString());


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject1, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (mResponseListener != null)
                    notifySuccessListener(type, response.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (mResponseListener != null)
                    notifyFailureListener(type, error);
            }
        });

        jsonObjectRequest.setShouldCache(false);
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        addToRequestQueue(jsonObjectRequest);

    }

        /**
         * Check Login validation for Login
         * */
    public void apiCallLoginValidation(final CallType type, final String url,
                                       final String userId, final String password, final String Android) {

        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // convert the String response to XM L
                        // if you use Simple, something like following should do
                        // it
                        if (mResponseListener != null)
                            notifySuccessListener(type, response);

                        Log.d(TAG, "in onRespose response ==> " + response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // handle error response
                if (mResponseListener != null)
                    notifyFailureListener(type, error);
            }
        }) {

            @Override
            public byte[] getBody() {
                Map<String, String> params = new HashMap<String, String>();

                Log.d(TAG, "in getParams LoginModule ==> " + Constants.LOGIN_API_VALUE);
                Log.d(TAG, "in getParams userId ==> " + userId);
                Log.d(TAG, "in getParams password ==> " + password);
                Log.d(TAG, "in getParams User_Type ==> " + Android);

                params.put(MODULE_KEY,  Constants.LOGIN_API_VALUE);
                params.put(Constants.LOGIN_USERID_KEY, userId);
                params.put(Constants.LOGIN_PASSWORD_KEY, password);
                params.put(Constants.LOGIN_USERTYPE_KEY, Android);


                if (params != null && params.size() > 0) {
                    return encodeParameters(params, getParamsEncoding());
                }
                return null;
            }

            private byte[] encodeParameters(Map<String, String> params,
                                            String paramsEncoding) {
                StringBuilder encodedParams = new StringBuilder();
                try {
                    for (Map.Entry<String, String> entry : params.entrySet()) {
                        encodedParams.append(URLEncoder.encode(entry.getKey(),
                                paramsEncoding));
                        encodedParams.append('=');
                        encodedParams.append(URLEncoder.encode(
                                entry.getValue(), paramsEncoding));
                        encodedParams.append('&');
                    }
                    return encodedParams.toString().getBytes(paramsEncoding);
                } catch (UnsupportedEncodingException uee) {
                    throw new RuntimeException("Encoding not supported: "
                            + paramsEncoding, uee);
                }
            }

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=" + getParamsEncoding();
            }
        };
        request.setShouldCache(false);
        request.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        addToRequestQueue(request);
    }


    /**
     * jsonSyncData is JSON Data we push to server
     * @param jsonSyncData
     * @param type
     * @param url
     * */
     /*
    public void syncAndroidData(final CallType type, final String url, final JSONObject jsonSyncData) {
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (mResponseListener != null)
                            notifySuccessListener(type, response);
                        Log.d(TAG, "in onRespose response ==> " + response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (mResponseListener != null)
                    notifyFailureListener(type, error);
            }
        }) {

            @Override
            public byte[] getBody() {
                Map<String, String> params = new HashMap<String, String>();

                Log.d(TAG, "In Get Params Sync Module ==> " + Constants.SYNC_ANDROID_DATA_VALUE);
                Log.d(TAG, "In Get Params Json Data ==> " + String.valueOf(jsonSyncData));

                params.put(MODULE_KEY,  Constants.SYNC_ANDROID_DATA_VALUE);
                params.put(Constants.JSON_DATA_KEY, String.valueOf(jsonSyncData));

                if (params != null && params.size() > 0) {
                    return encodeParameters(params, getParamsEncoding());
                }
                return null;
            }

            private byte[] encodeParameters(Map<String, String> params, String paramsEncoding) {
                StringBuilder encodedParams = new StringBuilder();
                try {
                    for (Map.Entry<String, String> entry : params.entrySet()) {
                        encodedParams.append(URLEncoder.encode(entry.getKey(),
                                paramsEncoding));
                        encodedParams.append('=');
                        encodedParams.append(URLEncoder.encode(
                                entry.getValue(), paramsEncoding));
                        encodedParams.append('&');
                    }
                    return encodedParams.toString().getBytes(paramsEncoding);
                } catch (UnsupportedEncodingException uee) {
                    throw new RuntimeException("Encoding not supported: "
                            + paramsEncoding, uee);
                }
            }

         *//*   @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset="
                        + getParamsEncoding();
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String responseString = "";
                if (response != null) {
                    responseString = String.valueOf(response.statusCode);
                    // can get more details such as response.headers
                }
                return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));

            }*//*
        };

        request.setShouldCache(false);
        request.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        addToRequestQueue(request);
    }*/

    public void getCommercialDeliveryCount(final CallType type, final String url){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (mResponseListener != null)
                    notifySuccessListener(type, response.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (mResponseListener != null)
                    notifyFailureListener(type, error);
            }
        });

        jsonObjectRequest.setShouldCache(false);
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        addToRequestQueue(jsonObjectRequest);
    }

    public void getPurchaseERV(final CallType type, final String url){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (mResponseListener != null)
                    notifySuccessListener(type, response.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (mResponseListener != null)
                    notifyFailureListener(type, error);
            }
        });

        jsonObjectRequest.setShouldCache(false);
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        addToRequestQueue(jsonObjectRequest);
    }

    public void getConsumerDetails(final CallType type, final String url){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (mResponseListener != null)
                    notifySuccessListener(type, response.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (mResponseListener != null)
                    notifyFailureListener(type, error);
            }
        });

        jsonObjectRequest.setShouldCache(false);
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        addToRequestQueue(jsonObjectRequest);
    }


    public void getCommercialConsumerDetails(final CallType type, final String url){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (mResponseListener != null)
                    notifySuccessListener(type, response.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (mResponseListener != null)
                    notifyFailureListener(type, error);
            }
        });

        jsonObjectRequest.setShouldCache(false);
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        addToRequestQueue(jsonObjectRequest);
    }

    public void getStocksDetails(final CallType type, final String url, JSONObject jsonObject){
        Log.d("getStock",jsonObject.toString());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (mResponseListener != null)
                    notifySuccessListener(type, response.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (mResponseListener != null)
                    notifyFailureListener(type, error);
            }
        });

        jsonObjectRequest.setShouldCache(false);
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        addToRequestQueue(jsonObjectRequest);
    }

    public void postStocksDetails(final CallType type, final String url, JSONObject jsonObject){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (mResponseListener != null)
                    notifySuccessListener(type, response.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (mResponseListener != null)
                    notifyFailureListener(type, error);
            }
        });

        jsonObjectRequest.setShouldCache(false);
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        addToRequestQueue(jsonObjectRequest);
    }

    public void postConsumerDetails(final CallType type, final String url, JSONObject jsonObject){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (mResponseListener != null)
                    notifySuccessListener(type, response.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (mResponseListener != null)
                    notifyFailureListener(type, error);
            }
        });

        jsonObjectRequest.setShouldCache(false);
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        addToRequestQueue(jsonObjectRequest);
    }

    public void saveCommercialConsumerDelivery(final CallType type, final String url, JSONObject jsonObject){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (mResponseListener != null)
                    notifySuccessListener(type, response.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (mResponseListener != null)
                    notifyFailureListener(type, error);
            }
        });

        jsonObjectRequest.setShouldCache(false);
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        addToRequestQueue(jsonObjectRequest);
    }


    public void test_syncAndroidData(final CallType type, final String url, final JSONObject jsonSyncData){
        Log.d("bodyjson",jsonSyncData.toString());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonSyncData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (mResponseListener != null)
                    notifySuccessListener(type, response.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (mResponseListener != null)
                    notifyFailureListener(type, error);
            }
        });

        jsonObjectRequest.setShouldCache(false);
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        addToRequestQueue(jsonObjectRequest);
    }



    public void fetch_all_data(final CallType type, final String url){

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (mResponseListener != null)
                    notifySuccessListener(type, response.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (mResponseListener != null)
                    notifyFailureListener(type, error);
            }
        });

        jsonObjectRequest.setShouldCache(false);
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        addToRequestQueue(jsonObjectRequest);
    }




    /**
     * deliveryDomesticFreshTrip Network Call for the Domestic fresh trip
     * @param type
     * @param url
     * */
    public void ownerReport(final CallType type, final String url, final String moduleType) {
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // convert the String response to XM L
                        // if you use Simple, something like following should do
                        // it
                        if (mResponseListener != null)
                            notifySuccessListener(type, response);

                        Log.d(TAG, "in onRespose response ==> " + response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // handle error response
                if (mResponseListener != null)
                    notifyFailureListener(type, error);
            }
        }) {

            @Override
            public byte[] getBody() {
                Map<String, String> params = new HashMap<String, String>();

                Log.d(TAG, "module_type ==> " + MODULE_KEY);
                params.put(MODULE_KEY,  moduleType);

                if (params != null && params.size() > 0) {
                    return encodeParameters(params, getParamsEncoding());
                }
                return null;
            }

            private byte[] encodeParameters(Map<String, String> params,
                                            String paramsEncoding) {
                StringBuilder encodedParams = new StringBuilder();
                try {
                    for (Map.Entry<String, String> entry : params.entrySet()) {
                        encodedParams.append(URLEncoder.encode(entry.getKey(),
                                paramsEncoding));
                        encodedParams.append('=');
                        encodedParams.append(URLEncoder.encode(
                                entry.getValue(), paramsEncoding));
                        encodedParams.append('&');
                    }
                    return encodedParams.toString().getBytes(paramsEncoding);
                } catch (UnsupportedEncodingException uee) {
                    throw new RuntimeException("Encoding not supported: "
                            + paramsEncoding, uee);
                }
            }

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset="
                        + getParamsEncoding();
            }
        };
        addToRequestQueue(request);
    }


    public void get_commercial_report(final CallType type,final String reportName, final String date, final String url){
        JSONObject jsonObject = new JSONObject();
        int UserId= PreferencesHelper.getInstance().getIntValue(Constants.LOGIN_DELIVERYMAN_ID,0);
        try {
            jsonObject.put(Constants.KEY_REPORT_NAME, reportName);
            jsonObject.put("Date",date);
            jsonObject.put("UserID",UserId);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (mResponseListener != null)
                    notifySuccessListener(type, response.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (mResponseListener != null)
                    notifyFailureListener(type, error);
            }
        });

        jsonObjectRequest.setShouldCache(false);
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        addToRequestQueue(jsonObjectRequest);
    }



    /**
     * Report List  for Login
     * */
    public void apiGetReportList(final CallType type, final String url, final String reportName, final String godown_id) {
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // convert the String response to XM L
                        // if you use Simple, something like following should do
                        // it
                        if (mResponseListener != null)
                            notifySuccessListener(type, response);

                        Log.d(TAG, "in onRespose response ==> " + response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // handle error response
                if (mResponseListener != null)
                    notifyFailureListener(type, error);
            }
        }) {

            @Override
            public byte[] getBody() {
                Map<String, String> params = new HashMap<String, String>();

                Log.d(TAG, "in getParams LoginModule ==> " + Constants.LOGIN_API_VALUE);
                Log.d(TAG, "in getParams reportName ==> " + reportName);

                params.put(MODULE_KEY,  Constants.REPORTS);
                params.put(Constants.KEY_REPORT_NAME, reportName);
                params.put(Constants.KEY_GODOWN_ID, godown_id);

                if (params != null && params.size() > 0) {
                    return encodeParameters(params, getParamsEncoding());
                }
                return null;
            }

            private byte[] encodeParameters(Map<String, String> params,
                                            String paramsEncoding) {
                StringBuilder encodedParams = new StringBuilder();
                try {
                    for (Map.Entry<String, String> entry : params.entrySet()) {
                        encodedParams.append(URLEncoder.encode(entry.getKey(),
                                paramsEncoding));
                        encodedParams.append('=');
                        encodedParams.append(URLEncoder.encode(
                                entry.getValue(), paramsEncoding));
                        encodedParams.append('&');
                    }
                    return encodedParams.toString().getBytes(paramsEncoding);
                } catch (UnsupportedEncodingException uee) {
                    throw new RuntimeException("Encoding not supported: "
                            + paramsEncoding, uee);
                }
            }

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset="
                        + getParamsEncoding();
            }
        };
        addToRequestQueue(request); }


    /**
     * Report List  for Login
     * */
    public void apiGetStockReport(final CallType type, final String url, final String reportName, final String godown_code) {
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // convert the String response to XM L
                        // if you use Simple, something like following should do
                        // it
                        if (mResponseListener != null)
                            notifySuccessListener(type, response);

                        Log.d(TAG, "in onRespose response ==> " + response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // handle error response
                if (mResponseListener != null)
                    notifyFailureListener(type, error);
            }
        }) {

            @Override
            public byte[] getBody() {
                Map<String, String> params = new HashMap<String, String>();


                Log.d(TAG, "in getParams reportName ==> " + reportName);

                params.put(MODULE_KEY,  reportName);
                params.put(Constants.KEY_GODOWN_ID,  godown_code);

                if (params != null && params.size() > 0) {
                    return encodeParameters(params, getParamsEncoding());
                }
                return null;
            }

            private byte[] encodeParameters(Map<String, String> params,
                                            String paramsEncoding) {
                StringBuilder encodedParams = new StringBuilder();
                try {
                    for (Map.Entry<String, String> entry : params.entrySet()) {
                        encodedParams.append(URLEncoder.encode(entry.getKey(),
                                paramsEncoding));
                        encodedParams.append('=');
                        encodedParams.append(URLEncoder.encode(
                                entry.getValue(), paramsEncoding));
                        encodedParams.append('&');
                    }
                    return encodedParams.toString().getBytes(paramsEncoding);
                } catch (UnsupportedEncodingException uee) {
                    throw new RuntimeException("Encoding not supported: "
                            + paramsEncoding, uee);
                }
            }

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset="
                        + getParamsEncoding();
            }
        };
        addToRequestQueue(request); }






    /**
     * Check Login validation for Login
     * */
    public void apiGetAgencyName(final CallType type, final String url) {
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // convert the String response to XM L
                        // if you use Simple, something like following should do
                        // it
                        if (mResponseListener != null)
                            notifySuccessListener(type, response);

                        Log.d(TAG, "in onRespose response ==> " + response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // handle error response
                if (mResponseListener != null)
                    notifyFailureListener(type, error);
            }
        }) {

            @Override
            public byte[] getBody() {
                Map<String, String> params = new HashMap<String, String>();

                Log.d(TAG, "in getParams LoginModule ==> " + Constants.MODULE_AGENCY_NAME);
                Log.d(TAG, "in getParams userId ==> " + "");
                Log.d(TAG, "in getParams password ==> " + "");

                params.put(MODULE_KEY,  Constants.MODULE_AGENCY_NAME);

                if (params != null && params.size() > 0) {
                    return encodeParameters(params, getParamsEncoding());
                }
                return null;
            }

            private byte[] encodeParameters(Map<String, String> params,
                                            String paramsEncoding) {
                StringBuilder encodedParams = new StringBuilder();
                try {
                    for (Map.Entry<String, String> entry : params.entrySet()) {
                        encodedParams.append(URLEncoder.encode(entry.getKey(),
                                paramsEncoding));
                        encodedParams.append('=');
                        encodedParams.append(URLEncoder.encode(
                                entry.getValue(), paramsEncoding));
                        encodedParams.append('&');
                    }
                    return encodedParams.toString().getBytes(paramsEncoding);
                } catch (UnsupportedEncodingException uee) {
                    throw new RuntimeException("Encoding not supported: "
                            + paramsEncoding, uee);
                }
            }

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=" + getParamsEncoding();
            }
        };
        request.setShouldCache(false);
        request.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        addToRequestQueue(request);
    }





    public void apiAvailableCYL(final CallType type, final String url,
                                       final String productId, final String godown_code) {
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // convert the String response to XM L
                        // if you use Simple, something like following should do
                        // it
                        if (mResponseListener != null)
                            notifySuccessListener(type, response);

                        Log.d(TAG, "in onRespose response ==> " + response);
                        Log.e(TAG, "in onRespose response ==> " + response);


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // handle error response
                if (mResponseListener != null)
                    notifyFailureListener(type, error);
            }
        }) {

            @Override
            public byte[] getBody() {
                Map<String, String> params = new HashMap<String, String>();

                Log.d(TAG, "in getParams Module ==> " + Constants.LOGIN_API_VALUE);
                Log.d(TAG, "in getParams productId ==> " + productId);
                Log.d(TAG, "in getParams godown_code ==> " + godown_code);

                params.put(MODULE_KEY,  "getAvilableCylinders");
                params.put(PRODUCT_ID, productId);
                params.put(GODOWN_CODE, godown_code);

                if (params != null && params.size() > 0) {
                    return encodeParameters(params, getParamsEncoding());
                }
                return null;
            }

            private byte[] encodeParameters(Map<String, String> params,
                                            String paramsEncoding) {
                StringBuilder encodedParams = new StringBuilder();
                try {
                    for (Map.Entry<String, String> entry : params.entrySet()) {
                        encodedParams.append(URLEncoder.encode(entry.getKey(),
                                paramsEncoding));
                        encodedParams.append('=');
                        encodedParams.append(URLEncoder.encode(
                                entry.getValue(), paramsEncoding));
                        encodedParams.append('&');
                    }
                    return encodedParams.toString().getBytes(paramsEncoding);
                } catch (UnsupportedEncodingException uee) {
                    throw new RuntimeException("Encoding not supported: "
                            + paramsEncoding, uee);
                }
            }

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=" + getParamsEncoding();
            }
        };
        request.setShouldCache(false);
        request.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        addToRequestQueue(request);
    }

    public void new_apiAvailableCYL(final CallType type, final String url,
                                final String productId, final String godown_code) {



        JSONObject jsonObject = new JSONObject();

        try {


            jsonObject.put(MODULE_KEY, "getAvilableCylinders");
            jsonObject.put(PRODUCT_ID,Integer.parseInt(productId));
            jsonObject.put(GODOWN_CODE,Integer.parseInt(godown_code));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (mResponseListener != null)
                    notifySuccessListener(type, response.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (mResponseListener != null)
                    notifyFailureListener(type, error);
            }
        });

        jsonObjectRequest.setShouldCache(false);
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        addToRequestQueue(jsonObjectRequest);

    }



}
