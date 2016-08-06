package com.github.fakegps.pokemon.joystick;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.github.fakegps.pokemon.model.LocationPoint;
import com.github.fakegps.pokemon.ui.AddMarkDialogActivity;
import com.github.fakegps.pokemon.service.LocationThread;

/**
 * Created by tiger on 7/22/16.
 */
public class JoyStickManager implements IJoyStickPresenter {

    private static final String TAG = "JoyStickManager";

    private static JoyStickManager INSTANCE = new JoyStickManager();

    public static double STEP_WALK = 0.00002; // 2m/s
    public static double STEP_BIKE = 0.00005; // 5m/s
    public static double STEP_CAR = 0.00010;  // 10m/s
    public static double STEP_DEFAULT = STEP_WALK;

    private double mMoveStep = STEP_DEFAULT;

    private LocationPoint mCurrentLocPoint;
    private LocationPoint mTargetLocPoint;

    private Context mContext;
    private LocationThread mLocationThread;
    private boolean mIsStarted = false;
    private boolean mIsFlyMode = false;

    private IJoyStickView mJoyStickView;

    private JoyStickManager() {
    }

    public void init(Context context) {
        mContext = context;
    }

    public static JoyStickManager get() {
        return INSTANCE;
    }

    public void start(@NonNull LocationPoint locPoint) {
        mCurrentLocPoint = locPoint;
        if (mLocationThread == null || !mLocationThread.isAlive()) {
            mLocationThread = new LocationThread(mContext.getApplicationContext(), this);
            mLocationThread.startThread();
        }
        showJoyStick();
        mIsStarted = true;
    }

    public void stop() {
        if (mLocationThread != null) {
            mLocationThread.stopThread();
            mLocationThread = null;
        }

        hideJoyStick();
        mIsStarted = false;
    }

    public boolean isStarted() {
        return mIsStarted;
    }

    public void showJoyStick() {
        if (mJoyStickView == null) {
            mJoyStickView = new PokemonGoJoyStick(mContext);
            mJoyStickView.setJoyStickPresenter(this);
        }
        mJoyStickView.show();
    }

    public void hideJoyStick() {
        if (mJoyStickView != null) {
            mJoyStickView.hide();
        }
    }

    public LocationPoint getCurrentLocPoint() {
        return mCurrentLocPoint;
    }

    public LocationPoint getUpdateLocPoint() {
        if (mIsFlyMode && !mCurrentLocPoint.equals(mTargetLocPoint)) {
            double lat = mCurrentLocPoint.getLatitude();
            double lon = mCurrentLocPoint.getLongitude();

            double tLat = mTargetLocPoint.getLatitude();
            double tLon = mTargetLocPoint.getLongitude();

            double latStep, lonStep;
            if (lat < tLat) {
                latStep = mMoveStep;
            } else {
                latStep = -mMoveStep;
            }
            if (lon < tLon) {
                lonStep = mMoveStep;
            } else {
                lonStep = -mMoveStep;
            }

            lat += latStep;
            lon += lonStep;

            if (latStep >= 0) {
                if (lat > tLat) lat = tLat;
            } else {
                if (lat < tLat) lat = tLat;
            }
            if (lonStep >= 0) {
                if (lon > tLon) lon = tLon;
            } else {
                if (lon < tLon) lon = tLon;
            }

            mCurrentLocPoint.setLatitude(lat);
            mCurrentLocPoint.setLongitude(lon);

            if (mCurrentLocPoint.equals(mTargetLocPoint)) {
                mIsFlyMode = false;
                Toast.makeText(mContext, "完成", Toast.LENGTH_SHORT).show();
            }
        }

        if (mJoyStickView != null) mJoyStickView.updateLocationPoint(mCurrentLocPoint);

        return mCurrentLocPoint;
    }

    public void jumpToLocation(@NonNull LocationPoint location) {
        mIsFlyMode = false;
        mCurrentLocPoint = location;
    }

    public void flyToLocation(@NonNull LocationPoint location) {
        mTargetLocPoint = location;
        mIsFlyMode = true;
    }

    public boolean isFlyMode() {
        return mIsFlyMode;
    }

    public void stopFlyMode() {
        mIsFlyMode = false;
    }

    public void setMoveStep(double moveStep) {
        mMoveStep = moveStep;
    }

    public double getMoveStep() {
        return mMoveStep;
    }


    @Override
    public void onBookmarkLocationClick() {
        Log.d(TAG, "onBookmarkLocationClick");
        if (mCurrentLocPoint != null) {
            LocationPoint locPoint = new LocationPoint(mCurrentLocPoint);
            AddMarkDialogActivity.startPage(mContext, "Bookmark", locPoint);
            Toast.makeText(mContext, "Current location is copied!" + "\n" + locPoint, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(mContext, "Service is not start!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onArrowUpClick() {
        Log.d(TAG, "onArrowUpClick");
        mCurrentLocPoint.setLatitude(mCurrentLocPoint.getLatitude() + mMoveStep);
    }

    @Override
    public void onArrowDownClick() {
        Log.d(TAG, "onArrowDownClick");
        mCurrentLocPoint.setLatitude(mCurrentLocPoint.getLatitude() - mMoveStep);
    }

    @Override
    public void onArrowLeftClick() {
        Log.d(TAG, "onArrowLeftClick");
        mCurrentLocPoint.setLongitude(mCurrentLocPoint.getLongitude() - mMoveStep);
    }

    @Override
    public void onArrowRightClick() {
        Log.d(TAG, "onArrowRightClick :" + mMoveStep);
        mCurrentLocPoint.setLongitude(mCurrentLocPoint.getLongitude() + mMoveStep);
    }

}
