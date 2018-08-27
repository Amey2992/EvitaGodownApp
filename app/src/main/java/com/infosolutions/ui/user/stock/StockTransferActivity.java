package com.infosolutions.ui.user.stock;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.infosolutions.database.DatabaseHelper;
import com.infosolutions.database.ProductDB;
import com.infosolutions.evita.R;
import com.infosolutions.ui.login.LoginActivity;
import com.infosolutions.utils.AppSettings;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import khangtran.preferenceshelper.PreferencesHelper;

import static com.infosolutions.network.Constants.KEY_GODOWN;
import static com.infosolutions.network.Constants.KEY_GODOWN_NAME;

public class StockTransferActivity extends AppCompatActivity {

    com.infosolutions.ui.user.stock.AutoCompleteTextView godown_edittext, product_edittext;
    List<String> lstgodown = new ArrayList();
    String godownJson;
    private String[] godownArr, productArr;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_transfer);
        init();
    }

    private void init(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Stock Transfer");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_backspace);

        String SELECTED_GODOWN_NAME = PreferencesHelper.getInstance().getStringValue(KEY_GODOWN_NAME,"");
        String SELECTED_GODOWN_CODE = PreferencesHelper.getInstance().getStringValue(KEY_GODOWN, "");
        String GODOWN_NAME_CODE = SELECTED_GODOWN_NAME + ":" + SELECTED_GODOWN_CODE;

        this.godownJson = AppSettings.getInstance(this).godownJson;
        try {
            JSONArray jsonArray = new JSONArray(this.godownJson);
            for (int position = 0; position < jsonArray.length(); position++) {
                try {
                    JSONObject jsonGodown = jsonArray.getJSONObject(position);

                    String godown_type_code = jsonGodown.getString("GODOWN_CODE");
                    int intGodownCode = Integer.parseInt(godown_type_code);
                    String godown_type_name = jsonGodown.getString("DISPLAY_NAME");
                    String godown_name_code = godown_type_name + ":" + godown_type_code;
                    //builder.addItem(intGodownCode, godown_name_code);
                    //mapLinked.add(godown_name_code);
                    if(!GODOWN_NAME_CODE.equalsIgnoreCase(godown_name_code)){
                        lstgodown.add(godown_name_code);
                    }

                } catch (JSONException e) {
                    //showErrorToast(LoginActivity.this, "Error", "Something went wrong " + e.getMessage());
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        godown_edittext = (com.infosolutions.ui.user.stock.AutoCompleteTextView)findViewById(R.id.godown_edittext);
        godown_edittext.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedgodwon = godownArr[position];

            }
        });
        product_edittext = (com.infosolutions.ui.user.stock.AutoCompleteTextView)findViewById(R.id.product_edittext);
        product_edittext.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               String selectedProduct = productArr[position];
            }
        });

        RuntimeExceptionDao<ProductDB, Integer> productDB = getHelper().getProductRTExceptionDao();
        List<ProductDB> productDBList = productDB.queryForAll();
        productArr = new String[productDBList.size()];
        for(int i = 0; i < productDBList.size(); i++){
            productArr[i] = productDBList.get(i).product_description;
        }

        godownArr = new String[lstgodown.size()];
        godownArr = lstgodown.toArray(godownArr);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item, godownArr);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item, productArr);

        godown_edittext.setThreshold(1);
        //Set the adapter
        godown_edittext.setAdapter(adapter);
        product_edittext.setAdapter(adapter1);


    }

    private DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
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
}
