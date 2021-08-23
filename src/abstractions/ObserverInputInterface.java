package abstractions;

import org.json.JSONObject;

import java.awt.event.ActionListener;

public interface ObserverInputInterface {

    JSONObject retrieveInputs();
    void addActionListener(ActionListener actionListener);

}
