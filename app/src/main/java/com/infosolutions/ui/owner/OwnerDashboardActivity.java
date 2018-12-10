package com.infosolutions.ui.owner;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

public class OwnerDashboardActivity extends
        AppCompatActivity implements ResponseListener{

    private String DETAILING_LAYOUT   = "";
    private final String TAG          = OwnerDashboardActivity.class.getSimpleName();
    private LinearLayout              layoutOpening;
    private LinearLayout              layoutDomestic;
    private LinearLayout              layoutCommerce;
    private LinearLayout              layoutReceive;
    private LinearLayout              layoutSend;
    private LinearLayout              layoutTv;
    private LinearLayout              layoutClose;
    private LinearLayout              layoutOther;

    private TableLayout               tableOpening;
    private TableLayout               tableDomestic;
    private TableLayout               tableCommercial;
    private TableLayout               tableReceive;
    private TableLayout               tableSend;
    private TableLayout               tableTvDetails;
    private TableLayout               tableClosing;
    private TableLayout               tableOther;

    private TextView                  tagOpening;
    private TextView                  tagDomestic;
    private TextView                  tagCommercial;
    private TextView                  tagReceive;
    private TextView                  tagSend;
    private TextView                  tagTvDetails;
    private TextView                  tagClosing;
    private TextView                  tagOther;
    private ImageView                 btnLogout;
    private ProgressBar               progressBar;


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }


    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder1 = new android.support.v7.app.AlertDialog.Builder(this);
        builder1.setTitle("Logout Owner");
        builder1.setMessage("Are you sure you want to logout Owner?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Logout",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner);

        initUI();

        Intent intent = getIntent();
        String owner_resp = intent.getStringExtra("owner_resp");
        if (!owner_resp.equalsIgnoreCase("")){
            Log.e("owner_resp", owner_resp);
            parseData(owner_resp);
            showDescription();
        }else {
            Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
        }

        VolleySingleton.getInstance(getApplicationContext())
                .addResponseListener(VolleySingleton.CallType.OWNER_REPORT, this);
    }



    private void initUI(){

        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        btnLogout = toolbar.findViewById(R.id.btnLogout);
        mTitle.setText(R.string.owner);
        setSupportActionBar(toolbar);

        progressBar     = findViewById(R.id.progress_bar);
        tableOpening    = findViewById(R.id.tableOpening);
        tableDomestic   = findViewById(R.id.tableDomestic);
        tableCommercial = findViewById(R.id.tableCommercial);
        tableReceive    = findViewById(R.id.tableReceive);
        tableSend       = findViewById(R.id.tableSend);
        tableTvDetails  = findViewById(R.id.tableTvDetails);
        tableClosing    = findViewById(R.id.tableClosing);
        tableOther      = findViewById(R.id.tableOther);
        layoutOpening   = findViewById(R.id.opening_layout);
        layoutDomestic  = findViewById(R.id.layoutDomestic);
        layoutCommerce  = findViewById(R.id.layoutCommercial);
        layoutReceive   = findViewById(R.id.layoutReceive);
        layoutSend      = findViewById(R.id.layoutSend);
        layoutTv        = findViewById(R.id.layoutTv);
        layoutClose     = findViewById(R.id.layoutClose);
        layoutOther     = findViewById(R.id.layoutOther);
        tagOpening      = findViewById(R.id.tagOpening);
        tagDomestic     = findViewById(R.id.tagDomestic);
        tagCommercial   = findViewById(R.id.tagCommercial);
        tagReceive      = findViewById(R.id.tagReceive);
        tagSend         = findViewById(R.id.tagSend);
        tagTvDetails    = findViewById(R.id.tagTvDetails);
        tagClosing      = findViewById(R.id.tagClosing);
        tagOther        = findViewById(R.id.tagOther);
    }



    private void parseData(String owner_resp) {

        try {
            JSONObject objectOpening = new JSONObject(owner_resp);


            if (objectOpening.has("OPENING_STOCKS_COMBINED")){
                openingLayout(tableOpening, new JSONArray(objectOpening.optString("OPENING_STOCKS_COMBINED")));
            }

            if (objectOpening.has("DOMESTIC_DELIVERY_STOCKS_COMBINED")){
                domesticLayout(tableDomestic, new JSONArray(objectOpening.optString("DOMESTIC_DELIVERY_STOCKS_COMBINED")));
            }

            if (objectOpening.has("COMMERCIAL_DELIVERY_STOCKS_COMBINED")){
                commercialLayout(tableCommercial, new JSONArray(objectOpening.optString("COMMERCIAL_DELIVERY_STOCKS_COMBINED")));
            }

            if (objectOpening.has("RECEVING_STOCKS_COMBINED")){
                receiveLayout(tableReceive, new JSONArray(objectOpening.optString("RECEVING_STOCKS_COMBINED")));
            }else {
                TextView textViewError = findViewById(R.id.tvErrorReceive);
                textViewError.setText("No data for Receive stock");
                textViewError.setVisibility(View.VISIBLE);
            }

            if (objectOpening.has("SENDING_STOCKS_COMBINED")) {
                sendingLayout(tableSend, new JSONArray(objectOpening.optString("SENDING_STOCKS_COMBINED")));
            }

            if (objectOpening.has("TV_COMBINED")){
                tvDetailLayout(tableTvDetails, new JSONArray(objectOpening.optString("TV_COMBINED")));
            }

            if (objectOpening.has("CLOSING_STOCKS_COMBINED")){
                closingLayout(tableClosing, new JSONArray(objectOpening.optString("CLOSING_STOCKS_COMBINED")));
            }

            if (objectOpening.has("OTHER_STOCKS_COMBINED")){
                otherLayout(tableOther, new JSONArray(objectOpening.optString("OTHER_STOCKS_COMBINED")));
            }


        } catch (JSONException e) {
            AlertDialog.Builder builder1 = new android.support.v7.app.AlertDialog.Builder(this);
            builder1.setTitle("Invalid Response");
            builder1.setMessage("Hi User, We have received invalid response from the server, Please contact to server administrator.\nError Type: "+e.getMessage());
            builder1.setCancelable(true);
            AlertDialog alert11 = builder1.create();
            alert11.show();
            e.printStackTrace();
        }

    }


    private void openingLayout(TableLayout tableLayout, JSONArray arrayOpening) throws JSONException {

        if (arrayOpening.length()>0){
            setHeaders(tableLayout,"PRODUCT", "FULL", "EMPTY", "DEFECTIVE","","","");

            for (int i=0; i<arrayOpening.length(); i++){

                JSONObject openingItem = arrayOpening.optJSONObject(i);
                String DESCRIPTION = openingItem.optString("DESCRIPTION");
                String OPENING_FULL = openingItem.optString("OPENING_FULL");
                String OPENING_EMPTY = openingItem.optString("OPENING_EMPTY");
                String DEFECTIVE = openingItem.optString("DEFECTIVE");

                createLayout(tableLayout, DESCRIPTION, OPENING_FULL, OPENING_EMPTY, DEFECTIVE,"","","");
            }
        }
    }
    private void domesticLayout(TableLayout tableLayout, JSONArray arrayDomestic) throws JSONException{

        if (arrayDomestic.length()>0){
            setHeaders(tableLayout, "PRODUCT", "FULL","EMPTY", "SV","", "","" );

            for (int i=0; i<arrayDomestic.length(); i++){
                JSONObject openingItem = arrayDomestic.optJSONObject(i);

                String Description = openingItem.optString("DESCRIPTION");
                String DELIVERY_FULL = openingItem.optString("DELIVERY_FULL");
                String DELIVERY_EMPTY = openingItem.optString("DELIVERY_EMPTY");
                String SV = openingItem.optString("SV");
                /* create table view*/

                createLayout(tableLayout, Description, DELIVERY_FULL, DELIVERY_EMPTY, SV, "", "","");
            }
        }

    }
    private void commercialLayout(TableLayout tableLayout, JSONArray arrayCommercial) throws JSONException{

        if (arrayCommercial.length()>0){
            setHeaders(tableLayout,"PRODUCT", "FULL","EMPTY", "SV","", "" ,"");

            for (int i=0; i<arrayCommercial.length(); i++){
                JSONObject openingItem = arrayCommercial.optJSONObject(i);

                String Description = openingItem.optString("DESCRIPTION");
                String DELIVERY_FULL = openingItem.optString("DELIVERY_FULL");
                String DELIVERY_EMPTY = openingItem.optString("DELIVERY_EMPTY");
                String SV = openingItem.optString("SV");

                /* create table view*/

                createLayout(tableLayout, Description, DELIVERY_FULL, DELIVERY_EMPTY, SV, "","","");
            }
        }
    }
    private void receiveLayout(TableLayout tableLayout, JSONArray arrayReceive) throws JSONException {

        if (arrayReceive.length()>0){
            setHeaders(tableLayout,"PRODUCT", "SOUND", "LOST","","","","");

            for (int i=0; i<arrayReceive.length(); i++){
                JSONObject openingItem = arrayReceive.optJSONObject(i);

                String PRODUCT_NAME = openingItem.optString("DESCRIPTION");
                String SOUND = openingItem.optString("SOUND");
                String LOST_TRUCK_RECEVING = openingItem.optString("LOST_TRUCK_RECEVING");

                createLayout(tableLayout, PRODUCT_NAME, SOUND, LOST_TRUCK_RECEVING,"","","","");
            }
        }


    }
    private void sendingLayout(TableLayout tableLayout, JSONArray arraySend) throws JSONException{
        if (arraySend.length()>0){
            setHeaders(tableLayout, "PRODUCT", "SOUND","DEFECTIVE","","","","");

            for (int i=0; i<arraySend.length(); i++){
                JSONObject openingItem = arraySend.optJSONObject(i);
                String PRODUCT_NAME = openingItem.optString("DESCRIPTION");
                String SOUND = openingItem.optString("SOUND");
                String DEFECTIVE = openingItem.optString("TRUCK_DEFECTIVE");

                createLayout(tableLayout, PRODUCT_NAME, SOUND, DEFECTIVE,"","","","");
            }
        }

    }
    private void tvDetailLayout(TableLayout tableLayout, JSONArray arrayTVDetails) throws JSONException {
        if (arrayTVDetails.length()>0){
            setHeaders(tableLayout, "PRODUCT", "CYLINDER", "", "","","","");
            Log.e("TV Details", arrayTVDetails.toString());
            for (int i=0; i<arrayTVDetails.length(); i++){
                JSONObject openingItem = arrayTVDetails.optJSONObject(i);
                String PRODUCT_NAME = openingItem.optString("DESCRIPTION");
                String CYLINDER = openingItem.optString("CYLINDER");

                createLayout(tableLayout, PRODUCT_NAME,CYLINDER,"", "","","","");
            }
        }else {
            TextView textViewError = findViewById(R.id.tvError);
            textViewError.setTextColor(getResources().getColor(R.color.colorPrimary));
            textViewError.setText("No TV details found");
            textViewError.setVisibility(View.VISIBLE);
        }

    }
    private void closingLayout(TableLayout tableLayout, JSONArray arrayClosing) throws JSONException{

        if (arrayClosing.length()>0){
            setHeaders(tableLayout, "PRODUCT", "FULL", "EMPTY", "DEFECTIVE","","","");

            for (int i=0; i<arrayClosing.length(); i++){
                JSONObject openingItem = arrayClosing.optJSONObject(i);
                String PRODUCT_NAME = openingItem.optString("DESCRIPTION");

                String CLOSING_FULL = openingItem.optString("CLOSING_FULL");
                String CLOSING_EMPTY = openingItem.optString("CLOSING_EMPTY");
                String DEFECTIVE = openingItem.optString("DEFECTIVE");

                createLayout(tableLayout, PRODUCT_NAME,CLOSING_FULL,CLOSING_EMPTY, DEFECTIVE,"","","");
            }
        }
    }



    private void otherLayout(TableLayout tableLayout, JSONArray arrayOther) throws JSONException{

        if (arrayOther.length()>0){
            setHeaders(tableLayout, "PRODUCT", "CREDIT", "ON FEILD", "LOST","","","");

            for (int i=0; i<arrayOther.length(); i++){
                JSONObject openingItem = arrayOther.optJSONObject(i);
                String PRODUCT_NAME = openingItem.optString("DESCRIPTION");
                String CREDIT = openingItem.optString("CREDIT");
                String ON_FIELD = openingItem.optString("ON_FIELD");
                String LOST_DELIVERY_CYLINDERS = openingItem.optString("LOST_DELIVERY_CYLINDERS");

                createLayout(tableLayout, PRODUCT_NAME, CREDIT, ON_FIELD, LOST_DELIVERY_CYLINDERS,"","","");
            }
        }
    }





    private void createLayout(TableLayout tableLayout,String headerTitle1,String headerTitle2,  String headerTitle3, String headerTitle4, String headerTitle5, String headerTitle6, String headerTitle7){

        TableRow row= new TableRow(getApplicationContext());
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        lp.setMargins(10,10,10,10);
        row.setLayoutParams(lp);


        TextView tvHeaderTitle1 = new TextView(getApplicationContext());
        TextView tvHeaderTitle2 = new TextView(getApplicationContext());
        TextView tvHeaderTitle3 = new TextView(getApplicationContext());
        TextView tvHeaderTitle4 = new TextView(getApplicationContext());
        TextView tvHeaderTitle5 = new TextView(getApplicationContext());
        TextView tvHeaderTitle6 = new TextView(getApplicationContext());


        tvHeaderTitle1.setText(headerTitle1);
        tvHeaderTitle2.setText(headerTitle2);
        tvHeaderTitle3.setText(headerTitle3);
        tvHeaderTitle4.setText(headerTitle4);
        tvHeaderTitle5.setText(headerTitle5);
        tvHeaderTitle6.setText(headerTitle6);

        applyRowMarginLeft(tvHeaderTitle1);
        applyRowMarginRight(tvHeaderTitle2);
        applyRowMarginRight(tvHeaderTitle3);
        applyRowMarginRight(tvHeaderTitle4);
        applyRowMarginRight(tvHeaderTitle5);
        applyRowMarginRight(tvHeaderTitle6);

        row.addView(tvHeaderTitle1);
        row.addView(tvHeaderTitle2);
        row.addView(tvHeaderTitle3);
        row.addView(tvHeaderTitle4);
        row.addView(tvHeaderTitle5);
        row.addView(tvHeaderTitle6);

        tableLayout.addView(row);
    }




    private void setHeaders(TableLayout tableLayout, String headerTitle1,String headerTitle2,  String headerTitle3, String headerTitle4, String headerTitle5, String headerTitle6, String headerTitle7) {

        TableRow row= new TableRow(getApplicationContext());
        row.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.circle_background));
        row.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

        TextView tvHeaderTitle1 = new TextView(getApplicationContext());
        TextView tvHeaderTitle2 = new TextView(getApplicationContext());
        TextView tvHeaderTitle3 = new TextView(getApplicationContext());
        TextView tvHeaderTitle4 = new TextView(getApplicationContext());
        TextView tvHeaderTitle5 = new TextView(getApplicationContext());
        TextView tvHeaderTitle6 = new TextView(getApplicationContext());


        tvHeaderTitle1.setText(headerTitle1);
        tvHeaderTitle2.setText(headerTitle2);
        tvHeaderTitle3.setText(headerTitle3);
        tvHeaderTitle4.setText(headerTitle4);
        tvHeaderTitle5.setText(headerTitle5);
        tvHeaderTitle6.setText(headerTitle6);


        applyHeaderMargin(tvHeaderTitle1);
        applyHeaderMargin(tvHeaderTitle2);
        applyHeaderMargin(tvHeaderTitle3);
        applyHeaderMargin(tvHeaderTitle4);
        applyHeaderMargin(tvHeaderTitle5);
        applyHeaderMargin(tvHeaderTitle6);


        row.addView(tvHeaderTitle1);
        row.addView(tvHeaderTitle2);
        row.addView(tvHeaderTitle3);
        row.addView(tvHeaderTitle4);
        row.addView(tvHeaderTitle5);
        row.addView(tvHeaderTitle6);

        tableLayout.addView(row);
    }

    private TextView applyHeaderMargin(TextView view) {

        view.setTextColor(Color.WHITE);
        view.setTextSize(15);
        view.setPadding(20, 10, 10, 4);
        return view;
    }

    private TextView applyRowMarginLeft(TextView viewHeader) {

        viewHeader.setTextSize(15);
        viewHeader.setTextColor(getResources().getColor(R.color.colorBlack));
        viewHeader.setPadding(10, 10, 10, 10);
        viewHeader.setGravity(Gravity.LEFT);
        return viewHeader;
    }


    private TextView applyRowMarginRight(TextView viewRow) {

        viewRow.setTextSize(15);
        viewRow.setTextColor(getResources().getColor(R.color.colorBlack));
        viewRow.setPadding(10, 10, 10, 10);
        viewRow.setGravity(Gravity.RIGHT);
        return viewRow;
    }


    private void showDescription(){

        DETAILING_LAYOUT = "";

        layoutOpening.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DETAILING_LAYOUT = "OPENING";
                requestDescription("getOpening");
            }
        });
        layoutOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DETAILING_LAYOUT = "OTHER";
                requestDescription("getOther");
            }
        });
        layoutReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DETAILING_LAYOUT = "RECEIVING";
                requestDescription("getReceving");
            }
        });
        layoutClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DETAILING_LAYOUT = "CLOSING";
                requestDescription("getClosing");
            }
        });
        layoutSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DETAILING_LAYOUT = "SENDING";
                requestDescription("getSending");
            }
        });
        layoutDomestic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DETAILING_LAYOUT = "DOMESTIC";
                requestDescription("getDomesticDelivery");
            }
        });
        layoutCommerce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DETAILING_LAYOUT = "COMMERCIAL";
                requestDescription("getCommercialDelivery");
            }
        });
        layoutTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DETAILING_LAYOUT = "TV-DETAILS";
                requestDescription("getTV");
            }
        });


        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private void requestDescription(String module_type) {

        progressBar.setVisibility(View.VISIBLE);
        VolleySingleton.getInstance(getApplicationContext())
                .ownerReport(VolleySingleton.CallType.OWNER_REPORT,
                Constants.OWNER_DETAIL_REPORT, module_type);
    }

    @Override
    public void onSuccess(VolleySingleton.CallType type, String response) {
        progressBar.setVisibility(View.GONE);
        Log.e(TAG, response);

        Intent intent = new Intent(getApplicationContext(), OwnerDetailingActivity.class);
        intent.putExtra("response", response);
        intent.putExtra("layout_type", DETAILING_LAYOUT);
        startActivity(intent);

    }


    @Override
    public void onFailure(VolleySingleton.CallType type, VolleyError error) {
        Log.e(TAG, ""+error.getLocalizedMessage());
        Toast.makeText(this, R.string.server_not_responding, Toast.LENGTH_SHORT).show();
        progressBar.setVisibility(View.GONE);
    }
}
