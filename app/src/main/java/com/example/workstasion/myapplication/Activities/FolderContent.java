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
import java.util.List;

public class FolderContent extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener{

    private final String TAG = "FolderContent";
    private GridView gridView;
    private ProgressBar progressBar;
    private FolderContentAdapter adapter;
    private String[] items;
    private List<Integer> selectedIndexes = new ArrayList<>();
    private MenuItem removeSelectedBtn;

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
        inflater.inflate(R.menu.folder_content_menu, menu);
        removeSelectedBtn = menu.findItem(R.id.item_rm_selected);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(selectedIndexes.size() > 0) {
            RelativeLayout r = (RelativeLayout) view.findViewById(R.id.checkbox);
            if (r != null) {
                if(r.getVisibility() == View.VISIBLE){
                    r.setVisibility(View.INVISIBLE );
                    selectedIndexes.remove( selectedIndexes.indexOf( Integer.valueOf(position) ) );
                }else{
                    r.setVisibility(View.VISIBLE);
                    selectedIndexes.add(Integer.valueOf(position));
                } adapter.setSelectedItems( selectedIndexes );
            }
        }else {
            Bundle bundle = new Bundle();
            bundle.putStringArray("items", items);
            bundle.putStringArray("names", items);
            bundle.putInt("start", position);
            startActivity(new Intent(FolderContent.this, ImageSlider.class).putExtras(bundle));
        } removeSelectedBtn.setVisible( selectedIndexes.size() > 0 );
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        RelativeLayout r = (RelativeLayout) view.findViewById(R.id.checkbox);
        if(r != null){
            r.setVisibility( View.VISIBLE );
            selectedIndexes.add( Integer.valueOf(position) );
            removeSelectedBtn.setVisible( true );
            adapter.setSelectedItems( selectedIndexes );
            return true;
        }

        return false;
    }
}
