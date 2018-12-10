package com.infosolutions.ui.user.tvdetails;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.VolleyError;
import com.daasuu.cat.CountAnimationTextView;
import com.infosolutions.adapter.ChipModel;
import com.infosolutions.adapter.ChipSelectionAdapter;
import com.infosolutions.core.BaseActivity;
import com.infosolutions.core.EvitaApplication;
import com.infosolutions.database.CommercialDeliveryCreditDB;
import com.infosolutions.database.ConsumerDetails;
import com.infosolutions.database.DatabaseHelper;
import com.infosolutions.database.DomesticDeliveryDB;
import com.infosolutions.database.ProductDB;
import com.infosolutions.database.TVDetailsDB;
import com.infosolutions.evita.R;
import com.infosolutions.network.Constants;
import com.infosolutions.network.ResponseListener;
import com.infosolutions.network.VolleySingleton;
import com.infosolutions.service.GetConsumerService;
import com.infosolutions.ui.login.LoginActivity;
import com.infosolutions.utils.AppSettings;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


import static com.infosolutions.network.Constants.MODULE_AVAILABLE_CYL;
import static com.infosolutions.network.Constants.PRODUCT_ID;


public class TVDetailsActivity extends BaseActivity {

    private static final String TAG = TVDetailsActivity.class.getSimpleName();
    private DatabaseHelper databaseHelper;
    private String selectedCylinderTYPE;
    private List<ChipModel> chipListModel = new ArrayList<>();
    @BindView(R.id.recycler_view_chip)
    RecyclerView recycler_view_chip;
    @BindView(R.id.viewTV)
    LinearLayout viewTV;
    @BindView(R.id.input_number_of_cylinders)
    EditText input_number_of_cylinders;
    @BindView(R.id.input_customerId)
    EditText input_customerId;

    @BindView(R.id.consumer_name)
    EditText consumer_name;
    @BindView(R.id.btnSubmit)
    AppCompatButton btnSubmit;
    @BindView(R.id.input_layout_customerId)
    TextInputLayout input_layout_customerId;
    @BindView(R.id.input_layout_noc)
    TextInputLayout input_layout_noc;
    @BindView(R.id.consumer_name_container)
    TextInputLayout consumer_name_container;
    @BindView(R.id.layout_cylinder)
    RelativeLayout layout_cylinder;
    @BindView(R.id.tv_available_cyl)
    TextView tv_available_cyl;
    @BindView(R.id.cyl_count)
    CountAnimationTextView cyl_count;
    private int TOTAL_AVAILABLE_CYL = 0;
    @BindView(R.id.progress_bar_container)
    RelativeLayout progress_bar_container;

    @BindView(R.id.cylinder_button)
    android.widget.Button cylinder_button;

    @BindView(R.id.getdata_button)
    Button getdata_button ;

    @BindView(R.id.childcontainer)
    LinearLayout childcontainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(AppSettings.getInstance(this).isServiceRunning) {
            progress_bar_container.setVisibility(View.VISIBLE);
            disableView();
        }else{
            enableView();
            progress_bar_container.setVisibility(View.GONE);
        }
        ButterKnife.bind(this);
        setupToolbar();

        setupProductType();
        submitBtnClickHandler();
        getCylinderButtonHandler();
        getDataButtonHandler();

