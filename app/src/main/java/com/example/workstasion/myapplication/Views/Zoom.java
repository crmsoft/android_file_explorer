package com.example.workstasion.myapplication.Views;

import android.content.Context;
import android.text.method.Touch;
import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Created by WORKSTASION on 27.09.2016.
 */

public class Zoom extends GestureDetector {
    public Zoom(Context context, OnGestureListener listener) {
        super(context, listener);
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return super.onTouchEvent(ev);
    }
}
