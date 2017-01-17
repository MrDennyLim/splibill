package cimb.niaga.app.billsplit.fragment;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.gordonwong.materialsheetfab.MaterialSheetFab;
import com.gordonwong.materialsheetfab.MaterialSheetFabEventListener;

import cimb.niaga.app.billsplit.R;
import cimb.niaga.app.billsplit.adapter.ViewPagerHomeAdapter;
import cimb.niaga.app.billsplit.corecycle.Fab;

/**
 * Created by Denny on 1/10/2017.
 */

public class HomeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(container != null) {
            container.removeAllViews();
        }

        View view = inflater.inflate(R.layout.home_fragment, container, false);

        TabLayout tabs = (TabLayout) view.findViewById(R.id.tabs);
        ViewPager pager = (ViewPager) view.findViewById(R.id.pager);
        ViewPagerHomeAdapter adapter = new ViewPagerHomeAdapter(getChildFragmentManager());
        pager.setAdapter(adapter);
        tabs.setupWithViewPager(pager);

        return view;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void switchContent(Fragment mFragment,String fragName,Boolean isBackstack) {
        if(isBackstack){
            Log.d("backstack", "masuk");
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content, mFragment, fragName)
                    .addToBackStack(null)
                    .commit();
        }
        else {
            Log.d("bukan backstack","masuk");
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content, mFragment, fragName)
                    .commit();

        }
    }


}
