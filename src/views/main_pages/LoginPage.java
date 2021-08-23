package views.main_pages;

import services.ViewManagerService;
import abstractions.ObserverInputInterface;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class LoginPage extends JPanel implements ObserverInputInterface {

    private JLabel activityTitle, usernameField, passwordField, registerField;
    private JTextField usernameInput;
    private JPasswordField passwordInput;
    private JButton loginUserButton, registerPageButton;

    public LoginPage() {
        this.setBorder(new EmptyBorder(15, 15, 15, 15));
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 0, 5);

        activityTitle = new JLabel("User Login");
        activityTitle.setHorizontalAlignment(JLabel.CENTER);
        activityTitle.setVerticalAlignment(JLabel.TOP);
        activityTitle.setFont(new Font("Bahnschrift", Font.BOLD, 20));
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 3;
        c.fill = GridBagConstraints.HORIZONTAL;
        this.add(activityTitle, c);


        usernameField = new JLabel("Username: ");
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        c.gridheight = 1;
        this.add(usernameField, c);
        usernameInput = new JTextField();
        c.gridx = 1;
        c.gridwidth = 2;
        this.add(usernameInput, c);

        passwordField = new JLabel("Password: ");
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 1;
        this.add(passwordField, c);
        passwordInput = new JPasswordField();
        c.gridx = 1;
        c.gridwidth = 2;
        this.add(passwordInput, c);

        loginUserButton = new JButton("Log In");
        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 3;
        this.add(loginUserButton, c);

        registerField = new JLabel("If you do not have a StuTor account, register for one instead here:");
        c.gridy = 5;
        c.gridx = 0;
        this.add(registerField, c);

        registerPageButton = new JButton("Register a StuTor Account");
        c.gridy = 6;
        c.gridx = 0;
        this.add(registerPageButton, c);


        registerPageButton.addActionListener(e -> ViewManagerService.loadPage(ViewManagerService.REGISTRATION_PAGE));
    }

    @Override
    public JSONObject retrieveInputs() {
        String username = usernameInput.getText();
        String password = passwordInput.getText();
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("userName", username).put("password", password);
        return jsonObj;
    }

    @Override
    public void addActionListener(ActionListener actionListener) {
        loginUserButton.addActionListener(actionListener);
    }
}
