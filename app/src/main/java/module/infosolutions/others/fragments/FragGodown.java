package module.infosolutions.others.fragments;


import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.infosolutions.evita.R;
import com.infosolutions.ui.user.truckdelivery.TruckReceiveFragment;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by shailesh on 8/8/17.
 */

public class FragGodown extends Fragment {

    private Typeface custom_font;
    private RelativeLayout host_layout;
    private ViewPager viewPager;
    private TabLayout tabs;
    private TextView tvMessage;

    public FragGodown() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {

        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_godown,container,false);
        final TextView viewMessage = rootView.findViewById(R.id.tvMessage);
        final RelativeLayout host_layout = rootView.findViewById(R.id.host_layout);

        custom_font = Typeface.createFromAsset(getContext().getAssets(),  "fonts/Ubuntu-M.ttf");
        viewMessage.setTypeface(custom_font);

        /**
         * show / hide View pager depends on server response
         *  if Error show tvMessage else setup Pager&Fragemnts
         *  sho or hide host_layout depending on response receved from server
         * */

        setupFragments(rootView);
        return rootView;
    }


    //Add ViewPager
    private void setupFragments(View rootView){

        viewPager = rootView.findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        tabs = rootView.findViewById(R.id.result_tabs);
        tabs.setupWithViewPager(viewPager);
    }



    // Add Fragments to Tabs
    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getChildFragmentManager());
        adapter.addFragment(new TruckReceiveFragment(), "Domestic");
        adapter.addFragment(new CommercialFragment(), "Commercial");
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
