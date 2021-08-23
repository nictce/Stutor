package views.tutor_responds;

import services.ApiRequest;
import services.ViewManagerService;
import abstractions.ObserverOutputInterface;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.http.HttpResponse;

public class FindTutorResponse extends JPanel implements ObserverOutputInterface {
    private String messageId, userId;
    private JLabel title, name, rate, competency, duration, startTime, day, preferredSession;
    private JButton backBtn;
    private JPanel detailPane;
    private JScrollPane scrollPane;
    private GridBagConstraints mainConst;

    public FindTutorResponse() {
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
        mainConst.weighty = 0.3;
        mainConst.weightx = 0.2;
        mainConst.gridheight = 2;
        mainConst.gridx = 0;
        mainConst.gridy = 30;
        this.add(backBtn, mainConst);

        backBtn.addActionListener(e -> ViewManagerService.loadPage(ViewManagerService.FIND_BID_DETAILS));
    }

    /**
     * Create the content to display the detail of the bid after user enter this page
     * @param bid the bid to display
     */
    void createContent(JSONObject bid){

        JSONObject initiator = bid.getJSONObject("poster");
        JSONObject additionalInfo = bid.getJSONObject("additionalInfo");

        name.setText("Name: " + initiator.getString("givenName") + " " + initiator.getString("familyName"));

        rate.setText("Rate: " + additionalInfo.get("rate"));
        competency.setText("Tutor competency: " + additionalInfo.get("minCompetency"));
        day.setText("Preferred Day(s): " + additionalInfo.get("day"));
        preferredSession.setText("Preferred no of sessions: " + additionalInfo.get("preferredSession") + " sessions per week");
        duration.setText("Duration: " + additionalInfo.get("duration") + " hours per lesson");
        startTime.setText("Start Time: " + additionalInfo.get("startTime"));

    }

    /**
     * Get the bidId from Find Bid page one user click on view bid to retrieve data from db
     * @param data any data that is crucial to the pages for them to request the information that they need from the database
     */
    @Override
    public void update(String data) {

        this.messageId = new JSONObject(data).getString("bidId");
        this.userId = new JSONObject(data).getString("userId");
        HttpResponse<String> response = ApiRequest.get("/message/"+ this.messageId + "?fields=messages");

        // if retrieve success
        if (response.statusCode() == 200){
            createContent(new JSONObject(response.body()));

        } else {
            String msg = "Error: " + new JSONObject(response.body()).get("message");
            JOptionPane.showMessageDialog(new JFrame(), msg, "Bad request", JOptionPane.ERROR_MESSAGE);
        }


    }
}
