package me.mrrobot97.eventbus;

import java.lang.reflect.Method;

/**
 * Created by mrrobot on 17/2/26.
 */

public class Subscription {
    public Method method;
    public Object subscriber;
    public ThreadMode mode;

    public Subscription(Method method, Object subscriber, ThreadMode mode) {
        this.method = method;
        this.subscriber = subscriber;
        this.mode = mode;
    }
}
