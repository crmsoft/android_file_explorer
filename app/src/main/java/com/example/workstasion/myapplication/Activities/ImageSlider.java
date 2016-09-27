package com.example.workstasion.myapplication.Activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.workstasion.myapplication.R;
import com.example.workstasion.myapplication.Workers.BitmapLoader;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ImageSlider extends AppCompatActivity {

    private static final String TAG = "IMAGESLIDER";
    private int position = 0;
    private String[] items;
    private String[] names;
    private PhotoView target;
    private int loadRetries = 1;
    private BitmapLoader loader;
    private PhotoViewAttacher attacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_slider);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDefaultDisplayHomeAsUpEnabled(true);
        }

        Bundle bundle = getIntent().getExtras();
        if(bundle == null){
            finish();return;
        }
        items = bundle.getStringArray("items");
        names = bundle.getStringArray("names");
        position = bundle.getInt("start");
        if(items == null || items.length == 0){
            finish();return;
        }

        loader = new BitmapLoader(getResources(),R.drawable.placeholder);
        loader.setLoadSizes(size.x,450);

        target = (PhotoView) findViewById(R.id.target);
        attacher = new PhotoViewAttacher(target);
        attacher.setOnSingleFlingListener(new PhotoViewAttacher.OnSingleFlingListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                float diff = e1.getX() - e2.getX();
                if(Math.abs(diff) > 150)
                    if(diff < 0){
                        if(position != 0) {
                            --position;
                            load();
                        }
                    }else{
                        if(position < (items.length-1)) {
                            ++position;
                            load();
                        }
                    } return true;
            }
        });

        if(items[position] != null)
            load();
    }

    private void load(){
        try {
            loader.loadBitmap(items[position],target);
            attacher.update();
        }catch (Exception ex){
            Toast.makeText(ImageSlider.this,"Can not dispaly an image: "+names[position],Toast.LENGTH_SHORT).show();
        }
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
}
