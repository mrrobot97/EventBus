package me.mrrobot97.eventbus;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by mrrobot on 17/2/26.
 */

public class MainThreadHandler extends Handler {
    private Subscription subscription;
    private Object eventType;

    public MainThreadHandler(Looper looper) {
        super(looper);
    }

    @Override
    public void handleMessage(Message msg) {
        try {
            subscription.method.invoke(subscription.subscriber,eventType);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public void post(Subscription subscription,Object eventType){
        this.subscription=subscription;
        this.eventType=eventType;
        sendMessage(Message.obtain());
    }
}
