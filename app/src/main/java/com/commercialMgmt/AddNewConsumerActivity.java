package com.commercialMgmt;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.commercialMgmt.models.CommercialProductModel;
import com.commercialMgmt.models.ConsumerModel;
import com.infosolutions.customviews.EvitaProgressDialog;
import com.infosolutions.database.ProductDB;
import com.infosolutions.evita.R;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.infosolutions.network.*;
import com.infosolutions.core.*;
import com.infosolutions.utils.AppSettings;
//import com.infosolutions.utils.Constant;
import com.infosolutions.database.*;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;
import khangtran.preferenceshelper.PreferencesHelper;
import com.infosolutions.network.*;

public class AddNewConsumerActivity extends AppCompatActivity implements ResponseListener{

    @BindView(R.id.scrollView)
    ScrollView scrollView;
    @BindView(R.id.et_consumer_name)
    EditText com_consumer_name;
    @BindView(R.id.et_mobile_number)
    EditText com_mobile_number;
    @BindView(R.id.et_state)
    EditText et_state;
    @BindView(R.id.et_address)
    EditText com_consumer_address;
    @BindView(R.id.et_email_id)
    EditText com_consumer_email_id;
    @BindView(R.id.et_product_name)
    AutoCompleteTextView com_product_name;
    @BindView(R.id.et_Discount)
    EditText com_consumer_discount;
    @BindView(R.id.et_Pan_No)
    EditText com_consumer_PAN_No;
    @BindView(R.id.et_GSTIN)
    EditText com_consumer_GSTIN;
    @BindView(R.id.btnAddNewConsumer)
    Button btn_addConsumer;
    @BindView(R.id.progress_bar_container)
    RelativeLayout progress_bar_container;


    private EvitaProgressDialog dialog;

    private DatabaseHelper databaseHelper;
    private int min = 10;
    private int max = 110;
    private int random = 0;
    private String uniqueId_AddConsumer;
    private String randomNumber;
    private int productId;
    private int productIdPosition;
    private  String mobile_no;
    private int UserId;
    private String default_str = "Select Product";
    private ArrayList<String> spinItems;
    private ArrayAdapter<String> spinAdapter;
    private  ArrayList<String> state ;


