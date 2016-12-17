package com.example.workstasion.myapplication.Adapters;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.workstasion.myapplication.R;
import com.example.workstasion.myapplication.Views.SquareImageView;
import com.example.workstasion.myapplication.Workers.Loader;

import java.util.List;

/**
 * Created by WORKSTASION on 15.12.2016.
 */

public class FolderAdapter extends ArrayAdapter<Loader.DirectoryPreview> {

    private LayoutInflater inflater;
    private ViewHolder viewHolder;
    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent("do_rm_folder");
            Bundle b = new Bundle();
            b.putInt("position",Integer.valueOf((int)v.getTag()));
            intent.putExtras(b);
            getContext().sendBroadcast(intent);
        }
    };

    public FolderAdapter(Context context, List<Loader.DirectoryPreview> list) {
        super(context, R.layout.folder_struct_item, list);
        inflater =  LayoutInflater.from(getContext());
    }

    static class ViewHolder{
        TextView folderName;
        TextView folderRootName;
        TextView itemCount;
        SquareImageView preview;
        ImageView deleteBtn;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup container) {
        if (convertView == null) { // if it's not recycled, initialize some attributes
            convertView = inflater.inflate(R.layout.folder_struct_item,container,false);
            viewHolder = new ViewHolder();
            viewHolder.folderName = (TextView)convertView.findViewById(R.id.folder_name);
            viewHolder.folderRootName = (TextView)convertView.findViewById(R.id.root_folder_name);
            viewHolder.itemCount = (TextView)convertView.findViewById(R.id.item_count);
            viewHolder.preview = (SquareImageView)convertView.findViewById(R.id.preview);
            viewHolder.deleteBtn = (ImageView)convertView.findViewById(R.id.folder_rm_btn);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        Loader.DirectoryPreview dir = getItem(position);
        viewHolder.folderName.setText(dir.getUpDirName());
        viewHolder.folderRootName.setText("/"+dir.getName());
        viewHolder.itemCount.setText(dir.getCount()+"");
        if( dir.getPreview() != null )
            viewHolder.preview.setImageBitmap( dir.getPreview() );

        viewHolder.deleteBtn.setTag( position );
        viewHolder.deleteBtn.setOnClickListener(listener);

        return convertView;
    }
}
