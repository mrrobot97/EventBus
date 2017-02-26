package me.mrrobot97.eventbus;

import android.os.Looper;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by mrrobot on 17/2/26.
 */

public class EventBus {
    public static final String TAG="YJW";

    private static EventBus defaultInstance;

    private MainThreadHandler mainThreadHandler;

    private EventBus(){
        mainThreadHandler=new MainThreadHandler(Looper.getMainLooper());
    };

    public static EventBus getDefault(){
        if(defaultInstance==null){
            synchronized (EventBus.class){
                if(defaultInstance==null) defaultInstance=new EventBus();
            }
        }
        return defaultInstance;
    }

    private HashMap<Class<?>,ArrayList<Subscription>> subscriptionsByEventType=new HashMap<>();

    public void register(Object subscriber){
        //根据反射机制，查找subscriber中所有已onEvent开头的method
        Class<?> clazz=subscriber.getClass();
        Method[] methods=clazz.getMethods();
        for(Method method:methods){
            String name=method.getName();
            if(name.startsWith("onEvent")){
                Class<?> param=method.getParameterTypes()[0];
                ArrayList<Subscription> subscriptions=subscriptionsByEventType.get(param);
                if(subscriptions==null){
                    subscriptions=new ArrayList<>();
                    subscriptionsByEventType.put(param,subscriptions);
                }
                //根据函数名字决定线程
                if(name.substring("onEvent".length()).length()==0){
                    //onEvent 默认为postThread
                    subscriptions.add(new Subscription(method,subscriber, ThreadMode.PostThread));
                }else{
                    //onEventMainThread
                    subscriptions.add(new Subscription(method,subscriber, ThreadMode.MainThread));
                }
            }
        }
    }

    public void unregister(Object subscriber){
        Class<?> clazz=subscriber.getClass();
        Method[] methods=clazz.getMethods();
        for(Method method:methods){
            String name=method.getName();
            if(name.startsWith("onEvent")){
                Class<?> param=method.getParameterTypes()[0];
                ArrayList<Subscription> subscriptions=subscriptionsByEventType.get(param);
                if(subscriptions!=null){
                    for(Subscription subscription:subscriptions){
                        if(subscription.subscriber==subscriber) subscriptions.remove(subscription);
                    }
                }
            }
        }
    }

    public void post(Object eventType){
        Class<?> clazz=eventType.getClass();
        ArrayList<Subscription> subscriptions=subscriptionsByEventType.get(clazz);
        if(subscriptions==null) Log.d(TAG,"EventBus: no subscriber has subscribed to this event");
        else{
            for(Subscription subscription:subscriptions){
                switch (subscription.mode){
                    case MainThread:
                        mainThreadHandler.post(subscription,eventType);
                        break;
                    case PostThread:
                        try {
                            subscription.method.invoke(subscription.subscriber,eventType);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
