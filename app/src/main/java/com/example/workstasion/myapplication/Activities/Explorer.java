package com.example.workstasion.myapplication.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.workstasion.myapplication.Adapters.FolderAdapter;
import com.example.workstasion.myapplication.Adapters.ImageAdapter;
import com.example.workstasion.myapplication.R;
import com.example.workstasion.myapplication.Workers.ImageScanner;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Explorer extends AppCompatActivity implements AdapterView.OnItemClickListener {

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
    private MenuItem doneMenuItem;
    private boolean isCheckEnabled = true;
    private ProgressBar progressBar;
    private int scroll = 0;

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

        ActivityCompat.requestPermissions(Explorer.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                MY_PERMISSIONS_REQUEST_READ_CONTACTS);

        mGridView = (GridView) findViewById(R.id.gridView);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(this);
        load();
    }

    private void load(){
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS : {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    load();
                }else{
                    ActivityCompat.requestPermissions(Explorer.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                }
            } break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.default_menu, menu);
        selectedItemCount = menu.findItem(R.id.item_count);
        checkboxItem = menu.findItem(R.id.check);
        doneMenuItem = menu.findItem(R.id.done);
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
            doneMenuItem.setVisible(false);
            if(f != null){
                f.selectedIndexes.clear();
                clearSelectionOfVisibleItems();
            }
        }
    }

    private void clearSelectionOfVisibleItems(){
        int f = mGridView.getFirstVisiblePosition();
        int l = mGridView.getLastVisiblePosition();
        for(int i=(l-f); i>=0; i-- ){
            View view = mGridView.getChildAt(i);
            if(view != null){
                view.findViewById(R.id.checkbox).setVisibility(View.GONE);
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
            case R.id.done : {
                Intent results = new Intent();
                Bundle bundle = new Bundle();
                ImageScanner.FoldStruct f = selectedFolder.get(0);
                if(f != null) {
                    String[] res = new String[f.selectedIndexes.size()];
                    int index = 0;
                    for (Integer i:f.selectedIndexes){
                       // res[index] = f.fullPath[index++];
                    }
                    bundle.putStringArray("results",res);
                }
                results.putExtras(bundle);
                setResult(Activity.RESULT_OK,results);
                finish();
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
                    mGridView.setSelection(scroll);
                }
            } break;
        } return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        ImageScanner.FoldStruct f;
        if(selectedLevel == 0) {
            f = folding.get(position);
            Collections.sort(f.filesInfo);
            setTitle(f.folder);
            selectedFolder.clear();
            f.selectedIndexes.clear();
            selectedFolder.add(f);
            scroll = mGridView.getFirstVisiblePosition();
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
                        doneMenuItem.setVisible(true);
                        selectedItemCount.setVisible(true);
                    }else {
                        checkbox.setVisibility(View.GONE);
                        f.selectedIndexes.remove(Integer.valueOf(position));
                        if(f.selectedIndexes.size() == 0){
                            doneMenuItem.setVisible(false);
                            selectedItemCount.setVisible(false);
                        }else{
                            selectedItemCount.setTitle(f.selectedIndexes.size()+"");
                        }
                    }
                else {
                    String[] pathArray = new String[f.filesInfo.size()];
                    String[] namesArray = new String[f.filesInfo.size()];
                    int i = 0;

                    for(ImageScanner.FileInfo info:f.filesInfo){
                        pathArray[i] = info.fullPath;
                        namesArray[i] = info.name;
                        ++i;
                    }

                    Bundle bundle = new Bundle();
                    bundle.putStringArray("items",pathArray);
                    bundle.putStringArray("names",namesArray);
                    bundle.putInt("start",position);
                    bundle.putInt("total",f.filesInfo.size());
                    startActivity(new Intent(Explorer.this, ImageSlider.class).putExtras(bundle));
                }
            }
        }
    }
}
