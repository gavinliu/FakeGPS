package com.github.fakegps.pokemon.joystick;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.github.fakegps.pokemon.R;
import com.github.fakegps.pokemon.model.LocationPoint;
import com.github.fakegps.pokemon.util.FakeGpsUtils;
import com.github.fakegps.pokemon.widget.FloatFrameLayout;

/**
 * Created by tiger on 7/22/16.
 */
public class PokemonGoJoyStick extends FloatFrameLayout implements IJoyStickView {

    private int mViewHeight;

    private boolean mIsShowing = false;

    private IJoyStickPresenter mJoyStickPresenter;

    private TextView mStatusTextView;

    private SettingPanel mSettingPanel;

    public PokemonGoJoyStick(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.joystick_layout, this);

        mStatusTextView = (TextView) findViewById(R.id.status);
        findViewById(R.id.btn_up).setOnClickListener(mOnClickListener);
        findViewById(R.id.btn_left).setOnClickListener(mOnClickListener);
        findViewById(R.id.btn_right).setOnClickListener(mOnClickListener);
        findViewById(R.id.btn_down).setOnClickListener(mOnClickListener);
        findViewById(R.id.btn_setting).setOnClickListener(mOnClickListener);

        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mViewHeight = context.getResources().getDimensionPixelSize(R.dimen.joystick_height);

        mSettingPanel = new SettingPanel(context);
    }

    @Override
    protected int getWindowFlags() {
        return WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
    }

    @Override
    protected int getWindowGravity() {
        return Gravity.BOTTOM;
    }

    @Override
    protected int getWindowWidth() {
        return WindowManager.LayoutParams.MATCH_PARENT;
    }

    @Override
    protected int getWindowHeight() {
        return mViewHeight;
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
    public boolean isShowing() {
        return mIsShowing;
    }

    @Override
    public void show() {
        if (!isShowing()) {
            super.addToWindow();
            mIsShowing = true;
        }
    }

    @Override
    public void hide() {
        if (isShowing()) {
            super.removeFromWindow();
            mIsShowing = false;
        }
    }

    @Override
    public void setJoyStickPresenter(IJoyStickPresenter joyStickPresenter) {
        mJoyStickPresenter = joyStickPresenter;
        if (mSettingPanel != null) mSettingPanel.setJoyStickPresenter(joyStickPresenter);
    }

    @Override
    public void updateLocationPoint(LocationPoint locationPoint) {
        if (locationPoint == null) {
            return;
        }
        if (mSettingPanel != null) mSettingPanel.updateLocationPoint(locationPoint);

        final String status = "纬度: " + FakeGpsUtils.scaleDouble(5, locationPoint.getLatitude()) + " \n经度: "
                + FakeGpsUtils.scaleDouble(5, locationPoint.getLongitude());

        mHandler.post(new Runnable() {

            @Override
            public void run() {
                mStatusTextView.setText(status);
            }
        });

    }

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private View.OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.btn_up:
                    if (mJoyStickPresenter != null) mJoyStickPresenter.onArrowUpClick();
                    break;

                case R.id.btn_down:
                    if (mJoyStickPresenter != null) mJoyStickPresenter.onArrowDownClick();

                    break;
                case R.id.btn_left:
                    if (mJoyStickPresenter != null) mJoyStickPresenter.onArrowLeftClick();
                    break;

                case R.id.btn_right:
                    if (mJoyStickPresenter != null) mJoyStickPresenter.onArrowRightClick();
                    break;

                case R.id.btn_setting:
                    mSettingPanel.show();
                    break;

//                case R.id.btn_set_loc:
//                    if (mJoyStickPresenter != null) mJoyStickPresenter.onSetLocationClick();
//                    break;
//
//                case R.id.btn_fly_to:
//                    if (mJoyStickPresenter != null) mJoyStickPresenter.onFlyClick();
//                    break;
//
//                case R.id.btn_bookmark:
//                    if (mJoyStickPresenter != null) mJoyStickPresenter.onBookmarkLocationClick();
//                    break;
            }
        }
    };


//    // touch point x pos according to screen
//    private float mXInScreen;
//    // touch point y pos according to screen
//    private float mYInScreen;
//    // touch point x pos according to view
//    private float mXInView;
//    // touch point y pos according to view
//    private float mYInView;
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                mXInView = event.getX();
//                mYInView = event.getY();
//                break;
//            case MotionEvent.ACTION_MOVE:
//                mXInScreen = event.getRawX();
//                mYInScreen = event.getRawY() - sStatusBarHeight;
//                updateViewPosition();
//                break;
//        }
//        return true;
//    }
//
//    private void updateViewPosition() {
//        mWindowLayoutParams.x = (int) (mXInScreen - mXInView);
//        mWindowLayoutParams.y = (int) (mYInScreen - mYInView);
//        mWindowManager.updateViewLayout(this, mWindowLayoutParams);
//    }
}
