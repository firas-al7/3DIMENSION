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
import com.dimension.a3dimension.models.GroundType;

import java.util.ArrayList;

public class GroundTypesAdapter extends ArrayAdapter<GroundType> implements View.OnClickListener  {
    private ArrayList<GroundType> dataSet;
    Context mContext;
    public GroundTypesAdapter(ArrayList<GroundType> data, Context context) {
        super(context, R.layout.ground_type, data);
        this.dataSet = data;
        this.mContext = context;

    }
    @Override
    public void onClick(View v) {

    }
    private static class ViewHolder {
        TextView groundTypeText;
        ImageView groundTypeImage;

        //LinearLayout linearLayout;



    }
    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        GroundType dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        GroundTypesAdapter.ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new GroundTypesAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.ground_type, parent, false);
            viewHolder.groundTypeText = (TextView) convertView.findViewById(R.id.ground_type_text);
            viewHolder.groundTypeImage = (ImageView) convertView.findViewById(R.id.ground_type_image);
            // viewHolder.linearLayout = (LinearLayout) convertView.findViewById(R.id.linearLayoutDeviceInfo);

            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (GroundTypesAdapter.ViewHolder) convertView.getTag();
            result = convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

       viewHolder.groundTypeText.setText(dataModel.getType());
       viewHolder.groundTypeImage.setBackground(dataModel.getGroundImage());


        // Return the completed view to render on screen
        return convertView;
    }
}
