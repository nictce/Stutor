package views.student_bids;

import services.ApiRequest;
import services.ViewManagerService;
import abstractions.ObserverInputInterface;
import abstractions.ObserverOutputInterface;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.http.HttpResponse;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;

public class CreateBid extends JPanel implements ObserverInputInterface, ObserverOutputInterface {

    private JLabel activityTitle, subjectField, qualificationField, lessonField, expireField, dayField, startTimeField, endTimeField, rateField, sessionLabel, typeField, durationLabel, rateLabel, sessionField;
    private JTextField lessonInput, dayInput, rateInput, sessionInput;
    private JButton submitButton;
    private JButton backBtn;
    private JComboBox<String> startMeridiem, typeCombo, subjectCombo, competencyCombo;
    private JSpinner startTime, duration, expireSpinner;
    private HashMap<String, String> subjectMapping;
    private String userId;

    public CreateBid(){
        submitButton = new JButton("Submit Request");
    }

    /**
     * Get the user competency to populate the jcombo box for the subject that the student can
     * request for tutor
     */
    private void getUserCompetency() {
        subjectMapping = new HashMap<>();

        if (this.userId != null){
            HttpResponse<String> response = ApiRequest.get("/user/" + this.userId + "?fields=competencies&fields=competencies.subject");
            JSONObject user = new JSONObject(response.body());
            JSONArray competencies = new JSONArray(user.getJSONArray("competencies"));

            // for every competencies, add into the hashmap mapping
            for (int i=0; i<competencies.length(); i++){

                JSONObject subject = competencies.getJSONObject(i).getJSONObject("subject");

                subjectMapping.put(subject.get("name").toString(), subject.get("id").toString());

            }
        }

    }

