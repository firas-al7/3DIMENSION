package com.dimension.a3dimension;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dimension.a3dimension.R;
import com.dimension.a3dimension.models.SavedScan;

import java.util.ArrayList;

public class SavedScansAdapter extends ArrayAdapter<SavedScan> implements View.OnClickListener {

    private ArrayList<SavedScan> dataSet;
    Context mContext;
    public static  int scanImage;
    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
        TextView txtDate;

        ImageView scanImage;
    }

    public SavedScansAdapter(ArrayList<SavedScan> data, Context context) {
        super(context, R.layout.general_list_item, data);
        this.dataSet = data;
        this.mContext=context;

    }

    @Override
    public void onClick(View v) {
        int position=(Integer) v.getTag();
        Object object= getItem(position);
        SavedScan dataModel=(SavedScan) object;

    }


    private int lastPosition = -1;
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        SavedScan dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.general_list_item, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.scanName);
            viewHolder.txtDate = (TextView) convertView.findViewById(R.id.scanDateTime);

            viewHolder.scanImage = (ImageView) convertView.findViewById(R.id.scanImage);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.txtName.setText(dataModel.getName());
        viewHolder.txtDate.setText(dataModel.getDate());
        viewHolder.scanImage.setImageResource(dataModel.getImage());

        // Return the completed view to render on screen
        return convertView;
    }
}
