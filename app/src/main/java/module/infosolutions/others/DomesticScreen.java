package module.infosolutions.others;

import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.infosolutions.evita.R;

import java.util.ArrayList;
import java.util.List;

import com.infosolutions.ui.user.truckdelivery.TruckReceiveFragment;
import com.infosolutions.ui.user.truckdelivery.TruckSendFragment;
import module.infosolutions.others.fragments.DomesticFragment5KG;


public class DomesticScreen extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout tabs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_godown);
        if (Build.VERSION.SDK_INT >= 21) {getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary)); }

        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Domestic");
        setSupportActionBar(toolbar);
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
        DomesticScreen.Adapter adapter = new DomesticScreen.Adapter(getSupportFragmentManager());
        adapter.addFragment(new TruckReceiveFragment(), "14.2 KG");
        adapter.addFragment(new TruckSendFragment(), "19 KG");
        adapter.addFragment(new DomesticFragment5KG(), "05 KG");
        viewPager.setAdapter(adapter);
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

}
