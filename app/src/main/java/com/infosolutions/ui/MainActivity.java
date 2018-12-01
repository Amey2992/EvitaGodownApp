package com.infosolutions.ui;

import android.Manifest;
import android.Manifest.permission;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.commercialMgmt.AddNewConsumerActivity;
import com.commercialMgmt.CommercialSaleActivity;
import com.infosolutions.adapter.ModuleAdapter;
import com.infosolutions.adapter.ModuleGridAdapter;
import com.infosolutions.adapter.ModuleModel;
import com.infosolutions.core.BaseActivity;
import com.infosolutions.core.EvitaApplication;
import com.infosolutions.database.CommercialDeliveryCreditDB;
import com.infosolutions.database.DatabaseHelper;
import com.infosolutions.database.DomesticDeliveryDB;
import com.infosolutions.event.EvitaEvent;
import com.infosolutions.evita.R;
import com.infosolutions.factory.IntentFactory;
import com.infosolutions.network.Constants;
import com.infosolutions.network.VolleySingleton;
import com.infosolutions.ui.login.LoginActivity;
import com.infosolutions.ui.user.commercial.CommercialActivity;
import com.infosolutions.ui.user.domestic.DomesticActivity;
import com.infosolutions.ui.user.reports.ReportListItemsActivity;
import com.infosolutions.ui.user.setting.SettingsActivity;
import com.infosolutions.ui.user.stock.StockListActivity;
import com.infosolutions.ui.user.stock.StockTransferActivity;
import com.infosolutions.ui.user.tvdetails.TVDetailsActivity;
import com.infosolutions.utils.AppSettings;
import com.infosolutions.utils.GlobalVariables;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.kcode.bottomlib.BottomDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import khangtran.preferenceshelper.PreferencesHelper;

import static com.infosolutions.network.Constants.KEY_GODOWN;
import static com.infosolutions.network.Constants.KEY_GODOWN_NAME;


public class MainActivity extends BaseActivity {

    private static final int RECORD_REQUEST_CODE = 3367;
    private static final int RQ_DEVICE_STATE = 3360;
    boolean doubleBackToExitPressedOnce = false;

    @Inject
    EventBus eventBus;
    Timer mtimer;
    TimerTask mTimerTask;
    private RecyclerView recyclerView;
    private List<ModuleModel> listModel = new ArrayList<>();
    private String view_type;
    private DatabaseHelper databaseHelper;

    // auto sync timer
    private android.content.BroadcastReceiver listener = new BroadcastReceiver() {
        @Override
        public void onReceive( Context context, Intent intent ) {
            stopTimer();
            if(login_type.equalsIgnoreCase(Constants.LOGIN_GODOWNKEEPER)) {
                startTimer();
            }

        }
    };
    private String login_type;

    @Override
    public void onSuccess(VolleySingleton.CallType type, String response) {
        serverSuccessResponse(type, response);
    }

    @Override
    public void onFailure(VolleySingleton.CallType type, VolleyError error) {
        serverFailResponse(error);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //PreferencesHelper.initHelper(this, "SharedPref");
        login_type = PreferencesHelper.getInstance().getStringValue(Constants.LOGIN_TYPE,"");
        Log.d("oncreate_logintype",login_type);
        loadToolbar();
        loadModuleList();
        initialiseUI();
        loadListView();

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                goToNextActivity(view, position);
            }

