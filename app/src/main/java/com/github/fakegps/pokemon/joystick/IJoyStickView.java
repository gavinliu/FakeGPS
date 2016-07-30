package com.github.fakegps.pokemon.joystick;

import com.github.fakegps.pokemon.model.LocationPoint;

/**
 * Created by Gavin on 2016/07/30.
 */
public interface IJoyStickView {

    void show();

    void hide();

    boolean isShowing();

    void setJoyStickPresenter(IJoyStickPresenter joyStickPresenter);

    void updateLocationPoint(LocationPoint locationPoint);
}
