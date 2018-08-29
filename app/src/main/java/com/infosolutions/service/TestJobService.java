package com.infosolutions.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.infosolutions.customviews.EvitaSnackbar;
import com.infosolutions.database.CommercialDeliveryDB;
import com.infosolutions.database.DatabaseHelper;
import com.infosolutions.database.DomesticDeliveryDB;
import com.infosolutions.database.TVDetailsDB;
import com.infosolutions.database.TruckDetailsDB;
import com.infosolutions.database.TruckSendDetailsDB;
import com.infosolutions.event.EvitaEvent;
import com.infosolutions.evita.R;
import com.infosolutions.network.Constants;
import com.infosolutions.network.ResponseListener;
import com.infosolutions.network.VolleySingleton;
import com.infosolutions.ui.splash.SplashActivity;
import com.infosolutions.utils.AppSettings;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.UpdateBuilder;

import junit.framework.Test;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.tatarka.support.job.JobParameters;
import me.tatarka.support.job.JobService;

import static com.infosolutions.network.Constants.PREF_SYNC_JSON_DATA;

/**
 * Created by shailesh on 10/1/18.
 */

public class TestJobService extends JobService implements ResponseListener {

    private Boolean isNeedToSync = false;
    private DatabaseHelper databaseHelper = null;

    private JSONObject localJSON_DATA;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {

        if (jobParameters.getExtras().containsKey("localData")) {
            //String localExtraData =jobParameters.getExtras().getString("localData");
            //this.localData = localExtraData;
            //Log.e("localData", localData);


            //if (!localData.equalsIgnoreCase("")){ new MyTask(this).execute(jobParameters);}
        }

       /* VolleySingleton.getInstance(getApplicationContext()).test_syncAndroidData(VolleySingleton.CallType.SYNC_LOCAL_DATA, Constants.post_url, localJSON_DATA);
        VolleySingleton.getInstance(getApplicationContext()).fetch_all_data(VolleySingleton.CallType.UPDATE_LOCAL_DATA, Constants.get_url);
*/
        Log.e("Sync"," Successfully sync.....................");


        //pushContentToServer(jobParameters);
        return isNeedToSync;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }

