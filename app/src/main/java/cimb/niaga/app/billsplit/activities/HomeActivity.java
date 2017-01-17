package cimb.niaga.app.billsplit.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.gordonwong.materialsheetfab.MaterialSheetFab;
import com.gordonwong.materialsheetfab.MaterialSheetFabEventListener;

import cimb.niaga.app.billsplit.R;
import cimb.niaga.app.billsplit.corecycle.Fab;
import cimb.niaga.app.billsplit.fragment.CameraFragment;
import cimb.niaga.app.billsplit.fragment.HomeFragment;

/**
 * Created by Denny on 1/10/2017.
 */

public class HomeActivity extends AppCompatActivity {
    FragmentManager fragmentManager;
    MaterialSheetFab materialSheetFab;

    @Override
    public void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        if (findViewById(R.id.content) != null) {
            if (savedInstanceState != null) {
                return;
            }

            HomeFragment home = new HomeFragment();
            fragmentManager = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.content, home);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }

        setupFab();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_help:
//                Intent i = new Intent(HomeActivity.this, SummaryActivity.class);
//                Toast.makeText(getApplication(), "Creating Bill..", Toast.LENGTH_LONG).show();
//                startActivity(i);
//            case R.id.action_logout:
//                finish();
//                System.exit(0);
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }

    public void switchContent(Fragment mFragment, String fragName, Boolean isBackstack) {

        if(isBackstack){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content, mFragment, fragName)
                    .addToBackStack(null)
                    .commit();
        }
        else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content, mFragment, fragName)
                    .commit();

        }

    }

    private void setupFab()
    {
        Fab fab = (Fab) findViewById(R.id.fab);
        View sheetView = findViewById(R.id.fab_sheet);
        View overlay = findViewById(R.id.overlay);
        int sheetColor = getResources().getColor(R.color.background_card);
        int fabColor = getResources().getColor(R.color.theme_accent);

        // Create material sheet FAB
        materialSheetFab = new MaterialSheetFab<>(fab, sheetView, overlay, sheetColor, fabColor);

        // Set material sheet event listener
        materialSheetFab.setEventListener(new MaterialSheetFabEventListener() {
            @Override
            public void onShowSheet() {
                // Save current status bar color
//                statusBarColor = getStatusBarColor();
                // Set darker status bar color to match the dim overlay
                setStatusBarColor(getResources().getColor(R.color.fab_color));
            }

            @Override
            public void onHideSheet() {
                // Restore status bar color
                setStatusBarColor(getResources().getColor(R.color.fab_color));
            }
        });

        // Set material sheet item click listeners
        findViewById(R.id.fab_sheet_item_recording).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplication(), "Add New Bill", Toast.LENGTH_LONG).show();
//                Intent i = new Intent(HomeActivity.this, CameraActivity.class);
//                Toast.makeText(getApplication(), "Opening Camera..", Toast.LENGTH_LONG).show();
//                startActivity(i);
                // finish();

                Toast.makeText(getApplication(), "Opening Camera..", Toast.LENGTH_LONG).show();

                CameraFragment camera = new CameraFragment();
                fragmentManager = getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content, camera);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                materialSheetFab.hideSheet();
            }
        });
        findViewById(R.id.fab_sheet_item_reminder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplication(), "Settings", Toast.LENGTH_LONG).show();
                Intent i = new Intent(HomeActivity.this, OCRActivity.class);
                Toast.makeText(getApplication(), "Creating Bill..", Toast.LENGTH_LONG).show();
                startActivity(i);
            }
        });
//        findViewById(R.id.fab_sheet_item_photo).setOnClickListener(this);
//        findViewById(R.id.fab_sheet_item_note).setOnClickListener(this);
    }

    private void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(color);
        }
    }
}
