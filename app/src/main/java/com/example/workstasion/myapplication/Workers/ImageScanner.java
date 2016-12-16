package com.example.workstasion.myapplication.Workers;

import android.media.Image;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by WORKSTASION on 25.09.2016.
 */

public class ImageScanner {

    private static final String TAG = "IMAGELOADER";
    private List<FoldStruct> folding;
    private static File startPath;
    private ScannerEvents scannerEvents;

    public interface ScannerEvents{
        public void loadDone();
        public void addPreview(String a,FoldStruct b);
    }

    public class FileInfo implements Comparable{
        public String name;
        public String  fullPath;
        public long modifyDate;

        @Override
        public int compareTo(Object o){
            long c = ((FileInfo)o).modifyDate;
            return c < modifyDate ? -1 : c == modifyDate ? 0 : 1;
        }
    }

    public class FoldStruct{

        FoldStruct(String r,String f){
            this.root = r;
            this.folder = f;
        }

        public String root;
        public String folder;
        public List<FileInfo> filesInfo = new ArrayList<>();
        public List<Integer> selectedIndexes = new ArrayList<>();
        public int counter = 0;
    }

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

    public List<FoldStruct> get(){
        return folding;
    }

    public ImageScanner(File startPath, ScannerEvents scannerEvents){
        this.startPath = startPath;
        this.scannerEvents = scannerEvents;
    }

    public void load(){
        folding = new ArrayList<>();
        LoadInBack loadInBack = new LoadInBack();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            loadInBack.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            loadInBack.execute();
        }
        if(startPath != null)
            new Thread(new Runnable() {
                @Override
                public void run() {
                    new deepThread(startPath,"root").start();
                }
            }).start();
    }

    private void process(){

        new Thread(new Runnable() {
            @Override
            public void run() {

                ImageScanner.this.scannerEvents.loadDone();
            }
        }).run();
    }

    private class LoadInBack extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {


            /*if(startPath != null)
                deep(startPath,"root");*/

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            ImageScanner.this.scannerEvents.loadDone();
        }
    }

    private String inPath = "";
    private class deepThread extends Thread{

        private File p;
        String parent;

        deepThread( File f, String r ){
            p = f;
            parent = r;
        }

        @Override
        public void run() {

            String[] fList = p.list(filter);
            if(fList == null) return;
            int size = fList.length;
            for (int i = 0 ; i < size; i++){
                final String current_path = fList[i];
                final File curr = new File(p,current_path);
                if(curr.isDirectory()){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            new deepThread( curr,parent+"|"+current_path ).start();
                        }
                    }).start();
                }else{
                    String[] p = parent.split("\\|");
                    if(checkAdd(p[p.length-1])){
                        inPath += "|"+p[p.length-1];
                        FileInfo info = new FileInfo();
                        info.fullPath = curr.getAbsolutePath();
                        info.modifyDate = curr.lastModified();
                        info.name = current_path;
                        FoldStruct _f = new FoldStruct(parent,parent);
                        _f.counter = 1;
                        _f.filesInfo.add(info);
                        ImageScanner.this.scannerEvents.addPreview(parent, _f );
                    }
                }
            }
        }
    }

    private synchronized boolean checkAdd(String s){
        Log.i(TAG, "checkAdd: "+inPath);
        return !inPath.contains(s);
    }

    private void dirPreview(File p, final String parent){
        String[] fList = p.list(filter);
        if(fList == null) return;
        int size = fList.length;
        for (int i = 0 ; i < size; i++){
            final String current_path = fList[i];
            final File curr = new File(p,current_path);
            if(curr.isDirectory()){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        dirPreview(curr,parent+"|"+current_path);
                    }
                }).start();
            }else{
                Log.i(TAG, "dirPreview: "+current_path);

            }
        }
    }

    private void deep(File p, final String parent ){
        String[] fList = p.list(filter);
        if(fList == null) return;
        String root = null, folder = null;
        FoldStruct foldStruct = null;
        int size = fList.length - 1;
        for (int i = size ; i >= 0; i--){
            final String curr_path = fList[i];
            final File curr = new File(p,curr_path);
            if(curr.isDirectory()){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        deep(curr,parent+"|"+curr_path);
                    }
                }).start();
            }else{
                boolean add = false;
                if(parent.equals("root")) {
                    root = folder = "root";
                    foldStruct = new FoldStruct(root,folder);
                    folding.add(foldStruct);
                }else if(root == null){
                    String[] parts = parent.split("\\|");
                    root = parts[1];
                    folder = parts[parts.length-1];
                    foldStruct = findByRootName(root,folder);
                    if(foldStruct == null){
                        add = true;
                        foldStruct = new FoldStruct(root,folder);
                        folding.add(foldStruct);
                    }
                }
                FileInfo info = new FileInfo();
                info.fullPath = curr.getAbsolutePath();
                info.modifyDate = curr.lastModified();
                info.name = fList[i];
                foldStruct.filesInfo.add(info);
                if(add){
                    FoldStruct _f = new FoldStruct(root,root);
                    _f.counter = 1;
                    _f.filesInfo.add(info);
                    ImageScanner.this.scannerEvents.addPreview(root, _f );
                }
                foldStruct.counter++;
                Log.i(TAG, parent + " deep: "+i);
            }
        }
    }

    private FoldStruct findByRootName(String root, String folder){
        for (FoldStruct f:folding){
            if(root.equals(f.root) && folder.equals(f.folder))
                return f;
        } return null;
    }
}
