package views.tutor_responds;

import services.ApiRequest;
import services.ViewManagerService;
import abstractions.ListenerLinkInterface;
import abstractions.ObserverOutputInterface;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.net.http.HttpResponse;
import java.util.ArrayList;

public class FindAllBids extends JPanel implements ListenerLinkInterface, ObserverOutputInterface {

    private JPanel contentPanel;
    private JScrollPane scrollPane;
    private JLabel activityTitle;
    private JSONArray bids;
    private GridBagConstraints c;
    private JButton viewBidBtn, backBtn;
    private ArrayList<JButton> buttonArr;
    private String userId;

    public FindAllBids() {
        this.setBorder(new EmptyBorder(2, 2, 2, 2));
        this.setLayout(new GridLayout(1,1, 2, 2));

        // wrap contentPanel inside a scrollpane
        scrollPane = new JScrollPane();
        this.add(scrollPane, c);

    }

    /***
     * Method to create all the bid available in the db and show it to the user inside the contentPanel
     */
    private void createContent() {

        buttonArr = new ArrayList<>();

        contentPanel.setLayout(new GridBagLayout());
        contentPanel.setMinimumSize(new Dimension(this.getWidth(), this.getHeight()));
        c = new GridBagConstraints();
        c.weightx = 1;
        c.insets = new Insets(1, 1, 1, 1);

        backBtn = new JButton("Back");
        c.gridy = 0;
        c.weightx = 0.0;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.gridx = 0;
        c.anchor = GridBagConstraints.PAGE_START;
        contentPanel.add(backBtn, c);

        activityTitle = new JLabel("Student Requests");
        activityTitle.setHorizontalAlignment(JLabel.CENTER);
        activityTitle.setVerticalAlignment(JLabel.TOP);
        activityTitle.setFont(new Font("Bahnschrift", Font.BOLD, 20));
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 1;
        c.gridwidth = 3;
        c.anchor = GridBagConstraints.NORTH;
        contentPanel.add(activityTitle, c);

        c.insets = new Insets(1,3,1,3); //spacing between each bids
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.PAGE_START;
        c.ipady = 50;

        // create a jPanel for each bids available
        if (bids.length() > 0) {
            for (int i=0; i < bids.length(); i++){

                JSONObject bid = bids.getJSONObject(i);

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
                bidPanelConstraint.gridwidth = 5;
                bidPanelConstraint.anchor = GridBagConstraints.WEST;
                JLabel bidLabel = new JLabel();
                bidLabel.setText(bid.getJSONObject("subject").get("name") + " (Level " +
                        bid.getJSONObject("additionalInfo").get("minCompetency") + ")");
                bidPanel.add(bidLabel, bidPanelConstraint);

                // type jlabel
                JLabel bidType = new JLabel();
                bidType.setText("Type: " + bid.getString("type"));
                bidPanelConstraint.gridy = 1;
                bidPanel.add(bidType, bidPanelConstraint);

                // initiator jlabel
                JLabel bidInitiator = new JLabel();
                bidInitiator.setText("Created By: " + bid.getJSONObject("initiator").getString("givenName") + " " +
                        bid.getJSONObject("initiator").getString("familyName"));
                bidPanelConstraint.gridy = 2;
                bidPanel.add(bidInitiator, bidPanelConstraint);

                // add view detail button
                bidPanelConstraint.gridy = 0;
                bidPanelConstraint.gridx = 6;
                bidPanelConstraint.gridwidth = 1;
                bidPanelConstraint.gridheight = 2;
                bidPanelConstraint.weightx = 0.2;
                viewBidBtn = new JButton("View Bid");

                // set button name to bidId and userId for ClosedBidResponse class to close Bid
                JSONObject btnData = new JSONObject();
                btnData.put("bidId", bid.get("id"));
                btnData.put("userId", this.userId);
                viewBidBtn.setName(btnData.toString());
                buttonArr.add(viewBidBtn); // add the button into button array
                bidPanel.add(viewBidBtn, bidPanelConstraint);

                c.gridx = 0;
                c.gridy = contentPanel.getComponentCount() - 1;
                c.gridwidth = 4;
                c.gridheight = 1;
                contentPanel.add(bidPanel, c);
            }

        } else { // if not relevant bid found
            JPanel bidPanel = new JPanel();
            JLabel noBid = new JLabel("No Bids Found");
            activityTitle.setHorizontalAlignment(JLabel.CENTER);
            activityTitle.setVerticalAlignment(JLabel.CENTER);
            activityTitle.setFont(new Font("Bahnschrift", Font.BOLD, 20));
            bidPanel.add(noBid);
            c.gridx = 0;
            c.gridy = contentPanel.getComponentCount();
            c.gridwidth = 4;
            c.gridheight = 1;
            contentPanel.add(bidPanel, c);
        }

        scrollPane.setViewportView(contentPanel);

        backBtn.addActionListener(e -> ViewManagerService.loadPage(ViewManagerService.DASHBOARD_PAGE));
    }

    /**
     * Update the bids inside this panels whenever this page is load
     * @param data The user id that are currently using this page
     */
    @Override
    public void update(String data) {

        this.userId = data;
        JSONObject user;
        bids = new JSONArray();

        // get all bid
        HttpResponse<String> response = ApiRequest.get("/bid");
        JSONArray returnedBids = new JSONArray(response.body());

        // get the detail of the user
        response = ApiRequest.get("/user/" + this.userId + "?fields=competencies&fields=competencies.subject");
        user = new JSONObject(response.body());

        if (user.getBoolean("isTutor")){

            // add every bid that is qualified to be taught by this tutor to bids
            for (int i = returnedBids.length() - 1; i > -1; i--){

                JSONObject bid = returnedBids.getJSONObject(i);

                // if the bid still open
                if (bid.isNull("dateClosedDown")) {
                    if (!bid.getJSONObject("additionalInfo").has("minCompetency")) {
                        bids.put(bid);

                    } else { // if that bid has competency
                        // check this subject with every competency of this user
                        JSONArray userCompetencies = user.getJSONArray("competencies");
                        for (int j = 0; j < userCompetencies.length(); j++){

                            // current competency
                            JSONObject competency = userCompetencies.getJSONObject(j);

                            // if user know this subject
                            if (competency.getJSONObject("subject").get("id").equals(bid.getJSONObject("subject").get("id"))) {

                                // compare the competency level
                                if (competency.getInt("level") >= (bid.getJSONObject("additionalInfo").getInt("minCompetency"))) {
                                    bids.put(bid);
                                }
                            }
                        }
                    }
                }
            }
        }

        // remake the jpanel
        contentPanel = new JPanel();
        createContent();
    }

    /**
     * Method to set event listener for every view bid button
     * @param actionListener actionListener for the view bid button
     */
    @Override
    public void addLinkListener(ActionListener actionListener) {

        if (buttonArr != null) {
            for (JButton button: buttonArr){
                button.addActionListener(actionListener);
            }
        }
    }
}
