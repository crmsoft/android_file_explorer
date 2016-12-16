package com.example.workstasion.myapplication.Workers;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;

/**
 * Created by WORKSTASION on 15.12.2016.
 */

public class Loader implements Runnable {

    private final String TAG = "Loader";
    private File startPoint;
    private Communicate listener;
    private FilenameFilter filter = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String filename) {
            File sel = new File(dir, filename);
            // Filters based on whether the file is hidden or not
            filename = filename.toLowerCase();
            return ((filename.contains(".png")
                    || filename.contains(".jpeg")
                    || filename.contains(".jpg")
                    || filename.contains(".gif")) || sel.isDirectory())
                    && !sel.isHidden();
        }
    };

    public interface Communicate{
        public void newDir( DirectoryPreview preview );
        public void done();
    }

    public Loader( File root, Communicate listener ){
        this.startPoint = root;
        this.listener = listener;
    }

    public class DirectoryPreview {
        private String name;
        private String upDirName;
        private Bitmap preview;
        private Long lastModified;
        private int count = 0;
        private String[] items;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void increment(){
            this.count++;
        }

        public int getCount() {
            return count;
        }

        public String getUpDirName() {
            return upDirName;
        }

        public void setUpDirName(String upDirName) {
            this.upDirName = upDirName;
        }

        public Long getLastModified() {
            return lastModified;
        }

        public void setLastModified(Long lastModified) {
            this.lastModified = lastModified;
        }

        public String[] getItems() {
            return items;
        }

        public void setItems(String[] items) {
            this.items = items;
        }

        private class LoadInBack extends AsyncTask<String, Void, Bitmap> {

            @Override
            protected Bitmap doInBackground(String... params) {

                String p = params[0];

                if (p != null) {
                    return Tasks.decodeSampledBitmapFromResource(p, 75, 75);
                }

                return null;
            }

            @Override
            protected void onPostExecute(Bitmap b) {
                if (b != null) {
                    DirectoryPreview.this.preview = b;
                    Loader.this.listener.newDir( DirectoryPreview.this );
                }
            }
        }

        public void setPreview(String path) {
            LoadInBack loadInBack = new LoadInBack();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                loadInBack.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, path);
            } else {
                loadInBack.execute(path);
            }
        }

        public Bitmap getPreview() {
            return preview;
        }

    }

    @Override
    public void run() {
        this.doWork( this.startPoint, this.startPoint.getName() );
        this.listener.done();
    }

    private void doWork(File d, String parent){

        DirectoryPreview directoryPreview = new DirectoryPreview();
        String[] fList = d.list(filter);
        if(fList == null) return;
        int size = fList.length;
        String[] tmp = new String[size];
        for (int i = size - 1; i >= 0; i--){
            final File curr = new File(d,fList[i]);
            if(curr.isDirectory()){
                doWork(curr, d.getName());
            } else {
                tmp[ directoryPreview.getCount() ] = curr.getPath();
                if(directoryPreview.name == null) {
                    directoryPreview.setName(d.getName());
                    directoryPreview.setPreview(curr.getPath());
                    directoryPreview.setUpDirName( parent );
                    directoryPreview.setLastModified( d.lastModified() );
                } directoryPreview.increment();
            }
        }

        if(directoryPreview.getCount() != tmp.length){
            directoryPreview.setItems( Arrays.copyOfRange(tmp,0,directoryPreview.getCount()) );
        }else{
            directoryPreview.setItems( tmp );
        }
    }
}
