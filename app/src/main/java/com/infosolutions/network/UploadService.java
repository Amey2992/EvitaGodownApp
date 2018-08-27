package com.infosolutions.network;

import android.app.IntentService;
import android.content.Intent;

import com.infosolutions.database.DatabaseHelper;
import com.infosolutions.database.DomesticDeliveryDB;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


/**
 * Created by shailesh on 1/9/17.
 */

public class UploadService extends IntentService{


    private JSONArray jsonArrayDelivery = new JSONArray();
    private JSONObject jsonObjectSyncAndroidDataToServer;
    private DatabaseHelper databaseHelper;


    public JSONObject getJsonObjectSyncAndroidDataToServer() {
        return jsonObjectSyncAndroidDataToServer;
    }

    public void setJsonObjectSyncAndroidDataToServer(JSONObject jsonObjectSyncAndroidDataToServer) {
        this.jsonObjectSyncAndroidDataToServer = jsonObjectSyncAndroidDataToServer;
    }


    public UploadService() {
        super("uploadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //Send JSON Data to server
        pushContentToServer();

    }



    private void pushContentToServer() {

        //Initialise and Sync Delivery data
        RuntimeExceptionDao<DomesticDeliveryDB, Integer> domesticDB = getHelper().getDomesticRuntimeExceptionDao();
        List<DomesticDeliveryDB> delivery = domesticDB.queryForAll();

        for (DomesticDeliveryDB cn : delivery) {

            try {

                JSONObject jsonObjectDelivery = new JSONObject();
                jsonObjectDelivery.put("Given_Time", cn.given_time);
                jsonObjectDelivery.put("Emp_Id", cn.employee_id);
                jsonObjectDelivery.put("Trip_No", cn.trip_number);
                jsonObjectDelivery.put("typeOfQuery", cn.type_of_query);
                jsonObjectDelivery.put("ID_PRODUCT", cn.product_id);
                jsonObjectDelivery.put("GIVEN_TIME", cn.given_time);
                jsonObjectDelivery.put("Full_Cyl_Given", cn.fresh_full_cylinder);
                jsonObjectDelivery.put("Giveen_By", cn.given_by);
                jsonObjectDelivery.put("empty_recieved", cn.empty_received);
                jsonObjectDelivery.put("SV", cn.sv_field);
                jsonObjectDelivery.put("DBC", cn.dbc_field);
                jsonObjectDelivery.put("Defective", cn.defective_field);
                jsonObjectDelivery.put("Return_Full", cn.return_full_cylinder);
                jsonObjectDelivery.put("Recieved_By", cn.received_by);
                jsonObjectDelivery.put("Recieved_time", cn.received_time);
                jsonObjectDelivery.put("Mode_of_Entry", cn.mode_of_entry);

                jsonArrayDelivery.put(jsonObjectDelivery);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            OpenHelperManager.releaseHelper();
        }

        /**
         *
         * Prepare JSONData in request server format
         *
         * */


        try {

            JSONObject deliveryJSON = new JSONObject();
            JSONArray deliveryArray = new JSONArray(jsonArrayDelivery.toString());
            deliveryJSON.put("ESS_DOMESTIC_DELIVERY", deliveryArray);
            setJsonObjectSyncAndroidDataToServer(deliveryJSON);

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }





    private DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }




}
