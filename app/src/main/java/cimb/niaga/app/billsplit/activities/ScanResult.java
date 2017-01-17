package cimb.niaga.app.billsplit.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import cimb.niaga.app.billsplit.R;

/**
 * Created by 8ldavid on 1/12/2017.
 */

public class ScanResult extends AppCompatActivity {

    EditText merchant, menu, qty, price;
    Button submit_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.scan_result);

        merchant = (EditText) findViewById(R.id.merchant);
        menu = (EditText) findViewById(R.id.input_menu);
        qty = (EditText) findViewById(R.id.input_qty);
        price = (EditText) findViewById(R.id.input_price);
        submit_btn = (Button) findViewById(R.id.btn_submit);

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate())
                {
                    nextActivity();
                }
            }
        });

    }

    public void nextActivity()
    {
        Intent i = new Intent(ScanResult.this, HomeActivity.class);
        Toast.makeText(getApplication(), "Success", Toast.LENGTH_LONG).show();
        startActivity(i);
        finish();
    }

    public boolean validate() {
        boolean valid = true;

        String _merchant = merchant.getText().toString();
        String _menu = menu.getText().toString();
        String _qty = qty.getText().toString();

        if (_merchant.isEmpty()) {
            merchant.setError("please insert your menu");
            valid = false;
        } else {
            merchant.setError(null);
        }

        if (_menu.isEmpty()) {
            menu.setError("please insert your menu");
            valid = false;
        } else {
            menu.setError(null);
        }

        if (_qty.isEmpty()) {
            qty.setError("please insert your quantity");
            valid = false;
        } else {
            qty.setError(null);
        }

        return valid;
    }

}