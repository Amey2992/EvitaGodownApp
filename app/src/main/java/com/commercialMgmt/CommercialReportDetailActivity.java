package com.commercialMgmt;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.commercialMgmt.models.CommercialStockModel;
import com.infosolutions.evita.R;
import com.infosolutions.network.Constants;
import com.infosolutions.network.ResponseListener;
import com.infosolutions.network.VolleySingleton;
import com.infosolutions.ui.user.reports.NewReportDetailsActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommercialReportDetailActivity extends AppCompatActivity implements ResponseListener {


    ExpandableListView reportsListView;
    NewReportDetailsActivity.ExpandableListAdapter adapter;
    List<String> listDataHeader;
    ProgressBar progressBar;
    HashMap<String, List<String>> listDataChild;
    private String headerTitle;
    private String requestType;
    List<CommercialStockModel> listStockDetailModel = new ArrayList<>();
    HashMap<String, List<CommercialStockModel>> stockHash = new HashMap<String, List<CommercialStockModel>>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_report_details);

        Intent intent = getIntent();
        requestType = intent.getStringExtra("reportName");
        headerTitle = intent.getStringExtra("header");
        setRequestType(requestType);

        Toolbar toolbar = findViewById(R.id.toolbar);

        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        progressBar = findViewById(R.id.progressBar);
        mTitle.setText(headerTitle);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_backspace);


        VolleySingleton.getInstance(getApplicationContext())
                .addResponseListener(VolleySingleton.CallType.COMMERCIAL_REPORT_STOCK, this);

        VolleySingleton.getInstance(getApplicationContext())
                .addResponseListener(VolleySingleton.CallType.COMMERCIAL_REPORT_CONSUMER, this);


        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;

        reportsListView = findViewById(R.id.reports_listview);
        reportsListView.setIndicatorBounds(width - GetPixelFromDips(35), width - GetPixelFromDips(5));
        reportsListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            int previousGroup = -1;

            @Override
            public void onGroupExpand(int groupPosition) {
                if (groupPosition != previousGroup)
                    reportsListView.collapseGroup(previousGroup);
                previousGroup = groupPosition;
            }
        });

        loadReport();
        //prepareListData();


    }

    private void loadReport() {

        if (Constants.isNetworkAvailable(getApplicationContext())) {
            progressBar.setVisibility(View.VISIBLE);
            if (headerTitle.equalsIgnoreCase(Constants.StockReportTitle)) {

                VolleySingleton.getInstance(getApplicationContext()).
                        get_commercial_report(VolleySingleton.CallType.COMMERCIAL_REPORT_STOCK,getRequestType(),
                                Constants.COMMERCIAL_REPORTS );
            }

            else if (headerTitle.equalsIgnoreCase(Constants.ConsumerReportTitle)) {

                VolleySingleton.getInstance(getApplicationContext()).
                        get_commercial_report(VolleySingleton.CallType.COMMERCIAL_REPORT_CONSUMER, getRequestType(),
                                Constants.COMMERCIAL_REPORTS);
            }

        } else {
            Toast.makeText(getApplicationContext(), R.string.no_network_available, Toast.LENGTH_SHORT).show();
        }

    }




    public int GetPixelFromDips(float pixels) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }


    @Override
    public void onSuccess(VolleySingleton.CallType type, String response) {
        progressBar.setVisibility(View.GONE);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(response);
            String respMessage = jsonObject.getString("responseMessage");
            String respCode = jsonObject.getString("responseCode");
            if (respCode.equalsIgnoreCase("200")) {
                if (type.equals(VolleySingleton.CallType.COMMERCIAL_REPORT_STOCK)) {
                    reportWiseData(jsonObject,type);
                } else if (type.equals(VolleySingleton.CallType.COMMERCIAL_REPORT_CONSUMER)) {
                    reportWiseData(jsonObject,type);
                }
            } else {

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
            e.printStackTrace();
        }

    }

    private void reportWiseData(JSONObject jsonObject, VolleySingleton.CallType type) {
        System.out.println(jsonObject.toString());
    }

    @Override
    public void onFailure(VolleySingleton.CallType type, VolleyError error) {
        progressBar.setVisibility(View.GONE);
    }
}
