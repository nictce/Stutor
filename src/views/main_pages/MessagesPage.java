package views.main_pages;

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

public class MessagesPage extends JPanel implements ObserverInputInterface, ObserverOutputInterface {

    private JLabel activityTitle, messages, addMessage;
    private JTextField messageInput;
    private JButton backButton, sendMessageButton;
    private JScrollPane messageList;
    private String bidId, userId;

    public MessagesPage() {

        this.setBorder(new EmptyBorder(15, 15, 15, 15));
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1;
        c.weighty = 1;
        c.insets = new Insets(5, 5, 0, 5);

        activityTitle = new JLabel("Messages");
        activityTitle.setHorizontalAlignment(JLabel.CENTER);
        activityTitle.setVerticalAlignment(JLabel.TOP);
        activityTitle.setFont(new Font("Bahnschrift", Font.BOLD, 20));
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 3;
        c.fill = GridBagConstraints.BOTH;
        this.add(activityTitle, c);

        messages = new JLabel("Messages: ");
        c.gridy = 1;
        c.gridwidth = 3;
        this.add(messages, c);

        messageList = new JScrollPane();
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 3;
        c.gridheight = 4;
        this.add(messageList, c);

        addMessage = new JLabel("Send a message: ");
        c.gridx = 0;
        c.gridy = 6;
        c.gridwidth = 3;
        c.gridheight = 1;
        this.add(addMessage, c);

        messageInput = new JTextField();
        c.gridx = 0;
        c.gridy = 7;
        c.gridwidth = 3;
        c.gridheight = 4;
        this.add(messageInput, c);

        backButton = new JButton("Back");
        c.gridy = 11;
        c.gridwidth = 1;
        c.gridheight = 1;
        this.add(backButton, c);

        sendMessageButton = new JButton("Send");
        c.gridx = 2;
        this.add(sendMessageButton, c);

        sendMessageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JSONObject message = retrieveInputs();
                HttpResponse<String> response = ApiRequest.post("/message", message.toString());
                JSONObject data = new JSONObject();
                data.put("bidId", bidId);
                data.put("userId", userId);
                update(data.toString());

                if (response.statusCode() == 201){
                    JOptionPane.showMessageDialog(new JFrame(), "Message Sent", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    String msg = "Error: " + new JSONObject(response.body()).get("message");
                    JOptionPane.showMessageDialog(new JFrame(), msg, "Bad request", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    @Override
    public void update(String data) {
        this.bidId = new JSONObject(data).getString("bidId");
        this.userId = new JSONObject(data).getString("userId");

        HttpResponse<String> response = ApiRequest.get("/bid/" + this.bidId + "?fields=messages");
        JSONObject bid = new JSONObject(response.body());
        if (response.statusCode() == 200) {
            this.messages.setText(bid.getJSONObject("initiator").getString("userName"));

            JSONArray messages = bid.optJSONArray("messages");
            JPanel messagesPanel = new JPanel();
            messagesPanel.setLayout(new BoxLayout(messagesPanel, BoxLayout.Y_AXIS));
            for (int j = 0; j < messages.length(); j++) {
                JPanel componentPanel = new JPanel();
                JSONObject message = (JSONObject) messages.get(j);
                componentPanel.add(new JLabel(message.optJSONObject("poster").optString("userName") + ": "));
                componentPanel.add(new JLabel(message.optString("content")));
                messagesPanel.add(componentPanel);
            }
            this.messageList.setViewportView(messagesPanel);
        }

        JSONObject user = new JSONObject(ApiRequest.get("/user/" + userId).body());
        // if tutor replying to student in findtutorbiddetail class
        if (user.getBoolean("isTutor")) { // bid.getJSONObject("initiator").getBoolean("isTutor")){
            backButton.addActionListener(e -> ViewManagerService.loadPage(ViewManagerService.FIND_BID_DETAILS));
        } else if (user.getBoolean("isStudent")){ // if student replying to student in seetutorfiddetail class

            backButton.addActionListener(e -> ViewManagerService.loadPage(ViewManagerService.SEE_BID_DETAILS));
        }

    }

    @Override
    public JSONObject retrieveInputs() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("bidId", bidId);
        jsonObject.put("posterId", userId);
        jsonObject.put("content", messageInput.getText());
        jsonObject.put("additionalInfo", new JSONObject());
        return jsonObject;
    }

    @Override
    public void addActionListener(ActionListener actionListener) {
        this.sendMessageButton.addActionListener(actionListener);
    }
}
