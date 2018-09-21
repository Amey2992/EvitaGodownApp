package com.infosolutions.service;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.infosolutions.database.ConsumerDetails;
import com.infosolutions.database.DatabaseHelper;
import com.infosolutions.network.Constants;
import com.infosolutions.network.ResponseListener;
import com.infosolutions.network.VolleySingleton;
import com.infosolutions.utils.AppSettings;
import com.infosolutions.utils.Constant;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import khangtran.preferenceshelper.PreferencesHelper;

public class GetConsumerService extends IntentService implements ResponseListener {

    private DatabaseHelper databaseHelper;
    boolean shouldContinue;
    private ArrayList<ConsumerDetails> consumerDetailsList;
    private RuntimeExceptionDao<ConsumerDetails, Integer> daoDatabase;

    public GetConsumerService(){
        super("GetConsumerService");
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        VolleySingleton.getInstance(getApplicationContext()).addResponseListener(VolleySingleton.CallType.CONSUMER_DETAILS, this);
        AppSettings.getInstance(this).isServiceRunning = true;
        AppSettings.getInstance(this).getConsumerDetails(this);

    }


    private DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("-----------onDestroy---------");
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        System.out.println("-----------onTaskRemoved-----------");
    }

    @Override
    public void onSuccess(VolleySingleton.CallType type, String response) {
        new ConsumerInsertOperation(type,this).execute(response.toString());
    }

    @Override
    public void onFailure(VolleySingleton.CallType type, VolleyError error) {

        System.out.println();
        Toast.makeText(this,"Error Occurred",Toast.LENGTH_SHORT).show();
        AppSettings.getInstance(this).isServiceRunning = false;
        this.stopSelf();
    }

    public class ConsumerInsertOperation extends AsyncTask<String, Void, Void> {

        private GetConsumerService consumerService;
        private  VolleySingleton.CallType type;

        public ConsumerInsertOperation(VolleySingleton.CallType type, GetConsumerService consumerService){
            this.type = type;
            this.consumerService = consumerService;
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                JSONObject objectResult = new JSONObject(strings[0]);
                //JSONObject consumerResult = newyJSONObject(strings[1]);



                    daoDatabase =
                            getHelper().getConsumerExceptionDao();


                try {
                    daoDatabase.deleteBuilder().delete();
                } catch (SQLException e) {
                    e.printStackTrace();
                }


                if (type.equals(VolleySingleton.CallType.CONSUMER_DETAILS)) {
                    JSONArray jsonArray = objectResult.optJSONArray("Table");
                    int length = 0;
                    if(jsonArray != null) {
                         length = jsonArray.length();
                    }
                    /*JSONArray jsonArray1 = objectResult.optJSONArray("Table1");
                    int countOfConsumer = jsonArray1.optJSONObject(0).optInt("CountOfConsumer");*/
                    PreferencesHelper.getInstance().setValue("consumer_count", length);
                    consumerDetailsList = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.optJSONObject(i);
                        ConsumerDetails consumerDetails = new ConsumerDetails(jsonObject);
                        consumerDetailsList.add(consumerDetails);
                        //daoDatabase.cre(consumerDetails);
                        System.out.println("----------"+i+"--------");
                    }

                        daoDatabase.create(consumerDetailsList);


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            Intent intent = new Intent();
            intent.setAction(Constants.CONSUMER_BROADCAST);
            LocalBroadcastManager.getInstance(GetConsumerService.this).sendBroadcast(intent);

            AppSettings.getInstance(this.consumerService).isServiceRunning = false;
            this.consumerService.stopSelf();
        }
    }
}
