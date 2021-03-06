package cimb.niaga.app.billsplit.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;

import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import cimb.niaga.app.billsplit.R;
import cimb.niaga.app.billsplit.adapter.PriceAdapter;
import cimb.niaga.app.billsplit.corecycle.GeneralizeImage;
import cimb.niaga.app.billsplit.corecycle.MyAPIClient;
import cimb.niaga.app.billsplit.corecycle.MyPicasso;
import cimb.niaga.app.billsplit.corecycle.RoundImageTransformation;
import cimb.niaga.app.billsplit.fragment.CameraFragment;
import dmax.dialog.SpotsDialog;

public class CameraActivity extends AppCompatActivity {
    Uri mCapturedImageURI;
    private final int RESULT_CAMERA = 200;
    public static final int RESULT_OK   = -1;
    ImageView img_bill;
    SpotsDialog dialog;

    LinearLayout targetLayout;
    ListView listSource;

    String commentMsg;

    String[] month ={
            "50.000",
            "45.000",
            "6.000",
            "7.000"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_activity);
        getSupportActionBar().show();

        img_bill = (ImageView) findViewById(R.id.img_bill);
        targetLayout = (LinearLayout)findViewById(R.id.targetlayout);
        listSource = (ListView)findViewById(R.id.sourcelist);

        String fileName = "temp.jpg";

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, fileName);

        mCapturedImageURI = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
        startActivityForResult(intent, RESULT_CAMERA);

    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case RESULT_CAMERA:
                if(resultCode == RESULT_OK && mCapturedImageURI!=null){
                    String[] projection = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(mCapturedImageURI, projection, null, null, null);
                    cursor.moveToFirst();
                    int column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    String filePath = cursor.getString(column_index_data);

                    File photoFile = new File(filePath);
                    final GeneralizeImage mGI = new GeneralizeImage(this,filePath);
                    uploadFile(mGI.Convert());
                    //getOrientationImage();
                    img_bill.setVisibility(View.VISIBLE);
                    setImageProfPic(photoFile);

                    listSource.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, month));
                    listSource.setOnItemLongClickListener(listSourceItemLongClickListener);
                }
                else{
                    Toast.makeText(this, "Try Again", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }
    }

    public void setImageProfPic(File filenya){
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.user_unknown_menu);
        RoundImageTransformation roundedImage = new RoundImageTransformation(bm);

        Picasso mPic;
        if(MyAPIClient.PROD_FLAG_ADDRESS)
            mPic = MyPicasso.getImageLoader(this);
        else
            mPic= Picasso.with(this);

        if(!filenya.exists()){
            mPic.load(R.mipmap.user_unknown_menu)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .error(roundedImage)
                    .fit().centerInside();
//                    .placeholder(R.anim.progress_animation)
//                    .transform(new RoundImageTransformation(getActivity())).into(img_bill);
            Bitmap myBitmap = BitmapFactory.decodeFile(filenya.getAbsolutePath());
            img_bill.setImageBitmap(myBitmap);
        }
        else {
            Bitmap myBitmap = BitmapFactory.decodeFile(filenya.getAbsolutePath());
            img_bill.setImageBitmap(myBitmap);
        }

    }

    AdapterView.OnItemLongClickListener listSourceItemLongClickListener
            = new AdapterView.OnItemLongClickListener(){

        @Override
        public boolean onItemLongClick(AdapterView<?> l, View v,
                                       int position, long id) {

            //Selected item is passed as item in dragData
            ClipData.Item item = new ClipData.Item(month[position]);

            String[] clipDescription = {ClipDescription.MIMETYPE_TEXT_PLAIN};
            ClipData dragData = new ClipData((CharSequence)v.getTag(),
                    clipDescription,
                    item);
            View.DragShadowBuilder myShadow = new MyDragShadowBuilder(v);

            v.startDrag(dragData, //ClipData
                    myShadow,  //View.DragShadowBuilder
                    month[position],  //Object myLocalState
                    0);    //flags

            commentMsg = v.getTag() + " : onLongClick.\n";
//            comments.setText(commentMsg);

            return true;
        }};

    private static class MyDragShadowBuilder extends View.DragShadowBuilder {
        private static Drawable shadow;

        public MyDragShadowBuilder(View v) {
            super(v);
            shadow = new ColorDrawable(Color.LTGRAY);
        }

        @Override
        public void onProvideShadowMetrics (Point size, Point touch){
            int width = getView().getWidth();
            int height = getView().getHeight();

            shadow.setBounds(0, 0, width, height);
            size.set(width, height);
            touch.set(width / 2, height / 2);
        }

        @Override
        public void onDrawShadow(Canvas canvas) {
            shadow.draw(canvas);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_next:
//                Intent i = new Intent(CameraActivity.this, SummaryActivity.class);
                Toast.makeText(getApplication(), "Next", Toast.LENGTH_LONG).show();
//                startActivity(i);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void uploadFile(File photoFile) {
        dialog = new SpotsDialog(CameraActivity.this, R.style.UploadDialog);
        dialog.show();
        RequestParams params = new RequestParams();
        try {
            params.put("image", photoFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Log.d("denny", params.toString());
        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        client.setTimeout(MyAPIClient.HTTP_DEFAULT_TIMEOUT);

        client.post(MyAPIClient.LINK_UPLOADER, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("denny", response.toString());

                try {
                    JSONObject object = response;

                    String img_url = object.getString("imgPublicUrl");

                    if(!img_url.equals(""))
                    {
                        dialog.dismiss();
                        callVisionAPI(img_url);
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

    public void callVisionAPI(String imgurl)
    {
        dialog = new SpotsDialog(CameraActivity.this, R.style.ScanDialog);
        dialog.show();
        RequestParams params = new RequestParams();
        params.put("img", imgurl);
        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        client.setTimeout(MyAPIClient.HTTP_DEFAULT_TIMEOUT);

        Log.d("denny", params.toString());

        client.get(MyAPIClient.LINK_OCR, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("denny", response.toString());

                try {
                    JSONObject object = response;

//                    String error_code = object.getString("errorCode");
                    dialog.dismiss();

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
}
