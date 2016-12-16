package com.example.workstasion.myapplication.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.example.workstasion.myapplication.Adapters.FolderContentAdapter;
import com.example.workstasion.myapplication.R;

import java.util.ArrayList;
import java.util.Arrays;

public class FolderContent extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener{

    private GridView gridView;
    private ProgressBar progressBar;
    private FolderContentAdapter adapter;
    private String[] items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Bundle b = getIntent().getExtras();

        items = b.getStringArray("items");

        if(items == null || items.length == 0) {
            finish();
            return;
        }

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDefaultDisplayHomeAsUpEnabled(true);
        }
        gridView = (GridView)findViewById(R.id.gridView);
        progressBar = (ProgressBar)findViewById(R.id.load_indicator);
        progressBar.setVisibility(View.INVISIBLE);

        adapter = new FolderContentAdapter(this, new ArrayList<String>(Arrays.asList(items)));
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(this);
        gridView.setOnItemLongClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.slider_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Bundle bundle = new Bundle();
        bundle.putStringArray("items",items);
        bundle.putStringArray("names",items);
        bundle.putInt("start",position);
        bundle.putInt("total",items.length);
        startActivity(new Intent(FolderContent.this, ImageSlider.class).putExtras(bundle));
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        RelativeLayout r = (RelativeLayout) view.findViewById(R.id.checkbox);
        if(r != null){
            r.setVisibility( View.VISIBLE );
            return true;
        }

        return false;
    }
}
