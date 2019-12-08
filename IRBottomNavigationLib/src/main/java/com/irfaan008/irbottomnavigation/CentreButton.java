package com.irfaan008.irbottomnavigation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.MotionEvent;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * Created by Chatikyan on 10.11.2016.
 */

public class CentreButton extends FloatingActionButton {

    public CentreButton(Context context) {
        super(context);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        boolean result = super.onTouchEvent(ev);
        if (!result) {
            if (ev.getAction() == MotionEvent.ACTION_UP) {
                cancelLongPress();
            }
            setPressed(false);
        }
        return result;
    }
}
