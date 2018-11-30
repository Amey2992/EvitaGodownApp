package com.infosolutions.ui.user.truckdelivery;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.infosolutions.database.ERVModel;
import com.infosolutions.database.PurchaseERVProduct;
import com.infosolutions.evita.R;
import com.infosolutions.network.ResponseListener;
import com.infosolutions.network.VolleySingleton;
import com.infosolutions.utils.AppSettings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class TruckDeliveryActivity extends AppCompatActivity implements ResponseListener {

    private ViewPager viewPager;
    private TabLayout tabs;
    public List<String> lstERVOWNModel = new ArrayList<>();
    public List<String> lstERVPCOModel = new ArrayList<>();
    public HashMap<String,List<PurchaseERVProduct>> hashProduct = new HashMap<>();
    public static boolean isShowPopup = false;
    private LocalBroadcastManager localBroadcastManager;
    private TruckSendFragment truckSendFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_truck_delivery);

        setupToolBar();
        VolleySingleton.getInstance(getApplicationContext()).addResponseListener(VolleySingleton.CallType.ERV_PURCHASE, this);
        AppSettings.getInstance(this).getPurchaseERV(this);
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
    }

    private void setupToolBar() {

        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Loads");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_backspace);
        setupFragments();
    }


    //Add ViewPager
    private void setupFragments(){
        viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        tabs = findViewById(R.id.result_tabs);
        tabs.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.d("viewpager:","onPageScrolled");
            }

            @Override
            public void onPageSelected(int position) {
                if(position == 1){
                     if(truckSendFragment != null ){
                         truckSendFragment.isFromHome = false;
                     }
                    Intent localIntent = new Intent("showDialog");
                    localBroadcastManager.sendBroadcast(localIntent);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.d("viewpager:","onPageScrollStateChanged");
            }
        });
    }


    private void setupViewPager(ViewPager viewPager) {
        TruckDeliveryActivity.Adapter adapter = new TruckDeliveryActivity.Adapter(getSupportFragmentManager());
        adapter.addFragment(new TruckReceiveFragment(), "Received");
        truckSendFragment = new TruckSendFragment();
        adapter.addFragment(truckSendFragment, "Send");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onSuccess(VolleySingleton.CallType type, String response) {
        //response = "";
        JSONObject jsonObject= null;
        try {
            jsonObject = new JSONObject(response);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // String responseCode = jsonObject.optString("responseCode");
        JSONArray jsonArray = jsonObject.optJSONArray("Table");
        if(lstERVOWNModel != null && lstERVOWNModel.size() > 0){
            lstERVOWNModel.clear();
        }
        if(lstERVPCOModel != null && lstERVPCOModel.size() > 0){
            lstERVPCOModel.clear();
        }
        if (type.equals(VolleySingleton.CallType.ERV_PURCHASE)) {
            if(jsonArray != null && jsonArray.length() > 0){
                for(int i = 0; i < jsonArray.length(); i++){
                    String ervno  = jsonArray.optJSONObject(i).optString("ERVNO");
                    String Vehicle_No  = jsonArray.optJSONObject(i).optString("Vehicle_No");
                    String PCO_Vehical_No  = jsonArray.optJSONObject(i).optString("PCO_Vehical_No");
                    int Vehical_Id  = jsonArray.optJSONObject(i).optInt("vehicleId");

                    if(!TextUtils.isEmpty(Vehicle_No)){
                        lstERVOWNModel.add(ervno);
                    }else {
                        lstERVPCOModel.add(ervno);
                    }

                    JSONArray productArr = jsonArray.optJSONObject(i).optJSONArray("Details");
                    List<PurchaseERVProduct> lstPurchaseProduct = new ArrayList<>();
                    if(productArr != null && productArr.length() > 0){
                        for(int j = 0; j < productArr.length(); j++){
                            PurchaseERVProduct purchaseERVProduct = new PurchaseERVProduct(productArr.optJSONObject(j),ervno,Vehicle_No,PCO_Vehical_No, Vehical_Id);
                            if(purchaseERVProduct != null){
                                lstPurchaseProduct.add(purchaseERVProduct);
                            }
                        }
                        hashProduct.put(ervno,lstPurchaseProduct);
                    }
                }
                isShowPopup = true;
            }
        }
    }

    @Override
    public void onFailure(VolleySingleton.CallType type, VolleyError error) {
        System.out.println();
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
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
    protected void onPause() {
        //AppSettings.hideKeyboard(this);
        super.onPause();

    }
}
