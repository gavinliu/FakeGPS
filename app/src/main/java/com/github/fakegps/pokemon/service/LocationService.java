package com.github.fakegps.pokemon.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;

import com.github.fakegps.pokemon.R;
import com.github.fakegps.pokemon.joystick.JoyStickManager;
import com.github.fakegps.pokemon.model.LocationPoint;

/**
 * Created by Gavin on 2016/07/29.
 */
public class LocationService extends Service {

    public static final String CMD_KEY = "cmd_key";
    public static final int START = 1;
    public static final int STOP = 2;

    private JoyStickManager mJoyStickManager;

    private void startNotification() {
        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.icon_app)  // the status icon
                .setTicker("setTicker")  // the status text
                .setWhen(System.currentTimeMillis())  // the time stamp
                .setContentTitle(getText(R.string.app_name))  // the label
                .setContentText("12")  // the contents of the entry
//                .setContentIntent(contentIntent)  // The intent to send when clicked
                .build();

        startForeground(R.string.app_name, notification);
    }

    private void stopNotification() {
        stopForeground(true);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startNotification();
        mJoyStickManager = JoyStickManager.get();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopNotification();

        mJoyStickManager.stop();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int cmd = intent.getIntExtra(CMD_KEY, 0);

        if (cmd == START) {
            LocationPoint point = (LocationPoint) intent.getSerializableExtra("point");
            mJoyStickManager.start(point);
        }

        return START_NOT_STICKY;
    }

}
