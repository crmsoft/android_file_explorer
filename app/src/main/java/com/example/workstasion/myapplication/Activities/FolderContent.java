package com.example.workstasion.myapplication.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
    private ArrayList<String> items;
    private ArrayList<String> names;
    private List<Integer> selectedIndexes = new ArrayList<>();
    private MenuItem removeSelectedBtn;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDefaultDisplayHomeAsUpEnabled(true);
        }

        Intent i = getIntent();

        if(i == null)
            return;

        Bundle b = i.getExtras();
        String[] tmp = b.getStringArray("items");
        String[] tmpNames = b.getStringArray("names");
        position = b.getInt("position");

        if(tmp == null || tmp.length == 0) {
            finish();
            return;
        }

        items = new ArrayList<String>( Arrays.asList(tmp));
        names = new ArrayList<String>( Arrays.asList(tmpNames));
        gridView = (GridView)findViewById(R.id.gridView);
        progressBar = (ProgressBar)findViewById(R.id.load_indicator);
        progressBar.setVisibility(View.INVISIBLE);

        adapter = new FolderContentAdapter(this, items);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(this);
        gridView.setOnItemLongClickListener(this);

        IntentFilter filter = new IntentFilter();
        filter.addAction("delete_items_done");
        registerReceiver(new itemDeletionDone(),filter);
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
        int id = item.getItemId();
        switch (id){
            case R.id.rm_folder:{
                Intent returnIntent = new Intent();
                Bundle b = new Bundle();
                b.putString("result","remove");
                b.putInt("position",position);
                returnIntent.putExtras(b);
                setResult(RESULT_OK,returnIntent);
                finish();
            } break;
            case R.id.item_rm_selected:{
                Intent i = new Intent("do_rm_files");
                Bundle b = new Bundle();
                b.putIntegerArrayList("items",new ArrayList<Integer>(selectedIndexes));
                b.putInt("position",position);
                i.putExtras(b);
                sendBroadcast(i);
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        } return true;
    }

    class itemDeletionDone extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent == null)return;
            if(intent.getAction().equals("delete_items_done")){
                Bundle b = intent.getExtras();
                if(b != null){
                    selectedIndexes.clear();
                    adapter.setSelectedItems(selectedIndexes);
                    items.clear();
                    names.clear();
                    items.addAll( new ArrayList<String>( Arrays.asList( b.getStringArray("items") ) ));
                    names.addAll( new ArrayList<String>( Arrays.asList( b.getStringArray("names") ) ));
                    adapter.notifyDataSetChanged();
                }
            }
        }
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
            bundle.putStringArray("items", items.toArray(new String[items.size()]));
            bundle.putStringArray("names", names.toArray(new String[names.size()]));
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
