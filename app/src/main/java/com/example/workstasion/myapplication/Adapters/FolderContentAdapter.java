package com.example.workstasion.myapplication.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.workstasion.myapplication.R;
import com.example.workstasion.myapplication.Views.SquareImageView;
import com.example.workstasion.myapplication.Workers.BitmapLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WORKSTASION on 15.12.2016.
 */

public class FolderContentAdapter extends ArrayAdapter<String> {
    private BitmapLoader bitmapLoader;
    private LayoutInflater inflater;
    private ViewHolder viewHolder;
    private List<Integer> selectedItems = new ArrayList<>();

    public FolderContentAdapter(Context context, List<String> objects) {
        super(context, R.layout.image_view_item, objects);
        bitmapLoader = new BitmapLoader(getContext().getResources(), R.drawable.placeholder);
        inflater = LayoutInflater.from(getContext());
    }

    public void setSelectedItems(List<Integer> selectedItems) {
        this.selectedItems = selectedItems;
    }

    public class ViewHolder{
        private ImageView preview;
        private RelativeLayout checkbox;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = inflater.inflate( R.layout.image_view_item, parent, false );
            viewHolder = new ViewHolder();
            viewHolder.preview = (SquareImageView) convertView.findViewById(R.id.image);
            viewHolder.checkbox = (RelativeLayout) convertView.findViewById(R.id.checkbox);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        if(this.selectedItems.contains(Integer.valueOf(position))){
            viewHolder.checkbox.setVisibility( View.VISIBLE );
        }else{
            viewHolder.checkbox.setVisibility( View.INVISIBLE );
        }
        bitmapLoader.loadBitmap( getItem(position), viewHolder.preview );

        return convertView;
    }
}