    @Override
    public void onSuccess(VolleySingleton.CallType type, String response) {
        String responseMsg = "";
        //progress_bar.setVisibility(View.GONE);
        JSONObject jsonResult = null;
        try {
            jsonResult = new JSONObject(response);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (type.equals(VolleySingleton.CallType.SYNC_LOCAL_DATA)) {

            try {

                responseMsg = jsonResult.getString("responseMessage");
                if (jsonResult.getString("responseCode").equalsIgnoreCase("200")) {
                    updateLocalFromServer();
                    /*updateDatabase();

                    EvitaEvent.EventDataSyncToServer eventDataSyncToServer = new EventDataSyncToServer();
                    eventDataSyncToServer.setDataSynced(true);
                    eventBus.post(eventDataSyncToServer);

                    finish();
*/
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }  else if (type.equals(VolleySingleton.CallType.UPDATE_LOCAL_DATA)) {


            RuntimeExceptionDao<DomesticDeliveryDB, Integer> daoDatabase =
                    getHelper().getDomesticRuntimeExceptionDao();
            Log.e("domestic delivery",daoDatabase.toString());

            try {
                daoDatabase.deleteBuilder().delete();
            } catch (SQLException e) {
                e.printStackTrace();
            }



            JSONArray jsonArray = jsonResult.optJSONArray("Table");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.optJSONObject(i);
                DomesticDeliveryDB domesticDeliveryDB = new DomesticDeliveryDB(jsonObject);
                daoDatabase.createOrUpdate(domesticDeliveryDB);
            }


            updateDatabase();
            Notification(getApplicationContext(), responseMsg);
        }
    }

    @Override
    public void onFailure(VolleySingleton.CallType type, VolleyError error) {

    }

    private class MyTask extends AsyncTask<JobParameters, Void, JobParameters> {

        private TestJobService myService;
        private String sync_started = "sync_started";
        private String sync_finished = "sync_finished";


        MyTask(TestJobService myService) {
            this.myService = myService;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected JobParameters doInBackground(JobParameters... param) {

            Log.e("doInBackground...", sync_started);
            String resultObj = postData();

            try {
                JSONObject objectResult = new JSONObject(resultObj);
                String responseMsg = objectResult.getString("responseMessage");
                Log.e("responseMessage", responseMsg);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return param[0];
        }


        @Override
        protected void onPostExecute(JobParameters param) {
            Log.e("onPostExecute...", sync_finished);

            myService.jobFinished(param, false);
        }




        public String postData() {

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(Constants.EVITA_API_URL);
            String resultString = "";
            try {

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair(Constants.MODULE_KEY, Constants.SYNC_ANDROID_DATA_VALUE));
                nameValuePairs.add(new BasicNameValuePair(Constants.JSON_DATA_KEY, localJSON_DATA.toString()));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httppost);
                resultString = readContent(response);
                JSONObject objectResult = new JSONObject(resultString);

                String responseCode = objectResult.getString("responseCode");
                String responseMsg = objectResult.getString("responseMessage");
                if (responseCode.equalsIgnoreCase("200")) {
                    updateDatabase();
                    Notification(getApplicationContext(), responseMsg);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return resultString;
        }
    }

    String readContent(HttpResponse response) {
        String text = "";
        InputStream in = null;

        try {
            in = response.getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            text = sb.toString();
        } catch (IllegalStateException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {

                in.close();
            } catch (Exception ex) {
            }
        }

        return text;
    }

    private void pushContentToServer(JobParameters mJobParameters) {

        try {
            localJSON_DATA = new JSONObject(AppSettings.getInstance(TestJobService.this).getBodyJson(TestJobService.this));
            updateLocalFromServer();
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        if(AppSettings.getInstance(TestJobService.this).isBodyJsonEmpy()){
            updateLocalFromServer();
            return;
        }
/*
        if (jsonArrayDomestic.length() == 0 && jsonArrayCommercial.length() == 0 && jsonArrayTVDetails.length() == 0 && jsonArrayTruckDetails.length() == 0 && jsonArrayTruckSendDetails.length() == 0) {
            Notification(getApplicationContext(), "No data to sync");
            return;
        }*/


        //new MyTask(this).execute(mJobParameters);
        autoSyncAndroidDataToServer();
    }


    private void autoSyncAndroidDataToServer() {
        VolleySingleton.getInstance(getApplicationContext()).test_syncAndroidData(VolleySingleton.CallType.SYNC_LOCAL_DATA, Constants.post_url, localJSON_DATA);
    }

    public void updateLocalFromServer() {
        VolleySingleton.getInstance(getApplicationContext()).fetch_all_data(VolleySingleton.CallType.UPDATE_LOCAL_DATA, Constants.get_url);
    }


    public String getDateTime() {

        SimpleDateFormat simpleDateFormat = null;
        Date date = null;
        try {
            DateFormat formatter = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
            date = formatter.parse(new Date().toString());
            simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return simpleDateFormat.format(date);
    }

    private void Notification(Context mContext, String stringContent) {

        Intent intent = new Intent(this, SplashActivity.class);
        intent.putExtra("title", "");
        PendingIntent pIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.evita_logo).setContentTitle(getString(R.string.notificationtitle)).setContentText(stringContent).setContentIntent(pIntent).setAutoCancel(true);
        NotificationManager notificationmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationmanager.notify(0, builder.build());
    }

    private void updateDatabase() {

        try {

            RuntimeExceptionDao<DomesticDeliveryDB, Integer> daoDatabase =
                    getHelper().getDomesticRuntimeExceptionDao();
            updateDomesticDB(daoDatabase);

            RuntimeExceptionDao<CommercialDeliveryDB, Integer> commercialDB =
                    getHelper().getCommercialRuntimeExceptionDao();
            updateCommercialDB(commercialDB);

            RuntimeExceptionDao<TVDetailsDB, Integer> tvDetails =
                    getHelper().getTVDetailRTExceptionDao();
            updateTVDetailDB(tvDetails);

            RuntimeExceptionDao<TruckDetailsDB, Integer> truckDetails =
                    getHelper().getTruckDetailRTExceptionDao();
            updateTruckDetailDB(truckDetails);

            RuntimeExceptionDao<TruckSendDetailsDB, Integer> truckSendDetails =
                    getHelper().getTruckDetailSendRTExceptionDao();
            updateTruckSendDetailDB(truckSendDetails);


        } catch (SQLException e) {
            //Toast.makeText(getApplicationContext(), "Invalid data received", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }

    private void updateTruckSendDetailDB(RuntimeExceptionDao<TruckSendDetailsDB, Integer> truckSendDetails) throws SQLException {

        UpdateBuilder<TruckSendDetailsDB, Integer> updateTruckSendDetailsBuilder = truckSendDetails.updateBuilder();
        updateTruckSendDetailsBuilder.updateColumnValue("is_sync", "Y");
        updateTruckSendDetailsBuilder.update();
    }

    private void updateTruckDetailDB(RuntimeExceptionDao<TruckDetailsDB, Integer> truckDetails) throws SQLException {

        UpdateBuilder<TruckDetailsDB, Integer> updateTruckDetailsBuilder = truckDetails.updateBuilder();
        updateTruckDetailsBuilder.updateColumnValue("is_sync", "Y");
        updateTruckDetailsBuilder.update();
    }

    private void updateTVDetailDB(RuntimeExceptionDao<TVDetailsDB, Integer> tvDetails) throws SQLException {

        UpdateBuilder<TVDetailsDB, Integer> updateTVDetailsBuilder = tvDetails.updateBuilder();
        updateTVDetailsBuilder.updateColumnValue("is_sync", "Y");
        updateTVDetailsBuilder.update();
    }

    private void updateCommercialDB(RuntimeExceptionDao<CommercialDeliveryDB, Integer> commercialDB) throws SQLException {

        UpdateBuilder<CommercialDeliveryDB, Integer> updateCommercialBuilder = commercialDB.updateBuilder();
        updateCommercialBuilder.updateColumnValue("is_sync", "Y");
        updateCommercialBuilder.update();
    }

    private void updateDomesticDB(RuntimeExceptionDao<DomesticDeliveryDB, Integer> daoDatabase) throws SQLException {

        UpdateBuilder<DomesticDeliveryDB, Integer> updateDomesticBuilder = daoDatabase.updateBuilder();
        updateDomesticBuilder.updateColumnValue("is_sync", "Y");
        updateDomesticBuilder.update();
    }

}
