package controllers;

import abstractions.Publisher;
import services.ApiRequest;
import abstractions.ObserverInputInterface;
import org.json.JSONArray;
import org.json.JSONObject;
import services.ViewManagerService;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.http.HttpResponse;

/**
 * Creates a bid for the student while checking for invalid competency levels and leads back to the DashboardPage.
 */
public class BidCreateController extends Publisher implements ActionListener {

    private ObserverInputInterface inputPage;

    public BidCreateController(ObserverInputInterface inputPage) {
        super();
        this.inputPage = inputPage;
        inputPage.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JSONObject bidDetails = inputPage.retrieveInputs();
        String initiatorId = bidDetails.getString("initiatorId");
        HttpResponse<String> response = ApiRequest.get("/user/" + initiatorId + "?fields=competencies.subject");
        JSONObject user = new JSONObject(response.body());
        JSONArray competencies = new JSONArray(user.getJSONArray("competencies"));

        // if new bid is created notify dashboard and go back to dashboard
        if (checkCompetency(competencies, bidDetails)) {
            notifySubscribers(initiatorId);
            ViewManagerService.loadPage(ViewManagerService.DASHBOARD_PAGE);
        }

    }

    private boolean checkCompetency(JSONArray competencies, JSONObject bidDetails) {// compares bid minimum competency level with the student's competency level to check if it is two levels above
        for (int i = 0; i < competencies.length(); i++) {
            JSONObject competency =  (JSONObject) competencies.get(i);

            // for that competency
            if (competency.getJSONObject("subject").getString("id").equals(bidDetails.getString("subjectId"))) {

                // if the user competency is 2 level lower
                if (competency.getInt("level") + 2 > (bidDetails.getJSONObject("additionalInfo").getInt("minCompetency"))) {
                    JOptionPane.showMessageDialog(new JFrame(), "The minimum competency level must be at least 2 levels higher than your subject level. Please try again.",
                            "Invalid Minimum Competency Level", JOptionPane.ERROR_MESSAGE);

                } else {
                    createBid(bidDetails);
                    return true;
                }
            }
        }
        return false;
    }

    private void createBid(JSONObject bidDetails) {
        HttpResponse<String> response = ApiRequest.post("/bid", bidDetails.toString());
        if (response.statusCode() == 201){
            JOptionPane.showMessageDialog(new JFrame(), "Success", "Bid Send Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            String msg = "Error: " + new JSONObject(response.body()).get("message");
            JOptionPane.showMessageDialog(new JFrame(), msg, "Bad Request", JOptionPane.ERROR_MESSAGE);
        }

    }
}
