package com.example.workstasion.myapplication.Activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.workstasion.myapplication.Adapters.Folder;
import com.example.workstasion.myapplication.R;
import com.example.workstasion.myapplication.Workers.DirectoryComparator;
import com.example.workstasion.myapplication.Workers.Loader;
import com.example.workstasion.myapplication.Workers.Tasks;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Explore extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private List<Loader.DirectoryPreview> directoryPreviewList = new ArrayList<>();
    private Folder directoryAdapter;
    private GridView gridView;
    private File path = new File(Environment.getExternalStorageDirectory() + "");
    private ProgressBar progressBar;
    private DirectoryComparator comparator = new DirectoryComparator();
    private final int foldListActivity = 111;

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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Loader.DirectoryPreview pr = directoryPreviewList.get( position );
        Bundle b = new Bundle();
        b.putStringArray( "items", pr.getItems() );
        b.putInt("position",position);
        Intent i = new Intent(Explore.this,FolderContent.class);
        i.putExtras(b);
        startActivityForResult(i,foldListActivity);
    }
}
