package com.example.workstasion.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static final String TAG = "MAINACTIVITY";
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1453;
    private ImageAdapter mAdapter;
    private Bitmap mPlaceHolderBitmap;

    // A static dataset to back the GridView adapter
    public final static Integer[] imageResIds = new Integer[] {
            R.drawable.ic_account_circle_black_24dp, R.drawable.ic_done_white_18dp, R.drawable.ic_more_horiz_white_18dp,
            R.drawable.ic_account_circle_black_24dp, R.drawable.ic_done_white_18dp, R.drawable.ic_more_horiz_white_18dp,
            R.drawable.ic_account_circle_black_24dp, R.drawable.ic_done_white_18dp, R.drawable.ic_more_horiz_white_18dp,
            R.drawable.ic_account_circle_black_24dp, R.drawable.ic_done_white_18dp, R.drawable.ic_more_horiz_white_18dp,
            R.drawable.ic_account_circle_black_24dp, R.drawable.ic_done_white_18dp, R.drawable.ic_more_horiz_white_18dp,
            R.drawable.ic_account_circle_black_24dp, R.drawable.ic_done_white_18dp, R.drawable.ic_more_horiz_white_18dp,
            R.drawable.ic_account_circle_black_24dp, R.drawable.ic_done_white_18dp, R.drawable.ic_more_horiz_white_18dp,
            R.drawable.ic_account_circle_black_24dp, R.drawable.ic_done_white_18dp, R.drawable.ic_more_horiz_white_18dp,
            R.drawable.ic_account_circle_black_24dp, R.drawable.ic_done_white_18dp, R.drawable.ic_more_horiz_white_18dp,
            R.drawable.ic_account_circle_black_24dp, R.drawable.ic_done_white_18dp, R.drawable.ic_more_horiz_white_18dp,
            R.drawable.ic_account_circle_black_24dp, R.drawable.ic_done_white_18dp, R.drawable.ic_more_horiz_white_18dp,
            R.drawable.ic_account_circle_black_24dp, R.drawable.ic_done_white_18dp, R.drawable.ic_more_horiz_white_18dp,
            R.drawable.ic_account_circle_black_24dp, R.drawable.ic_done_white_18dp, R.drawable.ic_more_horiz_white_18dp,
            R.drawable.ic_account_circle_black_24dp, R.drawable.ic_done_white_18dp, R.drawable.ic_more_horiz_white_18dp,
            R.drawable.ic_account_circle_black_24dp, R.drawable.ic_done_white_18dp, R.drawable.ic_more_horiz_white_18dp,
            R.drawable.ic_account_circle_black_24dp, R.drawable.ic_done_white_18dp, R.drawable.ic_more_horiz_white_18dp,
            R.drawable.ic_account_circle_black_24dp, R.drawable.ic_done_white_18dp, R.drawable.ic_more_horiz_white_18dp,
            R.drawable.ic_account_circle_black_24dp, R.drawable.ic_done_white_18dp, R.drawable.ic_more_horiz_white_18dp,
            R.drawable.ic_account_circle_black_24dp, R.drawable.ic_done_white_18dp, R.drawable.ic_more_horiz_white_18dp,
            R.drawable.ic_account_circle_black_24dp, R.drawable.ic_done_white_18dp, R.drawable.ic_more_horiz_white_18dp,
            R.drawable.ic_account_circle_black_24dp, R.drawable.ic_done_white_18dp, R.drawable.ic_more_horiz_white_18dp,
            R.drawable.ic_account_circle_black_24dp, R.drawable.ic_done_white_18dp, R.drawable.ic_more_horiz_white_18dp,
            R.drawable.ic_account_circle_black_24dp, R.drawable.ic_done_white_18dp, R.drawable.ic_more_horiz_white_18dp,
            R.drawable.ic_account_circle_black_24dp, R.drawable.ic_done_white_18dp, R.drawable.ic_more_horiz_white_18dp,
            R.drawable.ic_account_circle_black_24dp, R.drawable.ic_done_white_18dp, R.drawable.ic_more_horiz_white_18dp,
            R.drawable.ic_account_circle_black_24dp, R.drawable.ic_done_white_18dp, R.drawable.ic_more_horiz_white_18dp,
            R.drawable.ic_account_circle_black_24dp, R.drawable.ic_done_white_18dp, R.drawable.ic_more_horiz_white_18dp,
            R.drawable.ic_account_circle_black_24dp, R.drawable.ic_done_white_18dp, R.drawable.ic_more_horiz_white_18dp,
            R.drawable.ic_account_circle_black_24dp, R.drawable.ic_done_white_18dp, R.drawable.ic_more_horiz_white_18dp,
            R.drawable.ic_account_circle_black_24dp, R.drawable.ic_done_white_18dp, R.drawable.ic_more_horiz_white_18dp,
            R.drawable.ic_account_circle_black_24dp, R.drawable.ic_done_white_18dp, R.drawable.ic_more_horiz_white_18dp,
            R.drawable.ic_account_circle_black_24dp, R.drawable.ic_done_white_18dp, R.drawable.ic_more_horiz_white_18dp,
            R.drawable.ic_account_circle_black_24dp, R.drawable.ic_done_white_18dp, R.drawable.ic_more_horiz_white_18dp,
            R.drawable.ic_account_circle_black_24dp, R.drawable.ic_done_white_18dp, R.drawable.ic_more_horiz_white_18dp,
            R.drawable.ic_account_circle_black_24dp, R.drawable.ic_done_white_18dp, R.drawable.ic_more_horiz_white_18dp,
            R.drawable.ic_account_circle_black_24dp, R.drawable.ic_done_white_18dp, R.drawable.ic_more_horiz_white_18dp,
            R.drawable.ic_account_circle_black_24dp, R.drawable.ic_done_white_18dp, R.drawable.ic_more_horiz_white_18dp,
            R.drawable.ic_account_circle_black_24dp, R.drawable.ic_done_white_18dp, R.drawable.ic_more_horiz_white_18dp,
            R.drawable.ic_account_circle_black_24dp, R.drawable.ic_done_white_18dp, R.drawable.ic_more_horiz_white_18dp,
            R.drawable.ic_account_circle_black_24dp, R.drawable.ic_done_white_18dp, R.drawable.ic_more_horiz_white_18dp,
            R.drawable.ic_account_circle_black_24dp, R.drawable.ic_done_white_18dp, R.drawable.ic_more_horiz_white_18dp,
            R.drawable.ic_account_circle_black_24dp, R.drawable.ic_done_white_18dp, R.drawable.ic_more_horiz_white_18dp,
            R.drawable.ic_account_circle_black_24dp, R.drawable.ic_done_white_18dp, R.drawable.ic_more_horiz_white_18dp,
            R.drawable.ic_account_circle_black_24dp, R.drawable.ic_done_white_18dp, R.drawable.ic_more_horiz_white_18dp,
            R.drawable.ic_account_circle_black_24dp, R.drawable.ic_done_white_18dp, R.drawable.ic_more_horiz_white_18dp,
            R.drawable.ic_account_circle_black_24dp, R.drawable.ic_done_white_18dp, R.drawable.ic_more_horiz_white_18dp,
            R.drawable.ic_account_circle_black_24dp, R.drawable.ic_done_white_18dp, R.drawable.ic_more_horiz_white_18dp,
            R.drawable.ic_account_circle_black_24dp, R.drawable.ic_done_white_18dp, R.drawable.ic_more_horiz_white_18dp,
            R.drawable.ic_account_circle_black_24dp, R.drawable.ic_done_white_18dp, R.drawable.ic_more_horiz_white_18dp,
            R.drawable.ic_account_circle_black_24dp, R.drawable.ic_done_white_18dp, R.drawable.ic_more_horiz_white_18dp,
            R.drawable.ic_account_circle_black_24dp, R.drawable.ic_done_white_18dp, R.drawable.ic_more_horiz_white_18dp,
            R.drawable.ic_account_circle_black_24dp, R.drawable.ic_done_white_18dp, R.drawable.ic_more_horiz_white_18dp,
            R.drawable.ic_account_circle_black_24dp, R.drawable.ic_done_white_18dp, R.drawable.ic_more_horiz_white_18dp,
            R.drawable.ic_account_circle_black_24dp, R.drawable.ic_done_white_18dp, R.drawable.ic_more_horiz_white_18dp,
            R.drawable.ic_account_circle_black_24dp, R.drawable.ic_done_white_18dp, R.drawable.ic_more_horiz_white_18dp,
            R.drawable.ic_account_circle_black_24dp, R.drawable.ic_done_white_18dp, R.drawable.ic_more_horiz_white_18dp,
            R.drawable.ic_account_circle_black_24dp, R.drawable.ic_done_white_18dp, R.drawable.ic_more_horiz_white_18dp,
            R.drawable.ic_account_circle_black_24dp, R.drawable.ic_done_white_18dp, R.drawable.ic_more_horiz_white_18dp,
            R.drawable.ic_account_circle_black_24dp, R.drawable.ic_done_white_18dp, R.drawable.ic_more_horiz_white_18dp,
            R.drawable.ic_account_circle_black_24dp, R.drawable.ic_done_white_18dp, R.drawable.ic_more_horiz_white_18dp,
            R.drawable.ic_account_circle_black_24dp, R.drawable.ic_done_white_18dp, R.drawable.ic_more_horiz_white_18dp
    };

    private File path = new File(Environment.getExternalStorageDirectory() + "");
    private List<FoldStruct> folding = new ArrayList<>();


    private class FoldStruct{

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

    private class LoadCandidate{
        private int path;
        private int id;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAdapter = new ImageAdapter(this);

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                MY_PERMISSIONS_REQUEST_READ_CONTACTS);

        findViewById(R.id.go).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (FoldStruct s:folding){
                    //if(s == null) continue;
                    Log.d("Result", s.root+" **** "+s.folder);
                    for (String p : s.fullPath){
                        if(p == null) break;
                        Log.i("Result1", p);
                    }
                }
            }
        });

        deep(path,"root");
        /*final GridView mGridView = (GridView) findViewById(R.id.gridView);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(this);
        mPlaceHolderBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.ic_done_white_18dp);*/
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        Log.i(TAG, "onItemClick: "+position+" item was clicked");
    }

    private FoldStruct findByRootName(String root, String folder){
        for (FoldStruct f:folding){
            if(root.equals(f.root) && folder.equals(f.folder))
                return f;
        } return null;
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


    private class ImageAdapter extends BaseAdapter {
        private final Context mContext;

        public ImageAdapter(Context context) {
            super();
            mContext = context;
        }

        @Override
        public int getCount() {
            return imageResIds.length;
        }

        @Override
        public Object getItem(int position) {
            return imageResIds[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            ImageView imageView;
            if (convertView == null) { // if it's not recycled, initialize some attributes
                imageView = new SquareImageView(mContext);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setLayoutParams(new GridView.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            } else {
                imageView = (SquareImageView) convertView;
            }
            loadBitmap(imageResIds[position],imageView);
            return imageView;
        }
    }


    public void loadBitmap(int resId, ImageView imageView) {
        if (cancelPotentialWork(resId, imageView)) {
            final BitmapWorkerTask task = new BitmapWorkerTask(imageView);
            final AsyncDrawable asyncDrawable =
                    new AsyncDrawable(getResources(), mPlaceHolderBitmap, task);
            imageView.setImageDrawable(asyncDrawable);
            task.execute(resId);
        }
    }

    static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap,
                             BitmapWorkerTask bitmapWorkerTask) {
            super(res, bitmap);
            bitmapWorkerTaskReference =
                    new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
        }

        public BitmapWorkerTask getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }
    }

    public static boolean cancelPotentialWork(int data, ImageView imageView) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            final int bitmapData = bitmapWorkerTask.data;
            if (bitmapData != data) {
                // Cancel previous task
                bitmapWorkerTask.cancel(true);
            } else {
                // The same work is already in progress
                return false;
            }
        }
        // No task associated with the ImageView, or an existing task was cancelled
        return true;
    }

    private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        private int data = 0;

        public BitmapWorkerTask(ImageView imageView) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(Integer... params) {
            data = params[0];
            return decodeSampledBitmapFromResource(getResources(), data, 100, 100);
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }

            if (imageViewReference != null && bitmap != null) {
                final ImageView imageView = imageViewReference.get();
                final BitmapWorkerTask bitmapWorkerTask =
                        getBitmapWorkerTask(imageView);
                if (this == bitmapWorkerTask && imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }


}
