package views.main_pages;

import abstractions.ListenerLinkInterface;
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
import java.util.ArrayList;

public class ViewContracts extends JPanel implements ObserverOutputInterface, ListenerLinkInterface {

    private JPanel contentPanel, unsignedContractPanel;
    private JScrollPane contentScrollPane, unsignedScrollPane;
    private JLabel activityTitle, unsignedContractLabel;
    private GridBagConstraints c;
    private JButton viewBidBtn, backBtn;
    private ArrayList<JButton> buttonArr;
    private String userId;
    private boolean isTutor;

    public ViewContracts() {
        this.setBorder(new EmptyBorder(2, 2, 2, 2));
        this.setLayout(new GridBagLayout());
        this.setBackground(new Color(245, 245, 220));
        c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(1,3,1,3); //spacing between each bids

        backBtn = new JButton("Back");
        c.gridy = 0;
        c.gridx = 0;
        c.weightx = 0.1;
        c.gridwidth = 1;
        this.add(backBtn, c);

        activityTitle = new JLabel("Contracts");
        activityTitle.setHorizontalAlignment(JLabel.CENTER);
        activityTitle.setVerticalAlignment(JLabel.TOP);
        activityTitle.setFont(new Font("Bahnschrift", Font.BOLD, 20));
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 0.2;
        c.gridwidth = 4;
        this.add(activityTitle, c);

        // wrap contentPanel inside a scrollpane
        contentScrollPane = new JScrollPane();
        c.gridy = 2;
        c.weightx = 1;
        c.weighty = 1;
        c.gridwidth = 7;
        c.gridx = 0;
        this.add(contentScrollPane, c);

        unsignedContractLabel = new JLabel();
        unsignedContractLabel = new JLabel("Unsigned Contract");
        unsignedContractLabel.setFont(new Font("Bahnschrift", Font.BOLD, 20));
        c.gridy = 3;
        c.weightx = 1;
        c.weighty = 1;
        c.gridwidth = 7;
        c.gridx = 0;
        this.add(unsignedContractLabel, c);

        // wrap unsignedContract panel inside a scrollpane
        unsignedScrollPane = new JScrollPane();
        c.gridy = 4;
        c.weightx = 1;
        c.weighty = 1;
        c.gridwidth = 7;
        c.gridx = 0;
        this.add(unsignedScrollPane, c);

        backBtn.addActionListener(e -> ViewManagerService.loadPage(ViewManagerService.DASHBOARD_PAGE));
    }

    /**
     *
     * @param data receive userId from dashboard page (bidUpdateController)
     */
    @Override
    public void update(String data) {
        this.userId = data;

        buttonArr = new ArrayList<>(); // array to store all button for each bidPanel

        this.remove(unsignedScrollPane);
        this.remove(unsignedContractLabel);
        contentPanel = new JPanel();
        contentPanel.setLayout(new GridBagLayout());
        contentPanel.setBackground(Color.lightGray);

        HttpResponse<String> response = ApiRequest.get("/user/" + this.userId);
        JSONArray contracts = filterContracts(new JSONObject(response.body()));

        createPanels(contracts, contentPanel);
        this.contentScrollPane.setViewportView(contentPanel);

        // show unsigned renewed contract
        contracts = filterUnsignedContract(new JSONObject(response.body()));
        if (contracts.length() > 0) {

            c.gridy = 3;
            c.weightx = 1;
            c.weighty = 1;
            c.gridwidth = 7;
            c.gridx = 3;
            this.add(unsignedContractLabel, c);

            c.gridy = 4;
            c.weightx = 1;
            c.weighty = 1;
            c.gridwidth = 7;
            c.gridx = 0;
            this.add(unsignedScrollPane, c);

            unsignedContractPanel = new JPanel();
            unsignedContractPanel.setLayout(new GridBagLayout());
            unsignedContractPanel.setBackground(Color.lightGray);

            createPanels(contracts, unsignedContractPanel);
            this.unsignedScrollPane.setViewportView(unsignedContractPanel);
        }
    }

    /**
     * Method to filter for unsigned renewed contract for student
     * @param  user user's jsonobject from db
     * @return JSONArray jsonarray of unsigned contract
     */
    private JSONArray filterUnsignedContract(JSONObject user) {

        JSONArray returnArr = new JSONArray();

        // show all contract renewed by student that hasn't been signed by tutor
        JSONArray unsignedContract = new JSONArray(user.getJSONObject("additionalInfo").optJSONArray("unsignedContract"));

        for (int i = 0; i < unsignedContract.length(); i++) {
            JSONObject contract = new JSONObject(ApiRequest.get("/contract/" + unsignedContract.get(i)).body());
            returnArr.put(contract);

        }
        return returnArr;

    }

