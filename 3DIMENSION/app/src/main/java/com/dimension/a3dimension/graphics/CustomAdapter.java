package com.dimension.a3dimension.graphics;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dimension.a3dimension.graphics.Activities.diagrams.ViewScanDiagram;
import com.dimension.a3dimension.R;

import java.util.ArrayList;


public class CustomAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Item> mItem;

    @Override // android.widget.Adapter
    public long getItemId(int i) {
        return i;
    }

    public CustomAdapter(Context context, ArrayList<Item> arrayList) {
        this.mContext = context;
        this.mItem = arrayList;
    }

    @Override // android.widget.Adapter
    public int getCount() {
        return this.mItem.size();
    }

    @Override // android.widget.Adapter
    public Object getItem(int i) {
        return this.mItem.get(i);
    }


    private class ViewHolder {
        ImageView Delete;
        ImageView Share;
        ImageView Show;
        ImageView edit;
        TextView txtItemName;

        TextView txtItemDate;

        public ViewHolder(View view) {
            this.txtItemName = (TextView) view.findViewById(R.id.txtItemName);
            this.txtItemDate =(TextView) view.findViewById(R.id.scanDateTime);
            this.edit = (ImageView) view.findViewById(R.id.Diagram_edit);
            this.Show = (ImageView) view.findViewById(R.id.Diagram_Show);
            this.Share = (ImageView) view.findViewById(R.id.Diagram_Share);
            this.Delete = (ImageView) view.findViewById(R.id.Diagram_delete);

        }
    }

    @Override // android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        if (view == null) {
            view = LayoutInflater.from(this.mContext).inflate(R.layout.list_item, viewGroup, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.txtItemName.setText(((Item) getItem(i)).getName());
        viewHolder.txtItemDate.setText(((Item) getItem(i)).getDatetime());
        view.setOnClickListener(new View.OnClickListener() {
            @Override // android.view.View.OnClickListener
            public void onClick(View view2) {
                Intent intent = new Intent(CustomAdapter.this.mContext, ViewScanDiagram.class);
                intent.putExtra("data", viewHolder.txtItemName.getText());
                CustomAdapter.this.mContext.startActivity(intent);
            }
        });
        return view;
    }
}
