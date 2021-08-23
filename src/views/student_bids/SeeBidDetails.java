package views.student_bids;

import services.ApiRequest;
import abstractions.ListenerLinkInterface;
import services.ViewManagerService;
import abstractions.ObserverOutputInterface;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.net.http.HttpResponse;
import java.util.ArrayList;

/**
 * Class for user to see the detail of each bids that they have opened
 */
public class SeeBidDetails extends JPanel implements ObserverOutputInterface, ListenerLinkInterface {

    private String bidId, userId;
    private JLabel title, subjectLabel, nameLabel, rate, competency, duration, startTime, day, preferredSession, bidderLabel;
    private JButton closeBtn = new JButton("Buy Out");
    private JButton replyBtn = new JButton("Bid");
    private JButton backButton, viewBidBtn;
    private JPanel detailPane;
    private JScrollPane scrollPane;
    private ArrayList<JButton> buttonArr;
    private GridBagConstraints mainConst;

    public SeeBidDetails(){
        this.setLayout(new GridBagLayout());
        mainConst = new GridBagConstraints();

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
        c.anchor = GridBagConstraints.PAGE_START;
        this.add(title, c);

        backButton = new JButton("Back");
        c.gridy = 0;
        c.weightx = 0.0;
        c.gridwidth = 1;
        c.gridx = 0;
        c.anchor = GridBagConstraints.PAGE_START;
        this.add(backButton, c);

        backButton.addActionListener(e -> ViewManagerService.loadPage(ViewManagerService.DASHBOARD_PAGE));

        subjectLabel = new JLabel("Subject: ");
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridwidth = 3;
        c.gridy = 1;
        c.anchor = GridBagConstraints.PAGE_START;
        this.add(subjectLabel, c);

        nameLabel = new JLabel("Name: ");
        c.gridx = 0;
        c.gridwidth = 3;
        c.gridy = 2;
        c.anchor = GridBagConstraints.PAGE_START;
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
        c.gridwidth = 10;
        c.fill = GridBagConstraints.HORIZONTAL;
        this.add(scrollPane, c);

    }

    /**
     * Create the content to display the detail of the bid after user enter this page
     * @param bid the bid to display
     */
    void createContent(JSONObject bid){

        JSONObject additionalInfo = bid.getJSONObject("additionalInfo");
        JSONArray messages = bid.getJSONArray("messages");

        subjectLabel.setText("Subject: " + bid.getJSONObject("subject").getString("name"));
        nameLabel.setText("Name: " + bid.getJSONObject("initiator").getString("givenName") +" " + bid.getJSONObject("initiator").getString("familyName"));

        showTutors(messages);

        if (!additionalInfo.isEmpty()) {

            // if rate is provided in the bid
            rate.setText("Rate: " + additionalInfo.getString("rate"));

            // if competency is provided in the bid
            competency.setText("Minimum competency: " + additionalInfo.getInt("minCompetency"));

            // if day is provided in the bid
            day.setText("Preferred Day(s): " + additionalInfo.getString("day"));

            // if preferred session is provided in the bid
            preferredSession.setText("Preferred no of sessions: " + additionalInfo.getInt("preferredSession") + " sessions per week");

            // if duration is provided in the bid
            duration.setText("Duration: " + additionalInfo.getInt("duration") + " hours per lesson");

            // if start time is provided in the bid
            startTime.setText("Start Time: " + additionalInfo.getString("startTime"));
        }

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

            for (int i = 0; i < messages.length(); i++){
                JSONObject message = messages.getJSONObject(i);

                if (!(message.getJSONObject("additionalInfo").isEmpty())){ // show only non 'message' message
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
                    JSONObject bidder = message.getJSONObject("poster");
                    bidLabel.setText(bidder.get("givenName") + " " + bidder.get("familyName"));
                    bidPanel.add(bidLabel, bidPanelConstraint);

                    JLabel competency = new JLabel("Competency: Level " + message.getJSONObject("additionalInfo").getInt("minCompetency"));
                    bidPanelConstraint.gridy = 1;
                    bidPanel.add(competency, bidPanelConstraint);

                    // add view detail button
                    bidPanelConstraint.gridy = 0;
                    bidPanelConstraint.gridx = 6;
                    bidPanelConstraint.gridwidth = 1;
                    bidPanelConstraint.gridheight = 2;
                    bidPanelConstraint.weightx = 0.2;
                    viewBidBtn = new JButton("View Bid");

                    viewBidBtn.addActionListener(e -> ViewManagerService.loadPage(ViewManagerService.SEE_TUTOR_RESPONSE));

                    // set button name to bidId and userId for ClosedBidResponse class to close Bid
                    JSONObject btnData = new JSONObject();
                    btnData.put("messageId", message.get("id"));
                    btnData.put("userId", this.userId);
                    viewBidBtn.setName(btnData.toString());
                    buttonArr.add(viewBidBtn); // add the button into button array
                    bidPanel.add(viewBidBtn, bidPanelConstraint);

                    c.gridy = detailPane.getComponentCount();
                    detailPane.add(bidPanel, c);
                }
            }
        }
        scrollPane.setViewportView(detailPane);
    }


    /**
     * Get the bidId from See Bid page one user click on view bid to retrieve data from db
     * @param data userId and bidId
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
            createContent(new JSONObject(response.body()));

        } else {
            String msg = "Error: " + new JSONObject(response.body()).get("message");
            JOptionPane.showMessageDialog(new JFrame(), msg, "Bad request", JOptionPane.ERROR_MESSAGE);
        }


    }

    @Override
    public void addLinkListener(ActionListener actionListener) {
        if (buttonArr != null){ // check when the page first load during apps startup
            for (JButton btn: buttonArr){
                btn.addActionListener(actionListener);
            }
        }

    }
}
