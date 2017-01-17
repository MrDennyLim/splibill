package cimb.niaga.app.billsplit.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import cimb.niaga.app.billsplit.R;
import cimb.niaga.app.billsplit.corecycle.MyAPIClient;

/**
 * Created by Denny on 1/11/2017.
 */

public class SplashScreen extends AppCompatActivity {
    String deviceID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);
        getSupportActionBar().hide();

        TextView tx = (TextView)findViewById(R.id.txt_by);

        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/DroidSerif-Italic.ttf");
        tx.setTypeface(custom_font);

        StartAnimations();

        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.getDeviceId();

        deviceID = telephonyManager.getDeviceId();
        Log.d("denny", deviceID);

        checkAuth();


//        TimerTask task = new TimerTask() {
//
//            @Override
//            public void run() {
//
//                // go to the main activity
//                Intent nextActivity = new Intent(SplashScreen.this, LoginActivity.class);
//                startActivity(nextActivity);
//
//                // make sure splash screen activity is gone
//                SplashScreen.this.finish();
//
//
//            }
//
//        };
//
//        new Timer().schedule(task, 4000);
    }

    private void StartAnimations() {
        Animation fadein = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fadein.reset();
        Animation fadeout = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        fadeout.reset();
        Animation push_right_in = AnimationUtils.loadAnimation(this, R.anim.push_right_in);
        push_right_in.reset();

        ImageView iv_bill = (ImageView) findViewById(R.id.logo_billsplit);
        TextView tv_presented = (TextView) findViewById(R.id.txt_by);
        ImageView iv_niaga = (ImageView) findViewById(R.id.logo_cimb);

        iv_bill.clearAnimation();
        tv_presented.clearAnimation();
        iv_niaga.clearAnimation();

        iv_bill.startAnimation(push_right_in);
        tv_presented.startAnimation(fadein);
        iv_niaga.startAnimation(fadein);
    }

    public void checkAuth() {
        RequestParams params = new RequestParams();
        params.put("device", deviceID);
        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        client.setTimeout(MyAPIClient.HTTP_DEFAULT_TIMEOUT);

        client.post(MyAPIClient.LINK_CHECKDEVICE, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("denny", response.toString());

                try {
                    JSONObject object = response;

                    String error_code = object.getString("errorCode");

                    if(error_code.equals("00"))
                    {
                        // go to the main activity
                        Intent nextActivity = new Intent(SplashScreen.this, HomeActivity.class);
                        startActivity(nextActivity);

                        // make sure splash screen activity is gone
                        SplashScreen.this.finish();
                    }
                    else
                    {
                        // go to the main activity
                        Intent nextActivity = new Intent(SplashScreen.this, LoginActivity.class);
                        startActivity(nextActivity);

                        // make sure splash screen activity is gone
                        SplashScreen.this.finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("denny", "failed");
            }
        });
    }
}
