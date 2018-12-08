package com.infosolutions.ui.login;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;


import com.android.volley.VolleyError;
import com.commercialMgmt.models.CommercialProductModel;
import com.commercialMgmt.models.ConsumerModel;
import com.commercialMgmt.models.UserAssignedCylinderModel;
import com.google.gson.JsonObject;
import com.infosolutions.core.BaseActivity;
import com.infosolutions.core.EvitaApplication;
import com.infosolutions.database.CommercialDeliveryCreditDB;
import com.infosolutions.database.CommercialDeliveryDB;
import com.infosolutions.database.ConsumerDetails;
import com.infosolutions.database.DatabaseHelper;
import com.infosolutions.database.DomesticDeliveryDB;
import com.infosolutions.database.EmployeeDB;
import com.infosolutions.database.ProductDB;
import com.infosolutions.database.TVDetailsDB;
import com.infosolutions.database.TruckDetailsDB;
import com.infosolutions.database.TruckSendDetailsDB;
import com.infosolutions.database.VehicleDB;
import com.infosolutions.evita.R;
import com.infosolutions.network.Constants;
import com.infosolutions.network.VolleySingleton;
import com.infosolutions.service.GetCommercialConsumerService;
import com.infosolutions.service.GetConsumerService;
import com.infosolutions.ui.MainActivity;
import com.infosolutions.ui.owner.OwnerDashboardActivity;
import com.infosolutions.ui.user.tvdetails.TVDetailsActivity;
import com.infosolutions.utils.AppSettings;
import com.infosolutions.utils.Constant;
import com.infosolutions.utils.GlobalVariables.LOGINKEY;
import com.infosolutions.utils.GlobalVariables.Response;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import de.mrapp.android.bottomsheet.BottomSheet;
import khangtran.preferenceshelper.PreferencesHelper;

import static com.infosolutions.network.Constants.KEY_AGENCY_NAME;
import static com.infosolutions.network.Constants.KEY_GODOWN;
import static com.infosolutions.network.Constants.KEY_GODOWN_NAME;
import static com.infosolutions.network.Constants.LOGIN_GODOWNKEEPER;
import static com.infosolutions.network.Constants.LOGIN_DELIVERYMAN;
import static com.infosolutions.network.Constants.PREF_DATE;
import static com.infosolutions.network.Constants.SHARED_PREF;
import static com.infosolutions.network.Constants.LOGIN_TYPE;
import static com.infosolutions.network.Constants.saveWithSharedPreferences;


public class LoginActivity extends BaseActivity {

    private String TAG = LoginActivity.class.getSimpleName();
    private EditText editTextUsername, editTextPassword;
    private AppCompatButton btnLogin, btnCommercialLogin;
    private ScrollView scrollView;
    private DatabaseHelper databaseHelper = null;
    private String offline_module_list;
    private JSONArray GO_DOWN_ARRAY_LIST;
    private String USER_TYPE;
    private TextView version_textview;
    private ArrayList<ConsumerModel> consumerDetailsList;
    private String login_type;

    public String getUSER_ID() {
        return USER_ID;
    }

    public void setUSER_ID(String USER_ID) {
        this.USER_ID = USER_ID;
    }

    private String USER_ID;
    private String AGENCY_NAME;
    private TextView tvAgencyName;

    @Inject
    EventBus eventBus;


    @Override
    protected void onStart() {
        super.onStart();
        getAgencyName();
    }

    private void getAgencyName() {

        VolleySingleton.getInstance(getApplicationContext())
                .addResponseListener(VolleySingleton.CallType.AGENCY_NAME, this);

        VolleySingleton.getInstance(getApplicationContext())
                .apiGetAgencyName(VolleySingleton.CallType.AGENCY_NAME, Constants.AGENCY_NAME);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //PreferencesHelper.initHelper(this, Constants.SHARED_PREF);
        initIds();
        VolleySingleton.getInstance(getApplicationContext()).addResponseListener(VolleySingleton.CallType.SYNC_LOCAL_DATA, this);
        VolleySingleton.getInstance(getApplicationContext()).addResponseListener(VolleySingleton.CallType.UPDATE_LOCAL_DATA, this);
        VolleySingleton.getInstance(getApplicationContext()).addResponseListener(VolleySingleton.CallType.COMMERCIAL_DELIVERY_COUNT, this);

    }

