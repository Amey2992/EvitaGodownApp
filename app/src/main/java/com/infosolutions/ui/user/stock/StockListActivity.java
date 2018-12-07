package com.infosolutions.ui.user.stock;


import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.infosolutions.core.BaseActivity;
import com.infosolutions.core.EvitaApplication;
import com.infosolutions.evita.R;
import com.infosolutions.network.Constants;
import com.infosolutions.network.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class StockListActivity extends BaseActivity {

    private String TAG      = StockListActivity.class.getSimpleName();
    private String RESP_MSG = "";
    private TableLayout     left_table_layout,right_table_layout, opening_stock_table, delivery_table, closing_stock_table, other_stock_table;
    private ProgressBar     progressBar;
    private String GODOWN_ID;
    View divider;
    TextView loadsTextview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setToolbarHost();
    }

    @Override
    public void injectDependency() { EvitaApplication.getEvitaComponents().inject(StockListActivity.this);}

    @Override
    public int getLayoutId() { return R.layout.activity_stock_layout; }

    @SuppressLint("ClickableViewAccessibility")
    private void setToolbarHost() {

        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Stock Details");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_backspace);
        setGODOWN_ID(Constants.getSharedPrefWithKEY(getApplicationContext(),Constants.KEY_GODOWN));
        VolleySingleton.getInstance(getApplicationContext()).addResponseListener(VolleySingleton.CallType.STOCK_REPORT, this);
        left_table_layout = findViewById(R.id.left_table_layout);
        right_table_layout = findViewById(R.id.right_table_layout);
        opening_stock_table = findViewById(R.id.opening_stock_table);
        delivery_table = findViewById(R.id.delivery_table);
        closing_stock_table = findViewById(R.id.closing_stock_table);
        other_stock_table = findViewById(R.id.other_stock_table);
        divider = findViewById(R.id.divider);
        loadsTextview = findViewById(R.id.loadsTextview);
        loadsTextview.setTextColor(getResources().getColor(R.color.colorPrimary));
        loadsTextview.setTextSize(20);
        loadsTextview.setBackgroundColor(getResources().getColor(R.color.colorLightGrey));
        loadsTextview.setPadding(35, 20, 15, 20);


        if (Constants.isNetworkAvailable(getApplicationContext())){
            showProgressDialog();
            fetchNetworkCall();
        }else {
            Toast.makeText(this, "No Network Connection", Toast.LENGTH_SHORT).show();
        }


    }





    private void fetchNetworkCall() {

        VolleySingleton.getInstance(getApplicationContext())
                .apiGetStockReport(VolleySingleton.CallType.STOCK_REPORT,
                        Constants.STOCK_REPORT, "GETSTOCKSNEW", getGODOWN_ID());
    }


    @Override
    public void onSuccess(VolleySingleton.CallType type, String response) {

        hideProgressDialog();

        try {

            JSONObject jsonObject = new JSONObject(response);
            String responseCode = jsonObject.optString("ResponseCode");

            if (responseCode.equalsIgnoreCase("200")) {

                openingStock(jsonObject.optString("OPENING_STOCKS"));
                loadsTextview.setVisibility(View.VISIBLE);
                loadsReceived(jsonObject.optString("LOAD_RECEVIED"));
                divider.setVisibility(View.VISIBLE);
                loadsSend(jsonObject.optString("LOAD_SEND"));
                loadDelivery(jsonObject.optString("DELIVERY"));
                loadClosingStock(jsonObject.optString("CLOSING_STOCKS"));
                loadOtherStock(jsonObject.optString("OTHER_STOCKS"));

            } else {
                //RESP_MSG = jsonObject.optString("RESP_MSG");
                RESP_MSG = jsonObject.optString("responseMessage");
                Toast.makeText(StockListActivity.this, RESP_MSG, Toast.LENGTH_SHORT).show();
            }
        }catch (Exception ex)
        {
            Toast.makeText(StockListActivity.this,
                    R.string.invalid_data, Toast.LENGTH_SHORT).show();
            ex.printStackTrace();
        }
    }



    @Override
    public void onFailure(VolleySingleton.CallType type, VolleyError error)
    {

        hideProgressDialog();

        Toast.makeText(StockListActivity.this,
                R.string.server_not_responding,
                Toast.LENGTH_SHORT).show();
    }


    private void setHeaderStock(String description, String credit,  String field, String lost,TableLayout... tableLayouts) {

        TableRow row= new TableRow(this);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        lp.setMargins(10,10,10,10);
        row.setLayoutParams(lp);

        TextView tvDescription = new TextView(this);
        TextView tvCredit = new TextView(this);
        TextView tvOnFeild = new TextView(this);
        TextView tvLost = new TextView(this);


        tvDescription.setText(description);
        tvCredit.setText(credit);
        tvOnFeild.setText(field);
        tvLost.setText(lost);

        applyRowMarginLeft(tvDescription);
        applyRowMarginRight(tvCredit);
        applyRowMarginRight(tvOnFeild);
        applyRowMarginRight(tvLost);

        row.addView(tvDescription);
        row.addView(tvCredit);
        row.addView(tvOnFeild);
        row.addView(tvLost);

        tableLayouts[0].addView(row);
    }



    /* OPENING STOCKS*/
    private void openingStock(String loadOpeningStock){

        try {

            JSONArray arrayOpening = new JSONArray(loadOpeningStock);
            int openingLength=arrayOpening.length();
            if (openingLength>0){

                applyHeaderMargin("OPENING STOCKS",opening_stock_table);
                setHeaderStock("", "FULL", "EMPTY", "DEFECTIVE",opening_stock_table);

                for (int i = 0; i<arrayOpening.length(); i++)
                {
                    JSONObject jsonReceived = arrayOpening.optJSONObject(i);

                    String DEFECTIVE     =  jsonReceived.optString("DEFECTIVE");
                    String DESCRIPTION   =  jsonReceived.optString("DESCRIPTION");
                    String OPENING_FULL  =  jsonReceived.optString("OPENING_FULL");
                    String OPENING_EMPTY =  jsonReceived.optString("OPENING_EMPTY");

                    initOtherStockLayout(DESCRIPTION, OPENING_FULL, OPENING_EMPTY, DEFECTIVE,opening_stock_table);
                }

            }

        } catch (JSONException e) {
            Toast.makeText(this, "Invalid data received opening field", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }

    /* SEND LOADS */
    private void loadsSend(String load_send){
        //applyHeaderMargin("",right_table_layout);
        setLoadHeader("", "SEND", "", "", "",right_table_layout);
        setHeader("", "SOUND", "DEFECTIVE", "", "",right_table_layout);

        try {
            JSONArray arraySend = new JSONArray(load_send);


            for (int position = 0; position<arraySend.length(); position++){

                JSONObject jsonSend     = arraySend.optJSONObject(position);
                //JSONObject jsonReceived = arrayReceived.getJSONObject(position);
                String description      = jsonSend.optString("DESCRIPTION");
                //String soundReceive     = jsonReceived.getString("SOUND");
                //String lost             = jsonReceived.getString("LOST_TRUCK_RECEVING");
                String soundSend        = jsonSend.optString("SOUND");
                String defective        = jsonSend.optString("TRUCK_DEFECTIVE");

                //initLoadsLayout(description, soundReceive, lost, soundSend, defective);
                initLoadsLayout(description, soundSend, defective,right_table_layout);
            }

        } catch (JSONException e)
        {
            Toast.makeText(this, R.string.invalid_data_received, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    /* RECEIVED LOADS */
    private void loadsReceived(String load_received) {

        //applyHeaderMargin("LOADS",left_table_layout);
        setLoadHeader("", "RECEIVED", "", "", "",left_table_layout);
        setHeader("", "SOUND", "LOST", "", "",left_table_layout);

        try {
            //JSONArray arraySend = new JSONArray(load_send);
            JSONArray arrayReceived = new JSONArray(load_received);

            for (int position = 0; position<arrayReceived.length(); position++){

                //JSONObject jsonSend     = arraySend.getJSONObject(position);
                JSONObject jsonReceived = arrayReceived.optJSONObject(position);
                String description      = jsonReceived.optString("DESCRIPTION");
                String soundReceive     = jsonReceived.optString("SOUND");
                String lost             = jsonReceived.optString("LOST_TRUCK_RECEVING");
                //String soundSend        = jsonSend.getString("SOUND");
                //String defective        = jsonSend.getString("TRUCK_DEFECTIVE");

                //initLoadsLayout(description, soundReceive, lost, soundSend, defective);
                initLoadsLayout(description, soundReceive, lost,left_table_layout);
            }

        } catch (JSONException e)
        {
            Toast.makeText(this, R.string.invalid_data_received, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }




    /* HEADERS */
    private void setLoadHeader(String Title, String title1,  String title2, String title3, String title4,TableLayout... tableLayouts) {

        TableRow row= new TableRow(this);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        lp.setMargins(10,10,10,10);
        row.setGravity(Gravity.CENTER);
        row.setLayoutParams(lp);

        TextView tvTitle = new TextView(this);
        TextView tvTitle1 = new TextView(this);
        tvTitle1.setGravity(Gravity.CENTER);
        tvTitle1.setTextColor(Color.WHITE);
        tvTitle1.setTextSize(15);
        tvTitle1.setPadding(35, 20, 15, 20);

        TextView tvTitle2 = new TextView(this);
        TextView tvTitle3 = new TextView(this);
        TextView tvTitle4 = new TextView(this);

        tvTitle.setText(Title);
        tvTitle1.setText(title1);
        tvTitle2.setText(title2);
        tvTitle3.setText(title3);
        tvTitle4.setText(title4);

        tvTitle1.setBackgroundResource(R.drawable.btn_round_corner);
        tvTitle4.setBackgroundResource(R.drawable.btn_round_corner);

        applyRowMarginLeft(tvTitle);
        //applyRowMarginRight(tvTitle1);
        applyRowMarginRight(tvTitle2);
        applyRowMarginRight(tvTitle3);
        applyRowMarginRight(tvTitle4);

        tvTitle1.setTextColor(getResources().getColor(R.color.colorWhite));
        tvTitle4.setTextColor(getResources().getColor(R.color.colorWhite));


        row.addView(tvTitle1);
/*        row.addView(tvTitle);

        row.addView(tvTitle1);
        row.addView(tvTitle2);
        row.addView(tvTitle3);
        row.addView(tvTitle4);
*/

        tableLayouts[0].addView(row);
    }





    private void setHeader(String Title, String title1,  String title2, String title3, String title4,TableLayout... tableLayouts) {

        TableRow row= new TableRow(this);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        lp.setMargins(10,10,10,10);
        row.setLayoutParams(lp);

        TextView tvTitle = new TextView(this);
        TextView tvTitle1 = new TextView(this);
        TextView tvTitle2 = new TextView(this);
        TextView tvTitle3 = new TextView(this);
        TextView tvTitle4 = new TextView(this);

        tvTitle.setText(Title);
        tvTitle1.setText(title1);
        tvTitle2.setText(title2);
        tvTitle3.setText(title3);
        tvTitle4.setText(title4);


        applyRowMarginLeft(tvTitle);
        applyRowMarginRight(tvTitle1);
        applyRowMarginRight(tvTitle2);
        applyRowMarginRight(tvTitle3);
        applyRowMarginRight(tvTitle4);

        row.addView(tvTitle);
        row.addView(tvTitle1);
        row.addView(tvTitle2);
        row.addView(tvTitle3);
        row.addView(tvTitle4);

        tableLayouts[0].addView(row);
    }




    /* DELIVERY */
    private void loadDelivery(String loadDelivery){

        try {

            JSONArray arrayDelivery = new JSONArray(loadDelivery);
            int deliveryLength=arrayDelivery.length();

            if (deliveryLength>0){

                applyHeaderMargin("DELIVERY",delivery_table);
                setHeaderDelivery("", "FULL", "EMPTY", "SV/DBC", "DEFECTIVE", "TV Out",delivery_table);

                for (int i = 0; i<arrayDelivery.length(); i++){
                    JSONObject jsonReceived = arrayDelivery.optJSONObject(i);

                    String DESCRIPTION = jsonReceived.optString("DESCRIPTION");
                    String FULL = jsonReceived.optString("DELIVERY_FULL");
                    String EMPTY = jsonReceived.optString("DELIVERY_EMPTY");
                    String SV_DBC = jsonReceived.optString("SV");
                    String DEFECTIVE = jsonReceived.optString("DEFECTIVE");
                    String TVOUT = jsonReceived.optString("TV");

                    initOther5(DESCRIPTION, FULL, EMPTY, SV_DBC, DEFECTIVE, TVOUT,delivery_table);
                }
            }

        } catch (JSONException e) {
            Toast.makeText(this, R.string.invalid_data_received, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }



    /* CLOSING */
    private void loadClosingStock(String loadClosingStock){

        try {
            JSONArray arrayClosing = new JSONArray(loadClosingStock);
            int closingLength = arrayClosing.length();
            if (closingLength>0){
                applyHeaderMargin("CURRENT STOCKS",closing_stock_table);
                setHeaderStock("", "FULL", "EMPTY", "DEFECTIVE",closing_stock_table);

                for (int i = 0; i<arrayClosing.length(); i++){
                    JSONObject jsonReceived = arrayClosing.optJSONObject(i);

                    String DESCRIPTION =jsonReceived.optString("DESCRIPTION");
                    String CLOSING_FULL =jsonReceived.optString("CLOSING_FULL");
                    String CLOSING_EMPTY =jsonReceived.optString("CLOSING_EMPTY");
                    String DEFECTIVE =jsonReceived.optString("DEFECTIVE");

                    initOtherStockLayout(DESCRIPTION, CLOSING_FULL, CLOSING_EMPTY, DEFECTIVE,closing_stock_table);
                }
            }

        } catch (JSONException e) {
            Toast.makeText(this, "Invalid data received closing field", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }



    /* OTHERS */
    private void loadOtherStock(String loadOtherStock){

        try {
            JSONArray arrayOther = new JSONArray(loadOtherStock);
            int otherLength = arrayOther.length();
            if (otherLength>0){

                applyHeaderMargin("OTHER STOCKS",other_stock_table);
                setHeaderStock("", "CREDIT", "LOST", "ONFIELD",other_stock_table);

                for (int i = 0; i<arrayOther.length(); i++){

                    JSONObject jsonReceived = arrayOther.optJSONObject(i);
                    String DESCRIPTION = jsonReceived.optString("DESCRIPTION");
                    String CREDIT = jsonReceived.optString("CREDIT");
                    String LOST_DELIVERY_CYLINDERS = jsonReceived.optString("LOST_DELIVERY_CYLINDERS");
                    String ON_FIELD = jsonReceived.optString("ON_FIELD");

                    initOtherStockLayout(DESCRIPTION, CREDIT, LOST_DELIVERY_CYLINDERS, ON_FIELD,other_stock_table);
                }
            }

        } catch (JSONException e) {
            Toast.makeText(this, "Invalid data received other stock field", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }
    /* LOADS */
    private void initLoadsLayout(String description, String soundReceive, String lost, TableLayout... tableLayouts){

        TableRow row= new TableRow(this);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        lp.setMargins(10,10,10,10);
        row.setLayoutParams(lp);

        TextView tvDescription = new TextView(this);
        TextView tvSoundReceive = new TextView(this);
        TextView tvLost = new TextView(this);
        TextView tvSoundSend = new TextView(this);
        TextView tvDefective = new TextView(this);

        tvDescription.setText(description);
        tvSoundReceive.setText(soundReceive);
        tvLost.setText(lost);
        //tvSoundSend.setText(soundSend);
        //tvDefective.setText(defective);

        applyRowMarginLeft(tvDescription);
        applyRowMarginRight(tvSoundReceive);
        applyRowMarginRight(tvLost);
        //applyRowMarginRight(tvSoundSend);
        //applyRowMarginRight(tvDefective);

        row.addView(tvDescription);
        row.addView(tvSoundReceive);
        row.addView(tvLost);
        //row.addView(tvSoundSend);
        //row.addView(tvDefective);

        tableLayouts[0].addView(row);
    }




    /* RECEIVE LAYOUT */
    private void initReceivedLayout(String Descript, String sound, String lost){

        TableRow row= new TableRow(this);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        lp.setMargins(10,10,10,10);
        row.setLayoutParams(lp);

        TextView tvDescription = new TextView(this);
        TextView tvSound = new TextView(this);
        TextView tvLost = new TextView(this);

        tvDescription.setText(Descript);
        tvSound.setText(sound);
        tvLost.setText(lost);

        applyRowMarginLeft(tvDescription);
        applyRowMarginRight(tvSound);
        applyRowMarginRight(tvLost);

        row.addView(tvDescription);
        row.addView(tvSound);
        row.addView(tvLost);

        left_table_layout.addView(row);
    }




    /* OTHERS */
    private void initOtherStockLayout( String description,String credit, String onFeild, String lostCyl,TableLayout... tableLayouts) {

        TableRow row= new TableRow(this);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        lp.setMargins(10,10,10,10);
        row.setLayoutParams(lp);

        TextView tvDescription = new TextView(this);
        TextView tvCredit = new TextView(this);
        TextView tvOnFeild = new TextView(this);
        TextView tvLost = new TextView(this);

        tvDescription.setText(description);
        tvCredit.setText(credit);
        tvOnFeild.setText(onFeild);
        tvLost.setText(lostCyl);

        applyRowMarginLeft(tvDescription);
        applyRowMarginRight(tvCredit);
        applyRowMarginRight(tvOnFeild);
        applyRowMarginRight(tvLost);

        row.addView(tvDescription);
        row.addView(tvCredit);
        row.addView(tvOnFeild);
        row.addView(tvLost);

        tableLayouts[0].addView(row);

    }




    /* OTHER */
    private void initOther5( String description, String credit, String onFeild, String lostCyl, String abcd, String tv, TableLayout... tableLayouts) {

        TableRow row= new TableRow(this);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        lp.setMargins(10,10,10,10);
        row.setLayoutParams(lp);

        TextView tvDescription = new TextView(this);
        TextView tvCredit = new TextView(this);
        TextView tvOnFeild = new TextView(this);
        TextView tvLost = new TextView(this);
        TextView tvABCD = new TextView(this);
        TextView tvTV = new TextView(this);

        tvDescription.setText(description);
        tvCredit.setText(credit);
        tvOnFeild.setText(onFeild);
        tvLost.setText(lostCyl);
        tvABCD.setText(abcd);
        tvTV.setText(tv);

        applyRowMarginLeft(tvDescription);
        applyRowMarginRight(tvCredit);
        applyRowMarginRight(tvOnFeild);
        applyRowMarginRight(tvLost);
        applyRowMarginRight(tvABCD);
        applyRowMarginRight(tvTV);


        row.addView(tvDescription);
        row.addView(tvCredit);
        row.addView(tvOnFeild);
        row.addView(tvLost);
        row.addView(tvABCD);
        row.addView(tvTV);

        tableLayouts[0].addView(row);

    }




    /* DELIVERY */
    private void setHeaderDelivery(String Description, String Full, String Empty, String sv_dbc, String def,  String tv_out, TableLayout... tableLayouts) {

        TableRow row= new TableRow(this);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        lp.setMargins(10,10,10,10);
        row.setLayoutParams(lp);


        TextView tvDescription = new TextView(this);
        TextView tvSV = new TextView(this);
        TextView tvDeliveryEmpty = new TextView(this);
        TextView tvDefective = new TextView(this);
        TextView tvDeliveryFull = new TextView(this);
        TextView tvTV = new TextView(this);


        tvDescription.setText(Description);
        tvSV.setText(Full);
        tvDeliveryEmpty.setText(Empty);
        tvDefective.setText(sv_dbc);
        tvDeliveryFull.setText(def);
        tvTV.setText(tv_out);


        applyRowMarginLeft(tvDescription);
        applyRowMarginRight(tvSV);
        applyRowMarginRight(tvDeliveryEmpty);
        applyRowMarginRight(tvDefective);
        applyRowMarginRight(tvDeliveryFull);
        applyRowMarginRight(tvTV);

        row.addView(tvDescription);
        row.addView(tvSV);
        row.addView(tvDeliveryEmpty);
        row.addView(tvDefective);
        row.addView(tvDeliveryFull);
        row.addView(tvTV);

        tableLayouts[0].addView(row);
    }





    private void applyHeaderMargin(String title, TableLayout... tableLayouts) {

        TextView view = new TextView(this);
        view.setTextColor(getResources().getColor(R.color.colorPrimary));
        view.setTextSize(20);
        view.setBackgroundColor(getResources().getColor(R.color.colorLightGrey));
        view.setText(title);
        view.setPadding(35, 20, 15, 20);

        tableLayouts[0].addView(view);
        //opening_stock_table.addView(view);
    }


    private TextView applyRowMarginLeft(TextView viewHeader) {

        viewHeader.setTextSize(15);
        viewHeader.setTextColor(getResources().getColor(R.color.colorBlue));
        viewHeader.setPadding(15, 20, 15, 20);
        viewHeader.setGravity(Gravity.CENTER);
        return viewHeader;
    }


    private TextView applyRowMarginRight(TextView viewRow) {

        viewRow.setTextSize(15);
        viewRow.setTextColor(getResources().getColor(R.color.colorBlack));
        viewRow.setPadding(15, 20, 15, 20);
        viewRow.setGravity(Gravity.CENTER);
        return viewRow;
    }


    public String getGODOWN_ID() { return GODOWN_ID;}
    public void setGODOWN_ID(String GODOWN_ID) {
        this.GODOWN_ID = GODOWN_ID;
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




