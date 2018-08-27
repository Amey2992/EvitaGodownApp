package module.infosolutions.others.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import module.infosolutions.others.fragments.EmptyCylinderFragment;
import module.infosolutions.others.fragments.FullCylinderFragment;


public class CommercialPagerAdapter extends FragmentPagerAdapter {


    public CommercialPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                Fragment fragmentOne = new EmptyCylinderFragment();
                return fragmentOne;
            case 1:
                Fragment fragmentTwo = new FullCylinderFragment();
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
                return "Full Cylinder";
            case 1:
                return "Empty Cylinder";
        }
        return null;
    }
}
