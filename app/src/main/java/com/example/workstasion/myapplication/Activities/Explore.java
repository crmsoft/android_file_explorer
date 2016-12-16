package com.example.workstasion.myapplication.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;

import com.example.workstasion.myapplication.Adapters.Folder;
import com.example.workstasion.myapplication.R;
import com.example.workstasion.myapplication.Workers.DirectoryComparator;
import com.example.workstasion.myapplication.Workers.Loader;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Explore extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private List<Loader.DirectoryPreview> directoryPreviewList = new ArrayList<>();
    private Folder directoryAdapter;
    private GridView gridView;
    private File path = new File(Environment.getExternalStorageDirectory() + "");
    private ProgressBar progressBar;
    private DirectoryComparator comparator = new DirectoryComparator();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridView = (GridView)findViewById(R.id.gridView);
        progressBar = (ProgressBar)findViewById(R.id.load_indicator);

        directoryAdapter = new Folder( this, directoryPreviewList );

        gridView = (GridView)findViewById(R.id.gridView);
        gridView.setAdapter( directoryAdapter );
        gridView.setOnItemClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(directoryPreviewList.size() == 0){
            new Thread(new Loader(path, new Loader.Communicate() {
                @Override
                public void newDir(Loader.DirectoryPreview preview) {
                    directoryPreviewList.add(preview);
                    Collections.sort(directoryPreviewList, comparator);
                    Explore.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            directoryAdapter.notifyDataSetChanged();
                        }
                    });
                }

                @Override
                public void done( ) {
                    Explore.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            })).start();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Loader.DirectoryPreview pr = directoryPreviewList.get( position );
        Bundle b = new Bundle();
        b.putStringArray( "items", pr.getItems() );
        Intent i = new Intent(Explore.this,FolderContent.class);
        i.putExtras(b);
        startActivity(i);
    }
}
