package views.main_pages;

import abstractions.ObserverInputInterface;
import abstractions.ObserverOutputInterface;
import controllers.RenewContractController;
import org.json.JSONArray;
import org.json.JSONObject;
import services.ApiRequest;
import services.ViewManagerService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.net.http.HttpResponse;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.HashMap;

public class ViewContractDetails extends JPanel implements ObserverOutputInterface, ObserverInputInterface {

    private JLabel activityTitle, tutorField, qualificationField, lessonField, subjectField, dayField, expiryField, startTimeField, endTimeField, rateField, sessionLabel, typeField, durationLabel, rateLabel, sessionField, freeLessonField;
    private JTextField lessonInput, dayInput, rateInput, sessionInput, competencyInput, subjectInput, freeLessonInput;
    private JButton submitButton;
    private JButton backBtn;
    private JComboBox<String> startMeridiem,tutorCombo ;
    private JLabel typeName;
    private JSpinner startTime, duration, expireSpinner;
    private HashMap<String, String> tutorMapping;
    private String userId, contractId, subjectId;
    private Boolean isTutor, editable;

    public ViewContractDetails() {
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

        activityTitle = new JLabel("Contract Detail");
        activityTitle.setHorizontalAlignment(JLabel.CENTER);
        activityTitle.setVerticalAlignment(JLabel.TOP);
        activityTitle.setFont(new Font("Bahnschrift", Font.BOLD, 20));
        c.gridx = 0;
        c.weightx = 1;
        c.gridy = 0;
        c.gridwidth = 4;
        c.fill = GridBagConstraints.HORIZONTAL;
        this.add(activityTitle, c);

        // Tutor Field
        tutorField = new JLabel("Name: ");
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        c.gridheight = 1;
        this.add(tutorField, c);

        tutorCombo = new JComboBox<>();
        c.gridx = 1;
        c.gridy = 1;
        c.gridwidth = 1;
        c.gridheight = 1;
        this.add(tutorCombo, c);

        // Tutor Qualification
        qualificationField = new JLabel("Minimum Competency: ");
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 1;
        c.gridheight = 1;
        qualificationField.setEnabled(false);
        this.add(qualificationField, c);

        competencyInput = new JTextField();
        competencyInput.setText("3");
        competencyInput.setEnabled(false);
        c.gridx = 1;
        c.gridwidth = 4;
        this.add(competencyInput, c);

        //Subject Field
        subjectField = new JLabel("Subject: ");
        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 1;
        c.gridheight = 1;
        this.add(subjectField, c);

        subjectInput = new JTextField();
        c.gridx = 1;
        c.gridy = 3;
        c.gridwidth = 4;
        c.gridheight = 1;
        subjectInput.setEnabled(false);
        this.add(subjectInput, c);


        // Lesson
        lessonField = new JLabel("No of Lesson: ");
        c.gridx = 0;
        c.gridy = 4;
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
        c.gridy = 5;
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
        c.gridy = 6;
        c.gridwidth = 1;
        c.gridheight = 1;
        this.add(startTimeField, c);

        c.gridx = 1;
        startTime = new JSpinner(new SpinnerNumberModel(1, 1, 12, 1));
        this.add(startTime, c);

        c.gridx = 2;
        String[] meridiem = {"AM", "PM"};
        startMeridiem = new JComboBox<>(meridiem);
        this.add(startMeridiem, c);

        // Duration per session
        endTimeField = new JLabel("Duration: ");
        c.gridx = 0;
        c.gridy = 7;
        c.gridwidth = 1;
        c.gridheight = 1;
        this.add(endTimeField, c);

        c.gridx = 1;
        duration = new JSpinner(new SpinnerNumberModel(1, 1, 14, 1));
        this.add(duration, c);

        durationLabel = new JLabel(" hours per lesson");
        c.gridx = 2;
        c.gridy = 7;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 0.2;
        this.add(durationLabel, c);

        // Session per week
        sessionField = new JLabel("Preferred No of Lesson(s): ");
        c.gridx = 0;
        c.gridy = 8;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 1;
        this.add(sessionField, c);

        sessionInput = new JTextField();
        c.gridx = 1;
        c.gridy = 8;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 1;
        this.add(sessionInput, c);

        sessionLabel = new JLabel("sessions per week");
        c.gridx = 2;
        c.gridy = 8;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 0.2;
        this.add(sessionLabel, c);

        // Preferred Rate
        rateField = new JLabel("Rate: ");
        c.gridx = 0;
        c.gridy = 9;
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

        // free lesson
        freeLessonField = new JLabel("No of Free Lesson: ");
        c.gridx = 0;
        c.gridy = 10;
        c.gridwidth = 1;
        c.gridheight = 1;
        this.add(freeLessonField, c);

        freeLessonInput = new JTextField("1");
        c.gridx = 1;
        c.weightx = 0.2;
        c.gridwidth = 1;
        c.gridheight = 1;
        this.add(freeLessonInput, c);

        // checkbox to input bid type
        typeField = new JLabel("Bid Type");
        c.gridx = 0;
        c.gridy = 11;
        this.add(typeField, c);

        typeName = new JLabel("open");
        c.weightx = 0.1;
        c.gridx = 1;
        this.add(typeName, c);

        // contract length
        expiryField = new JLabel("Contract Duration");
        c.gridx = 0;
        c.gridy = 12;
        c.gridwidth = 1;
        c.gridheight = 1;
        this.add(expiryField, c);

        Integer[] contractLength = {3,6,12,24,36,48,96};
        expireSpinner = new JSpinner(new SpinnerListModel(contractLength));
        c.gridy = 12;
        c.gridx = 1;
        c.gridwidth = 2;
        this.add(expireSpinner, c);

        //submitBtn
        submitButton = new JButton("Submit Contract");
        c.weightx = 0.1;
        c.gridx = 0;
        c.gridy = 13;
        c.gridwidth = 4;
        this.add(submitButton, c);

        backBtn.addActionListener(e -> ViewManagerService.loadPage(ViewManagerService.DASHBOARD_PAGE));

    }

