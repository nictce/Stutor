package views.tutor_responds;

import controllers.MonitorBidController;
import services.ApiRequest;
import services.ViewManagerService;
import abstractions.ListenerLinkInterface;
import abstractions.ObserverInputInterface;
import abstractions.ObserverOutputInterface;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.net.http.HttpResponse;
import java.util.ArrayList;

public class FindBidDetails extends JPanel implements ObserverOutputInterface, ObserverInputInterface, ListenerLinkInterface {

    private String bidId, userId;
    private JLabel title, subjectLabel, nameLabel, rate, competency, duration, startTime, day, preferredSession, bidderLabel;
    private JButton buyoutButton, respondButton, monitorBidButton, backButton, viewBidButton;
    private JPanel detailPane;
    private JScrollPane scrollPane;
    private ArrayList<JButton> buttonArr;

    public FindBidDetails() {
        this.setLayout(new GridBagLayout());
        this.setBorder(new EmptyBorder(15, 15,15,15));
        GridBagConstraints c = new GridBagConstraints();

        c.weightx = 1;
        c.weighty = 0.2;
        c.insets = new Insets(2, 2, 2, 2);
        c.fill = GridBagConstraints.HORIZONTAL;
        // inner panel for detail
        c.weightx = 0.5;
        c.weighty = 0.5;

        title = new JLabel("Bid Details");
        title.setHorizontalAlignment(JLabel.CENTER);
        title.setVerticalAlignment(JLabel.TOP);
        title.setFont(new Font("Bahnschrift", Font.BOLD, 20));
        c.gridy = 0;
        c.gridwidth = 3;
        c.gridx = 1;
        c.fill = GridBagConstraints.CENTER;
        this.add(title, c);

        backButton = new JButton("Back");
        c.gridy = 0;
        c.weightx = 0.0;
        c.gridwidth = 1;
        c.gridx = 0;
        c.anchor = GridBagConstraints.PAGE_START;
        c.fill = GridBagConstraints.NONE;
        this.add(backButton, c);

        backButton.addActionListener(e -> ViewManagerService.loadPage(ViewManagerService.DASHBOARD_PAGE));

        subjectLabel = new JLabel("Subject: ");
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridwidth = 4;
        c.gridy = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        this.add(subjectLabel, c);

        nameLabel = new JLabel("Name: ");
        c.gridx = 0;
        c.gridwidth = 4;
        c.gridy = 2;
        this.add(nameLabel, c);

        rate = new JLabel("Rate: Not Provided");
        c.gridy = 3;
        this.add(rate, c);

        competency = new JLabel("Minimum competency: Not Provided");
        c.gridy = 4;
        this.add(competency, c);

        day = new JLabel("Preferred Day(s): Not Provided");
        c.gridy = 5;
        this.add(day, c);

        preferredSession = new JLabel("Preferred no of sessions: Not Provided");
        c.gridy = 6;
        this.add(preferredSession, c);

        duration = new JLabel("Duration: Not Provided");
        c.gridy = 7;
        this.add(duration, c);

        startTime = new JLabel("Start Time: Not Provided");
        c.gridy = 8;
        this.add(startTime, c);

        // wrap detailPane with a scrollPane
        scrollPane = new JScrollPane();

        // add scrollPane into this
        c.weighty = 1;
        c.weightx = 1;
        c.gridheight = 10;
        c.gridx = 0;
        c.gridy = 9;
        c.gridwidth = 4;
        c.fill = GridBagConstraints.HORIZONTAL;
        this.setOpaque(false);
        this.add(scrollPane, c);

        respondButton = new JButton("Bid");
        c.weighty = 1;
        c.weightx = 1;
        c.gridheight = 1;
        c.gridx = 0;
        c.gridy = 21;
        c.gridwidth = 4;
        c.fill = GridBagConstraints.HORIZONTAL;
        this.add(respondButton, c);

        buyoutButton = new JButton("Buy Out");
        c.weighty = 1;
        c.weightx = 1;
        c.gridheight = 1;
        c.gridx = 0;
        c.gridy = 22;
        c.gridwidth = 4;
        c.fill = GridBagConstraints.HORIZONTAL;
        this.add(buyoutButton, c);

        monitorBidButton = new JButton("Monitor Bid");
        c.gridy = 23;
        this.add(monitorBidButton, c);

    }

