package com.infosolutions.ui.splash;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.crashlytics.android.Crashlytics;
import com.infosolutions.core.BaseActivity;
import com.infosolutions.database.CommercialDeliveryDB;
import com.infosolutions.database.ConsumerDetails;
import com.infosolutions.database.DatabaseHelper;
import com.infosolutions.database.DomesticDeliveryDB;
import com.infosolutions.database.TVDetailsDB;
import com.infosolutions.database.TruckDetailsDB;
import com.infosolutions.database.TruckSendDetailsDB;
import com.infosolutions.evita.R;
import com.infosolutions.network.Constants;
import com.infosolutions.network.VolleySingleton;
import com.infosolutions.service.GetConsumerService;
import com.infosolutions.service.TestJobService;
import com.infosolutions.ui.login.LoginActivity;
import com.infosolutions.utils.AppSettings;
import com.infosolutions.utils.Constant;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import io.fabric.sdk.android.Fabric;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import khangtran.preferenceshelper.PreferencesHelper;
import me.tatarka.support.job.JobInfo;
import me.tatarka.support.job.JobScheduler;
import me.tatarka.support.os.PersistableBundle;

import static com.infosolutions.network.Constants.KEY_AGENCY_NAME;
import static com.infosolutions.network.Constants.PREF_SYNC_JSON_DATA;

public class SplashActivity extends BaseActivity {

    private DatabaseHelper databaseHelper = null;
    public JSONObject localJSON_DATA;
    private JSONObject localData;
    private JobScheduler mJobScheduler;
    private static final int JOB_ID = 100;
    private static final String first_install = "first_install";