    @Override
    public JSONObject retrieveInputs() {

        JSONObject jsonObj = new JSONObject();

        if (isTutor) {
            jsonObj.put("isTutor", true);
            jsonObj.put("contractId", this.contractId);

        } else {
            String noOfLesson = lessonInput.getText();
            String day = dayInput.getText();
            String time = startTime.getValue().toString() + startMeridiem.getSelectedItem().toString();
            String rate = rateInput.getText();
            String competency = competencyInput.getText();

            // getting local timestamp
            Timestamp ts = Timestamp.from(ZonedDateTime.now().toInstant());
            Instant now = ts.toInstant();

            JSONObject lessonInfo = new JSONObject();
            lessonInfo.put("minCompetency", competency);
            lessonInfo.put("noOfLesson", noOfLesson);
            lessonInfo.put("day", day);
            lessonInfo.put("startTime", time);
            lessonInfo.put("duration", duration.getValue().toString());
            lessonInfo.put("preferredSession", Integer.valueOf(sessionInput.getText()));
            lessonInfo.put("rate", rate);
            lessonInfo.put("contractLength", expireSpinner.getValue());
            lessonInfo.put("freeLesson", freeLessonInput.getText());

            JSONObject additionalInfo = new JSONObject();
            additionalInfo.put("tutorSigned", false);
            additionalInfo.put("studentSigned", false);

            jsonObj.put("firstPartyId", tutorMapping.get(tutorCombo.getSelectedItem()));
            jsonObj.put("secondPartyId", this.userId);
            jsonObj.put("subjectId", this.subjectId);
            jsonObj.put("dateCreated", now);
            jsonObj.put("lessonInfo", lessonInfo);
            jsonObj.put("additionalInfo", additionalInfo);
            jsonObj.put("isTutor", false);
            jsonObj.put("contractId", this.contractId);

        }

        return jsonObj;
    }

    @Override
    public void addActionListener(ActionListener actionListener) {
        this.submitButton.addActionListener(actionListener);

    }

