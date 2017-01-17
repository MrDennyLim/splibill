package cimb.niaga.app.billsplit.fragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cimb.niaga.app.billsplit.R;

/**
 * Created by Admin on 17/01/2017.
 */

public class FragmentOwedDetail extends DialogFragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.owed_detail_fragment, container, false);
        getDialog().setTitle("Owed Detail");

        return view;
    }
}
