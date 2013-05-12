/*
 * Copyright (C) 2012 Markus Junginger, greenrobot (http://greenrobot.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.greenrobot.event;

import java.lang.ref.WeakReference;

final class Subscription {
    private final WeakReference<Object> subscriber;
    private final SubscriberMethod subscriberMethod;
    
    Subscription(Object subscriber, SubscriberMethod subscriberMethod) {
        this.subscriber = new WeakReference<Object>(subscriber);
        this.subscriberMethod = subscriberMethod;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Subscription) {
            final Subscription otherSubscription = (Subscription) other;
            
            final Object lhs = subscriber.get();
            final Object rhs = otherSubscription.subscriber.get();
            
            return 
                lhs != null &&
                rhs != null &&
                lhs == rhs && 
                subscriberMethod.equals(otherSubscription.subscriberMethod);
            
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        Object sub = subscriber.get();
        
        if (sub != null) {
            return sub.hashCode() + subscriberMethod.methodString.hashCode();
        } else {
            return subscriberMethod.methodString.hashCode();
        }
    }

    public Object getSubscriber() {
        Object s = subscriber.get();
        
        // why am i doing this?
        if (s == null) {
            return null;
            
        } else {
            return s;
        }
    }

    public SubscriberMethod getSubscriberMethod() {
        return subscriberMethod;
    }
}