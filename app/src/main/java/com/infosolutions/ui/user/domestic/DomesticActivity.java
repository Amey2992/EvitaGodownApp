package com.infosolutions.ui.user.domestic;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.daasuu.cat.CountAnimationTextView;
import com.infosolutions.adapter.ChipModel;
import com.infosolutions.adapter.ChipSelectionAdapter;
import com.infosolutions.core.BaseActivity;
import com.infosolutions.core.EvitaApplication;
import com.infosolutions.database.DatabaseHelper;
import com.infosolutions.database.DomesticDeliveryDB;
import com.infosolutions.database.EmployeeDB;
import com.infosolutions.database.ProductDB;
import com.infosolutions.evita.R;
import com.infosolutions.network.Constants;
import com.infosolutions.network.VolleySingleton;
import com.infosolutions.ui.user.tvdetails.TVDetailsActivity;
import com.infosolutions.utils.AppSettings;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.UpdateBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;


import static com.infosolutions.network.Constants.MODULE_AVAILABLE_CYL;
import static com.infosolutions.network.Constants.PRODUCT_ID;
import static com.infosolutions.network.Constants.getSharedPrefWithKEY;
import static com.infosolutions.network.Constants.saveWithSharedPreferences;
import static java.lang.Integer.parseInt;



public class DomesticActivity extends BaseActivity {

    private final String TAG = DomesticActivity.class.getSimpleName();

