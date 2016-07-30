package com.github.fakegps.pokemon.util;

import android.content.ClipboardManager;
import android.content.Context;
import android.util.Log;
import android.widget.EditText;

import com.github.fakegps.pokemon.model.LocPoint;

import java.math.BigDecimal;

/**
 * Created by tiger on 7/23/16.
 */
public final class FakeGpsUtils {
    private static final String TAG = "FakeGpsUtils";

    private FakeGpsUtils() {
    }

    public static void copyToClipboard(Context context, String content) {
        ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(content.trim());
    }

    public static LocPoint getLocPointFromInput(Context context, EditText editText) {
        LocPoint point = null;
        String text = editText.getText().toString().replace("(", "").replace(")", "");
        String[] split = text.split(",");
        if (split.length == 2) {
            try {
                double lat = Double.parseDouble(split[0].trim());
                double lon = Double.parseDouble(split[1].trim());
                point = new LocPoint(lat, lon);
            } catch (NumberFormatException e) {
                Log.e(TAG, "Parse loc point error!", e);
            }
        }
        return point;
    }

    public static double getMoveStepFromInput(Context context, EditText editText) {
        double step = 0;
        String stepStr = editText.getText().toString().trim();
        try {
            step = Double.valueOf(stepStr);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Parse move step error!", e);
        }

        return step;
    }

    public static int getIntValueFromInput(Context context, EditText editText) {
        int value = 0;
        String stepStr = editText.getText().toString().trim();
        try {
            value = Integer.valueOf(stepStr);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Parse move step error!", e);
        }

        return value;
    }

    private static final double EARTH_RADIUS = 6378.137; //地球半径 KM

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    // 单位 km
    public static double getDistance(double lat1, double lng1, double lat2, double lng2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);

        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000d;

        BigDecimal bigDecimal = new BigDecimal(s);
        s = bigDecimal.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();

        return s;
    }

}
