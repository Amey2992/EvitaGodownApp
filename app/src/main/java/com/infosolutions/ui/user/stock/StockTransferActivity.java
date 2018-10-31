package com.infosolutions.ui.user.stock;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.infosolutions.core.BaseActivity;
import com.infosolutions.database.DatabaseHelper;
import com.infosolutions.database.DomesticDeliveryDB;
import com.infosolutions.database.ProductDB;
import com.infosolutions.evita.R;
import com.infosolutions.network.ResponseListener;
import com.infosolutions.network.VolleySingleton;
import com.infosolutions.ui.login.LoginActivity;
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
import java.util.HashMap;
import java.util.List;

import khangtran.preferenceshelper.PreferencesHelper;

import static com.infosolutions.network.Constants.KEY_GODOWN;
import static com.infosolutions.network.Constants.KEY_GODOWN_NAME;

public class StockTransferActivity extends BaseActivity implements ResponseListener {

    com.infosolutions.ui.user.stock.AutoCompleteTextView godown_edittext, product_edittext;
    List<String> lstgodown = new ArrayList();
    String godownJson = "";
    private String[] godownArr, productArr;
    private DatabaseHelper databaseHelper;
    TextView fulltextview_value, emptytextview_value, defectivetextview_value;
    EditText full_edittext, empty_edittext, def_edittext;
    private List<ProductDB> productDBList;
    android.widget.Button submitbutton;
    private String selectedgodwon;
    private int availableFullCylQty;
    private int availableEmptyCylQty;
    private int availableDefectiveQty;
    private int product_code;
    private int intGodownCode;
    HashMap<String,String> godownHash = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_transfer);
        VolleySingleton.getInstance(getApplicationContext()).addResponseListener(VolleySingleton.CallType.GET_STOCKS, this);
        VolleySingleton.getInstance(getApplicationContext()).addResponseListener(VolleySingleton.CallType.POST_STOCKS, this);

        init();
    }

    @Override
    public void injectDependency() {

    }

    @Override
    public int getLayoutId() {
        return 0;
    }


    private void init(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Stock Transfer");
        setSupportActionBar(toolbar);

        fulltextview_value = (TextView) findViewById(R.id.fulltextview_value);
        emptytextview_value = (TextView) findViewById(R.id.emptytextview_value);
        defectivetextview_value = (TextView) findViewById(R.id.defectivetextview_value);

        full_edittext = (EditText) findViewById(R.id.full_edittext);
        empty_edittext = (EditText) findViewById(R.id.empty_edittext);
        def_edittext = (EditText) findViewById(R.id.def_edittext);

        submitbutton = (Button) findViewById(R.id.submitbutton);
        submitbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int full_edittext_value;
                if(TextUtils.isEmpty(full_edittext.getText().toString()) && TextUtils.isEmpty(empty_edittext.getText().toString()) && TextUtils.isEmpty(def_edittext.getText().toString())){
                    Toast.makeText(StockTransferActivity.this,"Please Enter Quantity of Cylinders",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(full_edittext.getText().toString())){
                    full_edittext_value = 0;
                }else{
                    full_edittext_value = Integer.parseInt(full_edittext.getText().toString());;
                }

                int empty_edittext_value;
                if(TextUtils.isEmpty(empty_edittext.getText().toString())){
                    empty_edittext_value = 0;
                }else{
                    empty_edittext_value  = Integer.parseInt(empty_edittext.getText().toString());
                }

                int def_edittext_value;
                if(TextUtils.isEmpty(def_edittext.getText().toString())){
                    def_edittext_value = 0;
                }else{
                    def_edittext_value  = Integer.parseInt(def_edittext.getText().toString());
                }


                if(full_edittext_value <=  availableFullCylQty && empty_edittext_value <= availableEmptyCylQty && def_edittext_value <= availableDefectiveQty){
                    transferStock();
                }else{
                    Toast.makeText(StockTransferActivity.this,"Please select less than Available Cylinders",Toast.LENGTH_SHORT).show();
                }
            }
        });
        disabledView();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_backspace);

        String SELECTED_GODOWN_NAME = PreferencesHelper.getInstance().getStringValue(KEY_GODOWN_NAME,"");
        String SELECTED_GODOWN_CODE = PreferencesHelper.getInstance().getStringValue(KEY_GODOWN, "");
        String GODOWN_NAME_CODE = SELECTED_GODOWN_NAME + ":" + SELECTED_GODOWN_CODE;

        this.godownJson = AppSettings.getInstance(this).godownJson;
        try {
            JSONArray jsonArray = new JSONArray(this.godownJson);
            if(jsonArray != null ){
                for (int position = 0; position < jsonArray.length(); position++) {
                    try {
                        JSONObject jsonGodown = jsonArray.optJSONObject(position);

                        String godown_type_code = jsonGodown.optString("GODOWN_CODE");
                        intGodownCode = Integer.parseInt(godown_type_code);
                        String godown_type_name = jsonGodown.optString("DISPLAY_NAME");
                        String godown_name_code = godown_type_name + ":" + godown_type_code;
                        //builder.addItem(intGodownCode, godown_name_code);
                        //mapLinked.add(godown_name_code);
                        if (!GODOWN_NAME_CODE.equalsIgnoreCase(godown_name_code)) {
                            lstgodown.add(godown_name_code);
                        }

                        godownHash.put(godown_name_code,godown_type_code);

                    } catch (Exception e) {
                        //showErrorToast(LoginActivity.this, "Error", "Something went wrong " + e.getMessage());
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        godown_edittext = findViewById(R.id.godown_edittext);
        godown_edittext.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedgodwon = godownArr[position];
                getStockData(product_code);

            }
        });
        product_edittext = findViewById(R.id.product_edittext);
        product_edittext.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                product_code = productDBList.get(position).product_code;
                getStockData(product_code);
            }
        });

        RuntimeExceptionDao<ProductDB, Integer> productDB = getHelper().getProductRTExceptionDao();
        productDBList = productDB.queryForAll();
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

    private void transferStock() {
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Stock");
        alertDialog.setMessage(getResources().getString(R.string.proceed_msg));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "SAVE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showProgressDialog();
                saveStocks();
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

    private void saveStocks() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("ProductId",product_code);
            jsonObject.put("FrmGodownID",getGoDownId());
            jsonObject.put("ToGodownID", Integer.parseInt(godownHash.get(selectedgodwon)));
            jsonObject.put("FullCyll",full_edittext.getText().toString());
            jsonObject.put("EmptyCyll",empty_edittext.getText().toString());
            jsonObject.put("DefectiveCyll",def_edittext.getText().toString());
            jsonObject.put("Date",getDate());

            Log.d("stockjson",jsonObject.toString());
            AppSettings.getInstance(this).saveStocks(this,jsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //AppSettings.getInstance(this).saveStocks(this,new JSONObject());
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

    @Override
    public void onSuccess(VolleySingleton.CallType type, String response) {
        try {
            JSONObject objectResult = new JSONObject(response);

            if (type.equals(VolleySingleton.CallType.GET_STOCKS)){
                JSONArray jsonArray = objectResult.optJSONArray("Table");

                if (jsonArray != null) {
                    // Full cylinder
                    if(jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.optJSONObject(i);

                            availableFullCylQty = jsonObject.optInt("AvailableFullCylQty");
                            fulltextview_value.setText(Integer.toString(availableFullCylQty));
                        }
                    }else{
                        fulltextview_value.setText("0");
                    }
                }
                JSONArray jsonArray1 = objectResult.optJSONArray("Table1");
                // Empty cylinder
                if (jsonArray1 != null && jsonArray1.length() > 0) {
                    for (int i = 0; i < jsonArray1.length(); i++) {
                        JSONObject jsonObject = jsonArray1.optJSONObject(i);

                        availableEmptyCylQty = jsonObject.optInt("AvailableEmptyCylQty");
                        emptytextview_value.setText(Integer.toString(availableEmptyCylQty));
                    }
                }else{
                    emptytextview_value.setText("0");
                }

                JSONArray jsonArray2 = objectResult.optJSONArray("Table2");
                // Empty defective
                if (jsonArray2 != null && jsonArray2.length() > 0) {
                    for (int i = 0; i < jsonArray2.length(); i++) {
                        JSONObject jsonObject = jsonArray2.optJSONObject(i);

                        availableDefectiveQty = jsonObject.optInt("AvailableDefectiveQty");
                        defectivetextview_value.setText(Integer.toString(availableDefectiveQty));
                    }
                }else{
                    defectivetextview_value.setText("0");
                }

                enabledView();
                hideProgressDialog();
            }else{
                String responseMsg = objectResult.optString("responseMessage");
                if (objectResult.optString("responseCode").equalsIgnoreCase("200")) {
                    Toast.makeText(this, responseMsg, Toast.LENGTH_SHORT).show();
                    hideProgressDialog();
                    finish();
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(VolleySingleton.CallType type, VolleyError error) {
        hideProgressDialog();
    }

    private void getStockData(int... product_code){

        if(!TextUtils.isEmpty(product_edittext.getText().toString()) && !TextUtils.isEmpty(godown_edittext.getText().toString()) ) {
            JSONObject jsonObject = new JSONObject();
            JSONObject jsonObject1 = new JSONObject();
            try {
                jsonObject1.put("ProductId",product_code[0]);
                jsonObject1.put("FrmGodownID",getGoDownId());
                jsonObject1.put("Date",getDate());

                jsonObject.put("objAndrTransfer",jsonObject1);
                Log.d("--------------stock: ",jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            showProgressDialog();
            AppSettings.getInstance(this).getStocks(this,jsonObject);
        }

    }

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

    public void enabledView(){
        full_edittext.setEnabled(true);
        empty_edittext.setEnabled(true);
        def_edittext.setEnabled(true);
        submitbutton.setEnabled(true);
    }

    public void disabledView(){
        full_edittext.setEnabled(false);
        empty_edittext.setEnabled(false);
        def_edittext.setEnabled(false);
        submitbutton.setEnabled(false);
    }

}
