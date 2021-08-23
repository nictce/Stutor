package views.main_pages;

import services.ApiRequest;
import services.ViewManagerService;
import abstractions.ObserverOutputInterface;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.net.http.HttpResponse;

public class ProfilePage extends JPanel implements ObserverOutputInterface {

    private JLabel activityTitle, usernameField, nameField, accTypeField, competenciesField, qualificationsField, initBidsField;
    private JLabel username, name, accType;
    private JScrollPane competenciesList, qualificationsList;
    private JButton dashboardPageButton, logOutButton;
    private String userId;

    public ProfilePage() {
        this.setBorder(new EmptyBorder(15, 15, 15, 15));
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1;
        c.weighty = 1;
        c.insets = new Insets(5, 5, 0, 5);

        activityTitle = new JLabel("Profile");
        activityTitle.setHorizontalAlignment(JLabel.CENTER);
        activityTitle.setVerticalAlignment(JLabel.TOP);
        activityTitle.setFont(new Font("Bahnschrift", Font.BOLD, 20));
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 3;
        c.fill = GridBagConstraints.BOTH;
        this.add(activityTitle, c);

        usernameField = new JLabel("Username: ");
        c.gridy = 1;
        c.gridwidth = 1;
        this.add(usernameField, c);

        nameField = new JLabel("Name: ");
        c.gridy = 2;
        c.gridwidth = 1;
        this.add(nameField, c);

        accTypeField = new JLabel("Account Type: ");
        c.gridy = 3;
        c.gridwidth = 1;
        this.add(accTypeField, c);

        competenciesField = new JLabel("Competencies: ");
        c.gridy = 4;
        c.gridwidth = 1;
        this.add(competenciesField, c);

        qualificationsField = new JLabel("Qualifications: ");
        c.gridy = 8;
        c.gridwidth = 1;
        this.add(qualificationsField, c);

        username = new JLabel("");
        c.gridx = 1;
        c.gridy = 1;
        c.gridwidth = 2;
        this.add(username, c);

        name = new JLabel("");
        c.gridy = 2;
        c.gridwidth = 2;
        this.add(name, c);

        accType = new JLabel("");
        c.gridy = 3;
        c.gridwidth = 2;
        this.add(accType, c);

        dashboardPageButton = new JButton("Back to Dashboard");
        c.gridx = 2;
        c.gridy = 12;
        c.gridwidth = 1;
        this.add(dashboardPageButton, c);

        logOutButton = new JButton("Log Out");
        c.gridx = 1;
        this.add(logOutButton, c);

        competenciesList = new JScrollPane();
        c.gridy = 4;
        c.gridwidth = 2;
        c.gridheight = 4;
        this.add(competenciesList, c);

        qualificationsList = new JScrollPane();
        c.gridy = 8;
        c.gridwidth = 2;
        this.add(qualificationsList, c);

        dashboardPageButton.addActionListener(e -> ViewManagerService.loadPage(ViewManagerService.DASHBOARD_PAGE));

        logOutButton.addActionListener(e -> ViewManagerService.loadPage(ViewManagerService.LOGIN_PAGE));
    }

    @Override
    public void update(String userId) {
        this.userId = userId;
        HttpResponse<String> response = ApiRequest.get("/user/" + this.userId + "?fields=competencies.subject&fields=qualifications");
        if (response.statusCode() == 200) {
            JSONObject user = new JSONObject(response.body());
            this.username.setText(user.get("userName").toString());
            this.name.setText(user.get("givenName") + " " + user.get("familyName"));
            String accountType = "";
            if (user.optBoolean("isStudent")) {
                accountType += "Student";
            }
            if (user.optBoolean("isTutor")) {
                if (accountType.length() > 0) {
                    accountType += " ";
                }
                accountType += "Tutor";
            }
            this.accType.setText(accountType);

            JSONArray competencies = user.optJSONArray("competencies");
            JPanel competenciesPanel = new JPanel();
            competenciesPanel.setLayout(new BoxLayout(competenciesPanel, BoxLayout.Y_AXIS));
            for (int j = 0; j < competencies.length(); j++) {
                JPanel componentPanel = new JPanel();
                JSONObject competency = (JSONObject) competencies.get(j);
                componentPanel.add(new JLabel(
                        competency.optJSONObject("subject").optString("name") + " - " +
                                competency.optJSONObject("subject").optString("description") +
                                " (Level " + competency.optInt("level") + ")"));
                competenciesPanel.add(componentPanel);
            }
            this.competenciesList.setViewportView(competenciesPanel);

            JSONArray qualifications = user.optJSONArray("qualifications");
            JPanel qualificationsPanel = new JPanel();
            qualificationsPanel.setLayout(new BoxLayout(qualificationsPanel, BoxLayout.Y_AXIS));
            for (int j = 0; j < qualifications.length(); j++) {
                JPanel componentPanel = new JPanel();
                JSONObject qualification = (JSONObject) qualifications.get(j);
                String desc = qualification.optString("title");
                if (!qualification.optString("description").equals("")) {
                    desc += " - " + qualification.optString("description");
                }
                if (!qualification.optBoolean("verified")) {
                    desc += " (Unverified)";
                }
                componentPanel.add(new JLabel(desc));
                qualificationsPanel.add(componentPanel);
            }
            this.qualificationsList.setViewportView(qualificationsPanel);
        }
    }
}
