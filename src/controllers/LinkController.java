package controllers;

import abstractions.Publisher;
import services.ViewManagerService;
import abstractions.ListenerLinkInterface;
import abstractions.ObserverOutputInterface;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * A special type of controller that has the main purpose of solely updating its subscribers and moving to the next page.
 */
public class LinkController extends Publisher implements ActionListener, ObserverOutputInterface {

    private ListenerLinkInterface inputPage;
    private String linkedPage;

    public LinkController(ListenerLinkInterface inputPage, String linkedPage) {
        super();
        this.inputPage = inputPage;
        this.linkedPage = linkedPage;
    }

    @Override
    public void update(String data) {
        inputPage.addLinkListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton thisBtn = (JButton) e.getSource();
        notifySubscribers(thisBtn.getName());
        ViewManagerService.loadPage(linkedPage);
    }
}
