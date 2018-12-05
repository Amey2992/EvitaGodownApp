package com.infosolutions.ui.user.reports;


import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.commercialMgmt.CommercialReportDetailActivity;
import com.infosolutions.adapter.StockReportAdapter;
import com.infosolutions.evita.R;
import com.infosolutions.model.StockReportModel;
import com.infosolutions.network.Constants;
import com.infosolutions.utils.Constant;

import java.util.ArrayList;
import java.util.List;

import khangtran.preferenceshelper.PreferencesHelper;


public class ReportListItemsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<StockReportModel> listModel = new ArrayList<>();
    private StockReportAdapter listAdapter;
    private String login_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_stock_report);
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Report");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_backspace);

        initialiseUI();
        loadListView();
    }


    private void loadListView() {

        listAdapter = new StockReportAdapter(getApplicationContext(), listModel);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(listAdapter);
    }

    private void initialiseUI() {

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.addOnItemTouchListener(new ReportListItemsActivity.RecyclerTouchListener(getApplicationContext(), recyclerView, new ReportListItemsActivity.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                goToNextActivity(view, position);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        login_type = PreferencesHelper.getInstance().getStringValue(Constants.LOGIN_TYPE,"");

        if(login_type.equalsIgnoreCase(Constants.LOGIN_GODOWNKEEPER)) {
            loadData();
        }else{
            loadCommercialData();
        }
    }

    private void loadCommercialData() {
        listModel.add(new StockReportModel(Constants.StockReportTitle));
        listModel.add(new StockReportModel(Constants.ConsumerReportTitle));

    }

    private void goToNextActivity(View view, int position) {
        StockReportModel model = listModel.get(position);


            if (model.getStockReportTitle().equalsIgnoreCase("Domestic")) {

                Intent intent = new Intent(getApplicationContext(), NewReportDetailsActivity.class);
                intent.putExtra("reportName", "GET_TODAYS_DOMESTIC");
                intent.putExtra("header", "Domestic");
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

            } else if (model.getStockReportTitle().equalsIgnoreCase("Commercial")) {

                Intent intent = new Intent(getApplicationContext(), NewReportDetailsActivity.class);
                intent.putExtra("reportName", "GET_TODAYS_COMMERCIAL");
                intent.putExtra("header", "Commercial");
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

            } else if (model.getStockReportTitle().equalsIgnoreCase("Truck Received")) {

                Intent intent = new Intent(getApplicationContext(), NewReportDetailsActivity.class);
                intent.putExtra("reportName", "GET_TODAYS_RECEIVE_TRUCK");
                intent.putExtra("header", "Truck Received");
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

            } else if (model.getStockReportTitle().equalsIgnoreCase("Truck Send")) {

                Intent intent = new Intent(getApplicationContext(), NewReportDetailsActivity.class);
                intent.putExtra("reportName", "GET_TODAYS_SENDING_TRUCK");
                intent.putExtra("header", "Truck Send");
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

            } else if (model.getStockReportTitle().equalsIgnoreCase("TV Details")) {

                Intent intent = new Intent(getApplicationContext(), NewReportDetailsActivity.class);
                intent.putExtra("reportName", "GET_TODAYS_TV");
                intent.putExtra("header", "TV Details");
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

            }else if (model.getStockReportTitle().equalsIgnoreCase(Constants.StockReportTitle)) {

                Intent intent = new Intent(getApplicationContext(), CommercialReportDetailActivity.class);
                intent.putExtra("reportName", "GET_STOCK");
                intent.putExtra("header", Constants.StockReportTitle);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

            }else if (model.getStockReportTitle().equalsIgnoreCase(Constants.ConsumerReportTitle)) {

                Intent intent = new Intent(getApplicationContext(), CommercialReportDetailActivity.class);
                intent.putExtra("reportName", "GET_CONSUMER");
                intent.putExtra("header", Constants.ConsumerReportTitle);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

            } else {
                Toast.makeText(this, "Wrong Selection", Toast.LENGTH_SHORT).show();
            }
    }

    private void loadData() {

        listModel.add(new StockReportModel("Domestic"));
        listModel.add(new StockReportModel("Commercial"));
        listModel.add(new StockReportModel("Truck Received"));
        listModel.add(new StockReportModel("Truck Send"));
        listModel.add(new StockReportModel("TV Details"));
    }

    private interface ClickListener {
        void onClick(View view, int position);
        void onLongClick(View view, int position);
    }


    private class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ReportListItemsActivity.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ReportListItemsActivity.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

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

