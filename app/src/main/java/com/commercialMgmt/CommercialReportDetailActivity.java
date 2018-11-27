package com.commercialMgmt;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.commercialMgmt.models.CommercialConsumerStockReport;
import com.commercialMgmt.models.CommercialStockModel;
import com.infosolutions.evita.R;
import com.infosolutions.network.Constants;
import com.infosolutions.network.ResponseListener;
import com.infosolutions.network.VolleySingleton;
import com.infosolutions.ui.user.reports.NewReportDetailsActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.SimpleTimeZone;

public class CommercialReportDetailActivity extends AppCompatActivity implements ResponseListener {


    ExpandableListView reportsListView;
    CommercialReportDetailActivity.ExpandableListAdapter adapter;
    List<String> listDataHeader;
    ProgressBar progressBar;
    HashMap<String, List<String>> listDataChild;
    private String headerTitle;
    private String requestType;
    List<CommercialConsumerStockReport> listStockDetailModel = new ArrayList<>();
    HashMap<String, List<CommercialConsumerStockReport>> stockHash = new HashMap<String, List<CommercialConsumerStockReport>>();
    android.support.v7.widget.SearchView searchView;
    Button calendar_button;
    RelativeLayout commercialcontainer;
    String myFormat = "yyyy-MM-dd";

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

        commercialcontainer = (RelativeLayout) findViewById(R.id.commercialcontainer);
        commercialcontainer.setVisibility(View.VISIBLE);

