package cimb.niaga.app.billsplit.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Camera;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import cimb.niaga.app.billsplit.R;
import cimb.niaga.app.billsplit.fragment.CameraFragment;
import cimb.niaga.app.billsplit.fragment.FragmentOwed;

/**
 * Created by Admin on 16/01/2017.
 */

public class PriceAdapter extends BaseAdapter {
    private Activity activity;

    public PriceAdapter(Activity act) {
        this.activity = act;
    }

    @Override
    public int getCount() {
//        return CameraFragment.list_price.size();
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ListHolder holder;

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_price, null);
            holder = new ListHolder();

            convertView.setTag(holder);
        }else{
            holder = (ListHolder) convertView.getTag();
        }

        holder.txt_price            = (TextView) convertView.findViewById(R.id.txt_price);

//        holder.txt_price.setText(CameraFragment.list_price.get(position));


        convertView.setTag(holder);

        return convertView;
    }

    class ListHolder
    {
        TextView txt_price;
    }
}
