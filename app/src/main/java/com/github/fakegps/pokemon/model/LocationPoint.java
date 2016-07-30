package com.github.fakegps.pokemon.model;

import com.github.fakegps.pokemon.util.FakeGpsUtils;

import java.io.Serializable;

/**
 * Created by tiger on 7/23/16.
 */
public class LocationPoint implements Serializable {
    static final long serialVersionUID = -1770575152720897533L;

    private double mLatitude;
    private double mLongitude;

    public LocationPoint(LocationPoint locPoint) {
        this(locPoint.getLatitude(), locPoint.getLongitude());
    }

    public LocationPoint(double latitude, double longitude) {
        mLatitude = FakeGpsUtils.scaleDouble(5, latitude);
        mLongitude = FakeGpsUtils.scaleDouble(5, longitude);
    }

    public double getLatitude() {
        return mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLatitude(double latitude) {
        mLatitude = latitude;
    }

    public void setLongitude(double longitude) {
        mLongitude = longitude;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof LocationPoint)) {
            return false;
        }
        LocationPoint point = (LocationPoint) o;

        return point.getLatitude() == mLatitude && point.getLongitude() == mLongitude;
    }

    @Override
    public String toString() {
        return "(" + mLatitude + " , " + mLongitude + ")";
    }
}
