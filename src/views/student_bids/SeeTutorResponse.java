package views.student_bids;

import services.ApiRequest;
import abstractions.ListenerLinkInterface;
import services.ViewManagerService;
import abstractions.ObserverInputInterface;
import abstractions.ObserverOutputInterface;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.http.HttpResponse;

public class SeeTutorResponse extends JPanel implements ObserverOutputInterface, ObserverInputInterface, ListenerLinkInterface {

    private String messageId, userId, bidId, tutorId;
    private JLabel title, name, rate, competency, duration, startTime, day, preferredSession;
    private JButton backBtn;
    private  JButton messageBtn = new JButton("Message");
    private JButton confirmBtn = new JButton("Confirm Bid");
    private GridBagConstraints mainConst;
    private JPanel detailPane;

    public SeeTutorResponse() {
        this.setLayout(new GridBagLayout());
        mainConst = new GridBagConstraints();

        detailPane = new JPanel();
        detailPane.setBorder(new EmptyBorder(15, 15,15,15));
        detailPane.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1;
        c.weighty = 0.2;
        c.insets = new Insets(2, 2, 2, 2);
        c.fill = GridBagConstraints.HORIZONTAL;
        // inner panel for detail
        c.weightx = 0.5;
        c.weighty = 0.5;

        title = new JLabel("Bid Detail");
        title.setHorizontalAlignment(JLabel.CENTER);
        title.setVerticalAlignment(JLabel.TOP);
        title.setFont(new Font("Bahnschrift", Font.BOLD, 20));
        c.gridy = detailPane.getComponentCount();
        c.gridwidth = 3;
        c.gridx = 1;
        c.anchor = GridBagConstraints.PAGE_START;
        detailPane.add(title, c);

        name = new JLabel("Name: ");
        c.gridx = 0;
        c.gridwidth = 3;
        c.gridy = detailPane.getComponentCount();
        c.anchor = GridBagConstraints.PAGE_START;
        detailPane.add(name, c);

        rate = new JLabel("Rate not provided");
        c.gridy = detailPane.getComponentCount();
        detailPane.add(rate, c);

        competency = new JLabel("Competency not provided");
        c.gridy = detailPane.getComponentCount();
        detailPane.add(competency, c);

        day = new JLabel("Day not provided");
        c.gridy = detailPane.getComponentCount();
        detailPane.add(day, c);

        preferredSession = new JLabel("Preferred sessions not provided");
        c.gridy = detailPane.getComponentCount();
        detailPane.add(preferredSession, c);

        duration = new JLabel("Duration not provided");
        c.gridy = detailPane.getComponentCount();
        detailPane.add(duration, c);

        startTime = new JLabel("Start Time not provided");
        c.gridy = detailPane.getComponentCount();
        detailPane.add(startTime, c);

        mainConst.weighty = 0.2;
        mainConst.weightx = 0.2;
        mainConst.gridheight = 20;
        mainConst.gridx = 0;
        mainConst.gridy = 0;
        this.add(detailPane, mainConst);

        // add back button into this
        backBtn = new JButton("Back");
        mainConst.weighty = 0.2;
        mainConst.weightx = 0.2;
        mainConst.gridheight = 1;
        mainConst.fill = GridBagConstraints.HORIZONTAL;
        mainConst.gridx = 0;
        mainConst.gridy = 32;
        mainConst.gridwidth = 1;
        this.add(backBtn, mainConst);

        backBtn.addActionListener(e -> ViewManagerService.loadPage(ViewManagerService.DASHBOARD_PAGE));
    }

    /**
     * Create the content to display the detail of the message after user enter this page
     * @param message the message to display
     */
    private void createContent(JSONObject message){

        JSONObject initiator = message.getJSONObject("poster");
        JSONObject additionalInfo = message.getJSONObject("additionalInfo");
        this.bidId = message.getString("bidId");

        HttpResponse<String> response = ApiRequest.get("/bid/" + message.getString("bidId"));
        JSONObject bid = new JSONObject(response.body());

        name.setText("Name: " + initiator.getString("givenName") + " " + initiator.getString("familyName"));
        rate.setText("Rate: " + additionalInfo.get("rate"));
        competency.setText("Tutor competency: " + additionalInfo.get("minCompetency"));
        day.setText("Preferred Day(s): " + additionalInfo.get("day"));
        preferredSession.setText("Preferred no of sessions: " + additionalInfo.get("preferredSession") + " sessions per week");
        duration.setText("Duration: " + additionalInfo.get("duration") + " hours per lesson");
        startTime.setText("Start Time: " + additionalInfo.get("startTime"));

        mainConst.gridheight = 1;
        mainConst.gridx = 2;
        mainConst.gridy = 32;
        mainConst.fill = GridBagConstraints.HORIZONTAL;
        mainConst.weightx= 0.5;
        mainConst.gridwidth = 1;

        // if bid still open
        if (bid.isNull("dateClosedDown")) {
            if (bid.getString("type").equals("open")){  // if its open message
                confirmBtn.setName(this.bidId);
                this.add(confirmBtn, mainConst);

            } else if (bid.getString("type").equals("close")) {
                confirmBtn.setName(this.bidId);
                this.add(confirmBtn, mainConst);
                JSONObject data = new JSONObject().put("userId", this.userId).put("bidId", this.bidId);
                messageBtn.setName(data.toString());
                mainConst.gridy = 33;
                this.add(messageBtn, mainConst);

            }
        }

    }


    /**
     * Get the messageId from Find Bid page one user click on view bid to retrieve data from db
     * @param data any data that is crucial to the pages for them to request the information that they need from the database
     */
    @Override
    public void update(String data) {

        this.messageId = new JSONObject(data).getString("messageId");
        this.userId = new JSONObject(data).getString("userId");
        HttpResponse<String> response = ApiRequest.get("/message/"+ this.messageId);
        tutorId = new JSONObject(response.body()).getJSONObject("poster").getString("id");

        // if retrieve success
        if (response.statusCode() == 200){

            createContent(new JSONObject(response.body()));

        } else {
            String msg = "Error: " + new JSONObject(response.body()).get("message");
            JOptionPane.showMessageDialog(new JFrame(), msg, "Bad request", JOptionPane.ERROR_MESSAGE);
        }


    }

    @Override
    public JSONObject retrieveInputs() {
        JSONObject bidInfo = new JSONObject();
        bidInfo.put("bidId", bidId);
        bidInfo.put("messageId", messageId);
        bidInfo.put("tutorId", tutorId);
        bidInfo.put("hasExpired", false);
        return bidInfo;
    }

    @Override
    public void addActionListener(ActionListener actionListener) {
        this.confirmBtn.addActionListener(actionListener);
    }

    @Override
    public void addLinkListener(ActionListener actionListener) {
        this.messageBtn.addActionListener(actionListener);
    }
}
