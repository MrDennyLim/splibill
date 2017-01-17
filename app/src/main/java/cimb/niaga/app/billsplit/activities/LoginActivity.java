package cimb.niaga.app.billsplit.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import cimb.niaga.app.billsplit.R;
import cimb.niaga.app.billsplit.corecycle.MyAPIClient;
import dmax.dialog.SpotsDialog;

public class LoginActivity extends AppCompatActivity {

    EditText nickname, phone, email;
    Button submit_btn;
    SpotsDialog dialog;
    String deviceID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.login_activity);

        nickname = (EditText) findViewById(R.id.input_nick);
        phone = (EditText) findViewById(R.id.input_phone);
        email = (EditText) findViewById(R.id.input_email);
        submit_btn = (Button) findViewById(R.id.btn_submit);

        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.getDeviceId();

        deviceID = telephonyManager.getDeviceId();

        //set next enter
        nickname.setImeOptions(EditorInfo.IME_ACTION_NEXT);

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(validate())
//                {
                    nextActivity();
//                }
            }
        });
    }

    public void nextActivity()
    {
        dialog = new SpotsDialog(LoginActivity.this, R.style.LoginDialog);
        dialog.show();
        RequestParams params = new RequestParams();
        params.put("nickname", nickname.getText());
        params.put("username", phone.getText());
        params.put("email", email.getText());
        params.put("device", deviceID);
        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        client.setTimeout(MyAPIClient.HTTP_DEFAULT_TIMEOUT);

        Log.d("denny", params.toString());

        client.post(MyAPIClient.LINK_REGISTER, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("denny", response.toString());

                try {
                    JSONObject object = response;

                    String error_code = object.getString("errorCode");

                    if(error_code.equals("00"))
                    {
                        // go to the main activity
                        Intent nextActivity = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(nextActivity);

                        // make sure splash screen activity is gone
                        LoginActivity.this.finish();
                        dialog.dismiss();
                    }
                    else
                    {
                        dialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("denny", "failed");

                dialog.dismiss();
            }
        });


    }

    public boolean validate() {
        boolean valid = true;

        String _nickname = nickname.getText().toString();
        String _phone = phone.getText().toString();
        String _email = email.getText().toString();


        if (_nickname.isEmpty()) {
            nickname.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            nickname.setError(null);
        }

        if (_phone.isEmpty()) {
            phone.setError("please insert your phone number");
            valid = false;
        } else {
            phone.setError(null);
        }

        if (_email.isEmpty()) {
            email.setError("please insert your valid email");
            valid = false;
        } else {
            email.setError(null);
        }

        return valid;
    }
}