    @Override
    public void injectDependency() {
        EvitaApplication.getEvitaComponents().inject(LoginActivity.this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.content_login;
    }


    private void initIds() {

        editTextUsername = findViewById(R.id.input_username);
        editTextPassword = findViewById(R.id.input_password);
        btnLogin = findViewById(R.id.buttonLogin);
        btnCommercialLogin = findViewById(R.id.commercialbuttonLogin);
        tvAgencyName = findViewById(R.id.tvAgencyName);
        scrollView = findViewById(R.id.scrollView);
        version_textview = findViewById(R.id.version_textview);
        version_textview.setVisibility(View.GONE);
        version_textview.setText(AppSettings.getInstance(this).getAppVersion(this) +" " + (Constants.dbname));

        focusOnView(scrollView, editTextUsername);

        btnCommercialLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //PreferencesHelper.getInstance().setValue(Constants.LOGIN_TYPE,Constants.LOGIN_DELIVERYMAN);
                if (getTextString(editTextUsername).equalsIgnoreCase("")) {
                    focusOnView(scrollView, editTextUsername);
                    showErrorToast(LoginActivity.this, "Error", getResources().getString(R.string.proide_username));
                } else if (getTextString(editTextPassword).equalsIgnoreCase("")) {
                    focusOnView(scrollView, editTextPassword);
                    showErrorToast(LoginActivity.this, "Error", getResources().getString(R.string.empty_password));
                } else {

                    if (Constants.isNetworkAvailable(getApplicationContext())) {
                        showProgressDialog();

                        VolleySingleton.getInstance(getApplicationContext()).addResponseListener(VolleySingleton.CallType.USER_COMMERCIAL_LOGIN, LoginActivity.this);
                        VolleySingleton.getInstance(getApplicationContext())
                                .new_apiCallLoginValidation(VolleySingleton.CallType.USER_COMMERCIAL_LOGIN, Constants.LOGIN_URL, getTextString(editTextUsername),
                                        getTextString(editTextPassword), "Android",true);
                    } else {
                        showErrorToast(LoginActivity.this, "Error", getResources().getString(R.string.no_network_available));
                    }
                }
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (getTextString(editTextUsername).equalsIgnoreCase("")) {
                    focusOnView(scrollView, editTextUsername);
                    showErrorToast(LoginActivity.this, "Error", getResources().getString(R.string.proide_username));
                } else if (getTextString(editTextPassword).equalsIgnoreCase("")) {
                    focusOnView(scrollView, editTextPassword);
                    showErrorToast(LoginActivity.this, "Error", getResources().getString(R.string.empty_password));
                } else {

                    if (Constants.isNetworkAvailable(getApplicationContext())) {
                        showProgressDialog();

                        VolleySingleton.getInstance(getApplicationContext()).addResponseListener(VolleySingleton.CallType.USER_LOGIN, LoginActivity.this);
                        VolleySingleton.getInstance(getApplicationContext())
                                .apiCallLoginValidation(VolleySingleton.CallType.USER_LOGIN, Constants.LOGIN_URL, getTextString(editTextUsername),
                                        getTextString(editTextPassword), "Android");
                    } else {
                        showErrorToast(LoginActivity.this, "Error", getResources().getString(R.string.no_network_available));
                    }
                }
            }
        });

    }


    private void serverSuccessResponse(String response) {

        try {
            JSONObject objectESS = new JSONObject(response);
            String responseMessage = objectESS.optString("responseMessage");
            String responseCode = objectESS.optString("ResponseCode");

            if (objectESS.has("USERNAME")) {

                String USERNAME = objectESS.optString("USERNAME");
                SharedPreferences sharedpreferences = getSharedPreferences("USERNAME_PREF", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("username", USERNAME);
                editor.commit();
                editor.apply();

                Log.e("USER_NAME", USERNAME);
            }


            if (responseMessage.equalsIgnoreCase(Response.SUCCESS) && responseCode.equalsIgnoreCase(String.valueOf(Response.RESPONSECODE))) {

                USER_TYPE = objectESS.optString("USERTYPE");
                if (objectESS.has("AGENCY_NAME")) {
                    AGENCY_NAME = objectESS.optString("AGENCY_NAME");
                    PreferencesHelper.getInstance().setValue(KEY_AGENCY_NAME, AGENCY_NAME);
                }

                setUSER_TYPE(USER_TYPE);

               /* if (USER_TYPE.equalsIgnoreCase(LOGINKEY.TYPE_USER)) {
                    userTypeMode(objectESS);
                } else if (USER_TYPE.equalsIgnoreCase(LOGINKEY.TYPE_OWNER)) {
                    String ownerType = objectESS.optString("OWNER_DATA");
                    ownerTypeMode(ownerType);
                }*/
                if (USER_TYPE.equalsIgnoreCase(LOGINKEY.TYPE_OWNER)) {

                }else{
                    userTypeMode(objectESS);
                }

                String str = PreferencesHelper.getInstance().getStringValue("login_type","");
                clearPreviousData();
                syncData();
            } else {
                showErrorToast(LoginActivity.this, "Error", responseMessage);
            }

        } catch (JSONException e) {

            AlertDialog.Builder builder1 = new android.support.v7.app.AlertDialog.Builder(this);
            builder1.setTitle("Invalid Response");
            builder1.setMessage(getResources().getString(R.string.exception_message) + "\nError Type: " + e.getMessage());
            builder1.setCancelable(true);
            AlertDialog alert11 = builder1.create();
            alert11.show();

            e.printStackTrace();
        }


    }

    void syncData() {
        try {
            JSONObject localJSON_DATA = new JSONObject(AppSettings.getInstance(this).getBodyJson(this));
            AppSettings.getInstance(this).manualSyncAndroidDataToServer(this, localJSON_DATA);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
    }

    private void ownerTypeMode(String owner_resp) {
        Log.e(TAG, owner_resp);
        Intent intent = new Intent(getApplicationContext(), OwnerDashboardActivity.class);
        intent.putExtra("owner_resp", owner_resp);
        startActivity(intent);
    }


    private void serverFailResponse(VolleyError error) {

        String errorMessage = "Server is not responding: " + error.getLocalizedMessage();
        if (error.getLocalizedMessage() == null) {
            errorMessage = getResources().getString(R.string.server_not_responding);
        }

        showErrorToast(LoginActivity.this, "Error", errorMessage);
    }

    @Override
    public void onSuccess(VolleySingleton.CallType type, String response) {

        Log.e(TAG, response);


        String responseMsg = "";
        //progress_bar.setVisibility(View.GONE);
        JSONObject jsonResult = null;
        try {
            jsonResult = new JSONObject(response);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String usertype = jsonResult.optString("USERTYPE");
        responseMsg = jsonResult.optString("responseMessage");

        if (type.equals(VolleySingleton.CallType.SYNC_LOCAL_DATA)) {

            try {
                String responseCode = jsonResult.optString("responseCode");

                if (jsonResult.optString("responseCode").equalsIgnoreCase("200")) {
                    AppSettings.getInstance(this).getCommercialDeliveryCreditCount(this);
                    //AppSettings.getInstance(this).updateLocalFromServer(this);
                    /*updateDatabase();

                    EvitaEvent.EventDataSyncToServer eventDataSyncToServer = new EventDataSyncToServer();
                    eventDataSyncToServer.setDataSynced(true);
                    eventBus.post(eventDataSyncToServer);

                    finish();
*/
                } else {
                    //Toast.makeText(this, responseMsg, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        else if (type.equals(VolleySingleton.CallType.UPDATE_LOCAL_DATA)) {


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
            hideProgressDialog();
            bottomGodownSheetType();
        }

        else if(type.equals(VolleySingleton.CallType.COMMERCIAL_DELIVERY_COUNT)){
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
        if (type.equals(VolleySingleton.CallType.AGENCY_NAME)) {
            try {

                if (response != null) {

                    JSONObject objectAgency = new JSONObject(response);

                    String responseCode = jsonResult.optString("ResponseCode");
                    responseMsg = jsonResult.optString("responseMessage");
                    if (responseCode.equalsIgnoreCase("200")) {
                        AGENCY_NAME= objectAgency.optString("Agency_Name");
                        tvAgencyName.setText(AGENCY_NAME);

                    }
                    /*JSONArray arrayDistributor = objectAgency.optJSONArray("ESS_MST_DISTRIBUTOR");
                    AGENCY_NAME = arrayDistributor.optJSONObject(0).optString("AGENCY_NAME");
                    String status = arrayDistributor.optJSONObject(0).optString("STATUS");
                    if(!TextUtils.isEmpty(status) && status.trim().equalsIgnoreCase("approved")) {
                        tvAgencyName.setVisibility(View.VISIBLE);
                        tvAgencyName.setText(AGENCY_NAME);
                        tvAgencyName.setBackground(getResources().getDrawable(R.drawable.btn_white_background));
                    }else{
                        tvAgencyName.setVisibility(View.GONE);
                    }*/
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (type.equals(VolleySingleton.CallType.USER_LOGIN)) {
            if(!TextUtils.isEmpty(usertype) && usertype.equalsIgnoreCase(Constants.godown_keeper)){
                String responseCode = jsonResult.optString("ResponseCode");
                login_type = LOGIN_GODOWNKEEPER;
                saveLoginTypePreference(login_type);
                if(responseCode.equalsIgnoreCase("500")){
                    hideProgressDialog();

                }
                serverSuccessResponse(response);

            }else if(!TextUtils.isEmpty(usertype) && usertype.equalsIgnoreCase(Constants.commercial_deliveryman)){
                try {
                    String responseCode = jsonResult.optString(Constants.responseCcode);
                    login_type = LOGIN_DELIVERYMAN;
                    saveLoginTypePreference(login_type);
                    if(responseCode.equalsIgnoreCase("500")){
                        hideProgressDialog();
                        serverSuccessResponse(response);
                    }
                    else {
                        int user_id = jsonResult.optInt("user_id");
                        PreferencesHelper.getInstance().setValue(Constants.LOGIN_DELIVERYMAN_ID, user_id);

                        fillCommercialProductsDB(jsonResult);
                        fillUserAssignedCylinders(jsonResult);
                        Intent intent = new Intent(this, GetCommercialConsumerService.class);
                        startService(intent);
                        //fillCommercialConsumerDB(jsonResult);


                        String NENUS_LIST = jsonResult.optString("menus");

                        setOffline_module_list(NENUS_LIST);
                        hideProgressDialog();
                        openHomeScreen();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else if(!TextUtils.isEmpty(usertype) && usertype.equalsIgnoreCase(Constants.owner)){
                hideProgressDialog();
                String ownerType = jsonResult.optString("OWNER_DATA");
                ownerTypeMode(ownerType);
            }else{
                String responseCode = jsonResult.optString(Constants.responseCcode);
                if(responseCode.equalsIgnoreCase("500")){
                    hideProgressDialog();
                    showErrorToast(LoginActivity.this, "Error", responseMsg);
                }
            }


        }
    }


    @Override
    public void onFailure(VolleySingleton.CallType type, VolleyError error) {
        hideProgressDialog();
        serverFailResponse(error);
    }

    private void clearPreviousData() {
        DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        String current_date = dateFormat.format(new Date());
        SharedPreferences sharedPref = getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        String pref_date = getPreferences(PREF_DATE);

        if (!current_date.equalsIgnoreCase(pref_date)) {
            SharedPreferences.Editor editor = sharedPref.edit();
            sharedPref.edit();
            editor.clear();
            editor.commit();

            Intent intent = new Intent(this, GetConsumerService.class);
            startService(intent);

            //Amey
            clearTablesData();

        }
        savePreferences(PREF_DATE, current_date);
        saveLoginTypePreference(login_type);

        setOffline_module_list(getOffline_module_list());
    }

    private void saveLoginTypePreference(String value) {
        PreferencesHelper.getInstance().setValue(LOGIN_TYPE, value);
    }


    public String getOffline_module_list() {
        return offline_module_list;
    }

    public void setOffline_module_list(String offline_module_list) {

        String moduleKEY = this.getResources().getString(R.string.key_shared_pref_module_list);
        saveWithSharedPreferences(getApplicationContext(), moduleKEY, offline_module_list);
        saveWithSharedPreferences(this, Constants.KEY_USER_ID, getUSER_ID());
        this.offline_module_list = offline_module_list;
    }

    private void setLoginType(String loginType){

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

    private void userTypeMode(JSONObject objectESS) throws JSONException {

        JSONArray arrayPRODUCT = objectESS.optJSONArray("ESS_MST_PRODUCT");
        JSONArray arrayEMPLOYEE = objectESS.optJSONArray("ESS_MST_EMPLOYEE");
        JSONArray arrayVEHICLE = objectESS.optJSONArray("ESS_MST_VEHICLE");
        JSONArray ESS_MST_GODOWN = objectESS.optJSONArray("ESS_MST_GODOWN");
        String ESS_MST_STOCKS = objectESS.optString("STOCKS");
        JSONArray ESS_COMMERCIAL_DELIVERY_CREDIT = objectESS.optJSONArray("ESS_COMMERCIAL_DELIVERY_CREDIT");

        if (ESS_COMMERCIAL_DELIVERY_CREDIT != null) {
            storeCommercialCredit(ESS_COMMERCIAL_DELIVERY_CREDIT);
        }

        setGO_DOWN_ARRAY_LIST(ESS_MST_GODOWN);

        /******************************************************************************************/

        RuntimeExceptionDao<EmployeeDB, Integer> employeeDB = getHelper().getEmployeeRTExceptionDao();
        List<EmployeeDB> employeeDBList = employeeDB.queryForAll();
        int empSize = employeeDBList.size();

        if (empSize > 0) {
            employeeDB.delete(employeeDBList);
        }


        int EMP_LENGTH = arrayEMPLOYEE.length();
        if (EMP_LENGTH > 0) {

            for (int employee = 0; employee < EMP_LENGTH; employee++) {
                JSONObject objectEmployee = arrayEMPLOYEE.optJSONObject(employee);
                String TITLE = objectEmployee.optString("TITLE");
                String FIRST_NAME = objectEmployee.optString("FIRST_NAME");
                String MIDDLE_NAME = objectEmployee.optString("MIDDLE_NAME");
                String LAST_NAME = objectEmployee.optString("LAST_NAME");
                String ID_DESIGNATION = objectEmployee.optString("ID_DESIGNATION");
                String CREDIT_GIVEN = objectEmployee.optString("CREDIT_GIVEN");

                if(MIDDLE_NAME.trim().equalsIgnoreCase("null")){
                    MIDDLE_NAME = "";
                }else if(LAST_NAME.trim().equalsIgnoreCase("null")){
                    LAST_NAME = "";
                }

                String FULL_NAME = TITLE + " " + FIRST_NAME + " " + MIDDLE_NAME + " " + LAST_NAME;
                int EMP_CODE = Integer.parseInt(objectEmployee.optString("EMP_CODE"));

                Log.e("CREDIT_GIVEN: ", CREDIT_GIVEN);

                employeeDB.create(new EmployeeDB(1, EMP_CODE, FULL_NAME, ID_DESIGNATION, CREDIT_GIVEN));
            }
        }

        /******************************************************************************************/

        RuntimeExceptionDao<ProductDB, Integer> productDB = getHelper().getProductRTExceptionDao();
        List<ProductDB> productDBList = productDB.queryForAll();
        int productSize = productDBList.size();

        if (productSize > 0) {
            productDB.delete(productDBList);
        }

        for (int product = 0; product < arrayPRODUCT.length(); product++) {
            JSONObject objectProduct = arrayPRODUCT.optJSONObject(product);
            int PRODUCT_CODE = Integer.parseInt(objectProduct.optString("PRODUCT_CODE"));
            String PRODUCT_CATEGORY = objectProduct.optString("ID_PRODUCT_CATEGORY");
            String PRODUCT_DESCRIPTION = objectProduct.optString("DESCRIPTION");
            String UNIT_MEASUREMENT = objectProduct.optString("UNIT_OF_MEASUREMENT");

            productDB.create(new ProductDB(1, PRODUCT_CODE, PRODUCT_CATEGORY,
                    PRODUCT_DESCRIPTION, UNIT_MEASUREMENT));
        }

        /******************************************************************************************/

        RuntimeExceptionDao<VehicleDB, Integer> vehicleDB = getHelper().getVehicleRTExceptionDao();
        List<VehicleDB> vehicleDBList = vehicleDB.queryForAll();
        int vehSize = vehicleDBList.size();

        if (vehSize > 0) {
            //Log.e(TAG, "Deleted data from Vehicle Database");
            vehicleDB.delete(vehicleDBList);
        }
        for (int vehicle = 0; vehicle < arrayVEHICLE.length(); vehicle++) {

            JSONObject objectVehicle = arrayVEHICLE.optJSONObject(vehicle);
            String VEHICLE_NO = objectVehicle.optString("VEHICLE_NO");
            int VEHICLE_ID = Integer.parseInt(objectVehicle.optString("VEHICAL_ID"));
            vehicleDB.create(new VehicleDB(1, VEHICLE_ID, VEHICLE_NO));
        }

        /******************************************************************************************/

        USER_ID = objectESS.optString("ID_USER");
        setUSER_ID(USER_ID);
        String PRODUCT_LIST = objectESS.optString("productDetails");
        setOffline_module_list(PRODUCT_LIST);
        saveWithSharedPreferences(this, Constants.KEY_USER_TYPE, getUSER_TYPE());

    }

    void fillUserAssignedCylinders(JSONObject jsonObject){
        JSONArray arrayPRODUCT = jsonObject.optJSONArray("quantity");
        RuntimeExceptionDao<UserAssignedCylinderModel, Integer> assignedCylinderDB = getHelper().getUserAssignedCylinderModelRuntimeExceptionDao();
        try {
            assignedCylinderDB.deleteBuilder().delete();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        ArrayList<UserAssignedCylinderModel> cylinderModels = new ArrayList<>();
        if(arrayPRODUCT != null) {
            for (int i = 0; i < arrayPRODUCT.length(); i++) {
                JSONObject objectProduct = arrayPRODUCT.optJSONObject(i);
                UserAssignedCylinderModel model = new UserAssignedCylinderModel(objectProduct);
                cylinderModels.add(model);

            }
            assignedCylinderDB.create(cylinderModels);

        }

    }

    void fillCommercialProductsDB(JSONObject jsonObject){
        JSONArray arrayPRODUCT = jsonObject.optJSONArray("products");
        RuntimeExceptionDao<CommercialProductModel, Integer> productDB = getHelper().getCommercialProductModelExceptionDao();
        try {
            productDB.deleteBuilder().delete();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(arrayPRODUCT != null) {
            for (int product = 0; product < arrayPRODUCT.length(); product++) {
                JSONObject objectProduct = arrayPRODUCT.optJSONObject(product);
           /* int PRODUCT_CODE = Integer.parseInt(objectProduct.optString("PRODUCT_CODE"));
            String PRODUCT_CATEGORY = objectProduct.optString("ID_PRODUCT_CATEGORY");
            String PRODUCT_DESCRIPTION = objectProduct.optString("DESCRIPTION");
            String UNIT_MEASUREMENT = objectProduct.optString("UNIT_OF_MEASUREMENT");
*/
                CommercialProductModel commercialProductModel = new CommercialProductModel(objectProduct);
                productDB.create(commercialProductModel);
            }
        }
    }

    private void clearTablesData() {
        /*********************************************************************************************************/
        RuntimeExceptionDao<DomesticDeliveryDB, Integer> domesticDB =
                getHelper().getDomesticRuntimeExceptionDao();
        List<DomesticDeliveryDB> allDomesticLocalData =
                domesticDB.queryForAll();
        domesticDB.delete(allDomesticLocalData);

        /*********************************************************************************************************/
        RuntimeExceptionDao<CommercialDeliveryDB, Integer> commercialDB =
                getHelper().getCommercialRuntimeExceptionDao();
        List<CommercialDeliveryDB> allCommercialLocalData =
                commercialDB.queryForAll();
        commercialDB.delete(allCommercialLocalData);
        /*********************************************************************************************************/
        RuntimeExceptionDao<TVDetailsDB, Integer> tvDetails =
                getHelper().getTVDetailRTExceptionDao();
        List<TVDetailsDB> allTvDetails =
                tvDetails.queryForAll();
        tvDetails.delete(allTvDetails);
        /*********************************************************************************************************/
        RuntimeExceptionDao<TruckDetailsDB, Integer> truckDetails = getHelper().getTruckDetailRTExceptionDao();
        List<TruckDetailsDB> allTruckDetails =
                truckDetails.queryForAll();
        truckDetails.delete(allTruckDetails);
        /*********************************************************************************************************/
        RuntimeExceptionDao<TruckSendDetailsDB, Integer> truckSendDetails = getHelper().getTruckDetailSendRTExceptionDao();
        List<TruckSendDetailsDB> allTruckSendDetails = truckSendDetails.queryForAll();
        truckSendDetails.delete(allTruckSendDetails);

    }

    private void storeCommercialCredit(JSONArray ess_commercial_delivery_credit) throws JSONException {

        RuntimeExceptionDao<CommercialDeliveryCreditDB, Integer> ccCreditDB = getHelper().getCommercialCreditExceptionDao();
        List<CommercialDeliveryCreditDB> commercialDeliveryCreditDBList = ccCreditDB.queryForAll();
        int creditSize = commercialDeliveryCreditDBList.size();

        if (creditSize > 0) {
            ccCreditDB.delete(commercialDeliveryCreditDBList);
        }

        for (int position = 0; position < ess_commercial_delivery_credit.length(); position++) {
            JSONObject objectProduct = ess_commercial_delivery_credit.optJSONObject(position);

            int PRODUCT_ID = Integer.parseInt(objectProduct.optString("PRODUCT_ID"));
            int DELIVERY_ID = Integer.parseInt(objectProduct.optString("DELIVERY_ID"));
            String GODOWN_ID = objectProduct.optString("GODOWN_ID");
            String CREDIT_GIVEN = objectProduct.optString("CREDIT_GIVEN");
            String DATE_TIME = objectProduct.optString("DATE_TIME");

            //ccCreditDB.create(new CommercialDeliveryCreditDB(PRODUCT_ID, DELIVERY_ID, CREDIT_GIVEN, GODOWN_ID, DATE_TIME));
        }
    }

    private void bottomGodownSheetType() {

        //final ArrayList<String> listGodown = new ArrayList<>();
        final List<String> mapLinked = new ArrayList<>();

        final BottomSheet.Builder builder = new BottomSheet.Builder(this);
        builder.setTitle(R.string.bottom_sheet_title);
        builder.addDivider();
        builder.setDividerColor(getResources().getColor(R.color.colorBlack));
        BottomSheet bottomSheet = builder.create();

        if (getGO_DOWN_ARRAY_LIST() != null && getGO_DOWN_ARRAY_LIST().length() > 0){
            for (int position = 0; position < getGO_DOWN_ARRAY_LIST().length(); position++) {
                try {
                    JSONObject jsonGodown = getGO_DOWN_ARRAY_LIST().optJSONObject(position);

                    String godown_type_code = jsonGodown.optString("GODOWN_CODE");
                    int intGodownCode = Integer.parseInt(godown_type_code);
                    String godown_type_name = jsonGodown.optString("DISPLAY_NAME");
                    String godown_name_code = godown_type_name + ":" + godown_type_code;
                    builder.addItem(intGodownCode, godown_name_code);
                    mapLinked.add(godown_name_code);

                    Log.e("chipSelector", godown_name_code);

                } catch (Exception e) {
                    showErrorToast(LoginActivity.this, "Error", "Something went wrong " + e.getMessage());
                }
            }
        }

        builder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                position = position - 1;
                String selectedGodown = mapLinked.get(position);

                String[] leftRight = selectedGodown.split(":");
                PreferencesHelper.getInstance().setValue(KEY_GODOWN_NAME, leftRight[0]);
                PreferencesHelper.getInstance().setValue(KEY_GODOWN, leftRight[1]);

                openHomeScreen();
            }
        });
        bottomSheet.show();
    }

    void openHomeScreen(){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    public JSONArray getGO_DOWN_ARRAY_LIST() {
        return GO_DOWN_ARRAY_LIST;
    }

    public void setGO_DOWN_ARRAY_LIST(JSONArray GO_DOWN_ARRAY_LIST) {
        this.GO_DOWN_ARRAY_LIST = GO_DOWN_ARRAY_LIST;
        AppSettings.getInstance(this).godownJson = GO_DOWN_ARRAY_LIST.toString();
    }

    public String getUSER_TYPE() {
        return USER_TYPE;
    }

    public void setUSER_TYPE(String USER_TYPE) {
        this.USER_TYPE = USER_TYPE;
    }

    @Override
    protected void onResume() {
        super.onResume();
        editTextUsername.setText("");
        editTextUsername.requestFocus();
        editTextPassword.setText("");
    }
}
