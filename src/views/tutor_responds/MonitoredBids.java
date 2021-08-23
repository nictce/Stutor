package views.tutor_responds;

import abstractions.ListenerLinkInterface;
import abstractions.ObserverOutputInterface;
import org.json.JSONArray;
import org.json.JSONObject;
import services.ApiRequest;
import services.ViewManagerService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicBorders;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.http.HttpResponse;
import java.util.ArrayList;

public class MonitoredBids extends JPanel implements ObserverOutputInterface, ListenerLinkInterface {

    private JScrollPane scrollPane;
    private JLabel activityTitle;
    private JButton backButton;
    private String userId;
    private ArrayList<JButton> buttonArr;

    public MonitoredBids() {
        this.setBorder(new EmptyBorder(15, 15, 15, 15));
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1;
        c.weighty = 1;
        c.insets = new Insets(5, 5, 0, 5);

        activityTitle = new JLabel("Monitored Bids");
        activityTitle.setHorizontalAlignment(JLabel.CENTER);
        activityTitle.setVerticalAlignment(JLabel.TOP);
        activityTitle.setFont(new Font("Bahnschrift", Font.BOLD, 20));
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 1;
        c.gridwidth = 3;
        c.anchor = GridBagConstraints.NORTH;
        this.add(activityTitle, c);

        backButton = new JButton("Back");
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 1;
        this.add(backButton, c);

        scrollPane = new JScrollPane();
        c.gridy = 1;
        c.gridwidth = 4;
        c.gridheight = 10;
        c.fill = GridBagConstraints.HORIZONTAL;
        this.add(scrollPane, c);

        backButton.addActionListener(e -> ViewManagerService.loadPage(ViewManagerService.DASHBOARD_PAGE));

    }

    @Override
    public void update(String data) {
        this.userId = data;
        HttpResponse<String> userResponse = ApiRequest.get("/user/" + userId);
        JSONObject user = new JSONObject(userResponse.body());

        if (user.getBoolean("isTutor") && user.getJSONObject("additionalInfo").has("monitoredBids")) {
            buttonArr = new ArrayList<>();
            JSONArray monitoredBids = filterValidBids(user.getJSONObject("additionalInfo"));
            JPanel monitoredBidsPanel = new JPanel();
            monitoredBidsPanel.setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            for (int i = 0; i < monitoredBids.length(); i++) {
                JSONObject bid = (JSONObject) monitoredBids.get(i);
                JPanel bidPanel = new JPanel();
                bidPanel.setBorder(new BasicBorders.MenuBarBorder(Color.LIGHT_GRAY, Color.LIGHT_GRAY));
                bidPanel.setLayout(new GridBagLayout());
                c.fill = GridBagConstraints.HORIZONTAL;
                c.weightx = 1;
                c.insets = new Insets(1,2,1,2);

                JLabel subject = new JLabel(bid.getJSONObject("subject").getString("name") + " (Level " +
                        bid.getJSONObject("additionalInfo").getInt("minCompetency") + ")");
                c.gridx = 0;
                c.gridy = 0;
                c.gridwidth = 3;
                c.gridheight = 1;
                bidPanel.add(subject, c);

                JLabel type = new JLabel("Type: " + bid.getString("type"));
                c.gridy = 1;
                bidPanel.add(type, c);

                JLabel student = new JLabel("Created by: " + bid.getJSONObject("initiator").getString("givenName") +
                        " " + bid.getJSONObject("initiator").getString("familyName"));
                c.gridy = 2;
                bidPanel.add(student, c);

                JButton viewBidButton = new JButton("View Bid");
                c.gridx = 3;
                c.gridy = 1;
                c.gridwidth = 1;
                bidPanel.add(viewBidButton, c);

                JSONObject btnData = new JSONObject();
                btnData.put("bidId", bid.get("id"));
                btnData.put("userId", userId);
                viewBidButton.setName(btnData.toString());

                buttonArr.add(viewBidButton);

                c.gridx = 0;
                c.gridy = monitoredBidsPanel.getComponentCount() * 3;
                c.gridwidth = 4;
                c.gridheight = 3;
                monitoredBidsPanel.add(bidPanel, c);
            }
            scrollPane.setViewportView(monitoredBidsPanel);
        }
    }

    private JSONArray filterValidBids(JSONObject additionalInfo) {
        JSONArray monitoredBids = additionalInfo.getJSONArray("monitoredBids");
        boolean modified = false;
        for (int i = 0; i < monitoredBids.length(); i++) {
            JSONObject bid = (JSONObject) monitoredBids.get(i);
            JSONObject realBid = new JSONObject(ApiRequest.get("/bid/" + bid.getString("id")).body());
            if (!realBid.isNull("dateClosedDown")) {
                monitoredBids.remove(i);
                modified = true;
            }
        }
        if (modified) {
            JSONObject userPatch = new JSONObject().put("additionalInfo", additionalInfo);
            ApiRequest.patch("/user/" + userId, userPatch.toString());
        }
        return monitoredBids;
    }

    @Override
    public void addLinkListener(ActionListener actionListener) {
        if (buttonArr != null) {
            for (JButton button: buttonArr) {
                button.addActionListener(actionListener);
            }
        }
    }
}