    @BindView(R.id.recycler_view_chip)
    RecyclerView recycler_view_chip;
    @BindView(R.id.scrollView)
    ScrollView scrollView;
    @BindView(R.id.btnDeliveryMan)
    Button btnDeliveryMan;
    @BindView(R.id.tvDeliveryMan)
    TextView tvDeliveryMan;
    @BindView(R.id.input_fresh_trip_no)
    EditText input_fresh_trip_no;
    @BindView(R.id.input_fresh_full_cylinder)
    EditText input_fresh_full_cylinder;
    @BindView(R.id.input_return_trip_no)
    EditText input_return_trip_no;
    @BindView(R.id.input_full)
    EditText input_full;
    @BindView(R.id.input_empty_cylinder)
    EditText input_empty_cylinder;
    @BindView(R.id.input_return_sv)
    EditText input_return_sv;
    @BindView(R.id.input_return_defective)
    EditText input_return_defective;
    @BindView(R.id.input_return_dbc)
    EditText input_return_dbc;
    @BindView(R.id.input_lost)
    EditText input_lost;
    @BindView(R.id.input_return_full)
    EditText input_return_full;
    @BindView(R.id.btnSubmit)
    Button btnSubmit;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.layout_fresh)
    LinearLayout layout_fresh;
    @BindView(R.id.layout_return)
    LinearLayout layout_return;
    @BindView(R.id.layout_cylinder)
    RelativeLayout layout_cylinder;
    @BindView(R.id.tv_available_cyl)
    TextView tv_available_cyl;
    @BindView(R.id.cyl_count)
    CountAnimationTextView cyl_count;


    private ChipSelectionAdapter chipAdapter;
    private DatabaseHelper databaseHelper = null;
    private String KEY_DOMESTIC_TRIP_NO ;
    private String KEY_DOMESTIC_FULL_CYLINDER ;
    private String KEY_LAYOUT_TYPE ;
    private List<ChipModel> chipListModel = new ArrayList<>();
    private String productId;
    private String selectedProductName;
    private String deliveryManId;
    private int TOTAL_AVAILABLE_CYL = 0;
    private List<Integer> empIds = new ArrayList<>();
    private DomesticDeliveryDB currentDomesticDeliveryDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setupToolbar();

    }

    @Override
    public void injectDependency() { EvitaApplication.getEvitaComponents().inject(DomesticActivity.this);}

    @Override
    public int getLayoutId() { return R.layout.activity_domestic; }

    private void setupToolbar() {

        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Domestic");
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_backspace);
        setProductModel();


        productSelectionChipView();
    }

    private void setProductModel() {


        RuntimeExceptionDao<ProductDB, Integer> productDB = getHelper().getProductRTExceptionDao();
        List<ProductDB> product = productDB.queryForAll();

        for (ProductDB cn : product) {
            String CYLINDER_CODE = String.valueOf(cn.product_code);
            String CYLINDER_DESCRIPTION = String.valueOf(cn.product_description);
            String PRODUCT_CATEGORY = String.valueOf(cn.product_category);
            if (PRODUCT_CATEGORY.equalsIgnoreCase("1")){
                chipListModel.add(new ChipModel(CYLINDER_DESCRIPTION, CYLINDER_CODE, PRODUCT_CATEGORY));
            }
        }


        chipAdapter = new ChipSelectionAdapter(this, chipListModel);
        recycler_view_chip.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));
        recycler_view_chip.setItemAnimator(new DefaultItemAnimator());
        recycler_view_chip.setAdapter(chipAdapter);

    }

    private void productSelectionChipView() {

        chipAdapter.notifyDataSetChanged();
        recycler_view_chip.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recycler_view_chip, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                chipSelector(position);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    private void chipSelector(int position) {

        AppSettings.hideKeyboard(this);
        productId = chipListModel.get(position).getChipTitleId();
        setProductId(productId);

        tvDeliveryMan.setText("");
        getAvailableCYL(); /* Check available Cylinder */
        selectedProductName = chipListModel.get(position).getChipTitle();
        setSelectedProductName(selectedProductName);

        /* Hide All Layout */
        layout_return.setVisibility(View.GONE); layout_fresh.setVisibility(View.GONE);

        if (productId != null && !productId.equalsIgnoreCase("")) {
            btnDeliveryMan.setVisibility(View.VISIBLE);
            showSpinnerDialog();

        }else {
            showSnackBar(getResources().getString(R.string.select_product_type));
            showErrorToast(DomesticActivity.this,"Error", getResources().getString(R.string.select_product_type));
        }
    }


    private void showSpinnerDialog() {

        btnDeliveryMan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> listNames = new ArrayList();
                try{
                    RuntimeExceptionDao<EmployeeDB, Integer> employeeDB = getHelper().getEmployeeRTExceptionDao();
                    List<EmployeeDB> employee = employeeDB.queryForAll();
                    for (EmployeeDB cn : employee) {
                        String log = cn.employee_id+": "+cn.full_name;
                        if (cn.ID_DESIGNATION.equalsIgnoreCase("31")){
                            listNames.add(log);
                            empIds.add(cn.employee_id);
                        }
                    }

                    if (listNames.size()>0){
                        showDeliverymanDialog(listNames);
                    }

                }catch (Exception ex){
                    showErrorToast(DomesticActivity.this,"Error", "No data available");
                }
            }
        });
    }

    private void showDeliverymanDialog(ArrayList<String> listNames) {

        final SpinnerDialog dialog
                = new SpinnerDialog(DomesticActivity.this, listNames, getResources().getString(R.string.select_deliveryman));
        dialog.showSpinerDialog();
        dialog.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String selectedDeliveryMan, int position) {

                int id = empIds.get(position);
                getEmployeeDetails(selectedDeliveryMan, id);
                //old_loadUI(selectedDeliveryMan,position);

            }
        });
    }

    void old_loadUI(String selectedDeliveryMan, int position){

        String[] splitNameValue = selectedDeliveryMan.split(":");
        String deliveryManKEY = splitNameValue[0];
        String DeliveryManVALUE = splitNameValue[1];

        setDeliveryManId(deliveryManKEY);
        KEY_DOMESTIC_TRIP_NO = "trip_no";
        KEY_DOMESTIC_FULL_CYLINDER = "full_cyl";
        KEY_LAYOUT_TYPE = "view_type";

        String dynamicIds = getGoDownId()+productId+deliveryManKEY;

        //String dynamicIds = selectedDeliveryMan+getSelectedProductName();

        KEY_DOMESTIC_TRIP_NO = KEY_DOMESTIC_TRIP_NO+dynamicIds;
        KEY_DOMESTIC_FULL_CYLINDER = KEY_DOMESTIC_FULL_CYLINDER+dynamicIds;
        KEY_LAYOUT_TYPE = KEY_LAYOUT_TYPE+dynamicIds;

        tvDeliveryMan.setText(DeliveryManVALUE/*+"\nAvailable Cyl- "+CREDIT_GIVEN.trim()*/);
        tvDeliveryMan.setAllCaps(true);
        tvDeliveryMan.setVisibility(View.VISIBLE);

        InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
        input_fresh_full_cylinder.requestFocus();
        input_empty_cylinder.requestFocus();


        if (getTripNumber() > 0)
        {
            String VIEW_TYPE = getSharedPrefWithKEY(getApplicationContext(), KEY_LAYOUT_TYPE);
            if (VIEW_TYPE.equalsIgnoreCase("FRESH")){
                layout_return.setVisibility(View.GONE);
                layout_fresh.setVisibility(View.VISIBLE);
                initFreshLayout(getTripNumber());

            }else if (VIEW_TYPE.equalsIgnoreCase("RETURN"))
            {
                layout_fresh.setVisibility(View.GONE);
                layout_return.setVisibility(View.VISIBLE);
                initReturnLayout(getTripNumber());
            }
        }
        else {
            showErrorToast(DomesticActivity.this,"Error", "Something went wrong, Please Retry");
        }
    }

    private void getEmployeeDetails(String selectedDeliveryMan, int id) {
        RuntimeExceptionDao<DomesticDeliveryDB, Integer> domesticDB = getHelper().getDomesticRuntimeExceptionDao();
        try {
            List<DomesticDeliveryDB> employeeObj = domesticDB.queryBuilder().orderBy("trip_number",false)
                    .where().eq("employee_id",id).and().eq("product_id",getProductId()).query();
            String[] splitNameValue = selectedDeliveryMan.split(":");
            String deliveryManKEY = splitNameValue[0];
            String DeliveryManVALUE = splitNameValue[1];

            setDeliveryManId(deliveryManKEY);

            tvDeliveryMan.setText(DeliveryManVALUE/*+"\nAvailable Cyl- "+CREDIT_GIVEN.trim()*/);
            tvDeliveryMan.setAllCaps(true);
            tvDeliveryMan.setVisibility(View.VISIBLE);

            InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
            input_fresh_full_cylinder.requestFocus();
            input_empty_cylinder.requestFocus();

            if(employeeObj != null && employeeObj.size() > 0) {
                DomesticDeliveryDB domesticDeliveryDB = employeeObj.get(0);
                if(domesticDeliveryDB.isReturn){
                    new_initReturnLayout(domesticDeliveryDB);
                }else{
                    int tripNumber = domesticDeliveryDB.trip_number;
                    initFreshLayout(tripNumber + 1);
                }
            }else{

                initFreshLayout(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    private void initFreshLayout(int tripNumber) {

        layout_fresh.setVisibility(View.VISIBLE);
        layout_return.setVisibility(View.GONE);

        clearAllFields();
        btnSubmit.setText("Submit Fresh Trip");
        btnSubmit.setVisibility(View.VISIBLE);

        input_fresh_trip_no.setText(String.valueOf(tripNumber));
        input_fresh_full_cylinder.requestFocus();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String freshTrip = input_fresh_trip_no.getText().toString().trim();
                String freshFullCylinder = input_fresh_full_cylinder.getText().toString().trim();
                int FULL_CYL_COUNT = Integer.parseInt(freshFullCylinder);

                if (TextUtils.isEmpty(freshTrip)) {
                    focusOnView(scrollView, input_fresh_trip_no);
                    showErrorToast(DomesticActivity.this,"Error","Please enter trip number");

                }else if (TextUtils.isEmpty(freshFullCylinder)) {
                    focusOnView(scrollView, input_fresh_full_cylinder);
                    showErrorToast(DomesticActivity.this,"Error",getResources().getString(R.string.enter_full_cylnr));

                }else if (FULL_CYL_COUNT== 0) {
                    focusOnView(scrollView, input_fresh_full_cylinder);
                    input_fresh_full_cylinder.setError(getResources().getString(R.string.zero_not_allowed));
                    showErrorToast(DomesticActivity.this,"Error",getResources().getString(R.string.zero_not_allowed));

                }
                //sachin
                /*else if (TOTAL_AVAILABLE_CYL == 0 ){
                    showErrorToast(DomesticActivity.this,"Error","Can not enter more than available cylinders ");
                    focusOnView(scrollView, input_fresh_full_cylinder);
                }*/
                /*else if (TOTAL_AVAILABLE_CYL > 0 && FULL_CYL_COUNT > TOTAL_AVAILABLE_CYL){
                    showErro    rToast(DomesticActivity.this,"Error","Can not enter more than available cylinders ");
                    focusOnView(scrollView, input_fresh_full_cylinder);
                }*/else {

                    final AlertDialog alertDialog = new AlertDialog.Builder(DomesticActivity.this).create();
                    alertDialog.setTitle("Entry");
                    alertDialog.setIcon(getResources().getDrawable(R.drawable.demo_cyl));
                    alertDialog.setMessage(getResources().getString(R.string.proceed_msg));
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "SAVE",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    saveFreshTrip(getTextFromET(input_fresh_trip_no), getTextFromET(input_fresh_full_cylinder));

                                }
                            });

                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.show();
                }
            }
        });

    }

