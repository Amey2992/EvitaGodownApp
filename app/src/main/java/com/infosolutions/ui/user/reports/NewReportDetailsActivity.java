package com.infosolutions.ui.user.reports;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.infosolutions.core.BaseActivity;
import com.infosolutions.database.DomesticDeliveryDB;
import com.infosolutions.evita.R;
import com.infosolutions.network.Constants;
import com.infosolutions.network.ResponseListener;
import com.infosolutions.network.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import module.infosolutions.others.models.StockDetailModel;

public class NewReportDetailsActivity extends AppCompatActivity implements ResponseListener {

    ExpandableListView reportsListView;
    ExpandableListAdapter adapter;
    List<String> listDataHeader;
    ProgressBar progressBar;
    HashMap<String, List<String>> listDataChild;
    private String headerTitle;
    private String requestType;
    android.support.v7.widget.SearchView searchView;

    public String getGODOWN_ID() {
        return GODOWN_ID;
    }

    public void setGODOWN_ID(String GODOWN_ID) {
        this.GODOWN_ID = GODOWN_ID;
    }

    private String GODOWN_ID;
    List<com.infosolutions.model.StockDetailModel> listStockDetailModel = new ArrayList<>();
    HashMap<String, List<com.infosolutions.model.StockDetailModel>> stockHash = new HashMap<String, List<com.infosolutions.model.StockDetailModel>>();

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

