package controllers;

import services.ApiRequest;
import abstractions.Publisher;
import abstractions.ObserverInputInterface;
import abstractions.ObserverOutputInterface;
import org.json.JSONObject;
import contract_strategy.SignBidContractStrategy;
import contract_strategy.Contract;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.http.HttpResponse;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZonedDateTime;

/**
 * Notifies view components when a bid is closed, making them update themselves to show the correct bids and tutorials (contracts)
 */
public class BidClosingController extends Publisher implements ObserverOutputInterface, ActionListener {

    private String userId; // needed to update other bid view
    private Contract contractUtil;

    public BidClosingController() {
        super();
        this.contractUtil = new Contract();
        contractUtil.setStrategy(new SignBidContractStrategy());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ObserverInputInterface inputPage;
        if (e.getSource() instanceof JButton) { // triggered by a button
            JButton btn = (JButton) e.getSource();
            inputPage = (ObserverInputInterface) btn.getParent();
        } else { // triggered by ExpireBidService
            inputPage = (ObserverInputInterface) e.getSource();
        }
        JSONObject jsonBid = inputPage.retrieveInputs();
        closeBid(jsonBid);
        notifySubscribers(this.userId);
    }

    /***
     * Function for a tutor to close a bid immediately if he agree to the student's bid
     */
    private void closeBid(JSONObject bidInfo) {
        String bidId = bidInfo.getString("bidId");
        String tutorId = bidInfo.getString("tutorId");
        String messageId = bidInfo.getString("messageId");
        boolean hasExpired = bidInfo.getBoolean("hasExpired");

        HttpResponse<String> bidResponse = ApiRequest.get("/bid/" + bidId + "?fields=messages");
        JSONObject bid = new JSONObject(bidResponse.body());

        Timestamp ts = Timestamp.from(ZonedDateTime.now().toInstant());
        Instant now = ts.toInstant();

        JSONObject closeDate = new JSONObject();
        closeDate.put("dateClosedDown", now);
        HttpResponse<String> bidCloseDownResponse = ApiRequest.post("/bid/" + bidId + "/close-down", closeDate.toString()); // pass empty json object since this API call don't need it

        if (bidCloseDownResponse.statusCode() == 200) {
            if (hasExpired) {
                if (bid.getJSONObject("initiator").getString("id").equals(userId)) { // only shows expire message to the correct user
                    JOptionPane.showMessageDialog(new JFrame(), "Bid expired at " + now, "Bid Expired", JOptionPane.INFORMATION_MESSAGE);
                }
                return;
            } else {
                bid.put("userId", this.userId);
                bid.put("tutorId", tutorId);
                bid.put("messageId", messageId);
                contractUtil.postContract(bid);
            }
        } else {
            String msg = "Bid not closed down: Error " + bidCloseDownResponse.statusCode();
            JOptionPane.showMessageDialog(new JFrame(), msg, "Bad request", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void update(String data) {
        this.userId = data;
    }

}

