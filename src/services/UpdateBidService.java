package services;

import abstractions.ObserverOutputInterface;
import abstractions.Publisher;

import java.util.Timer;
import java.util.TimerTask;

public class UpdateBidService extends Publisher implements ObserverOutputInterface {

    private Timer bidUpdateTimer;
    private TimerTask bidUpdateTask;
    private String userId;

    public UpdateBidService() {
        bidUpdateTimer = new Timer();
    }

    private void runService() {
        bidUpdateTask = new TimerTask() {
            @Override
            public void run() {
                notifySubscribers(userId);
            }
        };
        bidUpdateTimer.schedule(bidUpdateTask, 10, 5000);
    }

    @Override
    public void update(String data) {
        userId = data;
        runService();
    }
}
