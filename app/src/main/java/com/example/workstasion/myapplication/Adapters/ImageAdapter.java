package com.example.workstasion.myapplication.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.workstasion.myapplication.Workers.BitmapLoader;
import com.example.workstasion.myapplication.R;
import com.example.workstasion.myapplication.Views.SquareImageView;
import com.example.workstasion.myapplication.Workers.ImageScanner;

import java.util.List;

/**
 * Created by WORKSTASION on 25.09.2016.
 */

public class ImageAdapter extends ArrayAdapter<ImageScanner.FoldStruct> {
    private BitmapLoader bitmapLoader;
    private List<ImageScanner.FoldStruct> item;
    private LayoutInflater inflater;

    public ImageAdapter(Context context, List<ImageScanner.FoldStruct> objects) {
        super(context, R.layout.image_view_item, objects);
        bitmapLoader = new BitmapLoader(getContext().getResources(), R.drawable.placeholder);
        item = objects;
        inflater = LayoutInflater.from(getContext());
    }

    @Override
    public int getCount() {

        if(item != null && item.size() > 0)
            return item.get(0).counter;

        return 0;
    }

    private ViewHolder viewHolder;
    private class ViewHolder{
        SquareImageView imageView;
        RelativeLayout checkbox;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = inflater.inflate( R.layout.image_view_item, parent, false );
            viewHolder = new ViewHolder();
            viewHolder.imageView = (SquareImageView) convertView.findViewById(R.id.image);
            viewHolder.checkbox = (RelativeLayout) convertView.findViewById(R.id.checkbox);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }


        String curr = item.get(0).fullPath[position];
        bitmapLoader.loadBitmap(curr,viewHolder.imageView);
        if(item.get(0).selectedIndexes.contains(position)){
            viewHolder.checkbox.setVisibility(View.VISIBLE);
        }else{
            viewHolder.checkbox.setVisibility(View.GONE);
        }

        return convertView;
    }
}
