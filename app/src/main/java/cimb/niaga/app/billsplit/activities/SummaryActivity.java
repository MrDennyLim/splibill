package cimb.niaga.app.billsplit.activities;

/**
 * Created by 8ldavid on 1/15/2017.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import cimb.niaga.app.billsplit.R;
import cimb.niaga.app.billsplit.adapter.ExpandableListAdapter;
import cimb.niaga.app.billsplit.corecycle.FirebaseAPI;
import cimb.niaga.app.billsplit.corecycle.MyAPIClient;
import dmax.dialog.SpotsDialog;

public class SummaryActivity extends AppCompatActivity {

    ExpandableListAdapter listAdapter;
    Button submit_btn;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.summary_activity);

        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.lvExp);

        // preparing list data
        prepareListData();

        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);

        // Listview Group click listener
        expListView.setOnGroupClickListener(new OnGroupClickListener() {

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
        expListView.setOnGroupExpandListener(new OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        listDataHeader.get(groupPosition) + " Expanded",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Listview Group collasped listener
        expListView.setOnGroupCollapseListener(new OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        listDataHeader.get(groupPosition) + " Collapsed",
                        Toast.LENGTH_SHORT).show();

            }
        });

        // Listview on child click listener
        expListView.setOnChildClickListener(new OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                // TODO Auto-generated method stub
                Toast.makeText(
                        getApplicationContext(),
                        listDataHeader.get(groupPosition)
                                + " : "
                                + listDataChild.get(
                                listDataHeader.get(groupPosition)).get(
                                childPosition), Toast.LENGTH_SHORT)
                        .show();
                return false;
            }
        });

        submit_btn = (Button) findViewById(R.id.btn_submit);

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    nextActivity();
            }
        });
    }

    public void nextActivity()
    {
        String tkn = FirebaseInstanceId.getInstance().getToken();
        Toast.makeText(SummaryActivity.this, "Current token ["+tkn+"]",
                Toast.LENGTH_LONG).show();
        Log.d("App", "Token ["+tkn+"]");

        RequestParams params = new RequestParams();
        params.put("to", tkn);
        params.put("title", "tes");
        params.put("body", "Anda memiliki bill sebesar Rp.15000 kepada David");
        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        client.setTimeout(FirebaseAPI.HTTP_DEFAULT_TIMEOUT);

        Log.d("denny", params.toString());

        client.post(FirebaseAPI.headaddress, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("denny", response.toString());

                try {
                    JSONObject object = response;

                    String error_code = object.getString("errorCode");

                    if (error_code.equals("00")) {
                        // go to the main activity
                        Intent nextActivity = new Intent(SummaryActivity.this, HomeActivity.class);
                        startActivity(nextActivity);

                        // make sure splash screen activity is gone
                        SummaryActivity.this.finish();
                        //dialog.dismiss();
                    } else {
                        Intent nextActivity = new Intent(SummaryActivity.this, HomeActivity.class);
                        startActivity(nextActivity);

                        // make sure splash screen activity is gone
                        SummaryActivity.this.finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
}