package com.infosolutions.ui.owner;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.infosolutions.evita.R;
import com.infosolutions.network.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OwnerDetailingActivity extends AppCompatActivity {

    private static final String TAG =      OwnerDetailingActivity.class.getSimpleName();
    private String                         GODOWN_NAME = "";
    private TableLayout                    tableLayout;


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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_scrolling_owner);

        Intent intent     =  getIntent();
        String response   =  intent.getStringExtra("response");
        String title      =  intent.getStringExtra("layout_type");
        Toolbar toolbar   = findViewById(R.id.toolbar);

        TextView mTitle   = toolbar.findViewById(R.id.toolbar_title);
        tableLayout       = findViewById(R.id.table_layout);

        mTitle.setText(title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (!response.equalsIgnoreCase("")){
            try{
                createStockDetailing(response);
            }catch (JSONException e){
                e.printStackTrace();
            }
        }

    }





    private void createStockDetailing(String resp) throws JSONException {

        JSONObject objectDetailing = new JSONObject(resp);

        if (objectDetailing.has("OPENING_STOCKS_GODOWN_WISE") && objectDetailing.optJSONArray("OPENING_STOCKS_GODOWN_WISE").length() > 0){

            JSONArray openingArray = objectDetailing.optJSONArray("OPENING_STOCKS_GODOWN_WISE");
            int arrayLength = openingArray.length();
            if (arrayLength>0){

                for (int i=0; i<arrayLength; i++)
                {

                    if (openingArray.optJSONObject(i).has("valueArray") && openingArray.optJSONObject(i).has("DISPLAY_NAME")){
                        JSONArray openArray = openingArray.optJSONObject(i).optJSONArray("valueArray");
                        String godownTitle = openingArray.optJSONObject(i).optString("DISPLAY_NAME");
                        createOpeningLayout(godownTitle, openArray);
                    }else {
                        Toast.makeText(this, "No result found", Toast.LENGTH_SHORT).show();
                    }

                }
            }else {showError();}


        }else if (objectDetailing.has("OTHER_STOCKS_GODOWN_WISE") && objectDetailing.optJSONArray("OTHER_STOCKS_GODOWN_WISE").length() > 0){
            JSONArray arrayDetailing = objectDetailing.optJSONArray("OTHER_STOCKS_GODOWN_WISE");
            int arrayLength = arrayDetailing.length();
            if (arrayLength>0){
                for (int i=0; i<arrayLength; i++)
                {

                    if (arrayDetailing.optJSONObject(i).has("valueArray") && arrayDetailing.optJSONObject(i).has("DISPLAY_NAME")){
                        JSONArray otherArray = arrayDetailing.optJSONObject(i).optJSONArray("valueArray");
                        String godownTitle = arrayDetailing.optJSONObject(i).optString("DISPLAY_NAME");
                        createOtherLayout(godownTitle, otherArray);
                    }else {
                        Toast.makeText(this, "No result found", Toast.LENGTH_SHORT).show();
                    }

                }
            }else {showError();}

        }else if (objectDetailing.has("RECEVING_STOCKS_GODOWN_WISE") && objectDetailing.optJSONArray("RECEVING_STOCKS_GODOWN_WISE").length() > 0){
            JSONArray arrayReceive = objectDetailing.optJSONArray("RECEVING_STOCKS_GODOWN_WISE");
            int arraylength = arrayReceive.length();

            if (arraylength>0){
                for (int i=0; i<arraylength; i++)
                {

                    if (arrayReceive.optJSONObject(i).has("valueArray") && arrayReceive.optJSONObject(i).has("DISPLAY_NAME")){
                        JSONArray commercialArray = arrayReceive.optJSONObject(i).optJSONArray("valueArray");
                        String godownTitle = arrayReceive.optJSONObject(i).optString("DISPLAY_NAME");
                        createReceivingLayout(godownTitle, commercialArray);
                    }else {
                        Toast.makeText(this, "No result found", Toast.LENGTH_SHORT).show();
                    }

                }
            }else {showError();}

        }else if (objectDetailing.has("DOMESTIC_DELIVERY_STOCKS_GODOWN_WISE") && objectDetailing != null && objectDetailing.optJSONArray("DOMESTIC_DELIVERY_STOCKS_GODOWN_WISE").length() > 0){
            JSONArray arrayDomestic = objectDetailing.optJSONArray("DOMESTIC_DELIVERY_STOCKS_GODOWN_WISE");
            int arrayLength =arrayDomestic.length();
            if (arrayLength>0){
                for (int i=0; i<arrayLength; i++)
                {
                    if (arrayDomestic.optJSONObject(i).has("valueArray") && arrayDomestic.optJSONObject(i).has("DISPLAY_NAME")){
                        JSONArray domesticArray = arrayDomestic.optJSONObject(i).optJSONArray("valueArray");
                        String godownTitle = arrayDomestic.optJSONObject(i).optString("DISPLAY_NAME");
                        createDomesticLayout(godownTitle, domesticArray);
                    }else {
                        Toast.makeText(this, "No result found", Toast.LENGTH_SHORT).show();
                    }

                }
            }else {showError();}

        }else if (objectDetailing.has("COMMERCIAL_DELIVERY_STOCKS_GODOWN_WISE") && objectDetailing.optJSONArray("COMMERCIAL_DELIVERY_STOCKS_GODOWN_WISE").length() > 0){
            JSONArray arrayCommercial = objectDetailing.optJSONArray("COMMERCIAL_DELIVERY_STOCKS_GODOWN_WISE");
            int arrayLength = arrayCommercial.length();

            if (arrayLength>0){
                for (int i=0; i<arrayLength; i++)
                {

                    if (arrayCommercial.optJSONObject(i).has("valueArray") && arrayCommercial.optJSONObject(i).has("DISPLAY_NAME")){
                        JSONArray commercialArray = arrayCommercial.optJSONObject(i).optJSONArray("valueArray");
                        String godownTitle = arrayCommercial.optJSONObject(i).optString("DISPLAY_NAME");
                        createCommercialLayout(godownTitle, commercialArray);
                    }else {
                        Toast.makeText(this, "No result found", Toast.LENGTH_SHORT).show();
                    }

                }
            }else {showError();}

        }else if (objectDetailing.has("SENDING_STOCKS_GODOWN_WISE") && objectDetailing.optJSONArray("SENDING_STOCKS_GODOWN_WISE").length() > 0){
            JSONArray arraySending = objectDetailing.optJSONArray("SENDING_STOCKS_GODOWN_WISE");
            int arrayLength = arraySending.length();

            if (arrayLength>0){
                for (int i=0; i<arrayLength; i++)
                {
                    if (arraySending.optJSONObject(i).has("valueArray") && arraySending.optJSONObject(i).has("DISPLAY_NAME")){
                        JSONArray openingArray = arraySending.optJSONObject(i).optJSONArray("valueArray");
                        String godownTitle = arraySending.optJSONObject(i).optString("DISPLAY_NAME");
                        createSendLayout(godownTitle, openingArray);
                    }else {
                        Toast.makeText(this, "No result found", Toast.LENGTH_SHORT).show();
                    }

                }
            }else {showError();}

        }else if (objectDetailing.has("CLOSING_STOCKS_GODOWN_WISE") && objectDetailing.optJSONArray("CLOSING_STOCKS_GODOWN_WISE").length() > 0){
            JSONArray arrayClosing = objectDetailing.optJSONArray("CLOSING_STOCKS_GODOWN_WISE");
            int arrayLength = arrayClosing.length();

            if (arrayLength>0){
                for (int i=0; i<arrayLength; i++)
                {
                    if (arrayClosing.optJSONObject(i).has("valueArray") && arrayClosing.optJSONObject(i).has("DISPLAY_NAME")){
                        JSONArray openingArray = arrayClosing.optJSONObject(i).optJSONArray("valueArray");
                        String godownTitle = arrayClosing.optJSONObject(i).optString("DISPLAY_NAME");
                        createClosingLayout(godownTitle, openingArray);
                    }else {
                        Toast.makeText(this, "No result found", Toast.LENGTH_SHORT).show();
                    }

                }
            }else {showError();}

        }else if (objectDetailing.has("TV_STOCKS_GODOWN_WISE") && objectDetailing.optJSONArray("TV_STOCKS_GODOWN_WISE").length() > 0){
            JSONArray jsonArrayTV = objectDetailing.optJSONArray("TV_STOCKS_GODOWN_WISE");
            int arrayLength = jsonArrayTV.length();

            if (arrayLength>0){
                for (int i=0; i<arrayLength; i++)
                {

                    if (jsonArrayTV.optJSONObject(i).has("valueArray") && jsonArrayTV.optJSONObject(i).has("DISPLAY_NAME")){
                        JSONArray tvArray = jsonArrayTV.optJSONObject(i).optJSONArray("valueArray");
                        String godownTitle = jsonArrayTV.optJSONObject(i).optString("DISPLAY_NAME");
                        createTVLayout(godownTitle, tvArray);
                    }else {
                        Toast.makeText(this, "No result found", Toast.LENGTH_SHORT).show();
                    }

                }
            }else {showError();}

        }

    }



    private void createOpeningLayout(String godown_name, JSONArray arrayOpening) throws JSONException{

        if (arrayOpening.length()>0){
            showDivider(godown_name);
            setHeaders("PRODUCT", "FULL","EMPTY",  "DEFECTIVE","", "","","");

            int openLength = arrayOpening.length();
            if (openLength>0){

                for (int i=0; i<arrayOpening.length(); i++){

                    JSONObject objectItem = arrayOpening.optJSONObject(i);
                    String DESCRIPTION = objectItem.optString("DESCRIPTION");
                    String DEFECTIVE = objectItem.optString("DEFECTIVE");
                    String FULL = objectItem.optString("OPENING_FULL");
                    String EMPTY = objectItem.optString("OPENING_EMPTY");

                    createLayout(DESCRIPTION, FULL, EMPTY, DEFECTIVE,"", "","","");
                }
            }else { showError();}

        }
    }

    private void showError() {

        TextView tvError = findViewById(R.id.tvError);
        tvError.setText(getResources().getString(R.string.no_record_found));
        tvError.setVisibility(View.VISIBLE);
    }


    private void createSendLayout(String godown_name,JSONArray arraySend) throws JSONException{

        if (arraySend.length()>0){

            showDivider(godown_name);
            setHeaders("PRODUCT",  "SOUND", "DEFECTIVE","",  "","", "","");
            for (int i=0; i<arraySend.length(); i++){
                JSONObject objectItem = arraySend.optJSONObject(i);
                String PRODUCT=objectItem.optString("DESCRIPTION");
                String SOUND=objectItem.optString("SOUND");
                String DEFECTIVE=objectItem.optString("TRUCK_DEFECTIVE");

                createLayout(PRODUCT, SOUND, DEFECTIVE, "", "", "", "","");
            }
        }else { showError();}
    }


    private void createClosingLayout(String godown_name,JSONArray arrayClosing) throws JSONException{


        if (arrayClosing.length()>0){
            showDivider(godown_name);
            setHeaders("PRODUCT", "FULL","EMPTY",  "DEFECTIVE", "","","","");
            for (int i=0; i<arrayClosing.length(); i++){

                JSONObject objectItem = arrayClosing.optJSONObject(i);

                String PRODUCT=objectItem.optString("DESCRIPTION");
                String FULL = objectItem.optString("CLOSING_FULL");
                String EMPTY=objectItem.optString("CLOSING_EMPTY");
                String DEFECTIVE=objectItem.optString("DEFECTIVE");

                createLayout(PRODUCT, FULL, EMPTY,  DEFECTIVE, "","","","");
            }
        }else { showError();}
    }


    private void createTVLayout(String godown_name,JSONArray arrayTV) throws JSONException{

        if (arrayTV.length()>0){
            showDivider(godown_name);
            setHeaders("PRODUCT", "NO. OF. CYLINDER", "", "","",  "", "","");
            for (int i=0; i<arrayTV.length(); i++){
                JSONObject objectItem = arrayTV.optJSONObject(i);
                String PRODUCT=objectItem.optString("DESCRIPTION");
                String NO_OF_CYLINDER =objectItem.optString("CYLINDER");

                createLayout(PRODUCT, NO_OF_CYLINDER, "", "", "",  "", "","");
            }
        }else { showError();}
    }



    private void createDomesticLayout(String godown_name,JSONArray arrayDelivery) throws JSONException {

        if (arrayDelivery.length()>0){
            showDivider(godown_name);
            setHeaders("PRODUCT", "DELIVERY", "SV", "DBC","DEFECTIVE",  "LOST", "","");
            for (int i=0; i<arrayDelivery.length(); i++){
                JSONObject objectItem = arrayDelivery.optJSONObject(i);

                String PRODUCT            = objectItem.optString("DESCRIPTION");
                String DELIVERY           = objectItem.optString("DELIVERY");
                String SV_CYLINDER        = objectItem.optString("SV");
                String DBC_CYLINDER       = objectItem.optString("DBC");
                String DEFECTIVE          = objectItem.optString("DEFECTIVE");
                String LOST               = objectItem.optString("LOST");

                createLayout(PRODUCT, DELIVERY, SV_CYLINDER, DBC_CYLINDER, DEFECTIVE, LOST, "", "");

            }
        }else { showError();}
    }



    private void createCommercialLayout(String godown_name, JSONArray arrayDelivery) throws JSONException {

        if (arrayDelivery.length()>0){
            showDivider(godown_name);
            setHeaders("PRODUCT",  "FULL", "EMPTY","SV",  "DBC", "DEFECTIVE","CREDIT", "LOST");

            for (int i=0; i<arrayDelivery.length(); i++){
                JSONObject objectItem = arrayDelivery.optJSONObject(i);

                String PRODUCT=objectItem.optString("DESCRIPTION");
                String FULL = objectItem.optString("DELIVERY_FULL");
                String EMPTY = objectItem.optString("DELIVERY_EMPTY");
                String SV=objectItem.optString("SV");
                String DBC=objectItem.optString("DBC");
                String DEFECTIVE = objectItem.optString("DEFECTIVE");
                String CREDIT=objectItem.optString("CREDIT");
                String LOST = objectItem.optString("LOST");

                createLayout(PRODUCT, FULL, EMPTY, SV, DBC, DEFECTIVE, CREDIT, LOST);
            }
        }else { showError();}
    }


    private void createReceivingLayout(String godown_name, JSONArray arrayReceiving) throws JSONException{

        if (arrayReceiving.length()>0){
            showDivider(godown_name);
            setHeaders("PRODUCT", "SOUND", "LOST","", "", "", "","");
            for (int i=0; i<arrayReceiving.length(); i++){
                JSONObject objectItem = arrayReceiving.optJSONObject(i);

                String PRODUCT=objectItem.optString("DESCRIPTION");
                String SOUND=objectItem.optString("SOUND");
                String LOST=objectItem.optString("LOST_TRUCK_RECEVING");

                createLayout(PRODUCT, SOUND, LOST, "","",  "", "","");
            }
        }else { showError();}
    }




    private void createOtherLayout(String godown_name, JSONArray arrayOther) throws JSONException{

        if (arrayOther.length()>0){

            showDivider(godown_name);
            setHeaders("PRODUCT", "CREDIT", "LOST", "ON FIELD","",  "", "","");
            for (int i=0; i<arrayOther.length(); i++){

                JSONObject objectItem = arrayOther.optJSONObject(i);

                String PRODUCT = objectItem.optString("DESCRIPTION");
                String CREDIT = objectItem.optString("CREDIT");
                String LOST = objectItem.optString("LOST_DELIVERY_CYLINDERS");
                String ON_FIELD = objectItem.optString("ON_FIELD");

                createLayout(PRODUCT, CREDIT, LOST, ON_FIELD, "", "","","");
            }
        }else { showError();}
    }


    private void createLayout(String headerTitle1,String headerTitle2,  String headerTitle3, String headerTitle4, String headerTitle5, String headerTitle6, String headerTitle7, String headerTitle8){


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

        tvHeaderTitle1.setText(headerTitle1);
        tvHeaderTitle2.setText(headerTitle2);
        tvHeaderTitle3.setText(headerTitle3);
        tvHeaderTitle4.setText(headerTitle4);
        tvHeaderTitle5.setText(headerTitle5);
        tvHeaderTitle6.setText(headerTitle6);
        tvHeaderTitle7.setText(headerTitle7);
        tvHeaderTitle8.setText(headerTitle8);


        applyRowMarginLeft(tvHeaderTitle1);
        applyRowMarginRight(tvHeaderTitle2);
        applyRowMarginRight(tvHeaderTitle3);
        applyRowMarginRight(tvHeaderTitle4);
        applyRowMarginRight(tvHeaderTitle5);
        applyRowMarginRight(tvHeaderTitle6);
        applyRowMarginRight(tvHeaderTitle7);
        applyRowMarginRight(tvHeaderTitle8);

        row.addView(tvHeaderTitle1);
        row.addView(tvHeaderTitle2);
        row.addView(tvHeaderTitle3);
        row.addView(tvHeaderTitle4);
        row.addView(tvHeaderTitle5);
        row.addView(tvHeaderTitle6);
        row.addView(tvHeaderTitle7);
        row.addView(tvHeaderTitle8);

        tableLayout.addView(row);
    }


    private void setHeaders(String headerTitle1,String headerTitle2,  String headerTitle3, String headerTitle4, String headerTitle5, String headerTitle6, String headerTitle7, String headerTitle8) {

        TableRow row= new TableRow(this);
        row.setBackground(getResources().getDrawable(R.drawable.rectangular_blue_background));
        row.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

        TextView tvHeaderTitle1 = new TextView(this);
        TextView tvHeaderTitle2 = new TextView(this);
        TextView tvHeaderTitle3 = new TextView(this);
        TextView tvHeaderTitle4 = new TextView(this);
        TextView tvHeaderTitle5 = new TextView(this);
        TextView tvHeaderTitle6 = new TextView(this);
        TextView tvHeaderTitle7 = new TextView(this);
        TextView tvHeaderTitle8 = new TextView(this);

        tvHeaderTitle1.setText(headerTitle1);
        tvHeaderTitle2.setText(headerTitle2);
        tvHeaderTitle3.setText(headerTitle3);
        tvHeaderTitle4.setText(headerTitle4);
        tvHeaderTitle5.setText(headerTitle5);
        tvHeaderTitle6.setText(headerTitle6);
        tvHeaderTitle7.setText(headerTitle7);
        tvHeaderTitle8.setText(headerTitle8);


        applyHeaderMargin(tvHeaderTitle1);
        applyHeaderMargin(tvHeaderTitle2);
        applyHeaderMargin(tvHeaderTitle3);
        applyHeaderMargin(tvHeaderTitle4);
        applyHeaderMargin(tvHeaderTitle5);
        applyHeaderMargin(tvHeaderTitle6);
        applyHeaderMargin(tvHeaderTitle7);
        applyHeaderMargin(tvHeaderTitle8);

        row.addView(tvHeaderTitle1);
        row.addView(tvHeaderTitle2);
        row.addView(tvHeaderTitle3);
        row.addView(tvHeaderTitle4);
        row.addView(tvHeaderTitle5);
        row.addView(tvHeaderTitle6);
        row.addView(tvHeaderTitle7);
        row.addView(tvHeaderTitle8);

        tableLayout.addView(row);
    }


    private void showDivider(String gowdown_name){

        TextView tvHeadline = new TextView(this);
        tvHeadline.setPadding(10,40,10,40);
        tvHeadline.setGravity(Gravity.CENTER);
        tvHeadline.setTextSize(16);
        tvHeadline.setText(gowdown_name);
        tvHeadline.setBackground(getResources().getDrawable(R.drawable.yellow_header));
        tableLayout.addView(tvHeadline);
    }


    private TextView applyHeaderMargin(TextView view) {

        view.setTextColor(Color.WHITE);
        view.setTextSize(15);
        view.setPadding(35, 20, 15, 20);
        return view;
    }
    private TextView applyRowMarginLeft(TextView viewHeader) {

        viewHeader.setTextSize(12);
        viewHeader.setPadding(35, 20, 15, 20);
        viewHeader.setGravity(Gravity.LEFT);
        return viewHeader;
    }
    private TextView applyRowMarginRight(TextView viewRow) {

        viewRow.setTextSize(12);
        viewRow.setPadding(35, 20, 15, 20);
        viewRow.setGravity(Gravity.RIGHT);
        return viewRow;
    }

}