    @Override
    public void update(String data) {
        this.userId = new JSONObject(data).getString("userId");
        this.contractId = new JSONObject(data).getString("contractId");

        HttpResponse<String> response = ApiRequest.get("/contract/" + this.contractId);
        JSONObject contract = new JSONObject(response.body());
        JSONObject contractDetail = contract.getJSONObject("lessonInfo");
        getTutor(contractDetail.getInt("minCompetency"), contract.getJSONObject("subject").getString("id"));
        this.subjectId = contract.getJSONObject("subject").getString("id");
        tutorCombo.setSelectedItem(contract.getJSONObject("firstParty").getString("givenName") + " " + contract.getJSONObject("firstParty").getString("familyName"));

        subjectInput.setText(contract.getJSONObject("subject").getString("name"));
        duration.setValue(Integer.valueOf(contractDetail.getString("duration")));
        lessonInput.setText(contractDetail.getString("noOfLesson")); //fix this typo
        rateInput.setText(contractDetail.getString("rate"));
        sessionInput.setText(String.valueOf(contractDetail.getInt("preferredSession")));
        dayInput.setText(contractDetail.getString("day"));
        competencyInput.setText(String.valueOf(contractDetail.getInt("minCompetency")));
        expireSpinner.setValue(contractDetail.getInt("contractLength"));

        if (contract.isNull("freeLesson")){ // the case when tutor buy out the bid immediately there is not free lesson
            freeLessonInput.setText("0");
        } else {
            freeLessonInput.setText(String.valueOf(contractDetail.getInt("freeLesson")));
        }

        String time = contractDetail.getString("startTime");
        startMeridiem.setSelectedItem(time.substring(time.length()-2)); // get the meridiem
        startTime.setValue(Integer.valueOf(time.substring(0, time.length()-2))); // get the time

        // check if the student is tutor or or student
        JSONObject user = new JSONObject(ApiRequest.get("/user/" + this.userId + "?fields=initiatedBids").body());
        isTutor = user.getBoolean("isTutor");
        if (user.getBoolean("isStudent")) {
            editable = checkContractsBidsCount(user);
        } else {editable = true;}

        if (editable) {
            if (isTutor) {
                disableEdit();
                if (contract.isNull("dateSigned")) {
                    submitButton.setText("Sign Contract");
                    submitButton.setVisible(true);
                    submitButton.setEnabled(true);
                } else {
                    submitButton.setVisible(false);
                    submitButton.setEnabled(false);
                }
            } else {
                // if this is a signed contract which means for renewal
                if (contract.isNull("dateSigned")) {
                    submitButton.setText("Sign Contract");
                    disableEdit();

                } else { // else for student to sign renewed contract
                    submitButton.setText("Submit Contract");
                    enableEdit();

                }
            }

        } else {
            submitButton.setVisible(false);
        }

    }

    /**
     * checks if the student has 5 or more active contracts and bids, and disallows them from creating more bids
     */
    private boolean checkContractsBidsCount(JSONObject user) {

        int counter = 0;

        if (user.getJSONObject("additionalInfo").has("activeContract")){
            counter += user.getJSONObject("additionalInfo").getJSONArray("activeContract").length();
        }

        if (user.getJSONObject("additionalInfo").has("unsignedContract")){
            counter += user.getJSONObject("additionalInfo").getJSONArray("unsignedContract").length();
        }

        JSONArray bids = user.getJSONArray("initiatedBids");
        for (int i = 0; i < bids.length(); i++) {
            JSONObject bid = (JSONObject) bids.get(i);
            if (bid.isNull("dateClosedDown")) {
                counter++;
            }
        }
        return counter < 5;
    }

    /**
     * Method to disable editing if the user is a tutor
     */
    private void disableEdit(){
        tutorCombo.setEnabled(false);
        lessonInput.setEditable(false);
        dayInput.setEditable(false);
        startTime.setEnabled(false);
        startMeridiem.setEnabled(false);
        duration.setEnabled(false);
        sessionInput.setEditable(false);
        rateInput.setEditable(false);
        freeLessonInput.setEditable(false);
        expireSpinner.setEnabled(false);
        sessionInput.setEditable(false);

    }

    /**
     * Method to enable editing if the user is a tutor
     */
    private void enableEdit(){
        tutorCombo.setEnabled(true);
        lessonInput.setEditable(true);
        dayInput.setEditable(true);
        startTime.setEnabled(true);
        startMeridiem.setEnabled(true);
        duration.setEnabled(true);
        sessionInput.setEditable(true);
        rateInput.setEditable(true);
        freeLessonInput.setEditable(true);
        expireSpinner.setEnabled(true);
        sessionInput.setEditable(true);

    }

    /**
     * Method to get qualified tutor for this contract
     */
    private void getTutor(int minCompetency, String subjectId) {

        HttpResponse<String> response = ApiRequest.get("/user?fields=competencies&fields=competencies.subject");
        JSONArray users = new JSONArray(response.body());
        tutorCombo.removeAllItems();
        tutorMapping = new HashMap<>();

        // get current contract
        response = ApiRequest.get("/contract/" + this.contractId);
        JSONObject contract = new JSONObject(response.body());

        // filter for tutor that qualified to teach this unit
        for (int i=0; i < users.length(); i++) {
            if (users.getJSONObject(i).getBoolean("isTutor")) {
                JSONObject user = users.getJSONObject(i);

                // check this subject with every competency of this user
                JSONArray userCompetencies = user.getJSONArray("competencies");
                for (int j = 0; j < userCompetencies.length(); j++){

                    // current competency
                    JSONObject competency = userCompetencies.getJSONObject(j);

                    // if user know this subject
                    if (competency.getJSONObject("subject").get("id").equals(subjectId)) {

                        // compare the competency level
                        if (competency.getInt("level") >= minCompetency) {
                            String tutorName = user.getString("givenName") + " " + user.getString("familyName");
                            tutorCombo.addItem(tutorName);
                            tutorMapping.put(tutorName, user.getString("id"));
                        }
                    }
                }
            }
        }

    }



}
