package com.example.workstasion.myapplication.Activities;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ProgressBar;

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
    private MenuItem selectedItemCount;
    private MenuItem checkboxItem;
    private boolean isCheckEnabled = true;
    private ProgressBar progressBar;

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
        progressBar = (ProgressBar)findViewById(R.id.load_indicator);

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
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        });
        loader.load();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.default_menu, menu);
        selectedItemCount = menu.findItem(R.id.item_count);
        checkboxItem = menu.findItem(R.id.check);
        return true;
    }

    private void toggleSelectItem(boolean enabled){
        if(enabled){
            isCheckEnabled = true;
            checkboxItem.setVisible(true);
        }else{
            isCheckEnabled = false;
            ImageScanner.FoldStruct f = selectedFolder.get(0);
            selectedItemCount.setVisible(false);
            if(f != null){
                f.selectedIndexes.clear();
                mAdapterDetailed.notifyDataSetChanged();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.check:{
                toggleSelectItem(!isCheckEnabled);
                if(isCheckEnabled){
                    checkboxItem.setIcon(R.drawable.ic_check_box_white_24dp);
                }else{
                    checkboxItem.setIcon(R.drawable.ic_check_box_outline_blank_white_24dp);
                }
            } break;
            default: {
                if(selectedLevel == 0){
                    return super.onOptionsItemSelected(item);
                }else{
                    selectedLevel = 0;
                    toggleSelectItem(false);
                    checkboxItem.setVisible(false);
                    setTitle(activityTitle);
                    mGridView.setAdapter(mAdapter);
                }
            } break;
        } return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        ImageScanner.FoldStruct f;
        if(selectedLevel == 0) {
            f = folding.get(position);
            setTitle(f.folder);
            selectedFolder.clear();
            f.selectedIndexes.clear();
            selectedFolder.add(f);
            mGridView.setAdapter(mAdapterDetailed);
            selectedLevel = 1;
            isCheckEnabled = false;
            checkboxItem.setIcon(R.drawable.ic_check_box_outline_blank_white_24dp);
            checkboxItem.setVisible(true);
        }else{
            f = selectedFolder.get(0);
            if(f == null) return;
            if(v != null) {
                View checkbox = v.findViewById(R.id.checkbox);
                if(checkbox != null && isCheckEnabled)
                    if(checkbox.getVisibility() == View.GONE) {
                        checkbox.setVisibility(View.VISIBLE);
                        f.selectedIndexes.add(position);
                        selectedItemCount.setTitle(f.selectedIndexes.size()+"");
                        selectedItemCount.setVisible(true);
                    }else {
                        checkbox.setVisibility(View.GONE);
                        f.selectedIndexes.remove(Integer.valueOf(position));
                        if(f.selectedIndexes.size() == 0){
                            selectedItemCount.setVisible(false);
                        }else{
                            selectedItemCount.setTitle(f.selectedIndexes.size()+"");
                        }
                    }
                else {
                    Bundle bundle = new Bundle();
                    bundle.putStringArray("items",selectedFolder.get(0).fullPath);
                    bundle.putStringArray("names",selectedFolder.get(0).filename);
                    bundle.putInt("start",position);
                    startActivity(new Intent(MainActivity.this, ImageSlider.class).putExtras(bundle));
                }
            }
        }
    }
}
