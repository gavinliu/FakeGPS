package com.github.fakegps.pokemon.joystick;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.github.fakegps.pokemon.R;
import com.github.fakegps.pokemon.model.LocationPoint;
import com.github.fakegps.pokemon.widget.FloatFrameLayout;

/**
 * Created by Gavin on 16-8-4.
 */
public class SettingPanel extends FloatFrameLayout implements IJoyStickView {

    private boolean mIsShowing = false;


    public SettingPanel(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.setting_layout, this);

        setupWindowLayoutParam();

        findViewById(R.id.cancel).setOnClickListener(mOnClickListener);
    }

    private View.OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.cancel:
                    hide();
                    break;
            }
        }
    };

    @Override
    protected void setupWindowLayoutParam() {
        mWindowLayoutParams = new WindowManager.LayoutParams();
        mWindowLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        mWindowLayoutParams.format = PixelFormat.RGBA_8888;
        mWindowLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mWindowLayoutParams.gravity = Gravity.TOP | Gravity.START;
        mWindowLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        mWindowLayoutParams.height = 1500;
        mWindowLayoutParams.y = 50;
    }

    @Override
    protected int getWindowType() {
        return 0;
    }

    @Override
    protected int getWindowFlags() {
        return 0;
    }

    @Override
    protected int getWindowGravity() {
        return 0;
    }

    @Override
    protected int getWindowWidth() {
        return WindowManager.LayoutParams.MATCH_PARENT;
    }

    @Override
    protected int getWindowHeight() {
        return 0;
    }

    @Override
    protected int getWindowX() {
        return 0;
    }

    @Override
    protected int getWindowY() {
        return 0;
    }

    @Override
    public void show() {
        if (!isShowing()) {
            mWindowManager.addView(this, mWindowLayoutParams);
            mIsShowing = true;
        }
    }

    @Override
    public void hide() {
        if (isShowing()) {
            mWindowManager.removeView(this);
            mIsShowing = false;
        }
    }

    @Override
    public boolean isShowing() {
        return mIsShowing;
    }

    @Override
    public void setJoyStickPresenter(IJoyStickPresenter joyStickPresenter) {

    }

    @Override
    public void updateLocationPoint(LocationPoint locationPoint) {

    }
}
