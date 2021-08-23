package controllers;

import abstractions.ObserverInputInterface;
import abstractions.ObserverOutputInterface;
import abstractions.Publisher;
import org.json.JSONObject;
import contract_strategy.Contract;
import contract_strategy.RenewContractStrategy;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class RenewContractController extends Publisher implements ActionListener, ObserverOutputInterface {

    private ObserverInputInterface inputPage;
    private Contract contractUtil;
    private String userId;

    public RenewContractController(ObserverInputInterface inputPage) {
        super();
        inputPage.addActionListener(this);
        this.contractUtil = new Contract();
        contractUtil.setStrategy(new RenewContractStrategy());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton thisBtn = (JButton) e.getSource();
        this.inputPage = (ObserverInputInterface) thisBtn.getParent();

        if (thisBtn.getText().equals("Submit Contract")){
            contractUtil.postContract(inputPage.retrieveInputs());

        } else {
            JSONObject contractId = new JSONObject().put("id", inputPage.retrieveInputs().getString("contractId"));
            boolean isTutor = inputPage.retrieveInputs().optBoolean("isTutor");
            contractUtil.signContract(contractId, isTutor);
        }

        notifySubscribers(this.userId);
    }

    @Override
    public void update(String data) {
        this.userId = data;
    }
}
