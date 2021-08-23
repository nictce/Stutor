package controllers;

import abstractions.ObserverInputInterface;
import abstractions.Publisher;
import org.json.JSONArray;
import org.json.JSONObject;
import services.ApiRequest;
import services.ViewManagerService;
import views.tutor_responds.FindBidDetails;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.http.HttpResponse;

public class MonitorBidController extends Publisher implements ActionListener {

    ObserverInputInterface inputPage;

    public MonitorBidController(FindBidDetails inputPage) {
        super();
        this.inputPage = inputPage;
        inputPage.addMonitorListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton btn = (JButton) e.getSource();
        inputPage = (ObserverInputInterface) btn.getParent();
        JSONObject context = inputPage.retrieveInputs();
        String userId = context.getString("userId");
        String bidId = context.getString("bidId");

        HttpResponse<String> userResponse = ApiRequest.get("/user/" + userId);

        if (userResponse.statusCode() == 200) {
            JSONObject additionalInfo = new JSONObject(userResponse.body()).getJSONObject("additionalInfo");
            String msg = "";

            if (btn.getText().equals("Monitor Bid")) {
                if (!additionalInfo.has("monitoredBids")) {
                    additionalInfo.put("monitoredBids", new JSONArray());
                }
                JSONObject newBid = new JSONObject(ApiRequest.get("/bid/" + bidId).body());
                additionalInfo.getJSONArray("monitoredBids").put(newBid);
                msg = "Bid successfully added into your monitoring list!";
            } else if (btn.getText().equals("Remove From Monitoring")) {
                JSONArray monitoredBids = additionalInfo.getJSONArray("monitoredBids");
                for (int i = 0; i < monitoredBids.length(); i++) {
                    JSONObject bid = (JSONObject) monitoredBids.get(i);
                    if (bid.getString("id").equals(bidId)) {
                        monitoredBids.remove(i);
                        msg = "Bid successfully removed from your monitoring list!";
                        break;
                    }
                }
            }

            JSONObject patchRequest = new JSONObject().put("additionalInfo", additionalInfo);
            HttpResponse<String> pathResponse = ApiRequest.patch("/user/" + userId, patchRequest.toString());

            if (pathResponse.statusCode() == 200) {
                JOptionPane.showMessageDialog(new JFrame(), msg, "Monitoring Bids", JOptionPane.INFORMATION_MESSAGE);
                notifySubscribers(userId);
            }
        }
    }
}
