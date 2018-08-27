package module.infosolutions.others.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


/**
 * Created by shailesh on 17/7/17.
 */

public class TVDetailAdapter extends FragmentPagerAdapter {


    public TVDetailAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                //Fragment fragDomestic = new TVFragment14KG();
                //return fragDomestic;
            case 1:
                //Fragment fragCommercial = new TVFragment19KG();
               // return fragCommercial;
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
                return "Domestic";
            case 1:
                return "Commercial";
        }
        return null;
    }
}
