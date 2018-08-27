package com.infosolutions.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.gson.JsonObject;
import com.infosolutions.database.CommercialDeliveryDB;
import com.infosolutions.database.DatabaseHelper;
import com.infosolutions.database.DomesticDeliveryDB;
import com.infosolutions.database.TVDetailsDB;
import com.infosolutions.database.TruckDetailsDB;
import com.infosolutions.database.TruckSendDetailsDB;
import com.infosolutions.evita.R;
import com.infosolutions.network.Constants;
import com.infosolutions.network.VolleySingleton;
import com.infosolutions.ui.MainActivity;
import com.infosolutions.ui.splash.SplashActivity;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.UpdateBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

public class AppSettings {

    private static JSONObject localJSON_DATA;
    private static JSONArray jsonArrayDomestic;
    private static JSONArray jsonArrayCommercial;
    private static JSONArray jsonArrayTVDetails;
    private static JSONArray jsonArrayTruckDetails;


    private static AppSettings _appsettings;
    private DatabaseHelper databaseHelper;
    private JSONArray jsonArrayTruckSendDetails;


    private String CHANNEL_ID = "1001";
    File backupDB;
    public String godownJson;

    private AppSettings() {
    }

    public static AppSettings getInstance(Context context) {

        //context = context;

        if (_appsettings == null) {
            _appsettings = new AppSettings();

        }


        return _appsettings;
    }

    public String getDateTime() {

        SimpleDateFormat simpleDateFormat = null;
        Date date = null;
        String dt = null;
        try {
            simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            dt = simpleDateFormat.format(Calendar.getInstance().getTime());
            Log.i("FORDATE", simpleDateFormat.format(Calendar.getInstance().getTime()));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return dt;
    }

    private DatabaseHelper getHelper(Context context) {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
        }
        return databaseHelper;
    }

    public void updateLocalFromServer(Context context) {
        VolleySingleton.getInstance(context.getApplicationContext()).fetch_all_data(VolleySingleton.CallType.UPDATE_LOCAL_DATA, Constants.get_url);
    }

    public void manualSyncAndroidDataToServer(Context context, JSONObject jsonObject) {


        VolleySingleton.getInstance(context.getApplicationContext()).test_syncAndroidData(VolleySingleton.CallType.SYNC_LOCAL_DATA, Constants.post_url, jsonObject);

    }