    @Override
    protected void onStart() {
        super.onStart();
        /*
        ProgressDialog pd;
        pd = ProgressDialog.show(this,"Please Wait...", "Loading Application..", false, true);
        pd.setCanceledOnTouchOutside(false);*/
        //mJobScheduler = JobScheduler.getInstance(this);
        //scheduleJob();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
      /*  requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
        super.onCreate(savedInstanceState);


        //Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_splash);

        broadcastIntent();

        gotoNextScreen();

        PreferencesHelper.initHelper(this, Constants.SHARED_PREF);

        checkIfFreshInstall();
    }

    private void checkIfFreshInstall() {

        boolean isFresh = PreferencesHelper.getInstance().getBooleanValue(first_install, true);
        if (isFresh) {
            PreferencesHelper.getInstance().setValue(first_install, false);
            Intent intent = new Intent(this, GetConsumerService.class);
            startService(intent);
        }else{

            RuntimeExceptionDao<ConsumerDetails,Integer> consumerExceptionDao = getHelper().getConsumerExceptionDao();

                int objCount = consumerExceptionDao.queryForAll().size();
                int prefsCount = PreferencesHelper.getInstance().getIntValue("consumer_count", 0);

                if(objCount != prefsCount){
                    Intent intent = new Intent(this, GetConsumerService.class);
                    startService(intent);
                }
        }
    }


    public void broadcastIntent() {
        Intent intent = new Intent();
        intent.setAction("com.infosolutions.evita");
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        sendBroadcast(intent);
    }


    @Override
    public void injectDependency() {

    }


    @Override
    public int getLayoutId() {
        return 0;
    }


    private void gotoNextScreen() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String stringJsonContent = fetchAllDataFromDB().toString();
                Log.e("local data content->", stringJsonContent);
                savePreferences(PREF_SYNC_JSON_DATA, stringJsonContent);


                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();

                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        }, 3000);
    }

    private void scheduleJob() {

        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, new ComponentName(this, TestJobService.class));
        PersistableBundle persistableBundle = new PersistableBundle();
        persistableBundle.putString("localData", AppSettings.getInstance(getApplicationContext()).getBodyJson(getApplicationContext()));
        builder.setPeriodic(10000).setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED).setPersisted(true)
                .setExtras(persistableBundle).build();

        int resultCode = mJobScheduler.schedule(builder.build());
        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            Log.d("message", "Job Scheduled");
            Log.e("message", "Job Scheduled");
        } else {
            Log.d("message", "Job not Scheduled");
            Log.e("message", "Job not Scheduled");
        }
    }


    public JSONObject fetchAllDataFromDB() {

        try {

            localJSON_DATA = new JSONObject();

            localJSON_DATA.put("ESS_ERV_DETAILS", getTruckSendDetailsData());
            localJSON_DATA.put("ESS_PURCHASE", getTruckDetailsData());
            localJSON_DATA.put("ESS_TV_DETAILS", getTVDetailsData());
            localJSON_DATA.put("ESS_DOMESTIC_DELIVERY", getDomesticData());
            localJSON_DATA.put("ESS_COMMERCIAL_DELIVERY", getCommercialData());

            setLocalData(localJSON_DATA);

        } catch (Exception ex) {
            Toast.makeText(context, "Invalid data", Toast.LENGTH_SHORT).show();
            ex.printStackTrace();

        }

        return localJSON_DATA;
    }

    private JSONArray getDomesticData() {

        JSONArray arrayDomestic = new JSONArray();

        RuntimeExceptionDao<DomesticDeliveryDB, Integer> domesticDB = getHelper().getDomesticRuntimeExceptionDao();
        List<DomesticDeliveryDB> allDomesticLocalData = domesticDB.queryForEq("is_sync", "N");
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

                    arrayDomestic.put(joDomestic);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        return arrayDomestic;
    }

    private JSONArray getCommercialData() {


        JSONArray objectCommercial = new JSONArray();
        RuntimeExceptionDao<CommercialDeliveryDB, Integer> commercialDB = getHelper().getCommercialRuntimeExceptionDao();
        List<CommercialDeliveryDB> allCommercialLocalData = commercialDB.queryForEq("is_sync", "N");


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

                    objectCommercial.put(commercialObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        return objectCommercial;
    }

    private JSONArray getTVDetailsData() {

        JSONArray objectTVDetails = new JSONArray();
        RuntimeExceptionDao<TVDetailsDB, Integer> tvDetails = getHelper().getTVDetailRTExceptionDao();
        List<TVDetailsDB> allTvDetails = tvDetails.queryForEq("is_sync", "N");
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

                    objectTVDetails.put(tvDetailObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }

        return objectTVDetails;
    }

    private JSONArray getTruckDetailsData() {

        JSONArray objectTruckDetails = new JSONArray();
        RuntimeExceptionDao<TruckDetailsDB, Integer> truckDetails = getHelper().getTruckDetailRTExceptionDao();
        List<TruckDetailsDB> allTruckDetails = truckDetails.queryForEq("is_sync", "N");

        if (allTruckDetails.size() > 0) {
            for (TruckDetailsDB cn : allTruckDetails) {
                try {

                    JSONObject truckDetailObject = new JSONObject();
                    truckDetailObject.put("Invoice_No", cn.invoiceNo);
                    truckDetailObject.put("Invoice_Date", cn.invoiceDate);
                    truckDetailObject.put("Vehical_Id", cn.vehicleId);
                    truckDetailObject.put("PCO_Vehical_No", cn.pcoVehicleNo);
                    truckDetailObject.put("Created_By", cn.createdBy);
                    truckDetailObject.put("Created_Date", cn.createdDate);
                    truckDetailObject.put("Id_product", cn.idProduct);
                    truckDetailObject.put("typeOfQuery", cn.typeOfQuery);
                    truckDetailObject.put("godown_code", cn.godownId);
                    truckDetailObject.put("Device_Id", cn.deviceId);
                    truckDetailObject.put("Sync_Time", getDateTime());
                    truckDetailObject.put("Mode_of_entry", "Mobile");

                    objectTruckDetails.put(truckDetailObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        return objectTruckDetails;
    }

    private JSONArray getTruckSendDetailsData() {

        JSONArray objectTruckSendDetails = new JSONArray();
        RuntimeExceptionDao<TruckSendDetailsDB, Integer> truckSendDetails = getHelper().getTruckDetailSendRTExceptionDao();
        List<TruckSendDetailsDB> allTruckSendDetails = truckSendDetails.queryForEq("is_sync", "N");
        if (allTruckSendDetails.size() > 0) {
            for (TruckSendDetailsDB cn : allTruckSendDetails) {
                try {
                    JSONObject truckDetailSend = new JSONObject();

                    truckDetailSend.put("ERV_No", cn.ervNo);
                    truckDetailSend.put("Id_product", cn.idProduct);
                    truckDetailSend.put("Vehical_Id", cn.vehicleId);
                    truckDetailSend.put("PCO_Vehical_No", cn.pcoVehicleNo);
                    truckDetailSend.put("Created_By", cn.createdBy);
                    truckDetailSend.put("Device_Id", cn.deviceId);
                    truckDetailSend.put("typeOfQuery", cn.typeOfQuery);
                    truckDetailSend.put("godown_code", cn.godown_Id);
                    truckDetailSend.put("Send_Date", cn.send_date);
                    truckDetailSend.put("Mode_of_entry", "Mobile");

                    objectTruckSendDetails.put(truckDetailSend);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return objectTruckSendDetails;
    }

    public JSONObject getLocalData() {
        return localData;
    }

    public void setLocalData(JSONObject localData) {
        this.localData = localData;
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

    private DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(getApplicationContext(), DatabaseHelper.class);
        }
        return databaseHelper;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }

    @Override
    public void onSuccess(VolleySingleton.CallType type, String response) {

    }

    @Override
    public void onFailure(VolleySingleton.CallType type, VolleyError error) {

    }
}