    private void buildPage(){
        String[] meridiem = {"AM", "PM"};

        this.setBorder(new EmptyBorder(15, 15, 15, 15));
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1;
        c.insets = new Insets(5, 5, 0, 5);

        backBtn = new JButton("Back");
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.weightx = 0.2;
        this.add(backBtn, c);

        activityTitle = new JLabel("Request for a Tutor");
        activityTitle.setHorizontalAlignment(JLabel.CENTER);
        activityTitle.setVerticalAlignment(JLabel.TOP);
        activityTitle.setFont(new Font("Bahnschrift", Font.BOLD, 20));
        c.gridx = 0;
        c.weightx = 1;
        c.gridy = 0;
        c.gridwidth = 4;
        c.fill = GridBagConstraints.HORIZONTAL;
        this.add(activityTitle, c);

        // subject
        subjectField = new JLabel("Subject Name: ");
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        c.gridheight = 1;
        this.add(subjectField, c);

        // retrieve all the subject name from the key mapping
        ArrayList<String> subjectsName = new ArrayList<>(subjectMapping.keySet());
        // convert arraylist into array for combobox
        String[] subjectsNameArr = new String[subjectsName.size()];
        subjectsName.toArray(subjectsNameArr);
        subjectCombo = new JComboBox<>(subjectsNameArr);
        c.gridx = 1;
        c.gridwidth = 4;
        this.add(subjectCombo, c);

        // Tutor Qualification
        qualificationField = new JLabel("Minimum Qualification: ");
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 1;
        c.gridheight = 1;
        this.add(qualificationField, c);

        competencyCombo = new JComboBox<>(new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"});
        c.gridx = 1;
        c.gridwidth = 4;
        this.add(competencyCombo, c);

        // Lesson
        lessonField = new JLabel("No of Lesson: ");
        c.gridx = 0;
        c.gridy = 3;
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
        c.gridy = 4;
        c.gridwidth = 1;
        c.gridheight = 1;
        this.add(dayField, c);

        dayInput = new JTextField();
        c.gridx = 1;
        c.gridwidth = 4;
        this.add(dayInput, c);

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

        // Duration per session
        endTimeField = new JLabel("Duration: ");
        c.gridx = 0;
        c.gridy = 6;
        c.gridwidth = 1;
        c.gridheight = 1;
        this.add(endTimeField, c);

        c.gridx = 1;
        duration = new JSpinner(new SpinnerNumberModel(1, 1, 14, 1));
        this.add(duration, c);

        durationLabel = new JLabel(" hours per lesson");
        c.gridx = 2;
        c.gridy = 6;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 0.2;
        this.add(durationLabel, c);

        // Session per week
        sessionField = new JLabel("Preferred No of Lesson(s): ");
        c.gridx = 0;
        c.gridy = 7;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 1;
        this.add(sessionField, c);

        sessionInput = new JTextField();
        c.gridx = 1;
        c.gridy = 7;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 1;
        this.add(sessionInput, c);

        sessionLabel = new JLabel("sessions per week");
        c.gridx = 2;
        c.gridy = 7;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 0.2;
        this.add(sessionLabel, c);

        // Preferred Rate
        rateField = new JLabel("Rate: ");
        c.gridx = 0;
        c.gridy = 8;
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

        // expiry date
        expireField = new JLabel("Contract length: ");
        c.gridx = 0;
        c.gridy = 9;
        c.gridwidth = 1;
        c.gridheight = 1;
        this.add(expireField, c);

        Integer[] contractLength = {3,6,12,24,36,48,96};
        expireSpinner = new JSpinner(new SpinnerListModel(contractLength));
        expireSpinner.setValue(Integer.valueOf(6));
        c.gridx = 1;
        c.gridwidth = 1;
        c.gridheight = 1;
        this.add(expireSpinner, c);

        // checkbox to input bid type
        typeField = new JLabel("Bid Type");
        c.gridx = 0;
        c.gridy = 10;
        this.add(typeField, c);

        typeCombo = new JComboBox<>(new String[]{"open", "close"});
        c.weightx = 0.1;
        c.gridx = 1;
        this.add(typeCombo, c);

        //submitBtn
        c.weightx = 0.1;
        c.gridx = 0;
        c.gridy = 11;
        c.gridwidth = 4;
        this.add(submitButton, c);

        backBtn.addActionListener(e -> ViewManagerService.loadPage(ViewManagerService.DASHBOARD_PAGE));
    }

    @Override
    public JSONObject retrieveInputs() {

        String noOfLesson = lessonInput.getText();
        String day = dayInput.getText();
        String time = startTime.getValue().toString() + startMeridiem.getSelectedItem().toString();
        String rate = rateInput.getText();
        String contractLength = expireSpinner.getValue().toString();
        Integer competency = Integer.valueOf(competencyCombo.getSelectedItem().toString());
        String subjectId = subjectMapping.get(subjectCombo.getSelectedItem());

        // getting local timestamp
        Timestamp ts = Timestamp.from(ZonedDateTime.now().toInstant());
        Instant now = ts.toInstant();

        JSONObject additionalInfo = new JSONObject();
        additionalInfo.put("minCompetency", competency);
        additionalInfo.put("noOfLesson", noOfLesson);
        additionalInfo.put("day", day);
        additionalInfo.put("startTime", time);
        additionalInfo.put("duration", duration.getValue().toString());
        additionalInfo.put("preferredSession", Integer.valueOf(sessionInput.getText()));
        additionalInfo.put("rate", rate);
        additionalInfo.put("contractLength", contractLength);

        JSONObject jsonObj = new JSONObject();
        jsonObj.put("subjectId", subjectId);
        jsonObj.put("type", typeCombo.getSelectedItem().toString());
        jsonObj.put("initiatorId", this.userId);
        jsonObj.put("dateCreated", now);
        jsonObj.put("additionalInfo", additionalInfo);

        return jsonObj;
    }

    @Override
    public void addActionListener(ActionListener actionListener) {
        this.submitButton.addActionListener(actionListener);
    }

    /***
     * Get update of the current user id when user login to get all the subject required and build the page (called after user login)
     * @param data any data that is crucial to the pages for them to request the information that they need from the database
     */
    @Override
    public void update(String data) {
        this.userId = data;

        this.removeAll();
        this.repaint();
        this.revalidate();

        getUserCompetency();
        buildPage();
    }
}
