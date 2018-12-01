package com.infosolutions.ui.user.setting;


import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.infosolutions.core.BaseActivity;
import com.infosolutions.core.EvitaApplication;
import com.infosolutions.database.CommercialDeliveryCreditDB;
import com.infosolutions.database.CommercialDeliveryDB;
import com.infosolutions.database.DatabaseHelper;
import com.infosolutions.database.DomesticDeliveryDB;
import com.infosolutions.database.TVDetailsDB;
import com.infosolutions.database.TruckDetailsDB;
import com.infosolutions.database.TruckSendDetailsDB;
import com.infosolutions.event.EvitaEvent;
import com.infosolutions.event.EvitaEvent.EventDataSyncToServer;
import com.infosolutions.evita.R;
import com.infosolutions.network.Constants;
import com.infosolutions.network.ResponseListener;
import com.infosolutions.network.VolleySingleton;
import com.infosolutions.service.TestJobService;
import com.infosolutions.ui.login.LoginActivity;
import com.infosolutions.utils.AppSettings;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.table.TableUtils;
import com.kcode.bottomlib.BottomDialog;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.sql.SQLException;
import java.util.List;

import javax.inject.Inject;


public class SettingsActivity extends BaseActivity implements ResponseListener {

    private DatabaseHelper databaseHelper = null;
    private ProgressBar progress_bar;
    private Button btnLogout;
    private ImageView ivSync;
    public JSONObject localJSON_DATA;
    private JSONObject jsonObjectSyncAndroidDataToServer;
    File backupDB;
    TextView appVersion;

