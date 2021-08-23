package services;

import abstractions.ObserverInputInterface;
import abstractions.ObserverOutputInterface;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.http.HttpResponse;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Utility class to automatically close bids
 */
public class ExpireBidService implements ObserverInputInterface, ObserverOutputInterface {

    private long openCounter, closedCounter; //counter of timer in minutes and days
    private Timer bidClosingTimer;
    private TimerTask bidClosingTask;
    private ActionListener actionListener;
    private JSONObject bid;
    private Instant currentTime;
    private String bidId;

    public ExpireBidService() {
        bidClosingTimer = new Timer();
    }

    public void setDuration(int minutes, int days) {
        this.openCounter = minutes * 60000L;
        this.closedCounter = days;
    }

    private void runService() {
        bidClosingTask = new TimerTask() {

            @Override
            public void run() {
                HttpResponse<String> bidResponse = ApiRequest.get("/bid?fields=messages");
                JSONArray bids = new JSONArray(bidResponse.body());

                // check every bid to see if they already expired
                for (int i=0; i<bids.length(); i++){
                    bid = bids.getJSONObject(i);
                    bidId = bid.getString("id");
                    Timestamp ts = Timestamp.from(ZonedDateTime.now().toInstant());
                    currentTime = ts.toInstant();

                    expireBid();
                }
            }
        };
        bidClosingTimer.schedule(bidClosingTask, 10, 20000);
    }

    private void expireBid() {
        if (bid.isNull("dateClosedDown")) {
            Instant bidStart = Instant.parse(bid.getString("dateCreated")); // bidStartTime
            Instant expiryTime = null;
            String command = null;

            if (bid.get("type").equals("open")) {
                expiryTime = bidStart.plus(openCounter, ChronoUnit.MILLIS);
                command = "Expire Open Bid";
            }
            else if (bid.get("type").equals("close")) {
                LocalDateTime time = LocalDateTime.ofInstant(bidStart, ZoneOffset.ofHours(0)).plus(closedCounter, ChronoUnit.DAYS);
                expiryTime = time.atZone(ZoneOffset.ofHours(0)).toInstant();
                command = "Expire Closed Bid";
            }

            if (currentTime.compareTo(expiryTime) > 0) { // if the currentTime is after expiryTime, close bid
                ActionEvent actionEvent = new ActionEvent(ExpireBidService.this, ActionEvent.ACTION_PERFORMED, command);
                actionListener.actionPerformed(actionEvent);
            }
        }
    }

    @Override
    public JSONObject retrieveInputs() {
        JSONArray messages = bid.optJSONArray("messages");
        JSONObject closeDate = new JSONObject();
        closeDate.put("dateClosedDown", currentTime);

        JSONObject bidInfo = new JSONObject();
        bidInfo.put("bidId", bidId);

        if (messages.length() == 0 || bid.getString("type").equals("closed")) {
            bidInfo.put("hasExpired", true);
            bidInfo.put("tutorId", "");
            bidInfo.put("messageId", "");
        } else {
            bidInfo.put("hasExpired", false);
            JSONObject message = messages.getJSONObject(messages.length()-1);
            bidInfo.put("tutorId", message.getJSONObject("poster").getString("id"));
            bidInfo.put("messageId", message.getString("id"));
        }
        return bidInfo;
    }

    @Override
    public void addActionListener(ActionListener actionListener) {
        this.actionListener = actionListener;
    }

    @Override
    public void update(String data) {runService();}
}
