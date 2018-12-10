package com.infosolutions.ui.user.commercial;

import android.content.Context;
import android.content.DialogInterface;
import android.database.SQLException;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.daasuu.cat.CountAnimationTextView;
import com.infosolutions.adapter.ChipModel;
import com.infosolutions.adapter.ChipSelectionAdapter;
import com.infosolutions.core.BaseActivity;
import com.infosolutions.core.EvitaApplication;
import com.infosolutions.database.CommercialDeliveryCreditDB;
import com.infosolutions.database.CommercialDeliveryDB;
import com.infosolutions.database.DatabaseHelper;
import com.infosolutions.database.EmployeeDB;
import com.infosolutions.database.ProductDB;
import com.infosolutions.evita.R;
import com.infosolutions.network.Constants;
import com.infosolutions.network.VolleySingleton;
import com.infosolutions.ui.user.domestic.DomesticActivity;
import com.infosolutions.utils.AppSettings;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.ceryle.segmentedbutton.SegmentedButtonGroup;
import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;


import static com.infosolutions.network.Constants.MODULE_AVAILABLE_CYL;
import static com.infosolutions.network.Constants.PRODUCT_ID;
import static java.lang.Integer.parseInt;


public class CommercialActivity extends BaseActivity {

    private static final String TAG = CommercialActivity.class.getSimpleName();
    @BindView(R.id.recycler_view_chip)
    RecyclerView recycler_view_chip;
    @BindView(R.id.btnDeliveryMan)
    Button btnDeliveryMan;
    @BindView(R.id.tvDeliveryMan)
    TextView tvDeliveryMan;
    @BindView(R.id.input_full_cylinder)
    EditText input_fresh_full_cylinder;
    @BindView(R.id.tvFreshTrip)
    TextView tvFreshTrip;
    @BindView(R.id.input_empty_cylinder)
    EditText input_empty_cylinder;
    @BindView(R.id.input_return_sv)
    EditText input_return_sv;
    @BindView(R.id.input_return_defective)
    EditText input_return_defective;
    @BindView(R.id.input_return_dbc)
    EditText input_return_dbc;
    @BindView(R.id.input_return_full)
    EditText input_return_full;
    @BindView(R.id.input_lost)
    EditText input_lost;
    @BindView(R.id.input_credit_given)
    EditText input_credit_given;
    @BindView(R.id.btnSubmit)
    Button btnSubmit;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.layout_fresh)
    LinearLayout layout_fresh;
    @BindView(R.id.layout_return)
    LinearLayout layout_return;
    @BindView(R.id.tvReturnTrip)
    TextView tvReturnTrip;
    @BindView(R.id.segmentedButtonGroup)
    SegmentedButtonGroup segmentedButtonGroup;
    @BindView(R.id.layout_cylinder)
    RelativeLayout layout_cylinder;
    @BindView(R.id.tv_available_cyl)
    TextView tv_available_cyl;
    @BindView(R.id.cyl_count)
    CountAnimationTextView cyl_count;

    private DatabaseHelper databaseHelper = null;
    private List<ChipModel> chipListModel = new ArrayList<>();
    private String productCategory;
    private RuntimeExceptionDao<CommercialDeliveryCreditDB, Integer> commercialCreditDB;
    private String productCode;

    public int getPRODUCT_CODE() {
        return PRODUCT_CODE;
    }

    public void setPRODUCT_CODE(int PRODUCT_CODE) {
        this.PRODUCT_CODE = PRODUCT_CODE;
    }

    private int PRODUCT_CODE;
    private int selectedDeliveryManId;
    private int TOTAL_AVAILABLE_CYL = 0;
    private int min = 10;
    private int max = 110;
    private int random = 0;
    private String uniqueId_Commercial;
    private String randomNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setupToolbar();

        random = new Random().nextInt((max - min) + 1) + min;
        Log.e("random number", Integer.toString(random));
        randomNumber = Integer.toString(random);

    }

    private static String currentDateTime() {

        SimpleDateFormat simpleDateFormat = null;
        Date date = null;
        try {
            DateFormat formatter = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
            date = (Date) formatter.parse(new Date().toString());
            simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return simpleDateFormat.format(date);
    }


    @Override
    public void injectDependency() {
        EvitaApplication.getEvitaComponents().inject(CommercialActivity.this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_commercial;
    }


    private void switchView() {

        segmentedButtonGroup.setVisibility(View.VISIBLE);
        btnDeliveryMan.setVisibility(View.VISIBLE);
        segmentedButtonGroup.setOnClickedButtonPosition(new SegmentedButtonGroup.OnClickedButtonPosition() {
            @Override
            public void onClickedButtonPosition(int position) {

                switch (position) {
                    case 0:
                        layout_fresh.setVisibility(View.VISIBLE);
                        layout_return.setVisibility(View.GONE);
                        initFreshLayout();
                        requestFocus(input_fresh_full_cylinder);
                        break;
                    case 1:
                        layout_fresh.setVisibility(View.GONE);
                        layout_return.setVisibility(View.VISIBLE);
                        initReturnLayout();
                        requestFocus(input_empty_cylinder);
                        input_empty_cylinder.selectAll();
                        break;
                }
            }
        });
        segmentedButtonGroup.setPosition(0, 0);
    }


    private void setupToolbar() {

        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Commercial");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_backspace);

        getAndSetCylinderType();

        chipSelectionView();
    }


    private void getAndSetCylinderType() {

        RuntimeExceptionDao<ProductDB, Integer> productDB = getHelper().getProductRTExceptionDao();
        List<ProductDB> product = productDB.queryForAll();

        for (ProductDB cn : product) {

            String CYLINDER_CODE = String.valueOf(cn.product_code);
            String CYLINDER_DESCRIPTION = String.valueOf(cn.product_description);
            String PRODUCT_CATEGORY = String.valueOf(cn.product_category);

            if (PRODUCT_CATEGORY.equalsIgnoreCase("5")) {
                chipListModel.add(new ChipModel(CYLINDER_DESCRIPTION, CYLINDER_CODE, PRODUCT_CATEGORY));

                Log.d("CYLINDER_CODE", CYLINDER_CODE);
                Log.d("CYLINDER_DESCRIPTION", CYLINDER_DESCRIPTION);
                Log.d("PRODUCT_CATEGORY", PRODUCT_CATEGORY);
            }
        }
    }

    private void chipSelectionView() {

        final ChipSelectionAdapter chipAdapter = new ChipSelectionAdapter(this, chipListModel);
        recycler_view_chip.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));
        recycler_view_chip.setItemAnimator(new DefaultItemAnimator());
        recycler_view_chip.setAdapter(chipAdapter);

        recycler_view_chip.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recycler_view_chip, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                productCode = chipListModel.get(position).getChipTitleId();
                productCategory = chipListModel.get(position).getChipTitleId();
                setPRODUCT_CODE(Integer.parseInt(productCode));
                getAvailableCYL();
                btnDeliveryMan.setVisibility(View.VISIBLE);
                showSpinnerDialog();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }


    private void showSpinnerDialog() {

        btnDeliveryMan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> listNames = new ArrayList();
                final ArrayList<String> listCrditCount = new ArrayList<>();

                try {
                    RuntimeExceptionDao<EmployeeDB, Integer> employeeDB = getHelper().getEmployeeRTExceptionDao();
                    List<EmployeeDB> employee = employeeDB.queryForAll();
                    for (EmployeeDB cn : employee) {
                        String log = cn.employee_id + ": " + cn.full_name;
                        if (cn.ID_DESIGNATION.equalsIgnoreCase("30")) {
                            listNames.add(log);
                            // listCrditCount.add(cn.CREDIT_GIVEN);
                        }
                    }

                    commercialCreditDB = getHelper().getCommercialCreditExceptionDao();
                    List<CommercialDeliveryCreditDB> commercialList = commercialCreditDB.queryForAll();
                    for (CommercialDeliveryCreditDB cn : commercialList) {
                        String log = cn.product_id + ": " + cn.delivery_id + ": " + cn.credit_given;
                        if (Integer.toString(cn.credit_given).equalsIgnoreCase("30")) {
                            listNames.add(log);
                        }
                    }

                } catch (Exception ex) {
                    Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.no_data_available),
                            Toast.LENGTH_SHORT).show();
                }

                final SpinnerDialog dialog = new SpinnerDialog(CommercialActivity.this, listNames, getResources().getString(R.string.select_deliveryman));

                if (listNames.size() > 0) {
                    dialog.showSpinerDialog();
                    dialog.bindOnSpinerListener(new OnSpinerItemClick() {
                        @Override
                        public void onClick(String selectedDeliveryMan, int position) {

                            String[] splitNameValue = selectedDeliveryMan.split(":");
                            String deliveryManKEY = splitNameValue[0];
                            String DeliveryManVALUE = splitNameValue[1];
                            //String CREDIT_GIVEN = splitNameValue[2];
                            //String CREDIT_GIVEN = listCrditCount.get(position);
                            Log.e(deliveryManKEY, DeliveryManVALUE);
                            setSelectedDeliveryManId(parseInt(deliveryManKEY));

                            //Amey
                            try {
                                List<CommercialDeliveryCreditDB> commercialDeliveryCreditDB = commercialCreditDB.queryBuilder().where().eq("delivery_id", Integer.parseInt(deliveryManKEY)).and().eq("product_id", getPRODUCT_CODE()).and().eq("godown_id", getGoDownId()).query();
                                tvDeliveryMan.setText(DeliveryManVALUE);
                                if (commercialDeliveryCreditDB.size() > 0) {
                                    tvDeliveryMan.setText(DeliveryManVALUE + "\nCredit Cyl- " + commercialDeliveryCreditDB.get(0).credit_given);
                                }
                                tvDeliveryMan.setAllCaps(true);
                                tvDeliveryMan.setVisibility(View.VISIBLE);

                            } catch (java.sql.SQLException e) {
                                e.printStackTrace();
                            }
                            switchView();
                        }
                    });
                }
            }
        });
    }

    private void initFreshLayout() {

        layout_fresh.setVisibility(View.VISIBLE);
        layout_return.setVisibility(View.GONE);

        clearAllFields();
        btnSubmit.setText("Submit Fresh Trip");
        btnSubmit.setVisibility(View.VISIBLE);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String FRESH_CYL = input_fresh_full_cylinder.getText().toString().trim();
                int FRESH_CYL_COUNT = Integer.parseInt(FRESH_CYL);

                if (FRESH_CYL_COUNT == 0) {
                    input_fresh_full_cylinder.requestFocus();
                    input_fresh_full_cylinder.setError(getResources().getString(R.string.enter_full_cylnr));

                } else if (input_fresh_full_cylinder.getText().toString().trim().equalsIgnoreCase("0")) {
                    input_fresh_full_cylinder.requestFocus();
                    input_fresh_full_cylinder.setError(getResources().getString(R.string.zero_not_allowed));

                }/*else if (TOTAL_AVAILABLE_CYL == 0 ){
                     showErrorToast(CommercialActivity.this,"Error","Can't enter more than available CYL ");
                     input_fresh_full_cylinder.setError("Can't enter more than available CYL ");
                 }*/ /*else if (TOTAL_AVAILABLE_CYL > 0 && FRESH_CYL_COUNT > TOTAL_AVAILABLE_CYL){
                     showErrorToast(CommercialActivity.this,"Error","Can't enter more than available CYL ");
                     input_fresh_full_cylinder.setError("Can't enter more than available CYL ");
                 }*/ else {


                    final AlertDialog alertDialog = new AlertDialog.Builder(CommercialActivity.this).create();
                    alertDialog.setTitle("Entry");
                    alertDialog.setIcon(getResources().getDrawable(R.drawable.demo_cyl));
                    alertDialog.setMessage(getResources().getString(R.string.proceed_msg));
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "SAVE",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    showProgressBar();
                                    saveFreshEntry();
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

    private void saveFreshEntry() {

        try {

            RuntimeExceptionDao<CommercialDeliveryDB, Integer> commercialDB = getHelper().getCommercialRuntimeExceptionDao();

            uniqueId_Commercial = Integer.toString(getGoDownId()) + getPRODUCT_CODE() + Integer.toString(getSelectedDeliveryManId())
                    + currentDateTime() + randomNumber;
            Log.e("Unique", uniqueId_Commercial);


            ///new
            /*CommercialDeliveryDB commercialDeliveryDB = new CommercialDeliveryDB(1, getSelectedDeliveryManId(), getGoDownId(),
                    getPRODUCT_CODE(), getDateTime(), getApplicationUserId(), getTextFromET(input_fresh_full_cylinder),
                    0, 0, 0, 0, 0, 0,
                    getDateTime(), 0,0, "MOBILE", "INSERT", "N",
                    getDeviceId(),uniqueId_Commercial);//,uniqueId_Commercial);
            commercialDB.create(commercialDeliveryDB);
            */
            //old
            commercialDB.create(new CommercialDeliveryDB(1, getSelectedDeliveryManId(), getGoDownId(),
                    getPRODUCT_CODE(), getDateTime(), getApplicationUserId(), getTextFromET(input_fresh_full_cylinder),
                    0, 0, 0, 0, 0, 0,
                    getDateTime(), 0, 0, "MOBILE", "INSERT", "N",
                    getDeviceId(), uniqueId_Commercial));
            clearAllFields();
            showToast(getResources().getString(R.string.saved_success_msg));
            dismissProgressBar();

            finish();

        } catch (SQLException e) {
            showMessageDialog("Sorry! failed while storing data");
            e.printStackTrace();
        }
    }


    private void initReturnLayout() {

        layout_return.setVisibility(View.VISIBLE);
        layout_fresh.setVisibility(View.GONE);

        clearAllFields();
        btnSubmit.setVisibility(View.VISIBLE);
        btnSubmit.setText("Submit Return Trip");
        input_empty_cylinder.requestFocus();


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (input_empty_cylinder.getText().toString().equalsIgnoreCase("0") &&
                        input_return_sv.getText().toString().equalsIgnoreCase("0") &&
                        input_credit_given.getText().toString().equalsIgnoreCase("0") &&
                        input_return_full.getText().toString().equalsIgnoreCase("0")) {

                    Toast.makeText(CommercialActivity.this, "Empty, SV, Credit and Return full cylinder fields can not be zero.", Toast.LENGTH_SHORT).show();
                } else if (input_return_defective.getText().toString().trim().equalsIgnoreCase("")) {
                    input_return_defective.requestFocus();
                    input_return_defective.setError(getResources().getString(R.string.enter_defective));
                } else if (input_return_dbc.getText().toString().trim().equalsIgnoreCase("")) {
                    input_return_dbc.requestFocus();
                    input_return_dbc.setError(getResources().getString(R.string.enter_bc));
                } else if (input_lost.getText().toString().trim().equalsIgnoreCase("")) {
                    input_lost.requestFocus();
                    input_lost.setError(getResources().getString(R.string.enter_lost_cyl));
                } else if (input_return_full.getText().toString().trim().equalsIgnoreCase("")) {
                    input_return_full.requestFocus();
                    input_return_full.setError(getResources().getString(R.string.enter_return_empty_cylndr));
                } else {


                    final AlertDialog alertDialog = new AlertDialog.Builder(CommercialActivity.this).create();
                    alertDialog.setTitle("Entry");
                    alertDialog.setIcon(getResources().getDrawable(R.drawable.demo_cyl));
                    alertDialog.setMessage(getResources().getString(R.string.proceed_msg));
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "SAVE",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    showProgressBar();
                                    saveReturnEntry();
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


    private void saveReturnEntry() {

        try {
            /*final int min = 10;
            final int max = 110;
            final int random = new Random().nextInt((max - min) + 1) + min;
            Log.e("random number" ,Integer.toString(random));

            String uniqueId_Commercial = Integer.toString(getGoDownId())+getPRODUCT_CODE() + Integer.toString(getSelectedDeliveryManId())
                    + Constants.currentDateTime()+Integer.toString(random);*/


            RuntimeExceptionDao<CommercialDeliveryDB, Integer> commercialDB = getHelper().getCommercialRuntimeExceptionDao();

            uniqueId_Commercial = Integer.toString(getGoDownId()) + getPRODUCT_CODE() + Integer.toString(getSelectedDeliveryManId())
                    + currentDateTime() + randomNumber;
            Log.e("Unique", uniqueId_Commercial);


            // new
           /* CommercialDeliveryDB commercialDeliveryDB = new CommercialDeliveryDB(1, getSelectedDeliveryManId(), getGoDownId(),
                    getPRODUCT_CODE(), getDateTime(), getApplicationUserId(), 0, getTextFromET(input_empty_cylinder), getTextFromET(input_return_sv),
                    getTextFromET(input_return_dbc), getTextFromET(input_return_defective), getTextFromET(input_return_full), getTextFromET(input_credit_given),
                    getDateTime(), getApplicationUserId(), getTextFromET(input_lost),
                    "MOBILE", "INSERT", "N",getDeviceId(),uniqueId_Commercial ); //,uniqueId_Commercial);
            commercialDB.create(commercialDeliveryDB);*/

            //  old
            commercialDB.create(new CommercialDeliveryDB(1, getSelectedDeliveryManId(), getGoDownId(),
                    getPRODUCT_CODE(), getDateTime(), getApplicationUserId(), 0, getTextFromET(input_empty_cylinder), getTextFromET(input_return_sv),
                    getTextFromET(input_return_dbc), getTextFromET(input_return_defective), getTextFromET(input_return_full), getTextFromET(input_credit_given),
                    getDateTime(), getApplicationUserId(), getTextFromET(input_lost),
                    "MOBILE", "INSERT", "N", getDeviceId(), uniqueId_Commercial));
            clearAllFields();
            dismissProgressBar();
            finish();
            showToast(getResources().getString(R.string.saved_success_msg));

        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), R.string.could_not_store, Toast.LENGTH_SHORT).show();
            ex.printStackTrace();
        }

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

    private void showMessageDialog(final String message) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(message);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void clearAllFields() {

        input_fresh_full_cylinder.setText("0");
        input_empty_cylinder.setText("0");
        input_return_sv.setText("0");
        input_return_defective.setText("0");
        input_return_dbc.setText("0");
        input_return_full.setText("0");
        input_lost.setText("0");

    }


    private void requestFocus(View view) {
        InputMethodManager imm2 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm2.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        view.requestFocus();
    }

    public int getSelectedDeliveryManId() {
        return selectedDeliveryManId;
    }


    public void setSelectedDeliveryManId(int selectedDeliveryManId) {
        this.selectedDeliveryManId = selectedDeliveryManId;
    }

    private void showProgressBar() {
        //btnFreshSubmit.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void dismissProgressBar() {
        //btnFreshSubmit.setEnabled(true);
        progressBar.setVisibility(View.GONE);
    }


    @Override
    public void onSuccess(VolleySingleton.CallType type, String response) {

        Log.e("Response:", response);

        try {
            JSONObject objectResult = new JSONObject(response);
            if (objectResult.has("responseCode") && objectResult.optString("responseCode").equalsIgnoreCase("200")) {
                /*JSONArray productArray = objectResult.optJSONArray("productArray");
                String CLOSING_FULL = productArray.optJSONObject(0).optString("CLOSING_FULL");*/

                int CLOSING_FULL = objectResult.optInt("CLOSING_FULL");
                layout_cylinder.setVisibility(View.VISIBLE);
                TOTAL_AVAILABLE_CYL = CLOSING_FULL;
                cyl_count.setAnimationDuration(1000).countAnimation(0, TOTAL_AVAILABLE_CYL);
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
        Log.e("Failed: ", error.getLocalizedMessage());
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private CommercialActivity.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final CommercialActivity.ClickListener clickListener) {
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


    private void getAvailableCYL() {

        VolleySingleton.getInstance(getApplicationContext()).
                addResponseListener(VolleySingleton.CallType.GET_AVAILABLE_CYL, this);

        VolleySingleton.getInstance(getApplicationContext()).
                new_apiAvailableCYL(VolleySingleton.CallType.GET_AVAILABLE_CYL,
                        Constants.GET_AVAILABLE_CYLINDERS, String.valueOf(getPRODUCT_CODE()), String.valueOf(getGoDownId()));
        }


    @Override
    protected void onPause() {
        AppSettings.hideKeyboard(this);
        super.onPause();

    }
}