        VolleySingleton.getInstance(getApplicationContext()).addResponseListener(VolleySingleton.CallType.CONSUMER_DETAILS, this);
        input_number_of_cylinders.setEnabled(false);
        consumer_name.setEnabled(false);
        btnSubmit.setEnabled(false);
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter(Constants.CONSUMER_BROADCAST));
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            progress_bar_container.setVisibility(View.GONE);
            enableView();
        }
    };

    private void disableView(){
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void enableView(){
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void getDataButtonHandler(){
        getdata_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AppSettings.getInstance(TVDetailsActivity.this).isServiceRunning = true;
                progress_bar_container.setVisibility(View.VISIBLE);
                disableView();
                //AppSettings.getInstance(TVDetailsActivity.this).getConsumerDetails(TVDetailsActivity.this);
                Intent intent = new Intent(TVDetailsActivity.this, GetConsumerService.class);
                startService(intent);
            }
        });
    }

    private void getCylinderButtonHandler() {

        cylinder_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(input_customerId.getText().toString())){

                    getCylinderCount(input_customerId.getText().toString());
                }else{
                    input_layout_customerId.setError("Please Provide Customer ID");
                    input_customerId.requestFocus();
                }
            }
        });
    }

    private void getCylinderCount(String consumer_id){
        RuntimeExceptionDao<ConsumerDetails,Integer> consumerExceptionDao = getHelper().getConsumerExceptionDao();

        try {
            List<ConsumerDetails> consumerDetails = consumerExceptionDao.queryBuilder().where().eq("ConsumerNo", consumer_id).and().eq("ProductId",getSelectedCylinderTYPE()).query();
            if(consumerDetails != null && consumerDetails.size() > 0){
                btnSubmit.setEnabled(true);
                input_number_of_cylinders.setText(Integer.toString(consumerDetails.get(0).NoOfCylinder));
                consumer_name.setText(consumerDetails.get(0).ConsumerName);
                input_customerId.setEnabled(false);
            }else{
                Toast.makeText(this, Constants.error_message,Toast.LENGTH_SHORT).show();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void injectDependency() { EvitaApplication.getEvitaComponents().inject(TVDetailsActivity.this);}

    @Override
    public int getLayoutId() { return R.layout.activity_tv_details; }

    private void submitBtnClickHandler() {

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String consumerNo = input_customerId.getText().toString().trim();
                String cylinderCount = input_number_of_cylinders.getText().toString().trim();
                //int CYL_COUNT_INT = Integer.parseInt(cylinderCount);

                input_layout_customerId.setErrorEnabled(false);
                input_layout_noc.setErrorEnabled(false);
                consumer_name_container.setErrorEnabled(false);

                if (input_customerId.getText() != null && input_customerId.getText().toString().trim().equalsIgnoreCase("")){
                    input_layout_customerId.setError("Please Provide Customer ID");
                    input_customerId.requestFocus();
                }else if (cylinderCount != null && cylinderCount.equalsIgnoreCase("0") || cylinderCount.equalsIgnoreCase("00")){
                    input_layout_noc.setError("Please Provide A Valid Cylinder Count");
                    input_number_of_cylinders.requestFocus();
                }else if (TextUtils.isEmpty(cylinderCount)){
                    input_layout_noc.setError("Please Provide Cylinder Count");
                    input_number_of_cylinders.requestFocus();
                }/*else if (TOTAL_AVAILABLE_CYL > 0 && CYL_COUNT_INT > TOTAL_AVAILABLE_CYL){
                    showErrorToast(TVDetailsActivity.this,"Error","Can't enter more than available CYL ");
                    input_number_of_cylinders.setError("Can't enter more than available CYL ");
                }*/else {

                    final int cylinderCountInteger = Integer.parseInt(cylinderCount);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    alertDialogBuilder.setTitle("TV Details");
                    alertDialogBuilder.setIcon(R.drawable.demo_cyl);
                    alertDialogBuilder.setMessage("Do you want to save this entry ?")
                            .setCancelable(false).setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            saveTOLocalDB(consumerNo, cylinderCountInteger);
                            finish();
                        }
                    }).setNegativeButton("No",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            dialog.cancel();
                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            }
        });

    }


    private void saveTOLocalDB(String consumerNo, int cylinderCount){

        RuntimeExceptionDao<TVDetailsDB, Integer> tvDB = getHelper().getTVDetailRTExceptionDao();
        tvDB.create(new TVDetailsDB(1, consumerNo, cylinderCount, getGoDownId(), getSelectedCylinderTYPE(), "INSERT",
                getDateTime(), "N", getDeviceId(), getApplicationUserId(),"OPEN"));

        showToast(getResources().getString(R.string.saved_success_msg));
    }


    private void setupProductType() {

        RuntimeExceptionDao<ProductDB, Integer> productDB = getHelper().getProductRTExceptionDao();
        List<ProductDB> product = productDB.queryForAll();
        for (ProductDB cn : product) {
            String CYLINDER_CODE = String.valueOf(cn.product_code);
            String CYLINDER_DESCRIPTION = String.valueOf(cn.product_description);
            String PRODUCT_CATEGORY = String.valueOf(cn.product_category);
            chipListModel.add(new ChipModel(CYLINDER_DESCRIPTION, CYLINDER_CODE, PRODUCT_CATEGORY));
        }

        chipSelectionView();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("TV Details");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_backspace);
    }

    private void chipSelectionView() {

        final ChipSelectionAdapter chipAdapter = new ChipSelectionAdapter(this, chipListModel);
        recycler_view_chip.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));
        recycler_view_chip.setItemAnimator(new DefaultItemAnimator());
        recycler_view_chip.setAdapter(chipAdapter);

        recycler_view_chip.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recycler_view_chip, new ClickListener() {
            @Override
            public void onClick(View view, int position) {

                selectedCylinderTYPE = chipListModel.get(position).getChipTitleId();
                setSelectedCylinderTYPE(selectedCylinderTYPE);
                viewTV.setVisibility(View.GONE);
                //getAvailableCYL();
                input_number_of_cylinders.setText("");
                consumer_name.setText("");
                input_customerId.setText("");
                input_customerId.setEnabled(true);
                if (selectedCylinderTYPE != null){
                    viewTVDetails();
                }else {
                    Toast.makeText(TVDetailsActivity.this, "Please select Cylinder Type", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    private void viewTVDetails() {
        viewTV.setVisibility(View.VISIBLE);
        btnSubmit.setVisibility(View.VISIBLE);
    }

    private DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(getApplicationContext(), DatabaseHelper.class);
        }
        return databaseHelper;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    public int getSelectedCylinderTYPE() {
        return Integer.parseInt(selectedCylinderTYPE);
    }

    public void setSelectedCylinderTYPE(String selectedCylinderTYPE) {
        this.selectedCylinderTYPE = selectedCylinderTYPE;
    }


    @Override
    public void onSuccess(VolleySingleton.CallType type, String response) {

        Log.e("Response:", response);

        try {
            JSONObject objectResult = new JSONObject(response);

            if (objectResult.has("responseCode") && objectResult.optString("responseCode").equalsIgnoreCase("200")){
                JSONArray productArray = objectResult.getJSONArray("productArray");
                String CLOSING_FULL =    productArray.getJSONObject(0).optString("CLOSING_FULL");

                //layout_cylinder.setVisibility(View.VISIBLE);
                TOTAL_AVAILABLE_CYL = Integer.parseInt(CLOSING_FULL);
                cyl_count. setAnimationDuration(1000).countAnimation(0, TOTAL_AVAILABLE_CYL);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(VolleySingleton.CallType type, VolleyError error) {

        //layout_cylinder.setVisibility(View.VISIBLE);
        tv_available_cyl.setTextColor(getResources().getColor(R.color.colorRed));
        tv_available_cyl.setText("Could not load available cylinders");
        Log.e("Failed: ", error.getLocalizedMessage());
    }

    //Chip RecyclerView
    public interface ClickListener {
        void onClick(View view, int position);
        void onLongClick(View view, int position);
    }


    // not getting this code
    public  class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private TVDetailsActivity.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final TVDetailsActivity.ClickListener clickListener) {
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


   /* private void getAvailableCYL(){

        VolleySingleton.getInstance(getApplicationContext()).
                addResponseListener(VolleySingleton.CallType.GET_AVAILABLE_CYL, this);

        VolleySingleton.getInstance(getApplicationContext()).
                apiAvailableCYL(VolleySingleton.CallType.GET_AVAILABLE_CYL,
                        EVITA_API_URL, String.valueOf(getSelectedCylinderTYPE()), String.valueOf(getGoDownId()));
    }
*/
}