    /**
     * Get the bidId from Find Bid page one user click on view bid to retrieve data from db
     * @param data any data that is crucial to the pages for them to request the information that they need from the database
     */
    @Override
    public void update(String data) {

        if (data.charAt(0) == '{') {
            this.bidId = new JSONObject(data).getString("bidId");
            this.userId = new JSONObject(data).getString("userId");

        }

        if (this.bidId == null) {
            return;
        }

        HttpResponse<String> response = ApiRequest.get("/bid/"+ this.bidId + "?fields=messages");

        // if retrieve success
        if (response.statusCode() == 200){
            // set the default value of reply button to respond
            respondButton.setText("Respond");
            JSONObject bid = new JSONObject(response.body());
            updateBidDetails(bid);
            updateBidFunctions(bid);
        } else {
            String msg = "Error: " + new JSONObject(response.body()).get("message");
            JOptionPane.showMessageDialog(new JFrame(), msg, "Bad request", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Create the content to display the detail of the bid after user enter this page
     * @param bid the bid to display
     */
    private void updateBidDetails(JSONObject bid){
        JSONObject initiator = bid.getJSONObject("initiator");
        JSONObject subject = bid.getJSONObject("subject");
        JSONObject additionalInfo = bid.getJSONObject("additionalInfo");

        subjectLabel.setText("Subject: " + subject.getString("name"));
        nameLabel.setText("Name: " + initiator.getString("givenName") +" " + initiator.getString("familyName"));
        if (!additionalInfo.isEmpty()) {
            rate.setText("Rate: " + additionalInfo.getString("rate"));
            competency.setText("Minimum competency: " + additionalInfo.getInt("minCompetency"));
            day.setText("Preferred Day(s): " + additionalInfo.getString("day"));
            preferredSession.setText("Preferred no of sessions: " + additionalInfo.getInt("preferredSession") + " sessions per week");
            duration.setText("Duration: " + additionalInfo.getInt("duration") + " hours per lesson");
            startTime.setText("Start Time: " + additionalInfo.getString("startTime"));
        }
    }

    private void updateBidFunctions(JSONObject bid) {
        JSONArray messages = bid.getJSONArray("messages");
        String data = new JSONObject().put("bidId", this.bidId).put("userId", this.userId).toString();
        respondButton.setName(data);

        if (bid.getString("type").equals("close")) {
            monitorBidButton.setVisible(false);
            scrollPane.setVisible(false);
            buyoutButton.setVisible(false);
            if (hasReplied(messages)) {respondButton.setText("Message");}
        }
        // if bid type is open
        else if (bid.getString("type").equals("open")) {
            monitorBidButton.setVisible(true);
            scrollPane.setVisible(true);
            buyoutButton.setVisible(true);
            showTutors(messages);
            buyoutButton.setName(this.bidId);
            if (isMonitored(bidId)) {monitorBidButton.setText("Remove From Monitoring");}
            else {monitorBidButton.setText("Monitor Bid");}
        }
    }

    private boolean isMonitored(String bidId) {
        HttpResponse<String> userResponse = ApiRequest.get("/user/" + userId);
        JSONObject additionalInfo = new JSONObject(userResponse.body()).getJSONObject("additionalInfo");
        if (additionalInfo.has("monitoredBids")) {
            JSONArray monitoredBids = additionalInfo.getJSONArray("monitoredBids");
            for (int i = 0; i < monitoredBids.length(); i++) {
                JSONObject bid = (JSONObject) monitoredBids.get(i);
                if (bid.getString("id").equals(bidId)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void showTutors(JSONArray messages) {

        detailPane = new JPanel();
        detailPane.setBorder(new EmptyBorder(15, 15,15,15));
        detailPane.setLayout(new GridBagLayout());
        detailPane.setBackground(new Color(255, 252, 252));

        buttonArr = new ArrayList<>();
        // create a Panel to show each message replied by tutor
        if (messages.length() > 0){
            GridBagConstraints c = new GridBagConstraints();

            bidderLabel = new JLabel("Bidders");
            bidderLabel.setHorizontalAlignment(JLabel.CENTER);
            bidderLabel.setFont(new Font("Bahnschrift", Font.BOLD, 20));
            detailPane.add(bidderLabel, c);

            for (int i=0; i < messages.length(); i++) {
                JSONObject message = messages.getJSONObject(i);

                // create the panel for each bid item
                JPanel bidPanel = new JPanel();
                GridBagConstraints bidPanelConstraint = new GridBagConstraints();
                bidPanelConstraint.fill = GridBagConstraints.HORIZONTAL;
                bidPanelConstraint.weightx = 1;
                bidPanelConstraint.insets = new Insets(1,2,1,2);
                bidPanel.setLayout(new GridBagLayout());
                bidPanel.setBackground(Color.lightGray);
                bidPanel.setMinimumSize(new Dimension(100, 120));
                bidPanel.setMaximumSize(new Dimension(100, 120));

                // add a description jlabel
                bidPanelConstraint.gridx = 0;
                bidPanelConstraint.gridy = 0;
                bidPanelConstraint.gridwidth = 3;
                bidPanelConstraint.anchor = GridBagConstraints.WEST;
                JLabel bidLabel = new JLabel();
                JSONObject bidder = message.getJSONObject("poster");
                bidLabel.setText(bidder.get("givenName") + " " + bidder.get("familyName"));
                bidPanel.add(bidLabel, bidPanelConstraint);

                JLabel competency = new JLabel("Competency: Level " + message.getJSONObject("additionalInfo").getInt("minCompetency"));
                bidPanelConstraint.gridy = 1;
                bidPanel.add(competency, bidPanelConstraint);

                // add view detail button
                viewBidButton = new JButton("View Bid");
                bidPanelConstraint.gridy = 0;
                bidPanelConstraint.gridx = 6;
                bidPanelConstraint.gridwidth = 1;
                bidPanelConstraint.gridheight = 2;
                bidPanelConstraint.weightx = 0.2;
                bidPanel.add(viewBidButton, bidPanelConstraint);

                viewBidButton.addActionListener(e -> ViewManagerService.loadPage(ViewManagerService.FIND_TUTOR_RESPONSE));

                // set button name to bidId and userId for ClosedBidResponse class to close Bid
                JSONObject btnData = new JSONObject();
                btnData.put("bidId", message.get("id"));
                btnData.put("userId", this.userId);
                viewBidButton.setName(btnData.toString());
                buttonArr.add(viewBidButton); // add the button into button array

                c.gridy = detailPane.getComponentCount();
                detailPane.add(bidPanel, c);
            }
            scrollPane.setViewportView(detailPane);
        }
    }

    /**
     * Method to check if the tutor replied to this bid before
     * @return true if tutor replied to this message before and false otherwise
     */
    private Boolean hasReplied(JSONArray messages){
        if (messages.isEmpty()){ // if this bid has no messages
            return false;
        } else {
            for (int i=0; i< messages.length(); i++){
                JSONObject message = messages.getJSONObject(i);
                if (message.getJSONObject("poster").getString("id").equals(this.userId)){ // if user replied to this bid before
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Links the responseBtn with the appropriate page depending on whether the current bid is an open or closed bid
     */
    @Override
    public void addLinkListener(ActionListener listener) {
        if (buttonArr != null){ // check when the page first load during apps startup
            for (JButton btn: buttonArr){
                btn.addActionListener(listener);
            }
        }
    }

    public void addResponseListener(ActionListener listener) {
        respondButton.addActionListener(listener);
    }

    public void addMonitorListener(ActionListener listener) {monitorBidButton.addActionListener(listener);}

    /**
     * called by BidClosingController, which is activate by buyoutBtn
     */
    @Override
    public JSONObject retrieveInputs() {
        JSONObject bidInfo = new JSONObject();
        bidInfo.put("bidId", bidId);
        bidInfo.put("messageId", "");
        bidInfo.put("tutorId", "");
        bidInfo.put("hasExpired", false);
        bidInfo.put("userId", userId);
        return bidInfo;
    }

    /**
     * adds the action listener from a BidClosingController object
     */
    @Override
    public void addActionListener(ActionListener actionListener) {
        this.buyoutButton.addActionListener(actionListener);
    }
}
