package com.commercialMgmt;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.commercialMgmt.models.CommercialProductModel;
import com.commercialMgmt.models.ConsumerModel;
import com.commercialMgmt.models.UserAssignedCylinderModel;
import com.infosolutions.customviews.EvitaProgressDialog;
import com.infosolutions.database.DatabaseHelper;
import com.infosolutions.database.DomesticDeliveryDB;
import com.infosolutions.evita.R;
import com.infosolutions.network.Constants;
import com.infosolutions.network.ResponseListener;
import com.infosolutions.network.VolleySingleton;
import com.infosolutions.service.GetCommercialConsumerService;
import com.infosolutions.utils.AppSettings;
import com.infosolutions.utils.Constant;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.UpdateBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;
import khangtran.preferenceshelper.PreferencesHelper;

import static java.lang.Integer.parseInt;

public class CommercialSaleActivity extends AppCompatActivity implements ResponseListener {

    @BindView(R.id.scrollView)
    ScrollView scrollView;
    @BindView(R.id.et_chalan)
    EditText et_chalan;
    @BindView(R.id.et_bpcl_rate)
    EditText et_bpcl_rate;
    @BindView(R.id.et_discount)
    EditText et_discount;
    @BindView(R.id.et_rate_for_party)
    EditText et_selling_price;
    @BindView(R.id.et_full_cyl)
    EditText et_full_cyl;
    @BindView(R.id.et_empty_cyl)
    EditText et_empty_cyl;
    @BindView(R.id.et_sv_cyl)
    EditText et_sv_cyl;
    @BindView(R.id.et_credit_cyl)
    EditText et_credit_cyl;
    @BindView(R.id.et_total_amt)
    EditText et_total_amt;
    @BindView(R.id.et_cash_amt)
    EditText et_cash_amt;
    @BindView(R.id.et_total_credit_cyl)
    EditText et_total_credit_cyl;
    @BindView(R.id.et_total_credit_amt)
    EditText et_total_credit_amt;
    @BindView(R.id.et_product_name)
    AutoCompleteTextView com_product_name;
    @BindView(R.id.et_consumer_name)
    EditText et_consumer_name;
    @BindView(R.id.et_balanced_credit_amt)
    EditText et_balanced_credit_amt;
    @BindView(R.id.btnSaveComDelivery)
    Button btnSaveComDelivery;

    @BindView(R.id.assigned_cylinder)
    TextView assigned_cylinder;

    private int productId;

    private EvitaProgressDialog dialog;
    private DatabaseHelper databaseHelper;
    private ArrayList<String> spinItems;
    private ArrayAdapter<String> spinAdapter;

    private int[] productArr;

    private String consumerList;
    private List<CommercialProductModel> productDBList;
    private List<ConsumerModel> consumerDBList;
    private String[] consumerArr;
    private ArrayList<String> consumerListItems;
    private ArrayAdapter<String> commercialAdapter;
    private Double BPCLrate;
    private int userId;

    private String selectedDeliveryManId;
    private ConsumerModel selectedConsumer;
    boolean isCommercialConsumerServiceRunning;

    private int min = 10;
    private int max = 110;
    private String uniqueId_AddConsumer;
    int creditCyl=0;

    private String consumer_name, Chalan;
    private Double MRP = 0.0, Discount = 0.0, Selling_price = 0.0, Total_Amt = 0.0, Cash_Amt = 0.0, Total_credit_amt = 0.0;
    private int full_cyl = 0, empty_cyl = 0, total_pending_cyl = 0, id_comm_party = 0, sv = 0;

    // Variables For Calculations
    private Double calSellingPrice = 0.0, calTotalAmt = 0.0;
    private int assignedCylinderQty , availableStock=0;
    private UserAssignedCylinderModel userAssignedCylinderModel;


    @BindView(R.id.progress_bar_container)
    RelativeLayout progress_bar_container;
    private int sv_cyl = 0;


    public String getSelectedDeliveryManId() {
        return selectedDeliveryManId;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.commercial_sale);

        isCommercialConsumerServiceRunning = AppSettings.getInstance(this).isCommercialConsumerServiceRunning;
        ButterKnife.bind(this);
        setupToolbar();


        disabledViews();

