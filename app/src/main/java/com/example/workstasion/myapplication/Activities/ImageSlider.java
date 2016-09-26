package com.example.workstasion.myapplication.Activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toolbar;

import com.example.workstasion.myapplication.R;

public class ImageSlider extends AppCompatActivity implements View.OnClickListener {

    private int position = 0;
    private String[] items;
    private String[] names;
    private ImageView target;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_slider);

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
        target = (ImageView) findViewById(R.id.target);
        target.setOnClickListener(this);

        if(items[position] != null)
            load();
    }

    private void load(){
        Bitmap b = BitmapFactory.decodeFile(items[position]);
        target.setImageBitmap(b);
        setTitle(names[position]);
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
    public void onClick(View v) {

        ++position;
        if(position >= items.length){
            position = 0;
        }

        if(items[position] != null)
            load();

    }
}
