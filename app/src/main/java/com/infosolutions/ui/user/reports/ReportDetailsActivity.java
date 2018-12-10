package com.infosolutions.ui.user.reports;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.infosolutions.evita.R;
import com.infosolutions.network.Constants;
import com.infosolutions.network.ResponseListener;
import com.infosolutions.network.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.infosolutions.network.Constants.TYPEFACE_BOLD;
import static com.infosolutions.network.Constants.TYPEFACE_LARGE;


public class ReportDetailsActivity extends AppCompatActivity implements ResponseListener{

    private TableLayout tableLayout ;
    private String requestType = null;
    private String headerTitle;
    private ProgressBar progressBar;
    public String getGODOWN_ID() {
        return GODOWN_ID;
    }
    public void setGODOWN_ID(String GODOWN_ID) {
        this.GODOWN_ID = GODOWN_ID;
    }
    private String GODOWN_ID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock);

        Intent intent = getIntent();
        requestType =intent.getStringExtra("reportName");
        headerTitle = intent.getStringExtra("header");
        setRequestType(requestType);

        Toolbar toolbar = findViewById(R.id.toolbar);

        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(headerTitle);
        setSupportActionBar(toolbar);
        tableLayout = findViewById(R.id.table_layout);
        progressBar = findViewById(R.id.progressBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_backspace);


        setGODOWN_ID(Constants.getSharedPrefWithKEY(getApplicationContext(),Constants.KEY_GODOWN));

        VolleySingleton.getInstance(getApplicationContext())
                .addResponseListener(VolleySingleton.CallType.REPORT_DOMESTIC, this);

        VolleySingleton.getInstance(getApplicationContext())
                .addResponseListener(VolleySingleton.CallType.REPORT_COMMERCIAL, this);

        VolleySingleton.getInstance(getApplicationContext())
                .addResponseListener(VolleySingleton.CallType.REPORT_TRUCK_RECEIVED, this);

        VolleySingleton.getInstance(getApplicationContext())
                .addResponseListener(VolleySingleton.CallType.REPORT_TRUCK_SEND, this);

        VolleySingleton.getInstance(getApplicationContext())
                .addResponseListener(VolleySingleton.CallType.REPORT_TV_DETAILS, this);

        loadReport();

    }



    private void loadReport() {

        if (Constants.isNetworkAvailable(getApplicationContext()))
        {
            progressBar.setVisibility(View.VISIBLE);

/*
            if (headerTitle.equalsIgnoreCase("Domestic")){

                VolleySingleton.getInstance(getApplicationContext()).
                        apiGetReportList(VolleySingleton.CallType.REPORT_DOMESTIC,
                                Constants.EVITA_API_URL, getRequestType(), getGODOWN_ID());

            }else if (headerTitle.equalsIgnoreCase("Commercial")){

                VolleySingleton.getInstance(getApplicationContext()).
                        apiGetReportList(VolleySingleton.CallType.REPORT_COMMERCIAL,
                                Constants.EVITA_API_URL, getRequestType(),  getGODOWN_ID());

            }else if (headerTitle.equalsIgnoreCase("Truck Received")){

                VolleySingleton.getInstance(getApplicationContext()).
                        apiGetReportList(VolleySingleton.CallType.REPORT_TRUCK_RECEIVED,
                                Constants.EVITA_API_URL, getRequestType(), getGODOWN_ID());

            }else if (headerTitle.equalsIgnoreCase("Truck Send")){

                VolleySingleton.getInstance(getApplicationContext()).
                        apiGetReportList(VolleySingleton.CallType.REPORT_TRUCK_SEND,
                                Constants.EVITA_API_URL, getRequestType(), getGODOWN_ID());

            }else if (headerTitle.equalsIgnoreCase("TV Details")){

                VolleySingleton.getInstance(getApplicationContext()).
                        apiGetReportList(VolleySingleton.CallType.REPORT_TV_DETAILS,
                                Constants.EVITA_API_URL, getRequestType(), getGODOWN_ID());
            }

*/


        }else {
            Toast.makeText(getApplicationContext(), R.string.no_network_available, Toast.LENGTH_SHORT).show();
        }

    }




    @Override
    public void onSuccess(VolleySingleton.CallType type, String response) {

        progressBar.setVisibility(View.GONE);

        try {
            JSONObject jsonObject = new JSONObject(response);
            String respMessage = jsonObject.optString("responseMessage");
            String respCode = jsonObject.optString("responseCode");

            if (respCode.equalsIgnoreCase("200")){
                JSONArray arrayReport = jsonObject.optJSONArray("reportResult");

                if (type.equals(VolleySingleton.CallType.REPORT_DOMESTIC)){

                    showHeaderTitle(headerTitle);
                    loadDomestic(arrayReport);

                }else if (type.equals(VolleySingleton.CallType.REPORT_COMMERCIAL)){

                    showHeaderTitle(headerTitle);
                    loadDomestic(arrayReport);

                }else if (type.equals(VolleySingleton.CallType.REPORT_TRUCK_RECEIVED)){

                    showHeaderTitle(headerTitle);
                    loadTruckReceived(arrayReport);

                }else if (type.equals(VolleySingleton.CallType.REPORT_TRUCK_SEND)){

                    showHeaderTitle(headerTitle);
                    loadTruckSend(arrayReport);

                }else if (type.equals(VolleySingleton.CallType.REPORT_TV_DETAILS)){

                    showHeaderTitle(headerTitle);
                    loadTvDetails(arrayReport);
                }


            }else {

                Toast.makeText(this, respMessage, Toast.LENGTH_SHORT).show();
                LinearLayout layoutError = findViewById(R.id.errorMsg);
                layoutError.setVisibility(View.VISIBLE);
                TextView tvNoDataFound = findViewById(R.id.tvNoDataFound);
                tvNoDataFound.setText(respMessage);

                Button btnRetry = findViewById(R.id.btnRetry);
                btnRetry.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
            }

        } catch (JSONException e) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, R.string.invalid_data, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }


    }


    @Override
    public void onFailure(VolleySingleton.CallType type, VolleyError error) {
        progressBar.setVisibility(View.GONE);
        Toast.makeText(this, R.string.server_not_responding,
                Toast.LENGTH_SHORT).show();
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

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    private void loadDomestic(JSONArray arrayReceived) {

        if (arrayReceived.length()>0){

            try {
                setHeaders("PRODUCT TYPE","NAME",  "FULL", "EMPTY", "SV", "DBC", "DEFECTIVE", "LOST CYLINDER", "RETURN FULL", "");

                for (int position = 0; position<arrayReceived.length(); position++){

                    JSONObject jsonReceived = arrayReceived.optJSONObject(position);
                    String productType = jsonReceived.optString("DESCRIPTION");
                    String name = jsonReceived.optString("NAME");
                    String fullCylinder = jsonReceived.optString("FULL_CYLINDER");
                    String empty = jsonReceived.optString("EMPTY");
                    String sv = jsonReceived.optString("SV");
                    String dbc = jsonReceived.optString("DBC");
                    String DEFECTIVE = jsonReceived.optString("DEFECTIVE");
                    String LOST = jsonReceived.optString("LOST");
                    String returnFull = jsonReceived.optString("RETURN_FULL");

                    createLayout(productType, name,  fullCylinder, empty, sv, dbc, DEFECTIVE, LOST,returnFull,"");
                }

            } catch (Exception e) {
                Toast.makeText(this, "Invalid data received field", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }else{
            Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show();
        }

    }

    private void loadTruckReceived(JSONArray arrayReceived) {

        if (arrayReceived.length()>0){

            try {
                setHeaders("TRUCK NUMBER",  "INVOICE NO", "PRODUCT TYPE",  "QUANTITY", "LOST", "", "","","","");

                for (int position = 0; position<arrayReceived.length(); position++){

                    JSONObject jsonReceived = arrayReceived.optJSONObject(position);

                    String VECHICAL_NO = jsonReceived.optString("VECHICAL_NO");
                    String INVOICE_NO = jsonReceived.optString("INVOICE_NO");
                    String DESCRIPTION = jsonReceived.optString("DESCRIPTION");
                    String QUANTITY = jsonReceived.optString("QUANTITY");
                    String LOST = jsonReceived.optString("LOST");

                    createLayout(VECHICAL_NO, INVOICE_NO, DESCRIPTION,  QUANTITY, LOST,"","","","","");
                }

            } catch (Exception e) {
                Toast.makeText(this, "Invalid data received field", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }else{
            Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show();
        }

    }

    private void loadTruckSend(JSONArray arrayReceived) {

        if (arrayReceived.length()>0){

            try {
                setHeaders("TRUCK NUMBER", "ERV NUMBER", "PRODUCT TYPE",  "QUANTITY", "DEFECTIVE", "","","","","");

                for (int position = 0; position<arrayReceived.length(); position++){

                    JSONObject jsonReceived = arrayReceived.optJSONObject(position);
                    String VEHICLE_NO = jsonReceived.optString("VECHICAL_NO");
                    String ERV_NO = jsonReceived.optString("ERV_NO");
                    String DESCRIPTION = jsonReceived.optString("DESCRIPTION");
                    String QUANTITY = jsonReceived.optString("QUANTITY");
                    String DIFFECTIVE = jsonReceived.optString("DIFFECTIVE");

                    createLayout(VEHICLE_NO, ERV_NO, DESCRIPTION, QUANTITY, DIFFECTIVE, "","","","","");
                }

            } catch (Exception e) {
                Toast.makeText(this, "Invalid data received field", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }else{
            Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show();
        }

    }

    private void loadTvDetails(JSONArray arrayReceived) {

        if (arrayReceived.length()>0){

            try {
                setHeaders("PRODUCT TYPE", "CONSUMER NUMBER", "QUANTITY", "", "", "","","","","");

                for (int position = 0; position<arrayReceived.length(); position++){

                    JSONObject jsonReceived = arrayReceived.optJSONObject(position);
                    String DESCRIPTION = jsonReceived.optString("DESCRIPTION");
                    String CONSUMER_NO = jsonReceived.optString("CONSUMER_NO");
                    String QUANTITY = jsonReceived.optString("QUANTITY");

                    createLayout(DESCRIPTION, CONSUMER_NO, QUANTITY, "", "", "","","","","");
                }

            } catch (Exception e) {
                Toast.makeText(this, "Invalid data received field", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }else{
            Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show();
        }

    }

    private void createLayout(String headerTitle1,String headerTitle2,  String headerTitle3, String headerTitle4, String headerTitle5, String headerTitle6, String headerTitle7, String headerTitle8, String headerTitle9, String headerTitle10){

        TableRow row= new TableRow(this);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        lp.setMargins(20,20,20,20);
        row.setBackground(getResources().getDrawable(R.drawable.btn_white_background));
        row.setLayoutParams(lp);


        TextView tvHeaderTitle1 = new TextView(this);
        TextView tvHeaderTitle2 = new TextView(this);
        TextView tvHeaderTitle3 = new TextView(this);
        TextView tvHeaderTitle4 = new TextView(this);
        TextView tvHeaderTitle5 = new TextView(this);
        TextView tvHeaderTitle6 = new TextView(this);
        TextView tvHeaderTitle7 = new TextView(this);
        TextView tvHeaderTitle8 = new TextView(this);
        TextView tvHeaderTitle9 = new TextView(this);
        TextView tvHeaderTitle10 = new TextView(this);


        tvHeaderTitle1.setText(headerTitle1);
        tvHeaderTitle2.setText(headerTitle2);
        tvHeaderTitle3.setText(headerTitle3);
        tvHeaderTitle4.setText(headerTitle4);
        tvHeaderTitle5.setText(headerTitle5);
        tvHeaderTitle6.setText(headerTitle6);
        tvHeaderTitle7.setText(headerTitle7);
        tvHeaderTitle8.setText(headerTitle8);
        tvHeaderTitle9.setText(headerTitle9);
        tvHeaderTitle10.setText(headerTitle10);


        applyRowMarginLeft(tvHeaderTitle1);
        applyRowMarginLeft(tvHeaderTitle2);
        applyRowMarginLeft(tvHeaderTitle3);
        applyRowMarginRight(tvHeaderTitle4);
        applyRowMarginRight(tvHeaderTitle5);
        applyRowMarginRight(tvHeaderTitle6);
        applyRowMarginRight(tvHeaderTitle7);
        applyRowMarginRight(tvHeaderTitle8);
        applyRowMarginRight(tvHeaderTitle9);
        applyRowMarginRight(tvHeaderTitle10);

        row.addView(tvHeaderTitle1);
        row.addView(tvHeaderTitle2);
        row.addView(tvHeaderTitle3);
        row.addView(tvHeaderTitle4);
        row.addView(tvHeaderTitle5);
        row.addView(tvHeaderTitle6);
        row.addView(tvHeaderTitle7);
        row.addView(tvHeaderTitle8);
        row.addView(tvHeaderTitle9);
        row.addView(tvHeaderTitle10);

        tableLayout.addView(row);
    }



    private void createDomesticLayout(String headerTitle1,String headerTitle2,  String headerTitle3, String headerTitle4, String headerTitle5, String headerTitle6, String headerTitle7, String headerTitle8, String headerTitle9, String headerTitle10){

        TableRow row= new TableRow(this);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        lp.setMargins(20,20,20,20);
        row.setBackground(getResources().getDrawable(R.drawable.circle_background));
        row.setLayoutParams(lp);

        TextView tvHeaderTitle1 = new TextView(this);
        TextView tvHeaderTitle2 = new TextView(this);
        TextView tvHeaderTitle3 = new TextView(this);
        TextView tvHeaderTitle4 = new TextView(this);
        TextView tvHeaderTitle5 = new TextView(this);
        TextView tvHeaderTitle6 = new TextView(this);
        TextView tvHeaderTitle7 = new TextView(this);
        TextView tvHeaderTitle8 = new TextView(this);
        TextView tvHeaderTitle9 = new TextView(this);
        TextView tvHeaderTitle10 = new TextView(this);


        tvHeaderTitle1.setText(headerTitle1);
        tvHeaderTitle2.setText(headerTitle2);
        tvHeaderTitle3.setText(headerTitle3);
        tvHeaderTitle4.setText(headerTitle4);
        tvHeaderTitle5.setText(headerTitle5);
        tvHeaderTitle6.setText(headerTitle6);
        tvHeaderTitle7.setText(headerTitle7);
        tvHeaderTitle8.setText(headerTitle8);
        tvHeaderTitle9.setText(headerTitle9);
        tvHeaderTitle10.setText(headerTitle10);


        applyRowMarginLeft(tvHeaderTitle1);
        applyRowMarginLeft(tvHeaderTitle2);
        applyRowMarginRight(tvHeaderTitle3);
        applyRowMarginRight(tvHeaderTitle4);
        applyRowMarginRight(tvHeaderTitle5);
        applyRowMarginRight(tvHeaderTitle6);
        applyRowMarginRight(tvHeaderTitle7);
        applyRowMarginRight(tvHeaderTitle8);
        applyRowMarginRight(tvHeaderTitle9);
        applyRowMarginRight(tvHeaderTitle10);

        row.addView(tvHeaderTitle1);
        row.addView(tvHeaderTitle2);
        row.addView(tvHeaderTitle3);
        row.addView(tvHeaderTitle4);
        row.addView(tvHeaderTitle5);
        row.addView(tvHeaderTitle6);
        row.addView(tvHeaderTitle7);
        row.addView(tvHeaderTitle8);
        row.addView(tvHeaderTitle9);
        row.addView(tvHeaderTitle10);

        tableLayout.addView(row);
    }



    private void setHeaders(String headerTitle1,String headerTitle2,  String headerTitle3, String headerTitle4, String headerTitle5, String headerTitle6, String headerTitle7, String headerTitle8, String headerTitle9, String headerTitle10) {

        TableRow row= new TableRow(this);
        row.setBackground(getResources().getDrawable(R.drawable.circle_background));
        row.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

        TextView tvHeaderTitle1 = new TextView(this);
        TextView tvHeaderTitle2 = new TextView(this);
        TextView tvHeaderTitle3 = new TextView(this);
        TextView tvHeaderTitle4 = new TextView(this);
        TextView tvHeaderTitle5 = new TextView(this);
        TextView tvHeaderTitle6 = new TextView(this);
        TextView tvHeaderTitle7 = new TextView(this);
        TextView tvHeaderTitle8 = new TextView(this);
        TextView tvHeaderTitle9 = new TextView(this);
        TextView tvHeaderTitle10 = new TextView(this);

        tvHeaderTitle1.setText(headerTitle1);
        tvHeaderTitle2.setText(headerTitle2);
        tvHeaderTitle3.setText(headerTitle3);
        tvHeaderTitle4.setText(headerTitle4);
        tvHeaderTitle5.setText(headerTitle5);
        tvHeaderTitle6.setText(headerTitle6);
        tvHeaderTitle7.setText(headerTitle7);
        tvHeaderTitle8.setText(headerTitle8);
        tvHeaderTitle9.setText(headerTitle9);
        tvHeaderTitle10.setText(headerTitle10);


        applyHeaderMargin(tvHeaderTitle1);
        applyHeaderMargin(tvHeaderTitle2);
        applyHeaderMargin(tvHeaderTitle3);
        applyHeaderMargin(tvHeaderTitle4);
        applyHeaderMargin(tvHeaderTitle5);
        applyHeaderMargin(tvHeaderTitle6);
        applyHeaderMargin(tvHeaderTitle7);
        applyHeaderMargin(tvHeaderTitle8);
        applyHeaderMargin(tvHeaderTitle9);
        applyHeaderMargin(tvHeaderTitle10);

        row.addView(tvHeaderTitle1);
        row.addView(tvHeaderTitle2);
        row.addView(tvHeaderTitle3);
        row.addView(tvHeaderTitle4);
        row.addView(tvHeaderTitle5);
        row.addView(tvHeaderTitle6);
        row.addView(tvHeaderTitle7);
        row.addView(tvHeaderTitle8);
        row.addView(tvHeaderTitle9);
        row.addView(tvHeaderTitle10);

        tableLayout.addView(row);
    }
    private void showHeaderTitle(String title){

        TextView viewHeaderTitle = new TextView(this);
        viewHeaderTitle.setText(title);
        viewHeaderTitle.setTextSize(20);
        viewHeaderTitle.setGravity(Gravity.CENTER);
        viewHeaderTitle.setPadding(0, 20, 0, 20);
        viewHeaderTitle.setTextColor(getResources().getColor(R.color.colorPrimary));
        tableLayout.addView(viewHeaderTitle);
    }
    private TextView applyHeaderMargin(TextView view) {

        view.setTextColor(Color.WHITE);
        view.setTextSize(15);
        view.setPadding(35, 20, 15, 20);
        return view;
    }
    private TextView applyRowMarginLeft(TextView viewHeader) {

        viewHeader.setTextSize(17);
        viewHeader.setPadding(35, 20, 15, 20);
        viewHeader.setGravity(Gravity.LEFT);
        return viewHeader;
    }
    private TextView applyRowMarginRight(TextView viewRow) {

        viewRow.setTextSize(15);
        viewRow.setPadding(35, 20, 15, 20);
        viewRow.setGravity(Gravity.RIGHT);
        return viewRow;
    }




}


