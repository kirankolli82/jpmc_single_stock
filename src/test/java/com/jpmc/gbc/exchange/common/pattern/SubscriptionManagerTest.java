package com.jpmc.gbc.exchange.common.pattern;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Kiran Kolli on 03-09-2016.
 */
public class SubscriptionManagerTest {

    @Test
    public void testSubscription_positive() {
        SubscriptionManager<TestSubscriber> subscriptionManager = new SubscriptionManager<>();
        TestSubscriber subscriber = new TestSubscriber();
        subscriptionManager.subscribe(subscriber);

        subscriptionManager.invokeOnAllSubscribers(TestSubscriber::invoke);

        Assert.assertTrue(subscriber.invoked);
    }

    @Test
    public void testUnSubscription_positive() {
        SubscriptionManager<TestSubscriber> subscriptionManager = new SubscriptionManager<>();
        TestSubscriber subscriber = new TestSubscriber();
        Object subscriptionHandle = subscriptionManager.subscribe(subscriber);

        subscriptionManager.invokeOnAllSubscribers(TestSubscriber::invoke);
        Assert.assertTrue(subscriber.invoked);

        subscriptionManager.unsubscribe(subscriptionHandle);

        subscriptionManager.invokeOnAllSubscribers(TestSubscriber::invoke);

        Assert.assertTrue(subscriber.invoked);
    }

    private class TestSubscriber {
        private boolean invoked = false;

        void invoke() {
            invoked = !invoked;
        }
    }
}