        userId = PreferencesHelper.getInstance().getIntValue(Constants.LOGIN_DELIVERYMAN_ID, 0);
        Log.e("UserID..", String.valueOf(userId));

        disabledFocusFromET();
        getConsumer();
        getProducts();
        saveCommercialSaleBtn();
        Calculation();
        VolleySingleton.getInstance(getApplicationContext()).addResponseListener(VolleySingleton.CallType.COMMERCIAL_SAVE_CONSUMER_DELIVERY, this);

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter(Constants.CONSUMER_BROADCAST));
        // To fetch assigned cylinder model


    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            progress_bar_container.setVisibility(View.GONE);

        }
    };

    private void disabledViews() {
        et_full_cyl.setEnabled(false);
        et_empty_cyl.setEnabled(false);
        et_sv_cyl.setEnabled(false);

        et_bpcl_rate.setEnabled(false);
        et_selling_price.setEnabled(false);
        et_credit_cyl.setEnabled(false);
        et_total_amt.setEnabled(false);
        et_total_credit_cyl.setEnabled(false);
        et_total_credit_amt.setEnabled(false);
        et_balanced_credit_amt.setEnabled(false);
    }

    private void enabledViews() {
        et_full_cyl.setEnabled(true);
        et_empty_cyl.setEnabled(true);
        et_sv_cyl.setEnabled(true);
    }

    private void Calculation() {

        // Calculation on changing dicount edittext

        et_discount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Double calMRP;
                Double calDiscount = 0.0;
                calMRP = Double.valueOf(et_bpcl_rate.getText().toString());

                if (!TextUtils.isEmpty(et_discount.getText())) {
                    calDiscount = Double.valueOf(et_discount.getText().toString());
                    calSellingPrice = calMRP - calDiscount;
                    if (calDiscount >= calMRP) {
                        et_discount.setError("You can't enter discount more than MRP");
                        et_discount.setText("0");
                        et_selling_price.setText(String.valueOf(calMRP));
                    } else {
                        et_selling_price.setText(String.valueOf(calSellingPrice));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        // Calculation on change full cyl edittext

        et_full_cyl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!TextUtils.isEmpty(et_full_cyl.getText().toString()) ) {
                    availableStock=assignedCylinderQty-Integer.parseInt(et_full_cyl.getText().toString());
                    /*if(userAssignedCylinderModel != null) {
                        userAssignedCylinderModel.Qty = availableStock;
                    }*/
                    if (Integer.parseInt(et_full_cyl.getText().toString()) > assignedCylinderQty) {
                        et_full_cyl.setError("Enter valid cylinder qty");
                        et_full_cyl.setText("0");
                    }
                }


                if (et_full_cyl.getText().toString().equalsIgnoreCase("0")) {
                    full_cyl = 0;
                    empty_cyl = 0;
                    sv = 0;
                    et_credit_cyl.setText("0");
                }
                calculateTotalAmt();

                // Credit amt calculattion without calculation
                Double balancedCreditAmt = 0.0;
                Double totalCreditAmt = 0.0, totalAmt = 0.0, cashAmt = 0.0;

                if (!TextUtils.isEmpty(et_total_credit_amt.getText().toString())
                        && !TextUtils.isEmpty(et_total_amt.getText().toString())) {
                    totalCreditAmt = Double.valueOf(et_total_credit_amt.getText().toString());
                    totalAmt = Double.valueOf(et_total_amt.getText().toString());
                    balancedCreditAmt = totalCreditAmt + totalAmt;
                    et_balanced_credit_amt.setText(String.valueOf(balancedCreditAmt));

                } else {
                    et_balanced_credit_amt.setText("0");
                }

                // credit cylinder calculation
                calculateCreditCylinder();

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        // calculation on change empty edittext
        et_empty_cyl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (et_full_cyl.getText().toString().equalsIgnoreCase("0")) {
                    full_cyl = 0;
                    empty_cyl = 0;
                    sv = 0;
                    et_credit_cyl.setText("0");
                }
                calculateCreditCylinder();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // calculations on change sv edittext
        et_sv_cyl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (et_full_cyl.getText().toString().equalsIgnoreCase("0")) {
                    full_cyl = 0;
                    empty_cyl = 0;
                    sv = 0;
                    et_credit_cyl.setText("0");
                }
                calculateCreditCylinder();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // calculations for cash amt based

        et_cash_amt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calcaulateCreditAmt();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    private void calcaulateCreditAmt() {
        Double balancedCreditAmt = 0.0;
        Double totalCreditAmt = 0.0, totalAmt = 0.0, cashAmt = 0.0;


        if (!TextUtils.isEmpty(et_total_credit_amt.getText().toString())
                && !TextUtils.isEmpty(et_total_amt.getText().toString())
                && !TextUtils.isEmpty(et_cash_amt.getText().toString())) {
            totalCreditAmt = Double.valueOf(et_total_credit_amt.getText().toString());
            totalAmt = Double.valueOf(et_total_amt.getText().toString());
            cashAmt = Double.valueOf(et_cash_amt.getText().toString());
            balancedCreditAmt = (totalCreditAmt + totalAmt) - cashAmt;
            if (cashAmt <= (totalCreditAmt + totalAmt)) {
                et_balanced_credit_amt.setText(String.valueOf(balancedCreditAmt));
            } else {
                et_cash_amt.setText("");
                et_cash_amt.setError("enter valid cash amt");
            }
        } else {
            totalCreditAmt = Double.valueOf(et_total_credit_amt.getText().toString());
            totalAmt = Double.valueOf(et_total_amt.getText().toString());
            et_balanced_credit_amt.setText(String.valueOf(totalCreditAmt + totalAmt));
        }

        /*Double d = new Double(et_balanced_credit_amt.getText().toString());
        selectedConsumer.amount_credit_cylinder = d.intValue();*/
    }

    private void calculateTotalAmt() {
        Double TotalAmt=0.0;

        if (!TextUtils.isEmpty(et_selling_price.getText().toString()) && !TextUtils.isEmpty(et_full_cyl.getText().toString()))
        {
            TotalAmt=Double.valueOf(et_selling_price.getText().toString())*Double.valueOf(et_full_cyl.getText().toString());
            et_total_amt.setText(String.valueOf(TotalAmt));
        }
        else
        {
            et_total_amt.setText("0");
        }

    }

    private void calculateCreditCylinder() {

        if(!TextUtils.isEmpty(et_full_cyl.getText().toString())) {
            full_cyl = Integer.parseInt(et_full_cyl.getText().toString()) + selectedConsumer.credit_cylinder;

        }else{
            full_cyl = selectedConsumer.credit_cylinder ;
        }
        if(!TextUtils.isEmpty(et_empty_cyl.getText().toString())) {
            empty_cyl = Integer.parseInt(et_empty_cyl.getText().toString());
            if(!TextUtils.isEmpty(et_sv_cyl.getText().toString())) {
                int finalcount = full_cyl - Integer.parseInt(et_sv_cyl.getText().toString());
                //int finalcount = Integer.parseInt(et_credit_cyl.getText().toString());
                if (empty_cyl <= finalcount) {
                    //et_empty_cyl.setText(Integer.toString(empty_cyl));
                } else {
                    if (et_sv_cyl.getText().toString().equalsIgnoreCase("0"))
                    {
                        //code
                    }
                    else {
                        et_sv_cyl.setText("0");
                        et_sv_cyl.setError("enter valid sv cyl");
                    }
                }
            }

        }else{
            empty_cyl = 0;
        }

        if(!TextUtils.isEmpty(et_sv_cyl.getText()) ) {

            sv = Integer.parseInt(et_sv_cyl.getText().toString());
            if(!TextUtils.isEmpty(et_empty_cyl.getText().toString())) {
                int finalcount = full_cyl - Integer.parseInt(et_empty_cyl.getText().toString());
                //int finalcount = Integer.parseInt(et_credit_cyl.getText().toString());
                if (sv <= finalcount) {
                    //et_empty_cyl.setText(Integer.toString(empty_cyl));
                } else {
                    if (et_empty_cyl.getText().toString().equalsIgnoreCase("0"))
                    {
                        //
                    }
                    else {
                        et_empty_cyl.setText("0");
                        et_empty_cyl.setError("enter valid empty cyl");
                    }
                }
            }

        }else{
            sv = 0;
        }
        creditCyl=full_cyl-empty_cyl-sv;
        et_credit_cyl.setText(Integer.toString(creditCyl));
       // selectedConsumer.credit_cylinder = creditCyl;
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        //AppSettings.hideKeyboard(this);
    }

    private void saveCommercialSaleBtn() {
        btnSaveComDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getETValues();

                if (et_consumer_name.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(),"Consumer Not Selected",Toast.LENGTH_SHORT).show();
                }
                else if (com_product_name.getText().toString().equalsIgnoreCase(""))
                {
                    Toast.makeText(getApplicationContext(),"Product Not Selected",Toast.LENGTH_SHORT).show();
                }
                else if (et_chalan.getText().toString().equalsIgnoreCase(""))
                {
                    Toast.makeText(getApplicationContext(),"Enter Chalan Number",Toast.LENGTH_SHORT).show();
                    et_chalan.requestFocus();
                }
                else if (et_full_cyl.getText().toString().equalsIgnoreCase(""))
                {
                    Toast.makeText(getApplicationContext(),"Enter Full Cylinder",Toast.LENGTH_SHORT).show();
                    et_full_cyl.requestFocus();
                }
                else if (et_cash_amt.getText().toString().equalsIgnoreCase(""))
                {
                    Toast.makeText(getApplicationContext(),"Enter Cash Amt",Toast.LENGTH_SHORT).show();
                    et_cash_amt.requestFocus();
                }
                else {
                    saveConfirmation();
                }
            }
        });
    }

    private void getETValues() {
        if(!TextUtils.isEmpty(et_consumer_name.getText())) {
            consumer_name = et_consumer_name.getText().toString();
        }
        if(!TextUtils.isEmpty(et_chalan.getText())) {
            Chalan = et_chalan.getText().toString();
        }
        if(!TextUtils.isEmpty(et_bpcl_rate.getText())) {
            MRP = Double.valueOf(et_bpcl_rate.getText().toString());
        }
        if(!TextUtils.isEmpty(et_discount.getText())) {
            Discount = Double.valueOf(et_discount.getText().toString());
        }
        if(!TextUtils.isEmpty(et_selling_price.getText())) {
            Selling_price = Double.valueOf(et_selling_price.getText().toString());
        }
        if(!TextUtils.isEmpty(et_total_amt.getText())) {
            Total_Amt = Double.valueOf(et_total_amt.getText().toString());
        }
        if(!TextUtils.isEmpty(et_cash_amt.getText())) {
            Cash_Amt = Double.valueOf(et_cash_amt.getText().toString());
        }
        if(!TextUtils.isEmpty(et_total_credit_amt.getText())) {
            Total_credit_amt = Double.valueOf(et_balanced_credit_amt.getText().toString());
        }
        if(!TextUtils.isEmpty(et_full_cyl.getText())) {
            full_cyl = Integer.parseInt(et_full_cyl.getText().toString());
        }
        if(!TextUtils.isEmpty(et_empty_cyl.getText())) {
            empty_cyl = Integer.parseInt(et_empty_cyl.getText().toString());
        }
        if(!TextUtils.isEmpty(et_total_credit_cyl.getText())) {
            total_pending_cyl = Integer.parseInt(et_total_credit_cyl.getText().toString());
        }
        if(!TextUtils.isEmpty(et_sv_cyl.getText())) {
            sv_cyl = Integer.parseInt(et_sv_cyl.getText().toString());
        }

    }


    private void saveConfirmation() {

        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Add Consumer");
            alertDialog.setMessage(getResources().getString(R.string.proceed_msg));
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "SAVE", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    showProgressDialog();
                    //isValidMail(com_consumer_email_id.getText().toString());

                    saveCommercialSale();
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

    private void saveCommercialSale() {

        JSONObject parentJsonObj = new JSONObject();
        JSONObject jsonObject = new JSONObject();

        try {


            jsonObject.put("DeliveredBy", userId);
            //jsonObject.put("DATETIME",Constants.getDateTime());
            jsonObject.put("ConsumerName",consumer_name);
            jsonObject.put("IdProduct",productId);
            jsonObject.put("ChallanNo",Chalan);
            jsonObject.put("MRP",MRP);
            jsonObject.put("Discount",Discount);
            jsonObject.put("SellingPrice",Selling_price);
            jsonObject.put("FullCylQty",full_cyl);
            jsonObject.put("EmptyCylRec",empty_cyl);
            jsonObject.put("TotalAmount",Total_Amt);
            jsonObject.put("CashAmount",Cash_Amt);
            jsonObject.put("TotalPendingEmptyCyl",total_pending_cyl);
            jsonObject.put("TotalCreditAmount",Total_credit_amt);
            jsonObject.put("IdCommParty",selectedConsumer.ConsumerID);
            jsonObject.put("YY", AppSettings.getYear());
            jsonObject.put("ModeOfEntry", "Mobile");
            jsonObject.put("sv", sv_cyl);

            //jsonObject.put("availableStock", "availableStock");


            parentJsonObj.put("objCommercialSale",jsonObject);
            AppSettings.getInstance(this).saveCommercialConsumerDelivery(this,parentJsonObj);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }


    private void disabledFocusFromET() {
        et_consumer_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(et_consumer_name.getWindowToken(), 0);
                }
            }
        });
    }



    private void getConsumer() {
        consumerListItems= new ArrayList<>();
        RuntimeExceptionDao<ConsumerModel, Integer> comConsumerDB = getHelper().getComConsumerRTExceptionDao();
        consumerDBList = comConsumerDB.queryForAll();
        int consumerSize = consumerDBList.size();
        consumerListItems.clear();

        consumerArr = new String[consumerDBList.size()];
        for(int i = 0; i < consumerDBList.size(); i++){

            consumerListItems.add(consumerDBList.get(i).consumer_name + " : "+ consumerDBList.get(i).consumer_no);
        }

        if(isCommercialConsumerServiceRunning){
            et_consumer_name.setEnabled(false);
        }else{
            et_consumer_name.setEnabled(true);
        }
        et_consumer_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final SpinnerDialog dialog = new SpinnerDialog( CommercialSaleActivity.this, consumerListItems, "Select Consumer");

                if(consumerListItems.size()>0)
                {
                    dialog.showSpinerDialog();
                    dialog.bindOnSpinerListener(new OnSpinerItemClick() {
                        @SuppressLint("LongLogTag")
                        @Override
                        public void onClick(String consumer, int i) {

                            selectedConsumer = consumerDBList.get(i);
                            String CossumerName=consumer;
                            AppSettings.hideKeyboard(CommercialSaleActivity.this);
                            et_consumer_name.setText(CossumerName);
                            et_total_credit_cyl.setText(Integer.toString(selectedConsumer.credit_cylinder));
                            et_total_credit_amt.setText(Integer.toString(selectedConsumer.amount_credit_cylinder));
                            et_balanced_credit_amt.setText(Integer.toString(selectedConsumer.amount_credit_cylinder));


                            // productId = productDBList.get(position).product_id;
                            Double discount= Double.valueOf(consumerDBList.get(i).discount);
                            Log.e("discount",String.valueOf(discount));
                            Log.e("Selected Consumer Id....",String.valueOf(selectedConsumer.ConsumerID));
                            //et_discount.setText(String.valueOf(discount));
                        }
                    });
                }
            }
        });
    }


    public void setSelectedDeliveryManId(String selectedDeliveryManId) {
        this.selectedDeliveryManId = selectedDeliveryManId;
    }


    private void getProducts() {

        spinItems = new ArrayList<>();

    RuntimeExceptionDao<CommercialProductModel, Integer> comProductDB = getHelper().getComProductRTExceptionDao();
    productDBList = comProductDB.queryForAll();
    int productSize = productDBList.size();

    //----------------------------------------------------------------------------------
    //  product poaitions

    productArr = new int[productDBList.size()];
        for(int i = 0; i < productDBList.size(); i++){
        productArr[i] = productDBList.get(i).product_id;
        Log.e("Products position....",String.valueOf(productArr[i]));
    }

    //------------------------------------------------------------------------------------

        spinItems.clear();

        if (productSize > 0) {
        for (CommercialProductModel item : productDBList)
            spinItems.add(item.product_name);
    }

    //spinItems.add(0,default_str);

    spinAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, spinItems);
        //spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        com_product_name.setAdapter(spinAdapter);

        com_product_name.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            productId = productDBList.get(position).product_id;
            BPCLrate= productDBList.get(position).bpcl_rate;
            enabledViews();
            try {

                userAssignedCylinderModel = getHelper().getUserAssignedCylinderModelRuntimeExceptionDao().queryBuilder().where().eq("PRODUCT_ID",productId).queryForFirst();
                if(userAssignedCylinderModel != null) {
                    assignedCylinderQty = userAssignedCylinderModel.Qty;
                    assigned_cylinder.setVisibility(View.VISIBLE);
                    assigned_cylinder.setText("Assigned Cylinders: " + Integer.toString(userAssignedCylinderModel.Qty));
                }else{
                    assignedCylinderQty = 0;
                    assigned_cylinder.setText("Assigned Cylinders: " + Integer.toString(assignedCylinderQty));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            et_bpcl_rate.setText(String.valueOf(BPCLrate));
                   if (!TextUtils.isEmpty(et_consumer_name.getText())) {
                       if (selectedConsumer.product_name.equalsIgnoreCase(productDBList.get(position).product_name)) {
                           et_discount.setText(Integer.toString(selectedConsumer.discount));
                       } else {
                           et_discount.setText("0");
                       }
                   }
            }
        });
    }

    private DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }

    public void showProgressDialog() {
        try {
            if (dialog != null) {
                if (dialog.isShowing()) {
                    return;
                }
            }
            dialog = new EvitaProgressDialog(getApplicationContext());
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hideProgressDialog() {
        if (dialog != null) {
            if (dialog.isShowing())
                dialog.dismiss();
        }
    }


    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Commercial Delivery");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_backspace);
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
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_transfer:
                progress_bar_container.setVisibility(View.VISIBLE);
                Intent intent = new Intent(this, GetCommercialConsumerService.class);
                startService(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

            Log.d("loginType:",Constants.LOGIN_DELIVERYMAN);
            MenuItem item = menu.findItem(R.id.action_transfer).setVisible(true);
            menu.findItem(R.id.action_setting).setVisible(false);
            menu.findItem(R.id.action_logout).setVisible(false);
            menu.findItem(R.id.action_list_view).setVisible(false);
            item.setIcon(R.drawable.consumer_refresh_icon);

        return true;
    }


    @Override
    public void onSuccess(VolleySingleton.CallType type, String response) {
        hideProgressDialog();
        // responseMessage = "Success", IsAuthenticate = true, responseCode = 200

        try {
            JSONObject objectResult = new JSONObject(response);

            String responseMsg = objectResult.optString("responseMessage");

            if (objectResult.optString(Constants.responseCcode).equalsIgnoreCase("200")) {
                try {
                    UpdateBuilder<ConsumerModel, Integer> updateBuilder;
                    UpdateBuilder<UserAssignedCylinderModel, Integer> updateBuilder1;
                    updateBuilder =  getHelper().getConsumerModelExceptionDao().updateBuilder();
                    updateBuilder.where().eq("ConsumerID",selectedConsumer.ConsumerID);
                    Double d = new Double(et_balanced_credit_amt.getText().toString());
                    updateBuilder.updateColumnValue("credit_cylinder",creditCyl);
                    updateBuilder.updateColumnValue("amount_credit_cylinder",d.intValue());
                    updateBuilder.update();

                    if(userAssignedCylinderModel != null) {
                        updateBuilder1 = getHelper().getUserAssignedCylinderModelRuntimeExceptionDao().updateBuilder();
                        updateBuilder1.where().eq("PRODUCT_ID", userAssignedCylinderModel.PRODUCT_ID);
                        updateBuilder1.updateColumnValue("Qty", availableStock);
                        updateBuilder1.update();
                    }


                } catch (SQLException e) {
                    e.printStackTrace();
                }



                Toast.makeText(this, responseMsg, Toast.LENGTH_SHORT).show();
                //saveConsumerToLocalDB();
                hideProgressDialog();
                finish();
            }

        }
        catch (JSONException e)
        {
            System.out.println(e.getMessage());
            Log.e("JSON RESULT",e.getMessage());
        }
    }

    @Override
    public void onFailure(VolleySingleton.CallType type, VolleyError error) {
        Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
    }
}