//--------------------------------------------------------------------------------------------

    public String getDate() {

        SimpleDateFormat simpleDateFormat = null;
        Date date = null;
        try {
            DateFormat formatter = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
            date = formatter.parse(new Date().toString());
            simpleDateFormat= new SimpleDateFormat("yyyy-MM-dd");
        }catch (Exception ex){
            ex.printStackTrace();
        }

        return simpleDateFormat.format(date);
    }

    private static String currentDateTime() {

        SimpleDateFormat simpleDateFormat = null;
        Date date = null;
        try {
            DateFormat formatter = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
            date = (Date)formatter.parse(new Date().toString());
            simpleDateFormat= new SimpleDateFormat("yyyyMMdd");
        }catch (Exception ex){

            ex.printStackTrace();
        }

        return simpleDateFormat.format(date);
    }

    public String getTodayDate() {

        Calendar c = Calendar.getInstance();
        //c.setTime(yourDate);
        int month = c.get(Calendar.DAY_OF_WEEK_IN_MONTH);
        int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
        int year = c.get(Calendar.YEAR);
        String finalString = Integer.toString(month)+Integer.toString(dayOfMonth)+Integer.toString(year);
        return finalString;
    }

    //--------------------------- Inserting the values on fresh trip of deliveryman------------------------------

    // Problem while inserting the data
    private void saveFreshTrip(int fresh_trip_no, int fresh_full) {

        try {

            DatabaseHelper databaseHelper = new DatabaseHelper(DomesticActivity.this);

            /*if(!databaseHelper.CheckIsDataAlreadyInDBorNot(getDate()))
            {*/
            RuntimeExceptionDao<DomesticDeliveryDB, Integer> domesticDB = getHelper().getDomesticRuntimeExceptionDao();

           //new
            String uniqueId = Integer.toString(getGoDownId()) + Integer.toString(getDeliveryManId()) + fresh_trip_no + currentDateTime()+getProductId  ();

            DomesticDeliveryDB domesticDeliveryDB = new DomesticDeliveryDB(1, getDeliveryManId(), fresh_trip_no, getProductId(), getDateTime(), getApplicationUserId(), fresh_full,
                    0 , 0 , 0 , 0 , 0, getDateTime(),
                    0,0, "MOBILE", "INSERT", "N", getGoDownId(),
                    getDeviceId(),getDate(),false,true,false,uniqueId);
            domesticDB.create(domesticDeliveryDB);
            saveWithSharedPreferences(getApplicationContext(), KEY_DOMESTIC_FULL_CYLINDER, String.valueOf(fresh_full));

            saveWithSharedPreferences(getApplicationContext(), KEY_LAYOUT_TYPE, "RETURN");
            clearAllFields();
            showSuccessToast(DomesticActivity.this,"Fresh Trip Saved Successfully !", getResources().getString(R.string.saved_success_msg));
            finish();
           /* }
            else
            {
                showSuccessToast(DomesticActivity.this,"Record Already Available!", getResources().getString(R.string.RecordError));

            }*/

        }catch (Exception ex){
            showSnackBar(getResources().getString(R.string.could_not_store));
            ex.printStackTrace();
        }

    }

    private void initReturnLayout(final int tripNumber, final int... fullCylinderCount) {

        layout_return.setVisibility(View.VISIBLE);
        layout_fresh.setVisibility(View.GONE);
        btnSubmit.setVisibility(View.VISIBLE);
        btnSubmit.setText("Submit Return Trip");
        clearAllFields();
        input_return_trip_no.setText(String.valueOf(tripNumber));
        input_return_trip_no.setEnabled(false);
        input_empty_cylinder.selectAll();

        if (getPreferences(KEY_DOMESTIC_FULL_CYLINDER).equalsIgnoreCase("No Preference Found")) {
            savePreferences(KEY_DOMESTIC_FULL_CYLINDER, String.valueOf(0));
        }

        //old
        //input_full.setText(getPreferences(KEY_DOMESTIC_FULL_CYLINDER));

        //new
        input_full.setText(Integer.toString(fullCylinderCount[0]));

        input_full.setEnabled(false);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (getTextString(input_return_trip_no).equalsIgnoreCase("")) {
                    input_return_trip_no.requestFocus();
                } else if (getTextString(input_full).equalsIgnoreCase("")) {
                    focusOnView(scrollView,input_full);
                    input_full.setError(getResources().getString(R.string.enter_cylnr_type));
                } else if (getTextString(input_empty_cylinder).equalsIgnoreCase("")) {
                    focusOnView(scrollView,input_empty_cylinder);
                    input_empty_cylinder.setError(getResources().getString(R.string.enter_return_empty_cylndr));
                } else if (getTextString(input_return_sv).equalsIgnoreCase("")) {
                    focusOnView(scrollView,input_return_sv);
                    input_return_sv.setError(getResources().getString(R.string.enter_sv));
                } else if (getTextString(input_return_defective).equalsIgnoreCase("")) {
                    focusOnView(scrollView,input_return_defective);
                    input_return_defective.setError(getResources().getString(R.string.enter_defective));
                } else if (getTextString(input_return_dbc).equalsIgnoreCase("")) {
                    focusOnView(scrollView,input_return_dbc);
                    input_return_dbc.setError(getResources().getString(R.string.enter_bc));
                }else if (getTextString(input_lost).equalsIgnoreCase("")) {
                    focusOnView(scrollView, input_lost);
                    input_lost.setError(getResources().getString(R.string.enter_lost_cyl));
                } else if (getTextString(input_return_full).equalsIgnoreCase("")) {
                    focusOnView(scrollView, input_return_full);
                    input_return_full.setError(getResources().getString(R.string.enter_return_empty_cylndr));
                } else {

                    if (getTextFromET(input_full) == returnTotalCalculation()){

                        final AlertDialog alertDialog = new AlertDialog.Builder(DomesticActivity.this).create();
                        alertDialog.setTitle("Entry");
                        alertDialog.setIcon(getResources().getDrawable(R.drawable.demo_cyl));
                        alertDialog.setMessage(getResources().getString(R.string.proceed_msg));
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "SAVE",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        saveReturnData(tripNumber);
                                    }
                                });
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                alertDialog.dismiss();
                            }});
                        alertDialog.show();

                    }else {
                        showErrorToast(DomesticActivity.this,"Error","Invalid Entry Calculation");
                    }

                }
            }
        });
    }


    private void new_initReturnLayout(final DomesticDeliveryDB domesticDeliveryDB) {

        layout_return.setVisibility(View.VISIBLE);
        layout_fresh.setVisibility(View.GONE);
        btnSubmit.setVisibility(View.VISIBLE);
        btnSubmit.setText("Submit Return Trip");
        clearAllFields();
        input_return_trip_no.setText(String.valueOf(domesticDeliveryDB.trip_number));
        input_return_trip_no.setEnabled(false);
        input_empty_cylinder.selectAll();

        if (getPreferences(KEY_DOMESTIC_FULL_CYLINDER).equalsIgnoreCase("No Preference Found")) {
            savePreferences(KEY_DOMESTIC_FULL_CYLINDER, String.valueOf(0));
        }

        //old
        //input_full.setText(getPreferences(KEY_DOMESTIC_FULL_CYLINDER));

        //new
        input_full.setText(Integer.toString(domesticDeliveryDB.fresh_full_cylinder));

        input_full.setEnabled(false);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (getTextString(input_return_trip_no).equalsIgnoreCase("")) {
                    input_return_trip_no.requestFocus();
                } else if (getTextString(input_full).equalsIgnoreCase("")) {
                    focusOnView(scrollView,input_full);
                    input_full.setError(getResources().getString(R.string.enter_cylnr_type));
                } else if (getTextString(input_empty_cylinder).equalsIgnoreCase("")) {
                    focusOnView(scrollView,input_empty_cylinder);
                    input_empty_cylinder.setError(getResources().getString(R.string.enter_return_empty_cylndr));
                } else if (getTextString(input_return_sv).equalsIgnoreCase("")) {
                    focusOnView(scrollView,input_return_sv);
                    input_return_sv.setError(getResources().getString(R.string.enter_sv));
                } else if (getTextString(input_return_defective).equalsIgnoreCase("")) {
                    focusOnView(scrollView,input_return_defective);
                    input_return_defective.setError(getResources().getString(R.string.enter_defective));
                } else if (getTextString(input_return_dbc).equalsIgnoreCase("")) {
                    focusOnView(scrollView,input_return_dbc);
                    input_return_dbc.setError(getResources().getString(R.string.enter_bc));
                }else if (getTextString(input_lost).equalsIgnoreCase("")) {
                    focusOnView(scrollView, input_lost);
                    input_lost.setError(getResources().getString(R.string.enter_lost_cyl));
                } else if (getTextString(input_return_full).equalsIgnoreCase("")) {
                    focusOnView(scrollView, input_return_full);
                    input_return_full.setError(getResources().getString(R.string.enter_return_empty_cylndr));
                } else {

                    if (getTextFromET(input_full) == returnTotalCalculation()){

                        final AlertDialog alertDialog = new AlertDialog.Builder(DomesticActivity.this).create();
                        alertDialog.setTitle("Entry");
                        alertDialog.setIcon(getResources().getDrawable(R.drawable.demo_cyl));
                        alertDialog.setMessage(getResources().getString(R.string.proceed_msg));
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "SAVE",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        new_saveReturnData(domesticDeliveryDB);
                                    }
                                });
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                alertDialog.dismiss();
                            }});
                        alertDialog.show();

                    }else {
                        showErrorToast(DomesticActivity.this,"Error","Invalid Entry Calculation");
                    }

                }
            }
        });
    }

    //-------   Updating the values while deliveryman giving bifercation for return trip    --------

    // problem while updating the values , update the values on daily basis , deliverymanwise , tripwise productwise etc.

    //old
    private void saveReturnData(int tripNumber) {

        try{
            Long tsLong = System.currentTimeMillis()/1000;
            String ts = tsLong.toString();

            DatabaseHelper databaseHelper = new DatabaseHelper(DomesticActivity.this);

            /*if(databaseHelper.CheckIsDataAlreadyAvailableDBorNot(ts))

            {*/
            /******************************************************************************************************************/
            RuntimeExceptionDao<DomesticDeliveryDB, Integer> domesticDao = getHelper().getDomesticRuntimeExceptionDao();
            UpdateBuilder<DomesticDeliveryDB, Integer> updateBuilder = domesticDao.updateBuilder();

            updateBuilder.where().eq("trip_number", tripNumber).and().eq("product_id",
                    getProductId()).and().eq("employee_id", getDeliveryManId()).and().eq("godown_Id",
                    getGoDownId()).and().eq("given_by", getApplicationUserId()).and().eq("given_date" , getDate());


            updateBuilder.updateColumnValue("empty_received", getTextFromET(input_empty_cylinder));
            updateBuilder.updateColumnValue("sv_field", getTextFromET(input_return_sv));
            updateBuilder.updateColumnValue("dbc_field", getTextFromET(input_return_dbc));
            updateBuilder.updateColumnValue("defective_field", getTextFromET(input_return_defective));
            updateBuilder.updateColumnValue("return_full_cylinder", getTextFromET(input_return_full));
            updateBuilder.updateColumnValue("received_time", getDateTime());
            updateBuilder.updateColumnValue("received_by", getApplicationUserId());
            updateBuilder.updateColumnValue("lost_cylinder", getTextFromET(input_lost));
            updateBuilder.updateColumnValue("mode_of_entry", "Mobile");
            updateBuilder.updateColumnValue("type_of_query", "UPDATE");
            updateBuilder.updateColumnValue("is_sync", "N");
            updateBuilder.updateColumnValue("godown_Id", getGoDownId());

            updateBuilder.update();
            /******************************************************************************************************************/
            String updateTripNumber = String.valueOf(tripNumber + 1);
            savePreferences(KEY_DOMESTIC_TRIP_NO, updateTripNumber);
            savePreferences(KEY_DOMESTIC_FULL_CYLINDER, "0");
            savePreferences(KEY_LAYOUT_TYPE, "FRESH");

            clearAllFields();
            showToast(getResources().getString(R.string.saved_success_msg));
            finish();
           /* }
            else
            {
                showToast(getResources().getString(R.string.error_occured));
            }*/

        }catch (Exception ex){
            showErrorToast(DomesticActivity.this,"Error",getResources().getString(R.string.taken_from_different_godown));
            ex.printStackTrace();
        }
    }

    private void new_saveReturnData(DomesticDeliveryDB domesticDeliveryDB) {

        try{
            Long tsLong = System.currentTimeMillis()/1000;
            String ts = tsLong.toString();

            DatabaseHelper databaseHelper = new DatabaseHelper(DomesticActivity.this);

            /*if(databaseHelper.CheckIsDataAlreadyAvailableDBorNot(ts))

            {*/
            /******************************************************************************************************************/
            RuntimeExceptionDao<DomesticDeliveryDB, Integer> domesticDao = getHelper().getDomesticRuntimeExceptionDao();
            UpdateBuilder<DomesticDeliveryDB, Integer> updateBuilder = domesticDao.updateBuilder();

            updateBuilder.where().eq("trip_number", domesticDeliveryDB.trip_number).and().eq("product_id",
                    domesticDeliveryDB.product_id).and().eq("employee_id", domesticDeliveryDB.employee_id).and().eq("godown_Id",
                    domesticDeliveryDB.godown_Id).and().eq("given_by", domesticDeliveryDB.given_by).and().eq("given_date" , getDate());


            updateBuilder.updateColumnValue("empty_received", getTextFromET(input_empty_cylinder));
            updateBuilder.updateColumnValue("sv_field", getTextFromET(input_return_sv));
            updateBuilder.updateColumnValue("dbc_field", getTextFromET(input_return_dbc));
            updateBuilder.updateColumnValue("defective_field", getTextFromET(input_return_defective));
            updateBuilder.updateColumnValue("return_full_cylinder", getTextFromET(input_return_full));
            updateBuilder.updateColumnValue("received_time", getDateTime());
            updateBuilder.updateColumnValue("received_by", getApplicationUserId());
            updateBuilder.updateColumnValue("lost_cylinder", getTextFromET(input_lost));
            updateBuilder.updateColumnValue("mode_of_entry", "Mobile");
            updateBuilder.updateColumnValue("type_of_query", "UPDATE");
            updateBuilder.updateColumnValue("is_sync", "N");
            updateBuilder.updateColumnValue("godown_Id", getGoDownId());
            updateBuilder.updateColumnValue("isFresh", true);
            updateBuilder.updateColumnValue("isReturn", false);
            updateBuilder.updateColumnValue("isCompleted", true);
            updateBuilder.updateColumnValue("uniqueID",domesticDeliveryDB.uniqueID);

            updateBuilder.update();
            /******************************************************************************************************************/
            String updateTripNumber = String.valueOf(domesticDeliveryDB.trip_number + 1);
            savePreferences(KEY_DOMESTIC_TRIP_NO, updateTripNumber);
            savePreferences(KEY_DOMESTIC_FULL_CYLINDER, "0");
            savePreferences(KEY_LAYOUT_TYPE, "FRESH");

            clearAllFields();
            showToast(getResources().getString(R.string.saved_success_msg));
            finish();
           /* }
            else
            {
                showToast(getResources().getString(R.string.error_occured));
            }*/

        }catch (Exception ex){
            showErrorToast(DomesticActivity.this,"Error",getResources().getString(R.string.taken_from_different_godown));
            ex.printStackTrace();
        }
    }

    private int returnTotalCalculation() {
        return  (getTextFromET(input_empty_cylinder)+ getTextFromET(input_return_sv)+ getTextFromET(input_return_defective)+ getTextFromET(input_return_dbc)+ getTextFromET(input_lost)+ getTextFromET(input_return_full));
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

    private void clearAllFields() {

        input_fresh_trip_no.setText("0");
        input_fresh_full_cylinder.setText("0");
        input_return_trip_no.setText("0");
        input_full.setText("0");
        input_empty_cylinder.setText("0");
        input_return_sv.setText("0");
        input_return_defective.setText("0");
        input_return_dbc.setText("0");
        input_return_full.setText("0");
        input_lost.setText("0");
    }

    private int getTripNumber() {

        String USER_TRIP_NO = getSharedPrefWithKEY(getApplicationContext(), KEY_DOMESTIC_TRIP_NO);
        if (USER_TRIP_NO.equalsIgnoreCase("No Preference Found")){
            saveWithSharedPreferences(getApplicationContext(), KEY_DOMESTIC_TRIP_NO, String.valueOf(1));
            saveWithSharedPreferences(getApplicationContext(), KEY_LAYOUT_TYPE, "FRESH");
            USER_TRIP_NO = getSharedPrefWithKEY(getApplicationContext(), KEY_DOMESTIC_TRIP_NO);
        }else {
            USER_TRIP_NO = getSharedPrefWithKEY(getApplicationContext(), KEY_DOMESTIC_TRIP_NO);
        }

        return parseInt(USER_TRIP_NO);
    }

    public int getProductId() {
        return Integer.parseInt(productId);
    }

    public void setProductId(String selectedCylinderTYPE) {
        this.productId = selectedCylinderTYPE;
    }

    public int getDeliveryManId() {
        return parseInt(deliveryManId);
    }

    public void setDeliveryManId(String deliveryManId) {
        this.deliveryManId = deliveryManId;
    }

    public String getSelectedProductName() {
        return selectedProductName;
    }

    public void setSelectedProductName(String selectedProductName) {
        this.selectedProductName = selectedProductName;
    }
    @Override
    public void onSuccess(VolleySingleton.CallType type, String response) {

        try {
            JSONObject objectResult = new JSONObject(response);
            if (objectResult.has("responseCode") && objectResult.getString("responseCode").equalsIgnoreCase("200")){
                /*JSONArray productArray = objectResult.optJSONArray("productArray");
                String CLOSING_FULL =    productArray.optJSONObject(0).optString("CLOSING_FULL");
*/
                int CLOSING_FULL = objectResult.optInt("CLOSING_FULL");
                layout_cylinder.setVisibility(View.VISIBLE);
                TOTAL_AVAILABLE_CYL = CLOSING_FULL;
                cyl_count. setAnimationDuration(1000).countAnimation(0, TOTAL_AVAILABLE_CYL);

            }else {
                showToast("Something went wrong, please try again letter");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    @Override
    public void onFailure(VolleySingleton.CallType type, VolleyError error) {

        layout_cylinder.setVisibility(View.VISIBLE);
        tv_available_cyl.setTextColor(getResources().getColor(R.color.colorRed));
        tv_available_cyl.setText("Could not load available cylinders");
    }

    public interface ClickListener {
        void onClick(View view, int position);
        void onLongClick(View view, int position);
    }

    public  class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private DomesticActivity.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final DomesticActivity.ClickListener clickListener) {
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

    private void getAvailableCYL(){

        VolleySingleton.getInstance(getApplicationContext())
                .addResponseListener(VolleySingleton.CallType.GET_AVAILABLE_CYL, this);
        VolleySingleton.getInstance(getApplicationContext())
                .new_apiAvailableCYL(VolleySingleton.CallType.GET_AVAILABLE_CYL, Constants.GET_AVAILABLE_CYLINDERS, productId, String.valueOf(getGoDownId()));
    }

    @Override
    protected void onPause() {
        AppSettings.hideKeyboard(this);
        super.onPause();
    }
}
