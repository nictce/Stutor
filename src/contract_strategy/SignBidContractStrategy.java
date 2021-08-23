package contract_strategy;

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

public class SignBidContractStrategy implements ContractStrategy {

    @Override
    public void postContract(JSONObject bid) {
        String tutorId = bid.getString("tutorId");
        String userId = bid.getString("userId");
        String messageId = bid.getString("messageId");

        JSONObject contract = new JSONObject();
        if (tutorId.equals("")) { // buyout action (there could be no responses for the bid when the tutor buys it out)
            contract.put("firstPartyId", userId);
            contract.put("secondPartyId", bid.getJSONObject("initiator").getString("id"));
            contract.put("lessonInfo", bid.getJSONObject("additionalInfo"));

            // if buyout then create additional Info cuz bid cannot store lessonInfo and additionalInfo
            contract.put("additionalInfo", new JSONObject().put("tutorSigned", true).put("studentSigned", true));

        } else { // a confirm bid action from the user or ExpireBidService chooses the last tutor as the winner (has response)
            contract.put("firstPartyId", tutorId);
            contract.put("secondPartyId", bid.getJSONObject("initiator").getString("id"));
            contract.put("additionalInfo", bid.getJSONObject("additionalInfo"));
            JSONObject message = new JSONObject(ApiRequest.get("/message/" + messageId).body());
            contract.put("lessonInfo", message.getJSONObject("additionalInfo"));

        }
        Timestamp ts = Timestamp.from(ZonedDateTime.now().toInstant());
        Instant now = ts.toInstant();
        contract.put("dateCreated", now);
        LocalDateTime time = LocalDateTime.ofInstant(ts.toInstant(), ZoneOffset.ofHours(0));
        time = time.plus(bid.getJSONObject("additionalInfo").getInt("contractLength"), ChronoUnit.MONTHS); // contract expires after a year
        Instant output = time.atZone(ZoneOffset.ofHours(0)).toInstant();
        Timestamp expiryDate = Timestamp.from(output);
        contract.put("subjectId", bid.getJSONObject("subject").getString("id"));
        contract.put("expiryDate", expiryDate);
        contract.put("paymentInfo", new JSONObject());
        HttpResponse<String> contractResponse = ApiRequest.post("/contract", contract.toString());

        if (contractResponse.statusCode() == 201) {
            contract = new JSONObject(contractResponse.body());
            contract.put("tutorId", tutorId);
            signContract(contract, false);
        } else {
            String msg = "Contract not posted: Error " + contractResponse.statusCode();
            JOptionPane.showMessageDialog(new JFrame(), msg, "Bad request", JOptionPane.ERROR_MESSAGE);
        }
    }

    // isTutor is not used in this strategy but I need it in renew contract to determine who is signing the contract
    // having extra parameter is better than duplicated code
    @Override
    public void signContract(JSONObject contractDetail, boolean isTutor) {
        JSONObject dateSigned = new JSONObject();
        Timestamp ts = Timestamp.from(ZonedDateTime.now().toInstant());
        Instant now = ts.toInstant();
        dateSigned.put("dateSigned", now);
        HttpResponse<String> contractSignResponse = ApiRequest.post("/contract/" + contractDetail.getString("id") + "/sign", dateSigned.toString());
        String msg;
        String tutorId = contractDetail.getString("tutorId");

        if (contractSignResponse.statusCode() == 200) {

            // add the contract into additionalInfo for both student nad patient
            patchSignedContract(contractDetail.getJSONObject("firstParty").getString("id"), contractDetail.getString("id"));
            patchSignedContract(contractDetail.getJSONObject("secondParty").getString("id"), contractDetail.getString("id"));

            msg = "Bid closed successfully and contract created at " + now;
            JOptionPane.showMessageDialog(new JFrame(), msg, "Bid Closed Successfully", JOptionPane.INFORMATION_MESSAGE);
            if (tutorId.equals("")) {
                ViewManagerService.loadPage(ViewManagerService.DASHBOARD_PAGE);
            }
        } else {
            msg = "Contract not signed: Error " + contractSignResponse.statusCode();
            JOptionPane.showMessageDialog(new JFrame(), msg, "Bad request", JOptionPane.ERROR_MESSAGE);
        }
    }
}
