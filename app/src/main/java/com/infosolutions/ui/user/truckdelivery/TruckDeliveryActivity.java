package com.infosolutions.ui.user.truckdelivery;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.infosolutions.database.ERVModel;
import com.infosolutions.evita.R;
import com.infosolutions.network.ResponseListener;
import com.infosolutions.network.VolleySingleton;
import com.infosolutions.utils.AppSettings;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class TruckDeliveryActivity extends AppCompatActivity implements ResponseListener {

    private ViewPager viewPager;
    private TabLayout tabs;
    public List<ERVModel> lstERVOWMModel = new ArrayList<>();
    public List<ERVModel> lstERVPCOModel = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_truck_delivery);

        setupToolBar();
        VolleySingleton.getInstance(getApplicationContext()).addResponseListener(VolleySingleton.CallType.ERV_PURCHASE, this);
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
    }



    private void setupViewPager(ViewPager viewPager) {
        TruckDeliveryActivity.Adapter adapter = new TruckDeliveryActivity.Adapter(getSupportFragmentManager());
        adapter.addFragment(new TruckReceiveFragment(), "Received");
        adapter.addFragment(new TruckSendFragment(), "Send");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onSuccess(VolleySingleton.CallType type, String response) {
        response = "";
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(response);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String responseCode = jsonObject.optString("responseCode");
        if (type.equals(VolleySingleton.CallType.ERV_PURCHASE)) {

        }
    }

    @Override
    public void onFailure(VolleySingleton.CallType type, VolleyError error) {

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
        AppSettings.hideKeyboard(this);
        super.onPause();

    }
}
