package com.example.workstasion.myapplication.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.workstasion.myapplication.Adapters.FolderAdapter;
import com.example.workstasion.myapplication.R;
import com.example.workstasion.myapplication.Workers.DirectoryComparator;
import com.example.workstasion.myapplication.Workers.Loader;
import com.example.workstasion.myapplication.Workers.Tasks;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Explore extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private List<Loader.DirectoryPreview> directoryPreviewList = new ArrayList<>();
    private FolderAdapter directoryAdapter;
    private GridView gridView;
    private File path = new File(Environment.getExternalStorageDirectory() + "");
    private ProgressBar progressBar;
    private DirectoryComparator comparator = new DirectoryComparator();
    private final int foldListActivity = 111;
    private ImageView rmBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridView = (GridView)findViewById(R.id.gridView);
        progressBar = (ProgressBar)findViewById(R.id.load_indicator);

        directoryAdapter = new FolderAdapter( this, directoryPreviewList );

        gridView = (GridView)findViewById(R.id.gridView);
        gridView.setAdapter( directoryAdapter );
        gridView.setOnItemClickListener(this);

        IntentFilter filter = new IntentFilter();
        filter.addAction("do_rm_files");
        registerReceiver(new requestDeleteItems(),filter);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.default_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.reload :{
                directoryPreviewList.clear();
                if (progressBar.isShown()) {
                    return false;
                }
                progressBar.setVisibility(View.VISIBLE);
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
            }break;
            default:{
                return super.onOptionsItemSelected(item);
            }
        } return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode){
            case foldListActivity :{
                if(data == null) return;
                Bundle b =  data.getExtras();
                if(b != null){
                    String action = b.getString("result");
                    if(action.equals("remove")){
                        Loader.DirectoryPreview d  = directoryPreviewList.get( b.getInt("position") );
                        if(d != null){
                            List<Integer> removeItems = new ArrayList<>();
                            removeItems.add(Integer.valueOf(b.getInt("position")));
                            String deleteAlso = "";
                           for(Loader.DirectoryPreview preview : directoryPreviewList){
                                if( !d.getName().equals(preview.getName()) && preview.getDirPath().contains( d.getName() ) ){
                                    deleteAlso += ","+preview.getName();
                                    removeItems.add(Integer.valueOf(directoryPreviewList.indexOf(preview)));
                                }
                            }
                            Resources r = getResources();
                            String ask = deleteAlso.length() == 0 ? r.getString( R.string.delete_dir, d.getName() ) : r.getString( R.string.delete_dir_and_sub, d.getName(), deleteAlso.substring( 1, deleteAlso.length() ) );
                            confirmRemoveDirectory( ask, d.getName(), removeItems );
                        }
                    }
                }
            }
        }

    }

    private void confirmRemoveDirectory(String msg, final String target, final List<Integer> r_indexes){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        Toast.makeText(getApplicationContext(),getResources().getString(R.string.delete_progress,target),Toast.LENGTH_SHORT).show();
                        Collections.sort(r_indexes, Collections.reverseOrder());
                        for(int i : r_indexes){
                            Loader.DirectoryPreview curr = directoryPreviewList.get(i);
                            if(curr != null && Tasks.rmDir( curr.getDirPath() )){
                                directoryPreviewList.remove( curr );
                            }
                        } directoryAdapter.notifyDataSetChanged();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.cancel();
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));
        builder.setMessage(msg).setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    class requestDeleteItems extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent == null)return;
            if(intent.getAction().equals("do_rm_files")) {
                Bundle b = intent.getExtras();
                if(b != null) {
                    ArrayList<Integer> indexes = b.getIntegerArrayList("items");
                    ArrayList<Integer> oks = new ArrayList<>();
                    int pos = b.getInt("position");
                    Loader.DirectoryPreview d = directoryPreviewList.get(pos);
                    if(d != null) {
                        Collections.sort(indexes, Collections.reverseOrder());
                        for(Integer i : indexes){
                            if (Tasks.rmDir(d.getItems()[i])) {
                                oks.add(i);
                            }
                        }
                        d.setItems( Tasks.removeArrayElements( d.getItems(), oks ) );
                        d.setItemNames( Tasks.removeArrayElements( d.getItemNames(), oks ) );
                        directoryPreviewList.remove(d);
                        if(d.getItems().length > 0 ) {
                            d.setPreview(d.getItems()[0]);
                        }
                        // notify
                        Intent i = new Intent("delete_items_done");
                        Bundle response = new Bundle();
                        response.putStringArray( "items", d.getItems() );
                        response.putStringArray( "names", d.getItemNames() );
                        i.putExtras(response);
                        sendBroadcast(i);
                    }
                }
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Loader.DirectoryPreview pr = directoryPreviewList.get( position );
        Bundle b = new Bundle();
        b.putStringArray( "items", pr.getItems() );
        b.putStringArray( "names", pr.getItemNames() );
        b.putInt("position",position);
        Intent i = new Intent(Explore.this,FolderContent.class);
        i.putExtras(b);
        startActivityForResult(i,foldListActivity);
    }
}
