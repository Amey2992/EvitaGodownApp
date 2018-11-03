package com.infosolutions.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.commercialMgmt.models.ConsumerModel;
import com.infosolutions.database.ConsumerDetails;
import com.infosolutions.database.DatabaseHelper;
import com.infosolutions.network.Constants;
import com.infosolutions.network.ResponseListener;
import com.infosolutions.network.VolleySingleton;
import com.infosolutions.utils.AppSettings;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;

import khangtran.preferenceshelper.PreferencesHelper;

public class GetCommercialConsumerService extends IntentService implements ResponseListener {
    private DatabaseHelper databaseHelper;
    boolean shouldContinue;
    private ArrayList<ConsumerModel> consumerDetailsList;
    private RuntimeExceptionDao<ConsumerModel, Integer> daoDatabase;

    public GetCommercialConsumerService(){
        super("GetCommercialConsumerService");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        VolleySingleton.getInstance(getApplicationContext()).addResponseListener(VolleySingleton.CallType.COMMERCIAL_CONSUMER_DETAILS, this);
        AppSettings.getInstance(this).getCommercialConsumerDetails(this);
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
        new GetCommercialConsumerService.ConsumerInsertOperation(type,this).execute(response.toString());
    }

    @Override
    public void onFailure(VolleySingleton.CallType type, VolleyError error) {
        System.out.println();
        Toast.makeText(this,"Error Occurred",Toast.LENGTH_SHORT).show();
        AppSettings.getInstance(this).isCommercialConsumerServiceRunning = false;
        this.stopSelf();
    }

    public class ConsumerInsertOperation extends AsyncTask<String, Void, Void> {

        private GetCommercialConsumerService consumerService;
        private  VolleySingleton.CallType type;

        public ConsumerInsertOperation(VolleySingleton.CallType type, GetCommercialConsumerService consumerService){
            this.type = type;
            this.consumerService = consumerService;
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                JSONObject objectResult = new JSONObject(strings[0]);
                //JSONObject consumerResult = newyJSONObject(strings[1]);

                if (type.equals(VolleySingleton.CallType.COMMERCIAL_CONSUMER_DETAILS)) {
                    fillCommercialConsumerDB(objectResult);

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
            LocalBroadcastManager.getInstance(GetCommercialConsumerService.this).sendBroadcast(intent);

            AppSettings.getInstance(this.consumerService).isCommercialConsumerServiceRunning = false;
            this.consumerService.stopSelf();
        }

        private void fillCommercialConsumerDB(JSONObject jsonObject) {
            JSONArray arrayPRODUCT = jsonObject.optJSONArray("consumerDetails");
            RuntimeExceptionDao<ConsumerModel, Integer> consumerDB = getHelper().getConsumerModelExceptionDao();
            try {
                consumerDB.deleteBuilder().delete();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            consumerDetailsList = new ArrayList<>();
            if(arrayPRODUCT != null) {
                for (int product = 0; product < arrayPRODUCT.length(); product++) {
                    JSONObject objectProduct = arrayPRODUCT.optJSONObject(product);
           /* int PRODUCT_CODE = Integer.parseInt(objectProduct.optString("PRODUCT_CODE"));
            String PRODUCT_CATEGORY = objectProduct.optString("ID_PRODUCT_CATEGORY");
            String PRODUCT_DESCRIPTION = objectProduct.optString("DESCRIPTION");
            String UNIT_MEASUREMENT = objectProduct.optString("UNIT_OF_MEASUREMENT");
*/
                    ConsumerModel consumerModel = new ConsumerModel(1, objectProduct);
                    consumerDetailsList.add(consumerModel);
                }
            }
            consumerDB.create(consumerDetailsList);
        }

    }
}