        searchView = (android.support.v7.widget.SearchView) findViewById(R.id.search);
        searchView.onActionViewExpanded();
        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String text = newText.toLowerCase(Locale.getDefault());
                if(adapter != null && text != null) {
                    adapter.filter(text);
                }
            return false;
            }
        });

        String dateTime = Constants.getDateTime();
        final Calendar myCalendar = Calendar.getInstance();
        calendar_button = (Button) findViewById(R.id.calendar_button);
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        calendar_button.setText(Constants.getDateTime());


        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                //updateLabel();

                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                calendar_button.setText(sdf.format(myCalendar.getTime()));
                loadReport(calendar_button.getText().toString());
                searchView.setQuery("",false);


            }

        };

        calendar_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(CommercialReportDetailActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });



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

        loadReport(Constants.getDateTime());
        //prepareListData();


    }

    private void loadReport(String date) {

        if (Constants.isNetworkAvailable(getApplicationContext())) {
            progressBar.setVisibility(View.VISIBLE);
            if (headerTitle.equalsIgnoreCase(Constants.StockReportTitle)) {
                searchView.setVisibility(View.GONE);
                VolleySingleton.getInstance(getApplicationContext()).
                        get_commercial_report(VolleySingleton.CallType.COMMERCIAL_REPORT_STOCK,getRequestType(),date,
                                Constants.COMMERCIAL_REPORTS );
            }

            else if (headerTitle.equalsIgnoreCase(Constants.ConsumerReportTitle)) {
                searchView.setVisibility(View.VISIBLE);
                VolleySingleton.getInstance(getApplicationContext()).
                        get_commercial_report(VolleySingleton.CallType.COMMERCIAL_REPORT_CONSUMER, getRequestType(),date,
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
            String respCode = jsonObject.getString(Constants.responseCcode);
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
        JSONArray jsonArray = jsonObject.optJSONArray("Report");
        List<CommercialConsumerStockReport> listStockDetailModel = new ArrayList<>();
        if (jsonArray != null && jsonArray.length() > 0) {
            for (int i = 0; i < jsonArray.length(); i++) {
                CommercialConsumerStockReport stockDetailModel = new CommercialConsumerStockReport(jsonArray.optJSONObject(i), type);

                listStockDetailModel.add(stockDetailModel);
                if(type.equals(VolleySingleton.CallType.COMMERCIAL_REPORT_STOCK)) {
                    stockHash.put(stockDetailModel.ProdName, listStockDetailModel);
                }else if(type.equals(VolleySingleton.CallType.COMMERCIAL_REPORT_CONSUMER)){
                    stockHash.put(stockDetailModel.ConsumerName, listStockDetailModel);
                }
            }
            listDataHeader = new ArrayList<>();
            listDataHeader.addAll(stockHash.keySet());
            loadListView(type.toString(),listStockDetailModel);
        } else {
            Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show();

            if(adapter != null) {
                if (adapter.stocksmodel != null)
                    adapter.stocksmodel.clear();

                if (adapter.arraylist != null)
                    adapter.arraylist.clear();

                adapter.notifyDataSetChanged();
            }
        }
    }

    private void loadListView(String type,List<CommercialConsumerStockReport> stocksmodel) {
        adapter = new CommercialReportDetailActivity.ExpandableListAdapter(this, listDataHeader, stockHash, type,stocksmodel);
        reportsListView.setAdapter(adapter);
    }

    public class ExpandableListAdapter extends BaseExpandableListAdapter {

        private Context context;
        private List<String> headerTitle;
        private HashMap<String, List<CommercialConsumerStockReport>> childData;
        String type;
        private List<CommercialConsumerStockReport> stocksmodel;
        private TextView consumer_name_textview;
        private TextView consumer_name_textview_value;
        private TextView prod_name_textview;
        private TextView prod_name_textview_value;
        private TextView mrp_textview;
        private TextView mrp_textview_value;
        private TextView discount_textview;
        private TextView discount_textview_value;
        private TextView fullcyl_textview;
        private TextView fullcyl_textview_value;
        private TextView emptycyl_textview;
        private TextView emptycyl_textview_value;
        private TextView creditcyl_textview;
        private TextView creditcyl_textview_value;
        private TextView amount_textview;
        private TextView amount_textview_value;
        private TextView credit_amount_textview;
        private TextView credit_amount_textview_value;
        private TextView payment_mode_textview;
        private TextView payment_mode_textview_value;
        private TextView payment_status_textview;
        private TextView payment_status_textview_value;
        private TextView challan_textview;
        private TextView challan_textview_value;
        private TextView credit_textview;
        private TextView credit_textview_value;
        private TextView closing_textview;
        private TextView closing_textview_value;
        private TextView opening_textview;
        private TextView opening_textview_value;



        private ArrayList<CommercialConsumerStockReport> arraylist=null;


        private ExpandableListAdapter(Context context, List<String> listDataHeader, HashMap<String, List<CommercialConsumerStockReport>> listChildData, String type, List<CommercialConsumerStockReport> stocksmodel ) {
            this.context = context;
            this.headerTitle = listDataHeader;
            this.childData = listChildData;
            this.type = type;
            this.stocksmodel = stocksmodel;

            this.arraylist = new ArrayList<CommercialConsumerStockReport>();
            this.arraylist.addAll(stocksmodel);

        }

        @Override
        public int getGroupCount() {
            return stocksmodel.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return 1;
        }

        @Override
        public Object getGroup(int groupPosition) {
            if(type.equalsIgnoreCase(VolleySingleton.CallType.COMMERCIAL_REPORT_CONSUMER.toString())) {
                return this.stocksmodel.get(groupPosition).ConsumerName;
            }else if(type.equalsIgnoreCase(VolleySingleton.CallType.COMMERCIAL_REPORT_STOCK.toString())){
                return this.stocksmodel.get(groupPosition).ProdName;
            }
            return "";
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
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
            final CommercialConsumerStockReport childModel = (CommercialConsumerStockReport) getChild(groupPosition, childPosition);
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.commercial_reports_row_child, null);

            }

            LinearLayout consumerContainer = convertView.findViewById(R.id.consumerContainer);
            LinearLayout stockcontainer = convertView.findViewById(R.id.stockcontainer);


            consumer_name_textview = convertView.findViewById(R.id.consumer_name_textview);
            consumer_name_textview_value = convertView.findViewById(R.id.consumer_name_textview_value);
            consumer_name_textview_value.setText(childModel.ConsumerName);

            prod_name_textview = convertView.findViewById(R.id.prod_name_textview);
            prod_name_textview_value = convertView.findViewById(R.id.prod_name_textview_value);

            mrp_textview = convertView.findViewById(R.id.mrp_textview);
            mrp_textview_value = convertView.findViewById(R.id.mrp_textview_value);

            discount_textview = convertView.findViewById(R.id.discount_textview);
            discount_textview_value = convertView.findViewById(R.id.discount_textview_value);

            fullcyl_textview = convertView.findViewById(R.id.fullcyl_textview);
            fullcyl_textview_value = convertView.findViewById(R.id.fullcyl_textview_value);

            emptycyl_textview = convertView.findViewById(R.id.emptycyl_textview);
            emptycyl_textview_value = convertView.findViewById(R.id.emptycyl_textview_value);

            creditcyl_textview = convertView.findViewById(R.id.creditcyl_textview);
            creditcyl_textview_value = convertView.findViewById(R.id.creditcyl_textview_value);

            amount_textview = convertView.findViewById(R.id.amount_textview);
            amount_textview_value = convertView.findViewById(R.id.amount_textview_value);

            credit_amount_textview = convertView.findViewById(R.id.credit_amount_textview);
            credit_amount_textview_value = convertView.findViewById(R.id.credit_amount_textview_value);

            payment_mode_textview = convertView.findViewById(R.id.payment_mode_textview);
            payment_mode_textview_value = convertView.findViewById(R.id.payment_mode_textview_value);

            payment_status_textview = convertView.findViewById(R.id.payment_status_textview);
            payment_status_textview_value = convertView.findViewById(R.id.payment_status_textview_value);

            challan_textview = convertView.findViewById(R.id.challan_textview);
            challan_textview_value = convertView.findViewById(R.id.challan_textview_value);

            credit_textview = convertView.findViewById(R.id.credit_textview);
            credit_textview_value = convertView.findViewById(R.id.credit_textview_value);

            closing_textview = convertView.findViewById(R.id.closing_textview);
            closing_textview_value = convertView.findViewById(R.id.closing_textview_value);

            opening_textview = convertView.findViewById(R.id.opening_textview);
            opening_textview_value = convertView.findViewById(R.id.opening_textview_value);


            if (this.type.equalsIgnoreCase(VolleySingleton.CallType.COMMERCIAL_REPORT_CONSUMER.toString())) {
                consumerContainer.setVisibility(View.VISIBLE);
                stockcontainer.setVisibility(View.GONE);

                consumer_name_textview.setText("Consumer Name: ");
                consumer_name_textview_value.setText(childModel.ConsumerName);

                prod_name_textview.setText("Product Name: ");
                prod_name_textview_value.setText(childModel.ProdName);

                mrp_textview.setText("MRP: ");
                mrp_textview_value.setText(Integer.toString(childModel.MRP));

                discount_textview.setText("Discount: ");
                discount_textview_value.setText(Integer.toString(childModel.Discount));

                fullcyl_textview.setText("FullCyl: ");
                fullcyl_textview_value.setText(Integer.toString(childModel.FullCyl));

                emptycyl_textview.setText("EmptyCyl: ");
                emptycyl_textview_value.setText(Integer.toString(childModel.EmptyCyl));

                creditcyl_textview.setText("CredityCyl: ");
                creditcyl_textview_value.setText(Integer.toString(childModel.CreditCyl));

                amount_textview.setText("Amount");
                amount_textview_value.setText(Integer.toString(childModel.Amount));

                credit_amount_textview.setText("Credit Amount: ");
                credit_amount_textview_value.setText(Integer.toString(childModel.CreditAmount));

                payment_mode_textview.setText("Payment Mode: ");
                payment_mode_textview_value.setText(childModel.PaymentMode);

                payment_status_textview.setText("Payment Status: ");
                payment_status_textview_value.setText(childModel.PaymentStatus.trim());

                challan_textview.setText("Challan No: ");
                challan_textview_value.setText(childModel.ChallanNo);

            }else if (this.type.equalsIgnoreCase(VolleySingleton.CallType.COMMERCIAL_REPORT_STOCK.toString())) {

                stockcontainer.setVisibility(View.VISIBLE);
                consumerContainer.setVisibility(View.GONE);



                credit_textview.setText("Credit: ");
                credit_textview_value.setText(Integer.toString(childModel.Credit));

                closing_textview.setText("Closing: ");
                closing_textview_value.setText(Integer.toString(childModel.Closing));

                opening_textview.setText("Opening: ");
                opening_textview_value.setText(Integer.toString(childModel.Opening));


            }
            return  convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }

        // Filter Class
        /*public void filter(String charText) {
            charText = charText.toLowerCase(Locale.getDefault());
            worldpopulationlist.clear();
            if (charText.length() == 0) {
                worldpopulationlist.addAll(arraylist);
            }
            else
            {
                for (WorldPopulation wp : arraylist)
                {
                    if (wp.getCountry().toLowerCase(Locale.getDefault()).contains(charText))
                    {
                        worldpopulationlist.add(wp);
                    }
                }
            }
            notifyDataSetChanged();
        }*/
        public void filter(String charText) {
            charText = charText.toLowerCase(Locale.getDefault());
            stocksmodel.clear();
            if (charText.length() == 0) {
                stocksmodel.addAll(arraylist);
            } else {
                for (CommercialConsumerStockReport wp : arraylist) {
                    if (wp.ConsumerName.toLowerCase(Locale.getDefault()).contains(charText)) {
                        stocksmodel.add(wp);
                    }
                }
            }
            notifyDataSetChanged();

        }

    }


        @Override
    public void onFailure(VolleySingleton.CallType type, VolleyError error) {
        progressBar.setVisibility(View.GONE);
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
