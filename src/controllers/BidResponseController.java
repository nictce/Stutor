package controllers;

import org.json.JSONArray;
import services.ApiRequest;
import services.ViewManagerService;
import views.tutor_responds.BidResponse;
import views.main_pages.MessagesPage;
import org.json.JSONObject;
import views.tutor_responds.FindBidDetails;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.http.HttpResponse;

/**
 * Coming from FindBidDetails, this class loads (and updates) the appropriate page according to the context of the bid,
 * where tutors can respond with their details or send a message to communicate with the student.
 */
public class BidResponseController implements ActionListener {

    private FindBidDetails findBidDetails;
    private MessagesPage messagesPage;
    private BidResponse bidResponse;

    public BidResponseController(FindBidDetails findBidDetails, BidResponse bidResponse, MessagesPage messagesPage) {
        this.findBidDetails = findBidDetails;
        this.bidResponse = bidResponse;
        this.messagesPage = messagesPage;
        findBidDetails.addResponseListener(this);
        bidResponse.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton thisBtn = (JButton) e.getSource();

        // get the bid from bidId
        JSONObject data = new JSONObject(thisBtn.getName().trim());
        HttpResponse<String> response = ApiRequest.get("/bid/" + data.get("bidId") + "?fields=messages");
        JSONObject bid = new JSONObject(response.body());

        // if submitting open bid, create a message to update bid
        if (thisBtn.getText().equals("Submit Bid")) {

            JSONObject inputData = bidResponse.retrieveInputs();
            response = ApiRequest.post("/message", inputData.toString());

            if (response.statusCode() == 201) { // successfully posted message
                JOptionPane.showMessageDialog(new JFrame(), "Success", "Response Sent Successfully", JOptionPane.INFORMATION_MESSAGE);
                ViewManagerService.loadPage(ViewManagerService.DASHBOARD_PAGE);

            } else { // failed API call
                String msg = "Error: " + new JSONObject(response.body()).get("message");
                JOptionPane.showMessageDialog(new JFrame(), msg, "Bad Request", JOptionPane.ERROR_MESSAGE);
            }

        } else if (thisBtn.getText().equals("Revise Response")) {

            JSONObject inputData = bidResponse.retrieveInputs();
            JSONArray messages = bid.getJSONArray("messages");
            HttpResponse<String> messagePatch = null;
            for (int i = 0; i < messages.length(); i++) {
                JSONObject message = messages.getJSONObject(i);
                if (message.getJSONObject("poster").getString("id").equals(inputData.getString("posterId")) && !message.isNull("additionalInfo")) {
                    JSONObject additionalInfo = new JSONObject().put("additionalInfo", inputData.getJSONObject("additionalInfo"));
                    messagePatch = ApiRequest.patch("/message/" + message.getString("id"), additionalInfo.toString());
                    break;
                }
            }
            if (messagePatch != null) {
                if (messagePatch.statusCode() == 200) { // successfully posted message
                    JOptionPane.showMessageDialog(new JFrame(), "Success", "Response Revised Successfully", JOptionPane.INFORMATION_MESSAGE);
                    ViewManagerService.loadPage(ViewManagerService.FIND_BID_DETAILS);
                } else { // failed API call
                    String msg = "Error: " + new JSONObject(messagePatch.body()).get("message");
                    JOptionPane.showMessageDialog(new JFrame(), msg, "Bad Request", JOptionPane.ERROR_MESSAGE);
                }
            }

        } else {

            // if message button is clicked for close bid
            if (thisBtn.getText().equals("Message")) {
                messagesPage.update(thisBtn.getName());
                ViewManagerService.loadPage(ViewManagerService.MESSAGES_PAGE);

            } else { // if bid button is clicked

                // if bid button in find bids detail page is clicked check if open or close bid then go to appropriate page
                // go to either openBidResponse or closedBidResponse
                bidResponse.update(thisBtn.getName());
                ViewManagerService.loadPage(ViewManagerService.BID_RESPONSE);
            }
        }
    }

}
