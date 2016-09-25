package com.example.workstasion.myapplication.Activities;

import android.Manifest;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;

import com.example.workstasion.myapplication.Adapters.FolderAdapter;
import com.example.workstasion.myapplication.Adapters.ImageAdapter;
import com.example.workstasion.myapplication.R;
import com.example.workstasion.myapplication.Workers.ImageScanner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static final String TAG = "MAINACTIVITY";
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1453;
    private FolderAdapter mAdapter;
    private ImageAdapter mAdapterDetailed;
    private List<ImageScanner.FoldStruct> selectedFolder = new ArrayList<>();

    private String activityTitle = "Please Select Image";
    private File path = new File(Environment.getExternalStorageDirectory() + "");
    private List<ImageScanner.FoldStruct> folding = new ArrayList<>();
    private ImageScanner loader;
    private GridView mGridView;
    private int selectedLevel = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle(activityTitle);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDefaultDisplayHomeAsUpEnabled(true);
        }

        mAdapter = new FolderAdapter(this,folding);
        mAdapterDetailed = new ImageAdapter(this,selectedFolder);

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                MY_PERMISSIONS_REQUEST_READ_CONTACTS);

        mGridView = (GridView) findViewById(R.id.gridView);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(this);

        loader = new ImageScanner(path, new ImageScanner.ScannerEvents() {
            @Override
            public void loadDone() {
                folding.clear();
                folding.addAll(loader.get());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((BaseAdapter)mGridView.getAdapter()).notifyDataSetChanged();
                    }
                });
            }
        });
        loader.load();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case 0: break;
            default: {
                if(selectedLevel == 0){
                    return super.onOptionsItemSelected(item);
                }else{
                    selectedLevel = 0;
                    selectedFolder.clear();
                    setTitle(activityTitle);
                    mGridView.setAdapter(mAdapter);
                }
            } break;
        } return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        if(selectedLevel == 0) {
            ImageScanner.FoldStruct f = folding.get(position);
            setTitle(f.folder);
            selectedFolder.clear();
            selectedFolder.add(f);
            mGridView.setAdapter(mAdapterDetailed);
            selectedLevel = 1;
        }else{
            if(v != null) {
                View checkbox = v.findViewById(R.id.checkbox);
                if(checkbox != null)
                    if(checkbox.getVisibility() == View.GONE)
                        checkbox.setVisibility(View.VISIBLE);
                    else
                        checkbox.setVisibility(View.GONE);
            }
        }
    }
}