    @Inject
    EventBus eventBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(R.string.action_settings);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_backspace);

        initID();
    }

    @Override
    public void injectDependency() {
        EvitaApplication.getEvitaComponents().inject(this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.screen_setting;
    }

    private void initID() {

        btnLogout = (Button) findViewById(R.id.btnLogout);
        progress_bar = (ProgressBar) findViewById(R.id.progress_bar);
        ivSync = (ImageView) findViewById(R.id.ivSync);
        appVersion = (TextView) findViewById(R.id.appVersion);
        appVersion.setText(AppSettings.getInstance(this).getAppVersion(this));
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

        VolleySingleton.getInstance(getApplicationContext()).addResponseListener(VolleySingleton.CallType.SYNC_LOCAL_DATA, this);
        VolleySingleton.getInstance(getApplicationContext()).addResponseListener(VolleySingleton.CallType.UPDATE_LOCAL_DATA, this);
        VolleySingleton.getInstance(getApplicationContext()).addResponseListener(VolleySingleton.CallType.SYNC_OTHERS, this);
        VolleySingleton.getInstance(getApplicationContext()).addResponseListener(VolleySingleton.CallType.COMMERCIAL_DELIVERY_COUNT,this);

        ivSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivSync.setEnabled(false);
                pushContentToServer();
            }
        });

        findViewById(R.id.ivExport).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportDB();
            }
        });

    }

    private void manualSyncAndroidDataToServer() {
        progress_bar.setVisibility(View.GONE);
        ivSync.setEnabled(true);
        showProgressDialog();
        //VolleySingleton.getInstance(getApplicationContext()).test_syncAndroidData(VolleySingleton.CallType.SYNC_LOCAL_DATA, Constants.post_url, getJsonObjectSyncAndroidDataToServer());
        AppSettings.getInstance(this).manualSyncAndroidDataToServer(this, localJSON_DATA);

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

                responseMsg = jsonResult.optString("responseMessage");
                if (jsonResult.optString("responseCode").equalsIgnoreCase("200")) {
                    //updateLocalFromServer();
                    AppSettings.getInstance(this).getCommercialDeliveryCreditCount(this);

                    /*updateDatabase();

                    EvitaEvent.EventDataSyncToServer eventDataSyncToServer = new EventDataSyncToServer();
                    eventDataSyncToServer.setDataSynced(true);
                    eventBus.post(eventDataSyncToServer);

                    finish();
*/
                } else {
                    progress_bar.setVisibility(View.GONE);
                    hideProgressDialog();
                    ivSync.setEnabled(true);
                    Toast.makeText(this, responseMsg, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (type.equals(VolleySingleton.CallType.UPDATE_LOCAL_DATA)) {


            RuntimeExceptionDao<DomesticDeliveryDB, Integer> daoDatabase =
                    getHelper().getDomesticRuntimeExceptionDao();

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


            //updateDatabase();
            AppSettings.getInstance(this).updateDatabase(this);

            EventDataSyncToServer eventDataSyncToServer = new EventDataSyncToServer();
            eventDataSyncToServer.setDataSynced(true);
            eventBus.post(eventDataSyncToServer);

            progress_bar.setVisibility(View.GONE);
            ivSync.setEnabled(true);
            hideProgressDialog();
            finish();


        }else if(type.equals(VolleySingleton.CallType.COMMERCIAL_DELIVERY_COUNT)){
            RuntimeExceptionDao<CommercialDeliveryCreditDB, Integer> daoDatabase =
                    getHelper().getCommercialCreditExceptionDao();

            try {
                daoDatabase.deleteBuilder().delete();
            } catch (SQLException e) {
                e.printStackTrace();
            }


            JSONArray jsonArray = jsonResult.optJSONArray("Table");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.optJSONObject(i);
                CommercialDeliveryCreditDB commercialDeliveryCreditDB = new CommercialDeliveryCreditDB(jsonObject);
                daoDatabase.createOrUpdate(commercialDeliveryCreditDB);
            }

            AppSettings.getInstance(this).updateLocalFromServer(this);

        }
    }

    public void updateLocalFromServer() {
        showProgressDialog();
        VolleySingleton.getInstance(getApplicationContext()).fetch_all_data(VolleySingleton.CallType.UPDATE_LOCAL_DATA, Constants.get_url);
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


    private void pushContentToServer() {
        try {
            localJSON_DATA = new JSONObject(AppSettings.getInstance(this).getBodyJson(this));
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        if (AppSettings.getInstance(this).isBodyJsonEmpy()) {
            updateLocalFromServer();
            return;
        }

        setJsonObjectSyncAndroidDataToServer(localJSON_DATA);

        manualSyncAndroidDataToServer();
    }

    private DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
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

    public JSONObject getJsonObjectSyncAndroidDataToServer() {
        return jsonObjectSyncAndroidDataToServer;
    }

    public void setJsonObjectSyncAndroidDataToServer(JSONObject jsonObjectSyncAndroidDataToServer) {
        this.jsonObjectSyncAndroidDataToServer = jsonObjectSyncAndroidDataToServer;
    }


    public void exportDB() {
        File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();


        if (sd.canWrite()) {
            String currDBName = "DatabaseHelper.db";
            String currentDBPath = "/data/user/0/" + context.getPackageName() + "/databases/" + DatabaseHelper.DATABASE_NAME + "";
            String backupDBPath = "backup_Evita" + ".db";
            File currentDB = new File(currentDBPath);
            if (backupDB != null && backupDB.exists()) {
                backupDB.delete();
            }

            backupDB = new File(sd, backupDBPath);


            if (currentDB.exists()) {
                FileChannel src = null;
                try {
                    src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();

                    try {
                        dst.transferFrom(src, 0, src.size());
                        src.close();
                        dst.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                showSnackBar(getString(R.string.data_saved_to_local_disk));
            }
        }
    }


    public void ReadDB(String evitaDB) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        builder.setTitle("Evita");
        builder.setMessage(evitaDB);
        builder.create();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void logout() {
        BottomDialog dialog = BottomDialog.newInstance("Are you sure do you want logout ?", "Dismiss", new String[]{"YES", "NO"});
        dialog.show(getSupportFragmentManager(), "dialog");
        dialog.setListener(new BottomDialog.OnClickListener() {
            @Override
            public void click(int position) {
                switch (position) {
                    case 0:
                        Intent next = new Intent(getApplicationContext(), LoginActivity.class);
                        next.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(next);
                        finish();
                    case 1:

                }
            }
        });


    }


    @Override
    public void onFailure(VolleySingleton.CallType type, VolleyError error) {
        progress_bar.setVisibility(View.GONE);
        hideProgressDialog();
        ivSync.setEnabled(true);
        showSnackBar(getString(R.string.could_not_sync));
        Toast.makeText(this, R.string.server_not_responding, Toast.LENGTH_SHORT).show();
    }

}
