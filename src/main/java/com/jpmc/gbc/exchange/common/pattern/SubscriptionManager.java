package com.jpmc.gbc.exchange.common.pattern;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Consumer;

/**
 * Created by Kiran Kolli on 03-09-2016.
 */
public class SubscriptionManager<SubscriberTypeT> {

    private final Map<Object, SubscriberTypeT> handleSubscriberMap = new WeakHashMap<>();
    private final Map<SubscriberTypeT, Object> subscriberHandleMap = new WeakHashMap<>();
    private final Object lock = new Object();

    public Object subscribe(SubscriberTypeT subscriber) {
        synchronized (lock) {
            if (subscriberHandleMap.containsKey(subscriber)) {
                return subscriberHandleMap.get(subscriber);
            } else {
                Object handle = new Object();
                subscriberHandleMap.put(subscriber, handle);
                handleSubscriberMap.put(handle, subscriber);
                return handle;
            }
        }
    }

    public void unsubscribe(Object handle) {
        synchronized (lock) {
            if (handleSubscriberMap.containsKey(handle)) {
                SubscriberTypeT subscriber = handleSubscriberMap.remove(handle);
                subscriberHandleMap.remove(subscriber);
            }
        }
    }

    public void invokeOnAllSubscribers(Consumer<SubscriberTypeT> subscriberConsumer) {
        synchronized (lock) {
            //noinspection Convert2MethodRef
            subscriberHandleMap.keySet().forEach(subscriber -> subscriberConsumer.accept(subscriber));
        }
    }
}
