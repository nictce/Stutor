package views.tutor_responds;

import abstractions.ObserverInputInterface;
import abstractions.ObserverOutputInterface;
import org.json.JSONArray;
import org.json.JSONObject;
import services.ApiRequest;
import services.ViewManagerService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.http.HttpResponse;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZonedDateTime;

public class BidResponse extends JPanel implements ObserverInputInterface, ObserverOutputInterface {

    private JLabel activityTitle, lessonField, dayField,sessionLabel, expiryField, startTimeField, sessionField, durationLabel, rateLabel, durationField, rateField, freeLessonField, messageField;
    private JTextField lessonInput, dayInput, rateInput, sessionInput;
    private JButton submitButton, backBtn;
    private JTextArea messageInput;
    private JComboBox<String> startMeridiem;
    private JSpinner duration, freeLesson, startTime, expireSpinner;
    private String bidId, userId;
    private boolean isClose; // true is this page is showing open bid
    private GridBagConstraints c;

    public BidResponse() {
        String[] meridiem = {"AM", "PM"};

        this.setBorder(new EmptyBorder(15, 15, 15, 15));
        this.setLayout(new GridBagLayout());
        c = new GridBagConstraints();
        c.weightx = 1;
        c.insets = new Insets(5, 5, 0, 5);

        backBtn = new JButton("Back");
        c.weightx = 0.2;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        this.add(backBtn, c);

        activityTitle = new JLabel("Bid for request: " + this.bidId);
        activityTitle.setHorizontalAlignment(JLabel.CENTER);
        activityTitle.setVerticalAlignment(JLabel.TOP);
        activityTitle.setFont(new Font("Bahnschrift", Font.BOLD, 20));
        c.gridx = 1;
        c.weightx = 0.5;
        c.gridy = 0;
        c.gridwidth = 4;
        c.fill = GridBagConstraints.HORIZONTAL;
        this.add(activityTitle, c);

        // Lesson
        lessonField = new JLabel("No of Lesson: ");
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        c.gridheight = 1;
        this.add(lessonField, c);

        lessonInput = new JTextField();
        c.gridx = 1;
        c.gridwidth = 4;
        this.add(lessonInput, c);

        // Preferred Day
        dayField = new JLabel("Preferred Day(s): ");
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 1;
        c.gridheight = 1;
        this.add(dayField, c);

        dayInput = new JTextField();
        c.gridx = 1;
        c.gridwidth = 4;
        this.add(dayInput, c);

        // Duration per session
        durationField = new JLabel("Duration: ");
        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 1;
        c.gridheight = 1;
        this.add(durationField, c);

        c.gridx = 1;
        duration = new JSpinner(new SpinnerNumberModel(1, 1, 14, 1));
        this.add(duration, c);

        durationLabel = new JLabel(" hours per lesson");
        c.gridx = 2;
        c.gridy = 3;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 0.2;
        this.add(durationLabel, c);

        // Session per week
        sessionField = new JLabel("Preferred No of Lesson(s): ");
        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 1;
        this.add(sessionField, c);

        sessionInput = new JTextField();
        c.gridx = 1;
        c.gridy = 4;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 1;
        this.add(sessionInput, c);

        sessionLabel = new JLabel("sessions per week");
        c.gridx = 2;
        c.gridy = 4;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 0.2;
        this.add(sessionLabel, c);

        // Start time
        startTimeField = new JLabel("Preferred Time: ");
        c.gridx = 0;
        c.gridy = 5;
        c.gridwidth = 1;
        c.gridheight = 1;
        this.add(startTimeField, c);

        c.gridx = 1;
        startTime = new JSpinner(new SpinnerNumberModel(1, 1, 12, 1));
        this.add(startTime, c);

        c.gridx = 2;
        startMeridiem = new JComboBox<>(meridiem);
        this.add(startMeridiem, c);

        // Preferred Rate
        rateField = new JLabel("Rate: ");
        c.gridx = 0;
        c.gridy = 6;
        c.gridwidth = 1;
        c.gridheight = 1;
        this.add(rateField, c);

        rateInput = new JTextField();
        c.gridx = 1;
        c.gridwidth = 1;
        this.add(rateInput, c);

        rateLabel = new JLabel("dollar per hour");
        c.gridx = 2;
        c.weightx = 0.2;
        c.gridwidth = 1;
        c.gridheight = 1;
        this.add(rateLabel, c);

        // option to provide free lesson
        freeLessonField = new JLabel("Free Lesson: ");
        c.gridx = 0;
        c.gridy = 7;
        c.gridwidth = 3;
        this.add(freeLessonField, c);

        freeLesson = new JSpinner(new SpinnerNumberModel(0,0,5,1));
        c.gridx = 1;
        c.gridy = 7;
        c.weightx = 0;
        c.gridwidth = 2;
        this.add(freeLesson, c);

        // contract Length
        expiryField = new JLabel("Contract Length");
        c.gridy = 8;
        c.gridx = 0;
        c.gridwidth = 1;
        this.add(expiryField, c);

        Integer[] contractLength = {3,6,12,24,36,48,96};
        expireSpinner = new JSpinner(new SpinnerListModel(contractLength));
        c.gridy = 8;
        c.gridx = 1;
        c.gridwidth = 2;
        this.add(expireSpinner, c);

        // messages
        messageField = new JLabel("Message: ");
        c.gridx = 0;
        c.gridy = 9;
        c.gridwidth = 1;
        this.add(messageField, c);

        messageInput = new JTextArea(5, 20);
        messageInput.setLineWrap(true);
        messageInput.setWrapStyleWord(true);
        c.gridx = 1;
        c.gridy = 9;
        c.gridwidth = 3;
        c.gridheight = 2;
        c.weighty = 0;
        this.add(messageInput, c);

        //submitBtn
        submitButton = new JButton("Submit Bid");
        c.gridx = 0;
        c.weightx = 1;
        c.gridy = 12;
        c.gridwidth = 4;
        this.add(submitButton, c);

        backBtn.addActionListener(e -> ViewManagerService.loadPage(ViewManagerService.FIND_BID_DETAILS));
    }

