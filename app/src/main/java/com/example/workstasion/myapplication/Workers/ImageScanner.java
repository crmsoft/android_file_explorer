package com.example.workstasion.myapplication.Workers;

import android.media.Image;
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
    }

    public class FoldStruct{

        FoldStruct(String r,String f){
            this.root = r;
            this.folder = f;
        }

        public String root;
        public String folder;
        public String[] fullPath;
        public String[] filename;
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
        process();
    }

    private void process(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(startPath != null)
                    deep(startPath,"root");

                ImageScanner.this.scannerEvents.loadDone();
            }
        }).run();
    }

    private void deep(File p, String parent ){
        String[] fList = p.list(filter);
        if(fList == null) return;
        String root = null, folder = null;
        FoldStruct foldStruct = null;
        int size = fList.length;
        for (int i = 0 ; i < size; i++){
            File curr = new File(p,fList[i]);
            if(curr.isDirectory()){
                deep(curr,parent+"|"+fList[i]);
            }else{
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
                        foldStruct = new FoldStruct(root,folder);
                        foldStruct.filename = new String[size];
                        foldStruct.fullPath = new String[size];
                        folding.add(foldStruct);
                    }
                }
                foldStruct.filename[foldStruct.counter] = fList[i];
                foldStruct.fullPath[foldStruct.counter++] = curr.getAbsolutePath();
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