        searchView = (android.support.v7.widget.SearchView) findViewById(R.id.search);
        searchView.setVisibility(View.GONE);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_backspace);

        setGODOWN_ID(Constants.getSharedPrefWithKEY(getApplicationContext(), Constants.KEY_GODOWN));

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

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    private void loadReport() {

        if (Constants.isNetworkAvailable(getApplicationContext())) {
            progressBar.setVisibility(View.VISIBLE);

            if (headerTitle.equalsIgnoreCase("Domestic")) {

                VolleySingleton.getInstance(getApplicationContext()).
                        apiGetReportList(VolleySingleton.CallType.REPORT_DOMESTIC,
                                Constants.GET_ALL_REPORT, getRequestType(), getGODOWN_ID());

            } else if (headerTitle.equalsIgnoreCase("Commercial")) {

                VolleySingleton.getInstance(getApplicationContext()).
                        apiGetReportList(VolleySingleton.CallType.REPORT_COMMERCIAL,
                                Constants.GET_ALL_REPORT, getRequestType(), getGODOWN_ID());

            } else if (headerTitle.equalsIgnoreCase("Truck Received")) {

                VolleySingleton.getInstance(getApplicationContext()).
                        apiGetReportList(VolleySingleton.CallType.REPORT_TRUCK_RECEIVED,
                                Constants.GET_ALL_REPORT, getRequestType(), getGODOWN_ID());

            } else if (headerTitle.equalsIgnoreCase("Truck Send")) {

                VolleySingleton.getInstance(getApplicationContext()).
                        apiGetReportList(VolleySingleton.CallType.REPORT_TRUCK_SEND,
                                Constants.GET_ALL_REPORT, getRequestType(), getGODOWN_ID());

            } else if (headerTitle.equalsIgnoreCase("TV Details")) {

                VolleySingleton.getInstance(getApplicationContext()).
                        apiGetReportList(VolleySingleton.CallType.REPORT_TV_DETAILS,
                                Constants.GET_ALL_REPORT, getRequestType(), getGODOWN_ID());
            }
        } else {
            Toast.makeText(getApplicationContext(), R.string.no_network_available, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSuccess(VolleySingleton.CallType type, String response) {
        progressBar.setVisibility(View.GONE);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(response);
            String respMessage = jsonObject.getString("responseMessage");
            String respCode = jsonObject.optString("ResponseCode");
            if (respCode.equalsIgnoreCase("200")) {
                if (type.equals(VolleySingleton.CallType.REPORT_DOMESTIC) || type.equals(VolleySingleton.CallType.REPORT_COMMERCIAL)) {
                    reportWiseData(jsonObject,type);
                } else if (type.equals(VolleySingleton.CallType.REPORT_TRUCK_RECEIVED) || type.equals(VolleySingleton.CallType.REPORT_TRUCK_SEND)) {
                    reportWiseData(jsonObject,type);
                } else if (type.equals(VolleySingleton.CallType.REPORT_TV_DETAILS)) {
                    reportWiseData(jsonObject,type);                }
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

    private void reportWiseData(JSONObject jsonObject, VolleySingleton.CallType type){
        JSONArray jsonArray = jsonObject.optJSONArray("reportResult");
        List<com.infosolutions.model.StockDetailModel> listStockDetailModel = new ArrayList<>();
        if (jsonArray != null && jsonArray.length() > 0) {
            for (int i = 0; i < jsonArray.length(); i++) {
                com.infosolutions.model.StockDetailModel stockDetailModel = new com.infosolutions.model.StockDetailModel(jsonArray.optJSONObject(i), type);

                listStockDetailModel.add(stockDetailModel);
                if(type.equals(VolleySingleton.CallType.REPORT_DOMESTIC) || type.equals(VolleySingleton.CallType.REPORT_COMMERCIAL)) {
                    stockHash.put(stockDetailModel.getName(), listStockDetailModel);
                }else if(type.equals(VolleySingleton.CallType.REPORT_TRUCK_SEND) || type.equals(VolleySingleton.CallType.REPORT_TRUCK_RECEIVED)){
                    stockHash.put(stockDetailModel.vehicle_no, listStockDetailModel);
                }else if(type.equals(VolleySingleton.CallType.REPORT_TV_DETAILS) ){
                    stockHash.put(stockDetailModel.consumer_no, listStockDetailModel);
                }
            }
            listDataHeader = new ArrayList<>();
            listDataHeader.addAll(stockHash.keySet());
            loadListView(type.toString(),listStockDetailModel);
        } else {
            Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFailure(VolleySingleton.CallType type, VolleyError error) {
        progressBar.setVisibility(View.GONE);
    }

    public int GetPixelFromDips(float pixels) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }

    private void loadListView(String type,List<com.infosolutions.model.StockDetailModel> stocksmodel) {
        adapter = new ExpandableListAdapter(this, listDataHeader, stockHash, type,stocksmodel);
        reportsListView.setAdapter(adapter);
    }


    public class ExpandableListAdapter extends BaseExpandableListAdapter {

        private Context context;
        private List<String> headerTitle;
        private HashMap<String, List<com.infosolutions.model.StockDetailModel>> childData;
        String type;
        private TextView empty_textview_value;
        private TextView empty_textview;
        private TextView full_cylinder_textview_value;
        private TextView full_cylinder_textview;
        private TextView return_full_textview_value;
        private TextView return_full_textview;
        private TextView lost_textview_value;
        private TextView lost_textview;
        private TextView tv_description_textview_value;
        private TextView tv_description_textview;
        private TextView defective_textview_value;
        private TextView defective_textview;
        private TextView dbc_textview_value;
        private TextView dbc_textview;
        private TextView name_textview;
        private TextView sv_textview_value;
        private TextView sv_textview;
        private List<com.infosolutions.model.StockDetailModel> stocksmodel;

        private ExpandableListAdapter(Context context, List<String> listDataHeader, HashMap<String, List<com.infosolutions.model.StockDetailModel>> listChildData, String type,List<com.infosolutions.model.StockDetailModel> stocksmodel ) {
            this.context = context;
            this.headerTitle = listDataHeader;
            this.childData = listChildData;
            this.type = type;
            this.stocksmodel = stocksmodel;
        }


        @Override
        public int getGroupCount() {
            //return this.headerTitle.size();
           return stocksmodel.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            //return this.childData.get(this.headerTitle.get(groupPosition)).size();
            return 1;
        }

        @Override
        public Object getGroup(int groupPosition) {
            if(type.equalsIgnoreCase(VolleySingleton.CallType.REPORT_DOMESTIC.toString()) || type.equalsIgnoreCase(VolleySingleton.CallType.REPORT_COMMERCIAL.toString())) {
                return this.stocksmodel.get(groupPosition).getName();
            }else if(type.equalsIgnoreCase(VolleySingleton.CallType.REPORT_TRUCK_SEND.toString()) || type.equalsIgnoreCase(VolleySingleton.CallType.REPORT_TRUCK_RECEIVED.toString())){
                return this.stocksmodel.get(groupPosition).vehicle_no;
            }else if(type.equalsIgnoreCase(VolleySingleton.CallType.REPORT_TV_DETAILS.toString())){
                return this.stocksmodel.get(groupPosition).consumer_no;
            }
            return "";
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
           // return this.childData.get(this.headerTitle.get(groupPosition)).get(childPosition);
            return this.stocksmodel.get(groupPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            String headerTitle = (String) getGroup(groupPosition);
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.reports_row_layout, null);
            }

            TextView textView = convertView.findViewById(R.id.textview_parent);
            textView.setText(headerTitle);
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            final com.infosolutions.model.StockDetailModel childModel = (com.infosolutions.model.StockDetailModel) getChild(groupPosition, childPosition);
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.reports_row_child, null);

            }


            LinearLayout domesticcommercialcontainer = convertView.findViewById(R.id.domesticcommercialcontainer);
            LinearLayout trucksendcontainer = convertView.findViewById(R.id.trucksendcontainer);
            LinearLayout tvcontainer = convertView.findViewById(R.id.tvcontainer);

            if (this.type.equalsIgnoreCase(VolleySingleton.CallType.REPORT_DOMESTIC.toString()) || this.type.equalsIgnoreCase(VolleySingleton.CallType.REPORT_COMMERCIAL.toString())) {
                domesticcommercialcontainer.setVisibility(View.VISIBLE);
                trucksendcontainer.setVisibility(View.GONE);
                tvcontainer.setVisibility(View.GONE);

                sv_textview = convertView.findViewById(R.id.sv_textview);
                sv_textview_value = convertView.findViewById(R.id.sv_textview_value);

                name_textview = convertView.findViewById(R.id.name_textview);

                dbc_textview = convertView.findViewById(R.id.dbc_textview);
                dbc_textview_value = convertView.findViewById(R.id.dbc_textview_value);

                defective_textview = convertView.findViewById(R.id.defective_textview);
                defective_textview_value = convertView.findViewById(R.id.defective_textview_value);

                tv_description_textview = convertView.findViewById(R.id.tv_description_textview);
                tv_description_textview_value = convertView.findViewById(R.id.tv_description_textview_value);

                lost_textview = convertView.findViewById(R.id.lost_textview);
                lost_textview_value = convertView.findViewById(R.id.lost_textview_value);

                return_full_textview = convertView.findViewById(R.id.return_full_textview);
                return_full_textview_value = convertView.findViewById(R.id.return_full_textview_value);

                full_cylinder_textview = convertView.findViewById(R.id.full_cylinder_textview);
                full_cylinder_textview_value = convertView.findViewById(R.id.full_cylinder_textview_value);

                empty_textview = convertView.findViewById(R.id.empty_textview);
                empty_textview_value = convertView.findViewById(R.id.empty_textview_value);

                sv_textview.setText("SV : ");
                sv_textview_value.setText(childModel.sv);

                name_textview.setText("NAME : " + childModel.name);

                dbc_textview.setText("DBC : ");
                dbc_textview_value.setText(childModel.dbc);

                defective_textview.setText("DEFECTIVE : ");
                defective_textview_value.setText(childModel.defective);

                tv_description_textview.setText("PRODUCT : ");
                tv_description_textview_value.setText(childModel.description);

                lost_textview.setText("LOST : ");
                lost_textview_value.setText(childModel.lost);

                return_full_textview.setText("RETURN FULL : ");
                return_full_textview_value.setText(childModel.return_full);

                full_cylinder_textview.setText("FULL CYLLINDER : ");
                full_cylinder_textview_value.setText(childModel.full_cylinder);

                empty_textview.setText("EMPTY : ");
                empty_textview_value.setText(childModel.empty);

            }
            else if(this.type.equalsIgnoreCase(VolleySingleton.CallType.REPORT_TRUCK_SEND.toString()) || this.type.equalsIgnoreCase(VolleySingleton.CallType.REPORT_TRUCK_RECEIVED.toString())) {
                trucksendcontainer.setVisibility(View.VISIBLE);

                TextView vehicle_no_textview = convertView.findViewById(R.id.vehicle_no_textview);
                TextView vehicle_no_textview_value = convertView.findViewById(R.id.vehicle_no_textview_value);

                TextView invoice_no_textview = convertView.findViewById(R.id.invoice_no_textview);
                TextView invoice_no_textview_value = convertView.findViewById(R.id.invoice_no_textview_value);

                TextView description_received_textview = convertView.findViewById(R.id.description_received_textview);
                TextView description_received_textview_value = convertView.findViewById(R.id.description_received_textview_value);

                TextView quantity_textview = convertView.findViewById(R.id.quantity_textview);
                TextView quantity_textview_value = convertView.findViewById(R.id.quantity_textview_value);

                TextView defective_received_textview = convertView.findViewById(R.id.defective_received_textview);
                TextView defective_received_textview_value = convertView.findViewById(R.id.defective_received_textview_value);

                TextView purchase_defective_textview = convertView.findViewById(R.id.purchase_defective_textview);
                TextView purchase_defective_textview_value = convertView.findViewById(R.id.purchase_defective_textview_value);

                RelativeLayout purchase_defective_container = (RelativeLayout) convertView.findViewById(R.id.purchase_defective_container);

                if (type.equalsIgnoreCase(VolleySingleton.CallType.REPORT_TRUCK_RECEIVED.toString())) {
                    vehicle_no_textview.setText("TRUCK NUMBER: ");
                    vehicle_no_textview_value.setText(childModel.vehicle_no);

                    invoice_no_textview.setText("INVOICE NO: ");
                    invoice_no_textview_value.setText(childModel.invoice_no);

                    description_received_textview.setText("PRODUCT TYPE: ");
                    description_received_textview_value.setText(childModel.description);

                    defective_received_textview.setText("LOST: ");
                    defective_received_textview_value.setText(childModel.lost);

                    quantity_textview.setText("QUANTITY: ");
                    quantity_textview_value.setText(childModel.quantity);

                    purchase_defective_container.setVisibility(View.VISIBLE);
                    purchase_defective_textview.setText("DEFECTIVE: ");
                    purchase_defective_textview_value.setText(childModel.defective);

                } else if (type.equalsIgnoreCase(VolleySingleton.CallType.REPORT_TRUCK_SEND.toString())) {
                    vehicle_no_textview.setText("TRUCK NUMBER: ");
                    vehicle_no_textview_value.setText(childModel.vehicle_no);

                    invoice_no_textview.setText("ERV NUMBER: ");
                    invoice_no_textview_value.setText(childModel.erv_no);

                    description_received_textview.setText("PRODUCT TYPE: ");
                    description_received_textview_value.setText(childModel.description);

                    defective_received_textview.setText("DEFECTIVE: ");
                    defective_received_textview_value.setText(childModel.defective);

                    quantity_textview.setText("QUANTITY: ");
                    quantity_textview_value.setText(childModel.quantity);

                    purchase_defective_container.setVisibility(View.GONE);
                }

                domesticcommercialcontainer.setVisibility(View.GONE);
                tvcontainer.setVisibility(View.GONE);
            }else if(this.type.equalsIgnoreCase(VolleySingleton.CallType.REPORT_TV_DETAILS.toString())){
                tvcontainer.setVisibility(View.VISIBLE);

                //TV Report
                TextView description_tv_textview = convertView.findViewById(R.id.description_tv_textview);
                TextView description_tv_textview_value = convertView.findViewById(R.id.description_tv_textview_value);

                TextView consumer_no_textview = convertView.findViewById(R.id.consumer_no_textview);
                TextView consumer_no_textview_value = convertView.findViewById(R.id.consumer_no_textview_value);

                TextView quantity_tv_textview = convertView.findViewById(R.id.quantity_tv_textview);
                TextView quantity_tv_textview_value = convertView.findViewById(R.id.quantity_tv_textview_value);

                description_tv_textview.setText("PRODUCT TYPE: ");
                description_tv_textview_value.setText(childModel.description);

                consumer_no_textview.setText("CONSUMER NUMBER: ");
                consumer_no_textview_value.setText(childModel.consumer_no);

                quantity_tv_textview.setText("QUANTITY: ");
                quantity_tv_textview_value.setText(childModel.quantity);

                trucksendcontainer.setVisibility(View.GONE);
                domesticcommercialcontainer.setVisibility(View.GONE);

            }

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
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
