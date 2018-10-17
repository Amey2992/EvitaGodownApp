package com.commercialMgmt;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.infosolutions.customviews.EvitaProgressDialog;
import com.infosolutions.evita.R;

import java.util.Random;
import com.infosolutions.network.*;
import com.infosolutions.core.*;
import com.infosolutions.utils.AppSettings;
import com.infosolutions.utils.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import khangtran.preferenceshelper.PreferencesHelper;

public class AddNewConsumer extends AppCompatActivity {


    @BindView(R.id.scrollView)
    ScrollView scrollView;
    @BindView(R.id.et_consumer_name)
    EditText com_consumer_name;
    @BindView(R.id.et_mobile_number)
    EditText com_mobile_number;
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


    private EvitaProgressDialog dialog;

    private int min = 10;
    private int max = 110;
    private int random = 0;
    private String uniqueId_AddConsumer;
    private String randomNumber;
    private int product_code;
    private  String mobile_no;
    private String UserId;

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_consumer);
        setupToolbar();

        UserId=PreferencesHelper.getInstance().getStringValue(Constants.LOGIN_DELIVERYMAN_ID,"");

        Log.e("UserId .................",UserId);

        saveConsumerBtn();

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
                saveConfirmation_consumer();
            }
        });
    }



    private void saveConfirmation_consumer() {
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Stock");
        alertDialog.setMessage(getResources().getString(R.string.proceed_msg));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "SAVE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showProgressDialog();
                saveConsumerDetails();
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

            JSONObject jsonObject = new JSONObject();
            try {
                //jsonObject.put("CreatedBy",UserId);
                jsonObject.put("ConsumerName",com_consumer_name.getText().toString());
                jsonObject.put("MobileNo",Integer.parseInt(com_mobile_number.getText().toString()));
                jsonObject.put("ConsumerAddress",com_consumer_address.getText().toString());
                jsonObject.put("ConsumerEmail",com_consumer_email_id.getText().toString());
                jsonObject.put("ProductId",com_product_name.getText().toString());
                jsonObject.put("Discount",Float.valueOf(com_consumer_discount.getText().toString()));
                jsonObject.put("ConsumerPAN",com_consumer_PAN_No.getText().toString());
                jsonObject.put("ConsumerGSTIN",com_consumer_GSTIN.getText().toString());
                jsonObject.put("DateTime",Constants.getDateTime());
                jsonObject.put("uniqueId",uniqueId());

                AppSettings.getInstance(this).saveCommercialConsumer(this,jsonObject);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            AppSettings.getInstance(this).saveCommercialConsumer(this,new JSONObject());

    }

    private String uniqueId() {
        random = new Random().nextInt((max - min) + 1) + min;
        Log.e("random number", Integer.toString(random));
        randomNumber = Integer.toString(random);

        uniqueId_AddConsumer=randomNumber+Constants.getDateTime()+mobile_no;

        return uniqueId_AddConsumer;
    }



    private void setupToolbar() {

        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Domestic");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_backspace);

        }

}
