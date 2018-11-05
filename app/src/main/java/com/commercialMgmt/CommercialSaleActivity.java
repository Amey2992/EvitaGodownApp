package com.commercialMgmt;

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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.commercialMgmt.models.CommercialProductModel;
import com.commercialMgmt.models.ConsumerModel;
import com.infosolutions.customviews.EvitaProgressDialog;
import com.infosolutions.database.DatabaseHelper;
import com.infosolutions.evita.R;
import com.infosolutions.network.Constants;
import com.infosolutions.utils.AppSettings;
import com.infosolutions.utils.Constant;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;

import static java.lang.Integer.parseInt;

public class CommercialSaleActivity extends AppCompatActivity {

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

    private String selectedDeliveryManId;
    private ConsumerModel selectedConsumer;
    boolean isCommercialConsumerServiceRunning;

    private int min = 10;
    private int max = 110;
    private String uniqueId_AddConsumer;

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

    }

    private void saveCommercialSaleBtn() {
        btnSaveComDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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


    private void saveConfirmation() {

        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Add Consumer");
            alertDialog.setMessage(getResources().getString(R.string.proceed_msg));
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "SAVE", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    showProgressDialog();
                    //isValidMail(com_consumer_email_id.getText().toString());

                   // saveCommercialSale();
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

            jsonObject.put("DATETIME",Constants.getDateTime());
            jsonObject.put("CONSUMER_NAME",et_consumer_name.getText().toString());
            jsonObject.put("PRODUCT_ID",productId);
            jsonObject.put("CHALAN",et_chalan.getText().toString());
            jsonObject.put("BPCL_RATE",et_bpcl_rate.getText().toString());
            jsonObject.put("DISCOUNT",et_discount);
            jsonObject.put("SELLING_PRICE",et_selling_price);
            jsonObject.put("FULL_CYL",et_full_cyl);
            jsonObject.put("EMPTY_CYL",et_empty_cyl);
            jsonObject.put("CREDIT_CYL",et_credit_cyl);
            jsonObject.put("TOTAL_AMT",et_total_amt);
            jsonObject.put("TOTAL_CREDIT_CYL",et_total_credit_cyl);
            jsonObject.put("TOTAL_CREDIT_AMT",et_total_credit_amt);


            parentJsonObj.put("objCommercialSale",jsonObject);
            AppSettings.getInstance(this).saveCommercialConsumer(this,parentJsonObj);
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

                final SpinnerDialog dialog = new SpinnerDialog(CommercialSaleActivity.this, consumerListItems, "Select Consumer");

                if(consumerListItems.size()>0)
                {
                    dialog.showSpinerDialog();
                    dialog.bindOnSpinerListener(new OnSpinerItemClick() {
                        @Override
                        public void onClick(String consumer, int i) {

                            selectedConsumer = consumerDBList.get(i);
                            String CossumerName=consumer;
                            et_consumer_name.setText(CossumerName);

                           // productId = productDBList.get(position).product_id;
                            Double discount= Double.valueOf(consumerDBList.get(i).discount);
                            Log.e("discount",String.valueOf(discount));
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

}
