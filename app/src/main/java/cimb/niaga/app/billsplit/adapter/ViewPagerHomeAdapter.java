package cimb.niaga.app.billsplit.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;

import cimb.niaga.app.billsplit.fragment.FragmentOwed;
import cimb.niaga.app.billsplit.fragment.FragmentOwing;

/**
 * Created by Denny on 1/11/2017.
 */

public class ViewPagerHomeAdapter extends FragmentStatePagerAdapter {
    private final int PAGES = 2;

    public ViewPagerHomeAdapter(FragmentManager fmb) {
        super(fmb);
    }

    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new FragmentOwed();
            case 1:
                return new FragmentOwing();

            default:
                throw new IllegalArgumentException("The item position should be less or equal to:" + PAGES);
        }
    }

    public int getCount() {
        return PAGES;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Owed";
            case 1:
                return "Owing";
            default:
                throw new IllegalArgumentException("The item position should be less or equal to: 2");
        }
    }

    public void destroyItem(ViewGroup container, int position, Object object) {
        if (position >= getCount()) {
            FragmentManager manager = ((Fragment) object).getFragmentManager();
            FragmentTransaction trans = manager.beginTransaction();
            trans.remove((Fragment) object);
            trans.commit();
        }
    }
}