    /**
     * Function to create all the previous contract when update functions are called
     * @param contracts JSONArray of all the contract from the API
     */
    private void createPanels(JSONArray contracts, JPanel thisPanel){

        // create a jPanel for each bids available
        if (contracts.length() > 0) {
            for (int i=0; i < contracts.length(); i++){

                JSONObject contract = contracts.getJSONObject(i);

                // create the panel for each contract item
                JPanel contractPanel = new JPanel();
                GridBagConstraints contractPanelConstraint = new GridBagConstraints();
                contractPanelConstraint.fill = GridBagConstraints.HORIZONTAL;
                contractPanelConstraint.weightx = 1;
                contractPanelConstraint.insets = new Insets(1,2,1,2);
                contractPanel.setLayout(new GridBagLayout());
                contractPanel.setBackground(Color.lightGray);
                contractPanel.setMinimumSize(new Dimension(100, 120));
                contractPanel.setMaximumSize(new Dimension(100, 120));

                // add a description jlabel
                contractPanelConstraint.gridx = 0;
                contractPanelConstraint.gridy = 0;
                contractPanelConstraint.gridwidth = 5;
                contractPanelConstraint.anchor = GridBagConstraints.WEST;
                JLabel bidLabel = new JLabel();
                bidLabel.setText(contract.getJSONObject("subject").getString("name"));
                contractPanel.add(bidLabel, contractPanelConstraint);

                // type jlabel
                JLabel peopleLabel = new JLabel();
                if (isTutor) {
                    peopleLabel.setText( "Initiator: " + contract.getJSONObject("secondParty").get("givenName") + " " + contract.getJSONObject("secondParty").get("familyName"));

                } else {
                    peopleLabel.setText( "Tutor: " + contract.getJSONObject("firstParty").get("givenName") + " " + contract.getJSONObject("firstParty").get("familyName"));

                }
                contractPanelConstraint.gridy = 1;
                contractPanel.add(peopleLabel, contractPanelConstraint);

                JLabel additionalLabel = new JLabel();
                // for tutor to view all renewed contract by student that is pending signing
                if (contract.isNull("dateSigned")) {
                    additionalLabel.setText("Rate: " + contract.getJSONObject("lessonInfo").getString("rate"));
                } else { // for student to view latest 5 signed contract
                    additionalLabel.setText("Signed On: " + contract.getString("dateSigned"));
                }

                contractPanelConstraint.gridy = 2;
                contractPanel.add(additionalLabel, contractPanelConstraint);

                // add view detail button
                contractPanelConstraint.gridy = 0;
                contractPanelConstraint.gridx = 6;
                contractPanelConstraint.gridwidth = 1;
                contractPanelConstraint.gridheight = 2;
                contractPanelConstraint.weightx = 0.2;
                viewBidBtn = new JButton("View Contract");

                // set button name to bidId and userId for ClosedBidResponse class to close Bid
                JSONObject btnData = new JSONObject();
                btnData.put("contractId", contract.get("id"));
                btnData.put("userId", this.userId);
                viewBidBtn.setName(btnData.toString());
                buttonArr.add(viewBidBtn); // add the button into button array
                contractPanel.add(viewBidBtn, contractPanelConstraint);

                contractPanelConstraint.gridx = 0;
                contractPanelConstraint.gridy = thisPanel.getComponentCount();
                contractPanelConstraint.gridwidth = 4;
                contractPanelConstraint.gridheight = 1;
                thisPanel.add(contractPanel, contractPanelConstraint);
            }

        } else { // if not relevant bid found
            JPanel contractPanel = new JPanel();
            JLabel noContract = new JLabel("No Contract Found");
            activityTitle.setHorizontalAlignment(JLabel.CENTER);
            activityTitle.setVerticalAlignment(JLabel.CENTER);
            activityTitle.setFont(new Font("Bahnschrift", Font.BOLD, 20));
            contractPanel.add(noContract);
            c.gridx = 1;
            c.gridwidth = 4;
            c.gridheight = 1;
            c.gridy = thisPanel.getComponentCount();
            c.anchor = GridBagConstraints.CENTER;
            thisPanel.add(contractPanel);
        }
    }

    /**
     * Function to - filter contract to the latest 5 contract signed for student
     *             - filter renewed contract for tutor that hasn't been signed
     * @param user user's jsonobject from db
     * @return JSONArray of the latest 5 contract
     */
    private JSONArray filterContracts(JSONObject user) {

        // check if user is tutor or student
        isTutor = user.getBoolean("isTutor");

        JSONArray returnArr = new JSONArray();
        JSONArray activeContract = new JSONArray(user.getJSONObject("additionalInfo").optJSONArray("activeContract"));

        for (int i = 0; i < activeContract.length(); i++) {
            JSONObject contract = new JSONObject(ApiRequest.get("/contract/" + activeContract.get(i)).body());
            returnArr.put(contract);

        }

        return returnArr;

    }

    @Override
    public void addLinkListener(ActionListener actionListener) {
        if(buttonArr != null) {
            for (JButton btn: buttonArr) {
                btn.addActionListener(actionListener);
            }
        }
    }
}