            @Override
            public void onLongClick(View view, int position) {
                goToNextActivity(view, position);
            }
        }));


        if(login_type.equalsIgnoreCase(Constants.LOGIN_GODOWNKEEPER)) {
            startTimer();
        }else{
            stopTimer();
        }
        permission();
        eventBus.register(this);

        VolleySingleton.getInstance(getApplicationContext()).addResponseListener(VolleySingleton.CallType.SYNC_LOCAL_DATA, this);
        VolleySingleton.getInstance(getApplicationContext()).addResponseListener(VolleySingleton.CallType.UPDATE_LOCAL_DATA, this);
        VolleySingleton.getInstance(getApplicationContext()).addResponseListener(VolleySingleton.CallType.COMMERCIAL_DELIVERY_COUNT, this);

        /*LocalBroadcastManager .getInstance(this).registerReceiver(listener,
                new IntentFilter(Constants.RESET_TIMER_BROADCAST));
*/
    }

    private void startTimer() {
        mtimer = new Timer();
        mTimerTask = new TimerTask() {
            public void run() {

                if (!AppSettings.getInstance(MainActivity.this).isSyncing) {
                    syncData();
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Toast.makeText(MainActivity.this, getResources().getString(R.string.syncing_text), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        };

        mtimer.scheduleAtFixedRate(mTimerTask, 200000, 200000);
        //mtimer.scheduleAtFixedRate(mTimerTask, 10000,10000);
    }

    private void stopTimer(){
        if(mTimerTask != null){
            mTimerTask.cancel();
        }

        if(mtimer != null){
            mtimer.cancel();
        }
    }

    @Override
    public void injectDependency() {
        EvitaApplication.getEvitaComponents().inject(this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.content_main;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_module, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_list_view:

                if (view_type.equalsIgnoreCase("list_view")) {
                    item.setIcon(getResources().getDrawable(R.drawable.ic_dns_black));
                    loadGridView();
                } else {
                    item.setIcon(getResources().getDrawable(R.drawable.ic_dashboard));
                    loadListView();
                }

                break;

            case R.id.action_setting:
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                break;

            case R.id.action_transfer:
                startActivity(new Intent(getApplicationContext(), StockTransferActivity.class));
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                break;

            case R.id.action_logout:
                logout();
                break;
        }
        return true;
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
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                           String permissions[], int[] grantResults) {
            switch (requestCode) {
            case RECORD_REQUEST_CODE: {

                if (grantResults.length == 0
                        || grantResults[0] !=
                        PackageManager.PERMISSION_GRANTED) {

                    showSnackBar(getString(R.string.permis_denied));

                } else {
                    showSnackBar(getString(R.string.permis_sd_card));
                }
                return;
            }

            case RQ_DEVICE_STATE: {
                if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    showSnackBar(getString(R.string.permis_denied));
                } else {
                    showSnackBar(getString(R.string.permis_sd_card));
                }

                return;
            }
        }
    }

    private void permission() {

        if (!GlobalVariables.permissionsEnabled(MainActivity.this, new String[]{permission.WRITE_EXTERNAL_STORAGE})) {
            ActivityCompat.requestPermissions(this, new String[]
                    {
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, RECORD_REQUEST_CODE);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
        eventBus.unregister(this);

        LocalBroadcastManager.getInstance(this).unregisterReceiver(listener);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EvitaEvent.EventDataSyncToServer event) {
        if (event != null && event.isDataSynced()) {
            showSnackBar(getString(R.string.data_to_sync_server));
        }
    }

    private void goToNextActivity(View view, int position) {

        ModuleModel model = listModel.get(position);
        String selectedModuleType = model.getModuleId();

        if(login_type.equalsIgnoreCase(Constants.LOGIN_GODOWNKEEPER)) {
            if (selectedModuleType.equalsIgnoreCase("1")) {
                startActivity(new Intent(getApplicationContext(), DomesticActivity.class));
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            } else if (selectedModuleType.equalsIgnoreCase("2")) {
                startActivity(new Intent(getApplicationContext(), CommercialActivity.class));
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            } else if (selectedModuleType.equalsIgnoreCase("3")) {
                Intent intent = IntentFactory.getIntent(this, IntentFactory.TRUCKDELIVERYACTIVITY);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            } else if (selectedModuleType.equalsIgnoreCase("4")) {
                startActivity(new Intent(getApplicationContext(), TVDetailsActivity.class));
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            } else if (selectedModuleType.equalsIgnoreCase("5")) {
                startActivity(new Intent(getApplicationContext(), StockListActivity.class));
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            } else if (selectedModuleType.equalsIgnoreCase("6")) {
                startActivity(new Intent(getApplicationContext(), ReportListItemsActivity.class));
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        }
        else{
            if (selectedModuleType.equalsIgnoreCase("7")) {
                startActivity(new Intent(getApplicationContext(), AddNewConsumerActivity.class));
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
            else if (selectedModuleType.equalsIgnoreCase("8")) {
                startActivity(new Intent(getApplicationContext(), CommercialSaleActivity.class));
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }else if (selectedModuleType.equalsIgnoreCase("9")) {
                startActivity(new Intent(getApplicationContext(), ReportListItemsActivity.class));
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        }
    }

    private void loadModuleList() {

        String moduleKEY = this.getResources().getString(R.string.key_shared_pref_module_list);
        String moduleVALUE = Constants.getSharedPrefWithKEY(getApplicationContext(), moduleKEY);

        try {
            JSONArray jsonArray = new JSONArray(moduleVALUE.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonModel = jsonArray.optJSONObject(i);
                String imagePathUrl = jsonModel.optString("imagePath");
                String moduleName = jsonModel.optString("name");
                String modulePosition = jsonModel.optString("seq");

                listModel.add(new ModuleModel(imagePathUrl, moduleName, modulePosition));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadToolbar() {

            Toolbar toolbar = findViewById(R.id.toolbar);
            TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
            mTitle.setText(R.string.godown);

            String SELECTED_GODOWN_NAME = PreferencesHelper.getInstance().getStringValue(KEY_GODOWN_NAME, "Empty");
            String SELECTED_GODOWN_CODE = PreferencesHelper.getInstance().getStringValue(KEY_GODOWN, "Empty");
            mTitle.setText(SELECTED_GODOWN_NAME + "( " + SELECTED_GODOWN_CODE + " )");

            //showToast(getPreferences(Constants.KEY_AGENCY_NAME));
            setSupportActionBar(toolbar);
    }

    @Override
        public boolean onPrepareOptionsMenu(Menu menu) {

        login_type = PreferencesHelper.getInstance().getStringValue(Constants.LOGIN_TYPE,"");
        if(login_type.equalsIgnoreCase(Constants.LOGIN_GODOWNKEEPER)) {
            Log.d("loginType:",Constants.LOGIN_GODOWNKEEPER);
            menu.findItem(R.id.action_transfer).setVisible(true);
            menu.findItem(R.id.action_setting).setVisible(true);
            menu.findItem(R.id.action_logout).setVisible(false);
        }else{
            Log.d("loginType:",Constants.LOGIN_DELIVERYMAN);
            menu.findItem(R.id.action_transfer).setVisible(false);
            menu.findItem(R.id.action_setting).setVisible(false);
            menu.findItem(R.id.action_logout).setVisible(true);
        }
        return true;
    }

    private void loadGridView() {

        view_type = "grid_view";
        ModuleGridAdapter gridAdapter = new ModuleGridAdapter(this, listModel);
        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(gridAdapter);
    }

    private void loadListView() {

        view_type = "list_view";
        ModuleAdapter listAdapter = new ModuleAdapter(this, listModel);
        LinearLayoutManager verticalManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(verticalManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(listAdapter);
    }

    private void initialiseUI() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
    }

    private void serverSuccessResponse(VolleySingleton.CallType type, String response) {
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
                    AppSettings.getInstance(this).getCommercialDeliveryCreditCount(this);

                    /*updateDatabase();

                    EvitaEvent.EventDataSyncToServer eventDataSyncToServer = new EventDataSyncToServer();
                    eventDataSyncToServer.setDataSynced(true);
                    eventBus.post(eventDataSyncToServer);

                    finish();
*/
                } else {
                    AppSettings.getInstance(this).isSyncing = false;
                    //Toast.makeText(this, responseMsg, Toast.LENGTH_SHORT).show();
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


            AppSettings.getInstance(this).updateDatabase(this);
            //AppSettings.getInstance(this).notification(getApplicationContext(), responseMsg);
            AppSettings.getInstance(this).isSyncing = false;

            EvitaEvent.EventDataSyncToServer eventDataSyncToServer = new EvitaEvent.EventDataSyncToServer();
            eventDataSyncToServer.setDataSynced(false);
            eventBus.post(eventDataSyncToServer);
        } else if (type.equals(VolleySingleton.CallType.COMMERCIAL_DELIVERY_COUNT)) {

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

    private void serverFailResponse(VolleyError error) {
        AppSettings.getInstance(this).isSyncing = false;
    }

    private void autoSync() {
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                syncData();
            }
        });
    }

    void syncData() {
        try {
            AppSettings.getInstance(this).isSyncing = true;
            JSONObject localJSON_DATA = new JSONObject(AppSettings.getInstance(this).getBodyJson(this));
            AppSettings.getInstance(this).manualSyncAndroidDataToServer(this, localJSON_DATA);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
    }

    private DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(getApplicationContext(), DatabaseHelper.class);
        }
        return databaseHelper;
    }

    private interface ClickListener {

        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    private class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private MainActivity.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final MainActivity.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }
    }

}

