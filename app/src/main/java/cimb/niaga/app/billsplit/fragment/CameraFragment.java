package cimb.niaga.app.billsplit.fragment;

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
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cimb.niaga.app.billsplit.R;
import cimb.niaga.app.billsplit.activities.CameraActivity;
import cimb.niaga.app.billsplit.adapter.OwedAdapter;
import cimb.niaga.app.billsplit.adapter.PriceAdapter;
import cimb.niaga.app.billsplit.adapter.ViewPagerHomeAdapter;
import cimb.niaga.app.billsplit.adapter.ViewPagerPersonAdapter;
import cimb.niaga.app.billsplit.corecycle.GeneralizeImage;
import cimb.niaga.app.billsplit.corecycle.ListViewDragDrop;
import cimb.niaga.app.billsplit.corecycle.MyAPIClient;
import cimb.niaga.app.billsplit.corecycle.MyPicasso;
import cimb.niaga.app.billsplit.corecycle.RoundImageTransformation;

import static cimb.niaga.app.billsplit.activities.CameraActivity.ALLOW_KEY;

/**
 * Created by Denny on 1/14/2017.
 */

public class CameraFragment extends Fragment {
    Uri mCapturedImageURI;
    private final int RESULT_CAMERA = 200;
    public static final int RESULT_OK   = -1;
    ImageView img_bill;
    TabLayout tabs;
    ViewPager pager;
    ListView listPrice;
    PriceAdapter priceAdapter;

    public static ArrayList<String> list_price = new ArrayList<String>();

    LinearLayout targetLayout;
    ListView listSource, listTarget;
    TextView comments;

    String commentMsg;

    MyDragEventListener myDragEventListener = new MyDragEventListener();

    String[] month ={
            "January",
            "February",
            "March",
            "April",
            "May",
            "June",
            "July",
            "August",
            "September",
            "October",
            "November",
            "December"};

    List<String> droppedList;
    ArrayAdapter<String> droppedAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(container != null) {
            container.removeAllViews();
        }

        View view = inflater.inflate(R.layout.camera_fragment, container, false);

        targetLayout = (LinearLayout)view.findViewById(R.id.targetlayout);
        listSource = (ListView)view.findViewById(R.id.sourcelist);
        listTarget = (ListView)view.findViewById(R.id.targetlist);
        comments = (TextView)view.findViewById(R.id.comments);

        // Create and set the tags for the Buttons
        final String SOURCELIST_TAG = "listSource";
        final String TARGETLIST_TAG = "listTarget";
        final String TARGETLAYOUT_TAG = "targetLayout";

        listSource.setTag(SOURCELIST_TAG);
        listTarget.setTag(TARGETLIST_TAG);
        targetLayout.setTag(TARGETLAYOUT_TAG);

        listSource.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, month));
        listSource.setOnItemLongClickListener(listSourceItemLongClickListener);


        droppedList = new ArrayList<String>();
        droppedAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, droppedList);
        listTarget.setAdapter(droppedAdapter);

        listSource.setOnDragListener(myDragEventListener);
        targetLayout.setOnDragListener(myDragEventListener);

        return view;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void switchContent(Fragment mFragment,String fragName,Boolean isBackstack) {
        if(isBackstack){
            Log.d("backstack", "masuk");
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content, mFragment, fragName)
                    .addToBackStack(null)
                    .commit();
        }
        else {
            Log.d("bukan backstack","masuk");
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content, mFragment, fragName)
                    .commit();

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
            comments.setText(commentMsg);

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

    protected class MyDragEventListener implements View.OnDragListener {

        @Override
        public boolean onDrag(View v, DragEvent event) {
            final int action = event.getAction();

            switch(action) {
                case DragEvent.ACTION_DRAG_STARTED:
                    //All involved view accept ACTION_DRAG_STARTED for MIMETYPE_TEXT_PLAIN
                    if (event.getClipDescription()
                            .hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN))
                    {
                        commentMsg += v.getTag()
                                + " : ACTION_DRAG_STARTED accepted.\n";
                        comments.setText(commentMsg);
                        return true; //Accept
                    }else{
                        commentMsg += v.getTag()
                                + " : ACTION_DRAG_STARTED rejected.\n";
                        comments.setText(commentMsg);
                        return false; //reject
                    }
                case DragEvent.ACTION_DRAG_ENTERED:
                    commentMsg += v.getTag() + " : ACTION_DRAG_ENTERED.\n";
                    comments.setText(commentMsg);
                    return true;
                case DragEvent.ACTION_DRAG_LOCATION:
                    commentMsg += v.getTag() + " : ACTION_DRAG_LOCATION - "
                            + event.getX() + " : " + event.getY() + "\n";
                    comments.setText(commentMsg);
                    return true;
                case DragEvent.ACTION_DRAG_EXITED:
                    commentMsg += v.getTag() + " : ACTION_DRAG_EXITED.\n";
                    comments.setText(commentMsg);
                    return true;
                case DragEvent.ACTION_DROP:
                    // Gets the item containing the dragged data
                    ClipData.Item item = event.getClipData().getItemAt(0);

                    commentMsg += v.getTag() + " : ACTION_DROP" + "\n";
                    comments.setText(commentMsg);

                    //If apply only if drop on buttonTarget
                    if(v == targetLayout){
                        String droppedItem = item.getText().toString();

                        commentMsg += "Dropped item - "
                                + droppedItem + "\n";
                        comments.setText(commentMsg);

                        droppedList.add(droppedItem);
                        droppedAdapter.notifyDataSetChanged();

                        return true;
                    }else{
                        return false;
                    }


                case DragEvent.ACTION_DRAG_ENDED:
                    if (event.getResult()){
                        commentMsg += v.getTag() + " : ACTION_DRAG_ENDED - success.\n";
                        comments.setText(commentMsg);
                    } else {
                        commentMsg += v.getTag() + " : ACTION_DRAG_ENDED - fail.\n";
                        comments.setText(commentMsg);
                    };
                    return true;
                default: //unknown case
                    commentMsg += v.getTag() + " : UNKNOWN !!!\n";
                    comments.setText(commentMsg);
                    return false;

            }
        }
    }
}
