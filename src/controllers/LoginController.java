package controllers;

import services.ApiRequest;
import abstractions.Publisher;
import services.ViewManagerService;
import abstractions.ObserverInputInterface;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.http.HttpResponse;

/**
 * Notifies necessary components of the user's ID, so that they can update themselves if necessary or process user interaction correctly
 */
public class LoginController extends Publisher implements ActionListener {

    private ObserverInputInterface inputPage;

    public LoginController(ObserverInputInterface inputPage) {
        super();
        this.inputPage = inputPage;
        inputPage.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JSONObject jsonObj = inputPage.retrieveInputs();
        HttpResponse<String> response = ApiRequest.post("/user/login", jsonObj.toString());
        if (response.statusCode() == 200) {
            ViewManagerService.loadPage(ViewManagerService.DASHBOARD_PAGE);
            notifySubscribers(getUserId(jsonObj.getString("userName")));
        } else if (response.statusCode() == 403) {
            JOptionPane.showMessageDialog(new JFrame(), "The username/password you have entered is invalid. Please try again.",
                    "Username/Password Invalid", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(new JFrame(), "Login failed! Error: " + response.statusCode(),
                    "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String getUserId(String username) {
        HttpResponse<String> response = ApiRequest.get("/user");
        if (response.statusCode() == 200) {
            JSONArray users = new JSONArray(response.body());
            JSONObject user;
            for (int i = 0; i < users.length(); i++) {
                user = users.getJSONObject(i);
                if (user.get("userName").equals(username)) {
                    return user.get("id").toString();
                }
            }
        }
        return null;
    }
}