    @Override
    public JSONObject retrieveInputs() {

        // getting local timestamp
        Timestamp ts = Timestamp.from(ZonedDateTime.now().toInstant());
        Instant now = ts.toInstant();

        String noOfLesson = lessonInput.getText();
        String day = dayInput.getText();
        String time = startTime.getValue().toString() + startMeridiem.getSelectedItem().toString();
        String rate = rateInput.getText();
        String contractLength = expireSpinner.getValue().toString();

        // creating the json body to pass to response bid listener
        JSONObject additionalInfo = new JSONObject();
        additionalInfo.put("noOfLesson", noOfLesson);
        additionalInfo.put("day", day);
        additionalInfo.put("contractLength", contractLength);
        additionalInfo.put("startTime", time);
        additionalInfo.put("duration", duration.getValue().toString());
        additionalInfo.put("preferredSession", Integer.valueOf(sessionInput.getText()));
        additionalInfo.put("rate", rate);
        additionalInfo.put("freeLesson", freeLesson.getValue());
        JSONArray competencies = new JSONObject(ApiRequest.get("/user/" + userId + "?fields=competencies.subject").body())
                .getJSONArray("competencies");
        String subjectId = new JSONObject(ApiRequest.get("/bid/" + bidId).body()).getJSONObject("subject").getString("id");
        for (int i = 0; i < competencies.length(); i++) {
            JSONObject competency = (JSONObject) competencies.get(i);
            if (competency.getJSONObject("subject").getString("id").equals(subjectId)) {
                additionalInfo.put("minCompetency", competency.getInt("level"));
                break;
            }
        }

        additionalInfo.put("tutorSigned", true);
        additionalInfo.put("studentSigned", true);

        JSONObject jsonObj = new JSONObject();
        jsonObj.put("bidId", this.bidId);
        jsonObj.put("posterId", this.userId);
        jsonObj.put("datePosted", now);

        // check for open or close bid for the content field
        if (isClose) {
            jsonObj.put("content", (messageInput.getText().equals("")) ? "string" : messageInput.getText()); // if messageInput empty return string else get messageInput
        } else {
            jsonObj.put("content", " ");
        }
        jsonObj.put("additionalInfo", additionalInfo);

        return jsonObj;
    }

    @Override
    public void addActionListener(ActionListener actionListener) {
        submitButton.addActionListener(actionListener);
    }

    @Override
    public void update(String data) {
        JSONObject jsonObject = new JSONObject(data);
        this.bidId = jsonObject.getString("bidId");
        this.userId = jsonObject.getString("userId");

        HttpResponse<String> response = ApiRequest.get("/bid/" + this.bidId + "?fields=messages");
        JSONObject bid = new JSONObject(response.body());
        String subjectName = bid.getJSONObject("subject").getString("name");
        activityTitle.setText(subjectName);

        this.remove(messageInput);
        this.remove(messageField);

        if (bid.getString("type").equals("close")) { // if is close bid show message field
            this.isClose = true;

            // messages
            messageField = new JLabel("Message: ");
            c.gridx = 0;
            c.gridy = 8;
            c.gridwidth = 1;
            this.add(messageField, c);

            messageInput = new JTextArea(5, 20);
            messageInput.setLineWrap(true);
            messageInput.setWrapStyleWord(true);
            c.gridx = 1;
            c.gridy = 8;
            c.gridwidth = 3;
            c.gridheight = 2;
            c.weighty = 0;
            this.add(messageInput, c);

        } else {
            this.isClose = false;
            this.remove(messageInput);
            this.remove(messageField);
        }

        checkPrevResponse(bid);

        // set the contract duration given by student as default in expireSpinner
        expireSpinner.setValue(Integer.valueOf(bid.getJSONObject("additionalInfo").getString("contractLength")));

        // set the name of this button as bidId and userId for querying with db
        JSONObject btnData = new JSONObject();
        btnData.put("bidId", this.bidId);
        btnData.put("userId", this.userId);
        submitButton.setName(btnData.toString());
    }

    private void checkPrevResponse(JSONObject bid) {
        JSONArray messages = bid.getJSONArray("messages");
        for (int i = 0; i < messages.length(); i++) {
            JSONObject message = messages.getJSONObject(i);
            if (message.getJSONObject("poster").getString("id").equals(userId) && !message.isNull("additionalInfo")) {
                submitButton.setText("Revise Response");
                JSONObject additionalInfo = message.getJSONObject("additionalInfo");

                lessonInput.setText(additionalInfo.getString("noOfLesson"));
                dayInput.setText(additionalInfo.getString("day"));
                duration.setValue(Integer.parseInt(additionalInfo.getString("duration")));
                sessionInput.setText(String.valueOf(additionalInfo.getInt("preferredSession")));
                String time = additionalInfo.getString("startTime");
                startMeridiem.setSelectedItem(time.substring(time.length()-2)); // get the meridiem
                startTime.setValue(Integer.valueOf(time.substring(0, time.length()-2))); // get the time
                rateInput.setText(additionalInfo.getString("rate"));
                freeLesson.setValue(additionalInfo.isNull("freeLesson") ? 0 : additionalInfo.getInt("freeLesson"));
                expireSpinner.setValue(Integer.parseInt(additionalInfo.getString("contractLength")));

                return;
            }
        }
        submitButton.setText("Submit Bid");
    }
}
