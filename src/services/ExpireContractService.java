package services;

import abstractions.ObserverOutputInterface;
import org.json.JSONArray;
import org.json.JSONObject;
import services.ApiRequest;
import services.ViewManagerService;

import javax.swing.*;
import java.net.http.HttpResponse;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

public class ExpireContractService implements ObserverOutputInterface {

    private String userId;
    private JSONObject contract;
    private Instant currentTime;
    private ObserverOutputInterface outputPage;

    public ExpireContractService(ObserverOutputInterface outputPage) {
        this.outputPage = outputPage;
    }

    @Override
    public void update(String data) {
        userId = data;
        HttpResponse<String> contractResponse = ApiRequest.get("/contract");
        JSONArray contracts = new JSONArray(contractResponse.body());

        for (int i=0; i < contracts.length(); i++) {
            contract = (JSONObject) contracts.get(i);

            if (!contract.isNull("dateSigned") && contract.isNull("terminationDate")) {
                String user;
                if (contract.getJSONObject("firstParty").getString("id").equals(userId)) {
                    user = contract.getJSONObject("secondParty").getString("givenName") + " " + contract.getJSONObject("secondParty").getString("familyName");
                } else if (contract.getJSONObject("secondParty").getString("id").equals(userId)) {
                    user = contract.getJSONObject("firstParty").getString("givenName") + " " + contract.getJSONObject("firstParty").getString("familyName");
                } else {
                    continue;
                }
                Instant expiryDate = Instant.parse(contract.getString("expiryDate"));

                LocalDateTime time = LocalDateTime.ofInstant(expiryDate, ZoneOffset.ofHours(0)).minus(30, ChronoUnit.DAYS);
                Instant notifyDate = time.atZone(ZoneOffset.ofHours(0)).toInstant();

                Timestamp ts = Timestamp.from(ZonedDateTime.now().toInstant());
                currentTime = ts.toInstant();

                if (currentTime.compareTo(notifyDate) > 0) {
                    String msg = "Your contract with " + user + " for the subject " +
                            contract.getJSONObject("subject").getString("name") + " is expiring soon! Do you want to view the contract?";
                    int input = JOptionPane.showConfirmDialog(new JFrame(), msg, "Contract Nearing Expiry", JOptionPane.OK_CANCEL_OPTION);
                    if (input == JOptionPane.YES_OPTION) {
                        JSONObject jsonObj = new JSONObject().put("userId", userId).put("contractId", contract.getString("id"));
                        outputPage.update(jsonObj.toString());
                        ViewManagerService.loadPage(ViewManagerService.VIEW_CONTRACT_DETAILS);
                    }
                }
            }
        }
    }
}