    public String getBodyJson(Context context) {
        Log.d("Local DB Content: ", "Reading Tables....");

        /*********************************************************************************************************/
        RuntimeExceptionDao<DomesticDeliveryDB, Integer> domesticDB =
                getHelper(context).getDomesticRuntimeExceptionDao();
        List<DomesticDeliveryDB> allDomesticLocalData =
                domesticDB.queryForEq("is_sync", "N");
        if (allDomesticLocalData.size() > 0) {
            Log.e("Domestic Data", allDomesticLocalData.toString());
        }

        /*********************************************************************************************************/
        RuntimeExceptionDao<CommercialDeliveryDB, Integer> commercialDB =
                getHelper(context).getCommercialRuntimeExceptionDao();
        List<CommercialDeliveryDB> allCommercialLocalData =
                commercialDB.queryForEq("is_sync", "N");
        if (allCommercialLocalData.size() > 0) {
            Log.e("Commercial Data", allCommercialLocalData.toString());
        }
        /*********************************************************************************************************/
        RuntimeExceptionDao<TVDetailsDB, Integer> tvDetails =
                getHelper(context).getTVDetailRTExceptionDao();
        List<TVDetailsDB> allTvDetails =
                tvDetails.queryForEq("is_sync", "N");
        if (allTvDetails.size() > 0) {
            Log.e("TV Details", allTvDetails.toString());
        }
        /*********************************************************************************************************/
        RuntimeExceptionDao<TruckDetailsDB, Integer> truckDetails = getHelper(context).getTruckDetailRTExceptionDao();
        List<TruckDetailsDB> allTruckDetails =
                truckDetails.queryForEq("is_sync", "N");
        if (allTruckDetails.size() > 0) {
            Log.e("Truck Details", allTruckDetails.toString());
        }
        /*********************************************************************************************************/
        RuntimeExceptionDao<TruckSendDetailsDB, Integer> truckSendDetails = getHelper(context).getTruckDetailSendRTExceptionDao();
        List<TruckSendDetailsDB> allTruckSendDetails = truckSendDetails.queryForEq("is_sync", "N");
        if (allTruckSendDetails.size() > 0) {
            Log.e("Truck Send Details", allTruckSendDetails.toString());
        }


        jsonArrayDomestic = new JSONArray();

        if (allDomesticLocalData.size() > 0) {

            for (DomesticDeliveryDB cn : allDomesticLocalData) {
                try {
                    JSONObject joDomestic = new JSONObject();

                    joDomestic.put("Given_Time", cn.given_time);
                    joDomestic.put("Emp_Id", cn.employee_id);
                    joDomestic.put("Trip_No", cn.trip_number);
                    joDomestic.put("typeOfQuery", cn.type_of_query);
                    joDomestic.put("ID_PRODUCT", cn.product_id);
                    joDomestic.put("Full_Cyl_Given", cn.fresh_full_cylinder);
                    joDomestic.put("Giveen_By", cn.given_by);
                    joDomestic.put("empty_recieved", cn.empty_received);
                    joDomestic.put("SV", cn.sv_field);
                    joDomestic.put("DBC", cn.dbc_field);
                    joDomestic.put("Defective", cn.defective_field);
                    joDomestic.put("Return_Full", cn.return_full_cylinder);
                    joDomestic.put("Recieved_By", cn.received_by);
                    joDomestic.put("Lost", cn.lost_cylinder);
                    joDomestic.put("Recieved_time", cn.received_time);
                    joDomestic.put("Mode_of_Entry", "Mobile");
                    joDomestic.put("godown_code", cn.godown_Id);
                    joDomestic.put("Device_Id", cn.deviceId);
                    joDomestic.put("Sync_Time", getDateTime());
                    joDomestic.put("isFresh", cn.isFresh);
                    joDomestic.put("isReturn", cn.isReturn);
                    joDomestic.put("isCompleted", cn.isCompleted);

                    jsonArrayDomestic.put(joDomestic);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }


        /* initialise commercial array */
        jsonArrayCommercial = new JSONArray();

        if (allCommercialLocalData.size() > 0) {

            for (CommercialDeliveryDB cn : allCommercialLocalData) {
                try {

                    JSONObject commercialObject = new JSONObject();
                    commercialObject.put("GIVEN_TIME", cn.given_time);
                    commercialObject.put("Emp_Id", cn.employee_id);
                    commercialObject.put("typeOfQuery", cn.type_of_query);
                    commercialObject.put("ID_PRODUCT", cn.product_id);
                    commercialObject.put("Full_Cyl_Given", cn.fresh_full_cylinder);
                    commercialObject.put("Given_By", cn.given_by);
                    commercialObject.put("Empty_Recieved", cn.empty_received);
                    commercialObject.put("SV", cn.sv_field);
                    commercialObject.put("DBC", cn.dbc_field);
                    commercialObject.put("Defective", cn.defective_field);
                    commercialObject.put("Return_Full", cn.return_full_cylinder);
                    commercialObject.put("Credit", cn.return_credit_given);
                    commercialObject.put("Recieved_By", cn.received_by);
                    commercialObject.put("Recieved_Time", cn.received_time);
                    commercialObject.put("Lost", cn.lost_cyl);
                    commercialObject.put("Mode_of_Entry", "Mobile");
                    commercialObject.put("godown_code", cn.godownId);
                    commercialObject.put("Device_Id", cn.deviceId);

                    jsonArrayCommercial.put(commercialObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }



        /* initialise commercial array */
        jsonArrayTVDetails = new JSONArray();
        if (allTvDetails.size() > 0) {
            for (TVDetailsDB cn : allTvDetails) {
                try {
                    JSONObject tvDetailObject = new JSONObject();
                    tvDetailObject.put("Consumer_No", cn.consumer_no);
                    tvDetailObject.put("Product_Code", cn.product_code);
                    tvDetailObject.put("No_of_Cyl", cn.numberOfCylinder);
                    tvDetailObject.put("typeOfQuery", cn.typeOfQuery);
                    tvDetailObject.put("Surrender_Date", cn.surrender_date);
                    tvDetailObject.put("godown_code", cn.godownId);
                    tvDetailObject.put("Device_Id", cn.deviceId);
                    tvDetailObject.put("Created_By", cn.userId);
                    tvDetailObject.put("Mode_of_Entry", "Mobile");
                    tvDetailObject.put("CREATED_DATE", cn.surrender_date);
                    tvDetailObject.put("TV_Status", cn.tv_status);

                    jsonArrayTVDetails.put(tvDetailObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }


        jsonArrayTruckDetails = new JSONArray();
        JSONArray productsJsonArray = new JSONArray();
        if (allTruckDetails.size() > 0) {

            for (int i = 0; i < allTruckDetails.size(); i++) {
                try {

                    TruckDetailsDB cn = allTruckDetails.get(i);
                    JSONObject truckDetailObject = new JSONObject();
                    JSONObject productJsonObject = new JSONObject();


                    if (i > 0) {
                        if (allTruckDetails.get(i - 1).invoiceNo.trim().equalsIgnoreCase(allTruckDetails.get(i).invoiceNo)) {
                            //product model
                            productJsonObject.put("Invoice_No", cn.invoiceNo);
                            productJsonObject.put("Id_product", cn.idProduct);
                            productJsonObject.put("Quantity", cn.Quantity);
                            productJsonObject.put("LostCylinder", cn.LostCylinder);
                            productJsonObject.put("godown_code", cn.godownId);

                            //get product json array and update with latest
                            JSONObject jsonObject = (JSONObject) jsonArrayTruckDetails.get(jsonArrayTruckDetails.length() - 1);
                            JSONArray jsonArray = jsonObject.optJSONArray("Products");
                            jsonArray.put(productJsonObject);
                            Log.d("modifiedJson", jsonArrayTruckDetails.toString());

                            continue;

                            //productsJsonArray.put(productJsonObject);
                            //truckDetailObject.put("Products", new JSONArray(productsJsonArray.toString()));

                            //jsonArrayTruckDetails.put(truckDetailObject);
                        } else {
                            //product model
                            productJsonObject.put("Invoice_No", cn.invoiceNo);
                            productJsonObject.put("Id_product", cn.idProduct);
                            productJsonObject.put("Quantity", cn.Quantity);
                            productJsonObject.put("LostCylinder", cn.LostCylinder);
                            productJsonObject.put("godown_code", cn.godownId);

                            productsJsonArray = new JSONArray();
                            productsJsonArray.put(productJsonObject);
                            truckDetailObject.put("Products", new JSONArray(productsJsonArray.toString()));


                        }
                    } else {
                        //product model
                        productJsonObject.put("Invoice_No", cn.invoiceNo);
                        productJsonObject.put("Id_product", cn.idProduct);
                        productJsonObject.put("Quantity", cn.Quantity);
                        productJsonObject.put("LostCylinder", cn.LostCylinder);
                        productJsonObject.put("godown_code", cn.godownId);

                        productsJsonArray.put(productJsonObject);
                        truckDetailObject.put("Products", new JSONArray(productsJsonArray.toString()));


                    }


                    truckDetailObject.put("Invoice_No", cn.invoiceNo);
                    truckDetailObject.put("Invoice_Date", cn.invoiceDate);
                    truckDetailObject.put("Vehical_Id", cn.vehicleId);
                    truckDetailObject.put("PCO_Vehical_No", cn.pcoVehicleNo);
                    truckDetailObject.put("Created_By", cn.createdBy);
                    truckDetailObject.put("Created_Date", cn.createdDate);
                    truckDetailObject.put("typeOfQuery", cn.typeOfQuery);
                    truckDetailObject.put("godown_code", cn.godownId);
                    truckDetailObject.put("Device_Id", cn.deviceId);
                    truckDetailObject.put("Sync_Time", getDateTime());
                    truckDetailObject.put("Mode_of_entry", "Mobile");


                    jsonArrayTruckDetails.put(truckDetailObject);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }


        jsonArrayTruckSendDetails = new JSONArray();
        JSONArray productsTrucksSendJsonArray = new JSONArray();
        int count = 0;
        if (allTruckSendDetails.size() > 0) {
            for (int i = 0; i < allTruckSendDetails.size(); i++) {
                try {

                    TruckSendDetailsDB cn = allTruckSendDetails.get(i);
                    JSONObject truckDetailSend = new JSONObject();
                    JSONObject productJsonObject = new JSONObject();

                    if (i > 0) {
                        int co = count;
                        if (allTruckSendDetails.get(i - 1).ervNo.trim().equalsIgnoreCase(allTruckSendDetails.get(i).ervNo)) {
                            //product model
                            productJsonObject.put("godown_code", cn.godown_Id);
                            productJsonObject.put("ERV_No", cn.ervNo);
                            productJsonObject.put("Id_product", cn.idProduct);
                            productJsonObject.put("Quantity", cn.Quantity);
                            productJsonObject.put("Defective", cn.Defective);

                            //get product json array and update with latest
                            JSONObject jsonObject = (JSONObject) jsonArrayTruckSendDetails.get(jsonArrayTruckSendDetails.length() - 1);
                            JSONArray jsonArray = jsonObject.optJSONArray("Products");
                            jsonArray.put(productJsonObject);
                            Log.d("modifiedJson", jsonArrayTruckDetails.toString());

                            continue;

                        } else {
                            //product model
                            productJsonObject.put("godown_code", cn.godown_Id);
                            productJsonObject.put("ERV_No", cn.ervNo);
                            productJsonObject.put("Id_product", cn.idProduct);
                            productJsonObject.put("Quantity", cn.Quantity);
                            productJsonObject.put("Defective", cn.Defective);

                            productsTrucksSendJsonArray = new JSONArray();
                            productsTrucksSendJsonArray.put(productJsonObject);
                            truckDetailSend.put("Products", new JSONArray(productsTrucksSendJsonArray.toString()));

                        }

                    } else {
                        //product model
                        productJsonObject.put("godown_code", cn.godown_Id);
                        productJsonObject.put("ERV_No", cn.ervNo);
                        productJsonObject.put("Id_product", cn.idProduct);
                        productJsonObject.put("Quantity", cn.Quantity);
                        productJsonObject.put("Defective", cn.Defective);

                        productsTrucksSendJsonArray.put(productJsonObject);
                        truckDetailSend.put("Products", new JSONArray(productsTrucksSendJsonArray.toString()));

                    }

                    truckDetailSend.put("ERV_No", cn.ervNo);
                    truckDetailSend.put("Vehical_Id", cn.vehicleId);
                    truckDetailSend.put("PCO_Vehical_No", cn.pcoVehicleNo);
                    truckDetailSend.put("Created_By", cn.createdBy);
                    truckDetailSend.put("Device_Id", cn.deviceId);
                    truckDetailSend.put("typeOfQuery", cn.typeOfQuery);
                    truckDetailSend.put("godown_code", cn.godown_Id);
                    truckDetailSend.put("Send_Date", cn.send_date);
                    truckDetailSend.put("Mode_of_entry", "Mobile");

                    jsonArrayTruckSendDetails.put(truckDetailSend);
                    count = jsonArrayTruckSendDetails.length();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }


        try {
            localJSON_DATA = new JSONObject();


            if (jsonArrayTruckDetails != null && jsonArrayTVDetails != null && jsonArrayDomestic != null && jsonArrayCommercial != null && jsonArrayTruckSendDetails != null) {

                try {
                    localJSON_DATA.put("ESS_ERV_DETAILS", new JSONArray(jsonArrayTruckSendDetails.toString()));
                    localJSON_DATA.put("ESS_PURCHASE", new JSONArray(jsonArrayTruckDetails.toString()));
                    localJSON_DATA.put("ESS_TV_DETAILS", new JSONArray(jsonArrayTVDetails.toString()));
                    localJSON_DATA.put("ESS_DOMESTIC_DELIVERY", new JSONArray(jsonArrayDomestic.toString()));
                    localJSON_DATA.put("ESS_COMMERCIAL_DELIVERY", new JSONArray(jsonArrayCommercial.toString()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        } catch (Exception e) {
            System.out.println(e);
        }


        return localJSON_DATA.toString();
    }

    public boolean isBodyJsonEmpy() {
        if (jsonArrayDomestic.length() == 0 && jsonArrayCommercial.length() == 0 && jsonArrayTVDetails.length() == 0 && jsonArrayTruckDetails.length() == 0 && jsonArrayTruckSendDetails.length() == 0) {
            // Notification(getApplicationContext(), "No data to sync");
            Log.e("DataSync", "No Data To Sync");
            return true;
        } else {
            return false;
        }


    }

    public void updateDatabase(Context context) {

        try {

            RuntimeExceptionDao<DomesticDeliveryDB, Integer> daoDatabase =
                    getHelper(context).getDomesticRuntimeExceptionDao();
            updateDomesticDB(daoDatabase);


            RuntimeExceptionDao<CommercialDeliveryDB, Integer> commercialDB =
                    getHelper(context).getCommercialRuntimeExceptionDao();
            updateCommercialDB(commercialDB);


            RuntimeExceptionDao<TVDetailsDB, Integer> tvDetails =
                    getHelper(context).getTVDetailRTExceptionDao();
            updateTVDetailDB(tvDetails);


            RuntimeExceptionDao<TruckDetailsDB, Integer> truckDetails =
                    getHelper(context).getTruckDetailRTExceptionDao();
            updateTruckDetailDB(truckDetails);


            RuntimeExceptionDao<TruckSendDetailsDB, Integer> truckSendDetails =
                    getHelper(context).getTruckDetailSendRTExceptionDao();
            updateTruckSendDetailDB(truckSendDetails);


        } catch (SQLException e) {
            Toast.makeText(context, "Invalid data received", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
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


    public void notification(Context mContext, String stringContent) {

        /*Intent intent = new Intent(mcontext, MainActivity.class);
        intent.putExtra("title", "");
        PendingIntent pIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(mcontext).setSmallIcon(R.drawable.evita_logo).setContentTitle(mcontext.getString(R.string.notificationtitle)).setContentText(stringContent).setContentIntent(pIntent).setAutoCancel(true);
        NotificationManager notificationmanager = (NotificationManager) mcontext.getSystemService(mcontext.NOTIFICATION_SERVICE);
        notificationmanager.notify(0, builder.build());*/


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                .setSmallIcon(R.drawable.evita_logo)
                .setContentTitle(mContext.getString(R.string.notificationtitle))
                .setContentText(stringContent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManager notificationmanager = (NotificationManager) mContext.getSystemService(mContext.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            mBuilder.setChannelId(CHANNEL_ID);
            notificationmanager.createNotificationChannel(notificationChannel);
        }


        notificationmanager.notify(0, mBuilder.build());
    }

    public void exportDatabase(Context context) {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();


            if (sd.canWrite()) {
                String currDBName = "DatabaseHelper.db";
                String currentDBPath = "/data/user/0/" + context.getPackageName() + "/databases/" + DatabaseHelper.DATABASE_NAME + "";
                String backupDBPath = "backup_Evita" + ".db";
                File currentDB = new File(currentDBPath);
                if(backupDB!= null && backupDB.exists()){
                    backupDB.delete();
                }

                backupDB = new File(sd, backupDBPath);
              /*  //test


                File file = new File(currentDBPath);
                FileInputStream fis = new FileInputStream(file);
                BufferedReader in = new BufferedReader(new InputStreamReader(fis));
                FileWriter fstream = new FileWriter(backupDB, true);
                BufferedWriter out = new BufferedWriter(fstream);
                String aLine = null;
                while ((aLine = in.readLine()) != null) {
                //Process each line and add output to Dest.txt file
                    out.write(aLine);
                    out.newLine();
                }
                // do not forget to close the buffer reader
                in.close();
                // close buffer writer
                out.close();

*/


                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                    //showSnackBar(getString(R.string.data_saved_to_local_disk));
                }
            }
        } catch (Exception e) {
            //showSnackBar(getString(R.string.error_while_exporing_data));
            e.printStackTrace();
        }
    }
}
