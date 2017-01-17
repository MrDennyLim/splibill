package cimb.niaga.app.billsplit.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cimb.niaga.app.billsplit.R;
import cimb.niaga.app.billsplit.activities.HomeActivity;
import cimb.niaga.app.billsplit.adapter.OwedAdapter;
import cimb.niaga.app.billsplit.adapter.OwingAdapter;
import cimb.niaga.app.billsplit.corecycle.MyAPIClient;

/**
 * Created by Denny on 1/11/2017.
 */

public class FragmentOwing extends Fragment {
    ProgressBar prgLoading;
    ListView listEvent;
    TextView txtAlert;
    OwingAdapter owingAdapter;

    public static ArrayList<String> owing_event = new ArrayList<String>();
    public static ArrayList<String> owing_price = new ArrayList<String>();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.owing_fragment, container, false);
        prgLoading = (ProgressBar) view.findViewById(R.id.prgLoading);
        listEvent = (ListView) view.findViewById(R.id.listEventOwing);
        txtAlert = (TextView) view.findViewById(R.id.txtAlert);

        owingAdapter = new OwingAdapter(getActivity());

        getList();

        listEvent.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

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