    private int[] productArr;
    private List<CommercialProductModel> productDBList;
    private String[] State={"Andhra Pradesh", "Arunachal Pradesh", "Assam", "Bihar", "Chhattisgarh", "Dadra and Nagar Haveli", "Daman and Diu", "Delhi", "Goa", "Gujarat", "Haryana", "Himachal Pradesh", "Jammu and Kashmir", "Jharkhand", "Karnataka",
            "Kerala", "Madhya Pradesh", "Maharashtra", "Manipur", "Meghalaya", "Mizoram", "Nagaland", "Orissa", "Puducherry", "Punjab", "Rajasthan", "Sikkim", "Tamil Nadu",
            "Telangana", "Tripura", "Uttar Pradesh", "Uttarakhand", "West Bengal"};

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_consumer);
        setupToolbar();

        ButterKnife.bind(this);

        com_consumer_discount.setText("0");
        disabledFocusFromET();
        fillState();

        UserId=PreferencesHelper.getInstance().getIntValue(Constants.LOGIN_DELIVERYMAN_ID,0);

        Log.e("UserId .................",String.valueOf(UserId));
        getProducts();
        saveConsumerBtn();
        VolleySingleton.getInstance(getApplicationContext()).addResponseListener(VolleySingleton.CallType.POST_COMMERCIAL_CONSUMER, this);
    }

    private void disabledFocusFromET() {

        et_state.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(et_state.getWindowToken(), 0);
                }
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void fillState() {
       state=new ArrayList<>();
        for (int i=0;i<State.length;i++)
        {
            state.add(State[i]);
        }


        et_state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SpinnerDialog dialog = new SpinnerDialog( AddNewConsumerActivity.this, state, "Select State");

                dialog.showSpinerDialog();
                dialog.bindOnSpinerListener(new OnSpinerItemClick() {
                    @Override
                    public void onClick(String state, int i) {
                        et_state.setText(state);

                    }
                });
            }
        });
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

    @SuppressLint("LongLogTag")
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
                Log.e("Item Position ",String.valueOf(productId));

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
    private void saveConsumerBtn() {
        btn_addConsumer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (com_consumer_name.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(),"Enter Consumer Name",Toast.LENGTH_SHORT).show();
                }
                else if (com_mobile_number.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(),"Enter Mobile Number",Toast.LENGTH_SHORT).show();

                }
                else if (!(com_mobile_number.getText().toString().length() ==10)) {
                    Toast.makeText(getApplicationContext(),"Mobile Number Not Valid",Toast.LENGTH_SHORT).show();

                }
                else if (com_product_name.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(),"Select Product",Toast.LENGTH_SHORT).show();
                }
                else if (et_state.getText().toString().equalsIgnoreCase(""))
                {
                    Toast.makeText(getApplicationContext(),"Select State",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    saveConfirmation_consumer();

                }
            }
        });
    }


    private boolean isValidMail(String email) {
        boolean check;
        Pattern p;
        Matcher m;

        String EMAIL_STRING = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        p = Pattern.compile(EMAIL_STRING);

        m = p.matcher(email);
        check = m.matches();

        if(!check) {
            com_consumer_email_id.setError("Not Valid Email");
        }
        return check;
    }

    private void saveConfirmation_consumer() {
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Add Consumer");
        alertDialog.setMessage(getResources().getString(R.string.proceed_msg));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "SAVE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //showProgressDialog();
                //isValidMail(com_consumer_email_id.getText().toString());
                progress_bar_container.setVisibility(View.VISIBLE);
                    saveConsumerDetails();
                progress_bar_container.setVisibility(View.GONE);
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

    private void saveConsumerDetails() {
        mobile_no=com_mobile_number.getText().toString();
        uniqueId();

        JSONObject parentJsonObj = new JSONObject();
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("CreatedBy",UserId);
                jsonObject.put("Business_Name",com_consumer_name.getText().toString());
                jsonObject.put("Contact_Person_mobile",com_mobile_number.getText().toString());
                jsonObject.put("Address1",com_consumer_address.getText().toString());
                jsonObject.put("Email",com_consumer_email_id.getText().toString());
                jsonObject.put("ProductID",productId);
                jsonObject.put("DisAmt",Double.valueOf(com_consumer_discount.getText().toString()));
                jsonObject.put("PAN",com_consumer_PAN_No.getText().toString());
                jsonObject.put("GSTIN",com_consumer_GSTIN.getText().toString());
                jsonObject.put("OpeningDate",Constants.getDateTime());
                jsonObject.put("uniqueId",uniqueId());
                jsonObject.put("ModeOfEntry","Mobile");
                jsonObject.put("IsActive","Y");
                jsonObject.put("State",et_state.getText().toString());

                parentJsonObj.put("objCommercialPartyMst",jsonObject);
                Log.e("consumer Data",parentJsonObj.toString());
                AppSettings.getInstance(this).saveCommercialConsumer(this,parentJsonObj);

            } catch (JSONException e) {
                e.printStackTrace();
            }
    }

    private String uniqueId() {
        random = new Random().nextInt((max - min) + 1) + min;
        Log.e("random number", Integer.toString(random));
        randomNumber = Integer.toString(random);

        uniqueId_AddConsumer=randomNumber+Constants.currentDateTime()+mobile_no;

        return uniqueId_AddConsumer;
    }


    private void setupToolbar() {

        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Add Customer");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_backspace);

        }

    @Override
    public void onSuccess(VolleySingleton.CallType type, String response) {
       hideProgressDialog();
        // responseMessage = "Success", IsAuthenticate = true, responseCode = 200

        try {
            JSONObject objectResult = new JSONObject(response);

            String responseMsg = objectResult.optString("responseMessage");

                if (objectResult.optString(Constants.responseCcode).equalsIgnoreCase("200")) {
                    Toast.makeText(this, responseMsg, Toast.LENGTH_SHORT).show();
                    JSONArray jsonArray = objectResult.optJSONArray("objCommercialPartyMst");
                    if(jsonArray != null) {
                        JSONObject jsonObject = jsonArray.optJSONObject(0);
                        if (jsonObject != null) {
                            saveConsumerToLocalDB(new ConsumerModel(1, jsonObject));
                        }
                    }
                    hideProgressDialog();
                    finish();
                }
        }
        catch (JSONException e)
        {
            System.out.println(e.getMessage());
            Log.e("JSON RESULT",e.getMessage());
        }

       // Toast.makeText(getApplicationContext(),"Consumer Added Successfully",Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("LongLogTag")
    private void saveConsumerToLocalDB(ConsumerModel consumerModel) {
        DatabaseHelper databaseHelper = new DatabaseHelper(AddNewConsumerActivity.this);
        RuntimeExceptionDao<ConsumerModel, Integer> consumerDB = getHelper().getComConsumerRTExceptionDao();

        /*ConsumerModel comdb = new ConsumerModel();

        int discount=Integer.parseInt(com_consumer_discount.getText().toString());

        comdb.consumer_no="";
        comdb.consumer_name=com_consumer_name.getText().toString();
        comdb.mobile_no=com_mobile_number.getText().toString();
        comdb.address = com_consumer_address.getText().toString();
        comdb.email_id= com_consumer_email_id.getText().toString();
        comdb.product_name= com_product_name.getText().toString();
        comdb.discount=discount ;
        comdb.pan_card=com_consumer_PAN_No.getText().toString() ;
        comdb.gstin = com_consumer_GSTIN.getText().toString();
        comdb.credit_cylinder = 0;
        //comdb.user_id=UserId;
        comdb.amount_credit_cylinder= 0;*/

        consumerDB.create(consumerModel);


    }

    @Override
    public void onFailure(VolleySingleton.CallType type, VolleyError error) {
        Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
    }
}
