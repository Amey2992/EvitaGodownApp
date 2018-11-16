package com.commercialMgmt;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.commercialMgmt.models.CommercialProductModel;
import com.commercialMgmt.models.ConsumerModel;
import com.infosolutions.customviews.EvitaProgressDialog;
import com.infosolutions.database.DatabaseHelper;
import com.infosolutions.evita.R;
import com.infosolutions.network.Constants;
import com.infosolutions.network.ResponseListener;
import com.infosolutions.network.VolleySingleton;
import com.infosolutions.utils.AppSettings;
import com.infosolutions.utils.Constant;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import org.json.JSONException;
import org.json.JSONObject;

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
    @BindView(R.id.et_credit_cyl)
    EditText et_credit_cyl;
    @BindView(R.id.et_total_amt)
    EditText et_total_amt;
    @BindView(R.id.et_total_credit_cyl)
    EditText et_total_credit_cyl;
    @BindView(R.id.et_total_credit_amt)
    EditText et_total_credit_amt;
    @BindView(R.id.et_product_name)
    AutoCompleteTextView com_product_name;
    @BindView(R.id.et_consumer_name)
    EditText et_consumer_name;
    @BindView(R.id.btnSaveComDelivery)
    Button btnSaveComDelivery;

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

    private String consumer_name,Chalan;
    private Double MRP,Discount,Selling_price,Total_Amt,Cash_Amt,Total_credit_amt;
    private int full_cyl,empty_cyl,total_pending_cyl,id_comm_party;

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
        et_bpcl_rate.setFocusable(false);

        disabledFocusFromET();

        getConsumer();
        getProducts();
        saveCommercialSaleBtn();
        VolleySingleton.getInstance(getApplicationContext()).addResponseListener(VolleySingleton.CallType.COMMERCIAL_SAVE_CONSUMER_DELIVERY, this);



    }


    @Override
    protected void onResume() {
        super.onResume();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        AppSettings.hideKeyboard(this);
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
                else if (et_bpcl_rate.getText().toString().equalsIgnoreCase(""))
                {
                    Toast.makeText(getApplicationContext(),"Enter Product Rate",Toast.LENGTH_SHORT).show();
                }
                else if (et_chalan.getText().toString().equalsIgnoreCase(""))
                {
                    Toast.makeText(getApplicationContext(),"Enter Chalan Number",Toast.LENGTH_SHORT).show();
                }
                else if (et_full_cyl.getText().toString().equalsIgnoreCase(""))
                {
                   // et_full_cyl.setText("0");
                }
                else {
                    saveConfirmation();
                }
            }
        });
    }

    private void getETValues() {

       /* private String consumer_name,Chalan;
        private Double MRP,Discount,Selling_price,Total_Amt,Cash_Amt,Total_credit_amt;
        private int full_cyl,empty_cyl,total_pending_cyl,id_comm_party;
*/
        consumer_name=et_consumer_name.getText().toString();
        Chalan=et_chalan.getText().toString();
        MRP=Double.valueOf(et_bpcl_rate.getText().toString());
        Discount=Double.valueOf(et_discount.getText().toString());
        Selling_price=Double.valueOf(et_selling_price.getText().toString());
        Total_Amt=Double.valueOf(et_total_amt.getText().toString());
        Cash_Amt=Double.valueOf(et_total_amt.getText().toString());
        Total_credit_amt=Double.valueOf(et_total_credit_amt.getText().toString());
        full_cyl=Integer.parseInt(et_full_cyl.getText().toString());
        empty_cyl=Integer.parseInt(et_empty_cyl.getText().toString());
        total_pending_cyl=Integer.parseInt(et_total_credit_cyl.getText().toString());



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

    private void getConsumer() {
        consumerListItems= new ArrayList<>();
        RuntimeExceptionDao<ConsumerModel, Integer> comConsumerDB = getHelper().getComConsumerRTExceptionDao();
        consumerDBList = comConsumerDB.queryForAll();
        int consumerSize = consumerDBList.size();
        consumerListItems.clear();

        consumerArr = new String[consumerDBList.size()];
        for(int i = 0; i < consumerDBList.size(); i++){

            consumerListItems.add(consumerDBList.get(i).consumer_name);
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
                            et_consumer_name.setText(CossumerName);


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

    spinAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinItems);
        spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        com_product_name.setAdapter(spinAdapter);

        com_product_name.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            productId = productDBList.get(position).product_id;
            BPCLrate= productDBList.get(position).bpcl_rate;

            et_bpcl_rate.setText(String.valueOf(BPCLrate));

                if(selectedConsumer.product_name.equalsIgnoreCase(productDBList.get(position).product_name)){
                    et_discount.setText(Integer.toString(selectedConsumer.discount));
                }else{
                    et_discount.setText("0");
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
    public void onSuccess(VolleySingleton.CallType type, String response) {
        hideProgressDialog();
        // responseMessage = "Success", IsAuthenticate = true, responseCode = 200

        try {
            JSONObject objectResult = new JSONObject(response);

            String responseMsg = objectResult.optString("responseMessage");

            if (objectResult.optString(Constants.responseCcode).equalsIgnoreCase("200")) {
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
