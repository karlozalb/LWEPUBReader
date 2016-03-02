package com.projectclean.lwepubreader.eventbus;

import com.squareup.otto.Bus;

/**
 * Created by Carlos Albaladejo Pérez on 25/02/2016.
 */
public class EventBusFactory {

    private static Bus mBus;

    public static void init(){
        mBus = new Bus();
    }

    public static Bus getInstance(){
        return mBus;
    }

}
