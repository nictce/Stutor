package abstractions;

import java.util.ArrayList;

public abstract class Publisher {

    protected ArrayList<ObserverOutputInterface> subscribers;
    protected ObserverInputInterface inputPage;

    public Publisher() {
        subscribers = new ArrayList<>();
    }

    public void subscribe(ObserverOutputInterface subscriber) {
        subscribers.add(subscriber);
    }

    public void unsubscribe(ObserverOutputInterface subscriber) {subscribers.remove(subscriber);}

    public void notifySubscribers(String data) {
        for (ObserverOutputInterface subscriber : subscribers) {
            subscriber.update(data);
        }
    }
}
