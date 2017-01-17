package cimb.niaga.app.billsplit.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cimb.niaga.app.billsplit.R;
import cimb.niaga.app.billsplit.activities.HomeActivity;
import cimb.niaga.app.billsplit.adapter.ExpandableListAdapter;
import cimb.niaga.app.billsplit.adapter.OwedAdapter;
import cimb.niaga.app.billsplit.adapter.OwingAdapter;
import cimb.niaga.app.billsplit.corecycle.MyAPIClient;

/**
 * Created by Denny on 1/11/2017.
 */

public class FragmentOwing extends Fragment {
    ProgressBar prgLoading;
//    ListView listEvent;
    ExpandableListView listEvent;
    TextView txtAlert;
    OwingAdapter owingAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    ExpandableListAdapter listAdapter;

    public static ArrayList<String> owing_event = new ArrayList<String>();
    public static ArrayList<String> owing_price = new ArrayList<String>();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.owing_fragment, container, false);
        prgLoading = (ProgressBar) view.findViewById(R.id.prgLoading);
//        listEvent = (ListView) view.findViewById(R.id.listEventOwing);
        txtAlert = (TextView) view.findViewById(R.id.txtAlert);
        listEvent = (ExpandableListView) view.findViewById(R.id.listEventOwing);

        prgLoading.setVisibility(View.GONE);
        prepareListData();
//        owingAdapter = new OwingAdapter(getActivity());

//        getList();

        listAdapter = new ExpandableListAdapter(getActivity(), listDataHeader, listDataChild);

        // setting list adapter
        listEvent.setAdapter(listAdapter);

        // Listview Group click listener
        listEvent.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                // Toast.makeText(getApplicationContext(),
                // "Group Clicked " + listDataHeader.get(groupPosition),
                // Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        // Listview Group expanded listener
        listEvent.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                Toast.makeText(getActivity(),
                        listDataHeader.get(groupPosition) + " Expanded",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Listview Group collasped listener
        listEvent.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                Toast.makeText(getActivity(),
                        listDataHeader.get(groupPosition) + " Collapsed",
                        Toast.LENGTH_SHORT).show();

            }
        });

        // Listview on child click listener
        listEvent.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                // TODO Auto-generated method stub
                Toast.makeText(
                        getActivity(),
                        listDataHeader.get(groupPosition)
                                + " : "
                                + listDataChild.get(
                                listDataHeader.get(groupPosition)).get(
                                childPosition), Toast.LENGTH_SHORT)
                        .show();
                return false;
            }
        });

        return view;
    }

    public void getList() {
        clearData();

        RequestParams params = new RequestParams();
        params.put("title", "Solaria");
        params.put("content", "Rp 57.000,00");
        params.put("id", "0");
        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        client.setTimeout(MyAPIClient.HTTP_DEFAULT_TIMEOUT);

        client.post(MyAPIClient.LINK_TEST, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Log.d("denny", response.toString());

                    JSONObject object = response;
                    owing_event.add(object.getString("title"));
                    owing_price.add(object.getString("content"));

                    prgLoading.setVisibility(View.GONE);
                    if (owing_event.size() > 0) {
                        prgLoading.setVisibility(View.GONE);
                        txtAlert.setVisibility(View.GONE);
                        listEvent.setVisibility(View.VISIBLE);
                        listEvent.setAdapter(owingAdapter);
                    } else {
                        prgLoading.setVisibility(View.GONE);
                        txtAlert.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("denny", "failed");
                prgLoading.setVisibility(View.GONE);
                txtAlert.setVisibility(View.VISIBLE);
            }
        });
    }

    /*
     * Preparing the list data
     */
    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding child data
        listDataHeader.add("David");
        listDataHeader.add("Denny");
        listDataHeader.add("Bluemix");

        // Adding child data
        List<String> top250 = new ArrayList<String>();
        top250.add("Price: 10000");
        top250.add("Item: Nasi Ayam Goreng");
        top250.add("Quantity: 1");

        List<String> nowShowing = new ArrayList<String>();
        nowShowing.add("Price: 30000");
        nowShowing.add("Item: Mie Ayam Bakso");
        nowShowing.add("Quantity: 1");

        List<String> comingSoon = new ArrayList<String>();
        comingSoon.add("Price: 50000");
        comingSoon.add("Item: Nasi Goreng");
        comingSoon.add("Quantity: 2");

        listDataChild.put(listDataHeader.get(0), top250); // Header, Child data
        listDataChild.put(listDataHeader.get(1), nowShowing);
        listDataChild.put(listDataHeader.get(2), comingSoon);
    }

    void clearData(){
        owing_event.clear();
        owing_price.clear();
    }

    private void switchFragment(Fragment fragment) {
        if (getActivity() == null)
            return;
        HomeActivity main = (HomeActivity) getActivity();
        main.switchContent(fragment,"Owed",true);
    }
}
