package module.infosolutions.others.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import module.infosolutions.others.fragments.ReceivedFragment;
import module.infosolutions.others.fragments.SendFragment;

/**
 * Created by shailesh on 17/7/17.
 */

public class TruckDetailAdapter extends FragmentPagerAdapter {


    public TruckDetailAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                Fragment fragmentOne = new ReceivedFragment();
                return fragmentOne;
            case 1:
                Fragment fragmentTwo = new SendFragment();
                return fragmentTwo;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }


    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Received Cylinder";
            case 1:
                return "Send Cylinder";
        }
        return null;
    }
}
