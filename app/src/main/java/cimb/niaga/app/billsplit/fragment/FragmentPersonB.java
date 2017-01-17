package cimb.niaga.app.billsplit.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cimb.niaga.app.billsplit.R;
import cimb.niaga.app.billsplit.activities.HomeActivity;

/**
 * Created by Admin on 16/01/2017.
 */

public class FragmentPersonB extends Fragment{

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.person_fragment, container, false);

        return view;
    }

    private void switchFragment(Fragment fragment) {
        if (getActivity() == null)
            return;
        HomeActivity main = (HomeActivity) getActivity();
        main.switchContent(fragment,"PersonA",true);
    }
}
