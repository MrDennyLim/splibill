package cimb.niaga.app.billsplit.activities;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ContentValues;
import android.content.Intent;
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
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cimb.niaga.app.billsplit.R;
import cimb.niaga.app.billsplit.corecycle.GeneralizeImage;
import cimb.niaga.app.billsplit.corecycle.MyAPIClient;
import cimb.niaga.app.billsplit.corecycle.MyPicasso;
import cimb.niaga.app.billsplit.corecycle.RoundImageTransformation;

/**
 * Created by Admin on 18/01/2017.
 */

public class BillAssignActivity extends AppCompatActivity {
    Uri mCapturedImageURI;
    private final int RESULT_CAMERA = 200;
    public static final int RESULT_OK   = -1;
    ImageView img_bill;

    LinearLayout targetLayout;
    ListView listSource, listTarget;

    String commentMsg;

    String[] month ={
            "50.000",
            "45.000",
            "6.000",
            "7.000"};

    List<String> droppedList;
    ArrayAdapter<String> droppedAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bill_assign_activity);
        getSupportActionBar().show();

        img_bill = (ImageView) findViewById(R.id.img_bill);
        targetLayout = (LinearLayout)findViewById(R.id.targetlayout);
        listSource = (ListView)findViewById(R.id.sourcelist);
        listTarget = (ListView)findViewById(R.id.targetlist);

        String fileName = "temp.jpg";

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, fileName);

        mCapturedImageURI = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
        startActivityForResult(intent, RESULT_CAMERA);

        // Create and set the tags for the Buttons
        final String SOURCELIST_TAG = "listSource";
        final String TARGETLIST_TAG = "listTarget";
        final String TARGETLAYOUT_TAG = "targetLayout";

        listSource.setTag(SOURCELIST_TAG);
        listTarget.setTag(TARGETLIST_TAG);
        targetLayout.setTag(TARGETLAYOUT_TAG);

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
                    //getOrientationImage();
                    img_bill.setVisibility(View.VISIBLE);
                    setImageProfPic(photoFile);

                    listSource.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, month));
                    listSource.setOnItemLongClickListener(listSourceItemLongClickListener);

                    droppedList = new ArrayList<String>();
                    droppedAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, droppedList);
                    listTarget.setAdapter(droppedAdapter);
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
}
