package com.github.fakegps.pokemon.widget;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.FrameLayout;

/**
 * Created by Gavin on 16-8-4.
 */
public abstract class FloatFrameLayout extends FrameLayout {

    protected WindowManager mWindowManager;
    protected WindowManager.LayoutParams mWindowLayoutParams;

    public FloatFrameLayout(Context context) {
        super(context);

        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mWindowLayoutParams = new WindowManager.LayoutParams();
    }

    protected void setupWindowLayoutParam() {
        mWindowLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        mWindowLayoutParams.format = PixelFormat.RGBA_8888;
        mWindowLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mWindowLayoutParams.gravity = Gravity.BOTTOM;
        mWindowLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
    }

    protected void addToWindow() {
        mWindowManager.addView(this, mWindowLayoutParams);
    }

    protected void removeFromWindow() {
        mWindowManager.removeView(this);
    }

    protected abstract int getWindowType();

    protected abstract int getWindowFlags();

    protected abstract int getWindowGravity();

    protected abstract int getWindowWidth();

    protected abstract int getWindowHeight();

    protected abstract int getWindowX();

    protected abstract int getWindowY();

}
