package com.github.fakegps.pokemon.joystick;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;

import com.github.fakegps.pokemon.R;
import com.github.fakegps.pokemon.model.LocationMark;
import com.github.fakegps.pokemon.model.LocationPoint;
import com.github.fakegps.pokemon.ui.MarkAdapter;
import com.github.fakegps.pokemon.util.DbUtils;
import com.github.fakegps.pokemon.util.FakeGpsUtils;
import com.github.fakegps.pokemon.util.ScreenUtils;
import com.github.fakegps.pokemon.widget.FloatFrameLayout;

import java.util.ArrayList;

/**
 * Created by Gavin on 16-8-4.
 */
public class SettingPanel extends FloatFrameLayout implements IJoyStickView {

    private boolean mIsShowing = false;

    private EditText mLocationLatEditText;
    private EditText mLocationLonEditText;

    private RadioGroup mMoveStepGroup;

    private ListView mListView;
    private MarkAdapter mAdapter;


    public SettingPanel(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.setting_layout, this);

        findViewById(R.id.cancel).setOnClickListener(mOnClickListener);
        findViewById(R.id.auto).setOnClickListener(mOnClickListener);
        findViewById(R.id.new_loc).setOnClickListener(mOnClickListener);
        findViewById(R.id.add_mark).setOnClickListener(mOnClickListener);

        mLocationLatEditText = (EditText) findViewById(R.id.inputLat);
        mLocationLonEditText = (EditText) findViewById(R.id.inputLon);

        mMoveStepGroup = (RadioGroup) findViewById(R.id.inputStep);
        mMoveStepGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                double step = JoyStickManager.STEP_WALK;

                switch (checkedId) {
                    case R.id.inputStepBike:
                        step = JoyStickManager.STEP_BIKE;
                        break;

                    case R.id.inputStepCar:
                        step = JoyStickManager.STEP_CAR;
                }

                JoyStickManager.get().setMoveStep(step);
            }
        });
        mMoveStepGroup.check(R.id.inputStepWalk);

        mListView = (ListView) findViewById(R.id.list_bookmark);
        View emptyView = findViewById(R.id.empty_view);
        mListView.setEmptyView(emptyView);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LocationPoint locPoint = mAdapter.getItem(position).getLocPoint();
                updateEditText(locPoint);
            }
        });
    }

    private View.OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            LocationPoint point = FakeGpsUtils.getLocPointFromInput(mLocationLatEditText, mLocationLonEditText);

            hide();
            switch (v.getId()) {
                case R.id.cancel:

                    break;

                case R.id.auto:
                    if (point != null) {
                        JoyStickManager.get().flyToLocation(point);
                    }
                    break;

                case R.id.new_loc:
                    if (point != null) {
                        JoyStickManager.get().jumpToLocation(point);
                    }
                    break;

                case R.id.add_mark:
                    JoyStickManager.get().onBookmarkLocationClick();
                    break;
            }
        }
    };

    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected int getWindowFlags() {
        return WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
    }

    @Override
    protected int getWindowGravity() {
        return Gravity.TOP | Gravity.START;
    }

    @Override
    protected int getWindowWidth() {
        return WindowManager.LayoutParams.MATCH_PARENT;
    }

    @Override
    protected int getWindowHeight() {
        return ScreenUtils.getScreenHeight(getContext()) - 90
                - ScreenUtils.getStatusBarHeight(getContext()) - 100;
    }

    @Override
    protected int getWindowX() {
        return 0;
    }

    @Override
    protected int getWindowY() {
        return 50;
    }

    @Override
    public void show() {
        if (!isShowing()) {
            super.addToWindow();
            mIsShowing = true;

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    LocationPoint locationPoint = JoyStickManager.get().getCurrentLocPoint();
                    updateEditText(locationPoint);

                    if (mAdapter == null) mAdapter = new MarkAdapter(getContext(), true);

                    ArrayList<LocationMark> allBookmark = DbUtils.getAllBookmark();
                    mAdapter.setLocBookmarkList(allBookmark);
                    mListView.setAdapter(mAdapter);
                }
            });
        }
    }

    private void updateEditText(LocationPoint locationPoint) {
        mLocationLatEditText.setText(String.valueOf(FakeGpsUtils.scaleDouble(5, locationPoint.getLatitude())));
        mLocationLonEditText.setText(String.valueOf(FakeGpsUtils.scaleDouble(5, locationPoint.getLongitude())));

        mLocationLatEditText.setSelection(mLocationLatEditText.getText().length());
        mLocationLonEditText.setSelection(mLocationLonEditText.getText().length());
    }

    @Override
    public void hide() {
        if (isShowing()) {
            super.removeFromWindow();
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
    public void updateLocationPoint(final LocationPoint locationPoint) {

    }
}
