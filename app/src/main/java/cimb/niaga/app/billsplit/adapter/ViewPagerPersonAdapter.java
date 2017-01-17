package cimb.niaga.app.billsplit.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;

import cimb.niaga.app.billsplit.fragment.FragmentOwed;
import cimb.niaga.app.billsplit.fragment.FragmentOwing;
import cimb.niaga.app.billsplit.fragment.FragmentPersonA;
import cimb.niaga.app.billsplit.fragment.FragmentPersonB;

/**
 * Created by Admin on 16/01/2017.
 */

public class ViewPagerPersonAdapter extends FragmentStatePagerAdapter {
    private final int PAGES = 2;

    public ViewPagerPersonAdapter(FragmentManager fmb) {
        super(fmb);
    }

    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new FragmentPersonA();
            case 1:
                return new FragmentPersonB();

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
                return "Person A";
            case 1:
                return "Person B";
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
