package com.commercialMgmt;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.commercialMgmt.models.CommercialProductModel;
import com.commercialMgmt.models.ConsumerModel;
import com.infosolutions.customviews.EvitaProgressDialog;
import com.infosolutions.database.DatabaseHelper;
import com.infosolutions.evita.R;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CommercialSale extends AppCompatActivity {

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
    @BindView(R.id.btnSaveComDelivery)
    Button btnSaveComDelivery;

    private int productId;
    private EvitaProgressDialog dialog;
    private DatabaseHelper databaseHelper;
    private ArrayList<String> spinItems;
    private ArrayAdapter<String> spinAdapter;

    private int[] productArr;

    private List<CommercialProductModel> productDBList;
    private List<ConsumerModel> consumerDBList;
    private String[] consumerArr;
    private ArrayList<String> consumerListItems;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.commercial_sale);

        ButterKnife.bind(this);
        setupToolbar();

        getProducts();
        getConsumer();

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
            consumerArr[i] = consumerDBList.get(i).consumer_name;
            Log.e("Products position....",String.valueOf(productArr[i]));
        }

/*
        if (consumerSize > 0) {
            for (ConsumerModel item : consumerListItems)
                consumerListItems.add(item.consumer_name);
        }
*/

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


    private void setupToolbar() {

        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Commercial Delivery");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_backspace);

    }


}
