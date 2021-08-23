package contract_strategy;

import org.json.JSONArray;
import org.json.JSONObject;
import services.ApiRequest;

public interface ContractStrategy {

    /**
     * Method to post a contract between student and tutor
     * @param contractDetail JSONObject containing all the detail of the contract
     */
    void postContract(JSONObject contractDetail);

    /**
     * Method to sign a contract between student and tutor
     * @param contractDetail Id of the contract
     * @param isTutor true if the one signing is tutor and false otherwise
     */
    void signContract(JSONObject contractDetail, boolean isTutor);

    /**
     * Method to add unsigned renewed contract into tutor/student additionalInfo to sign later on
     * @param userId id of the tutor/student
     * @param contractId id of the contract
     */
    default void patchUnsignedContract(String userId, String contractId) {
        JSONObject user = new JSONObject(ApiRequest.get("/user/" + userId).body());
        JSONObject additionalInfo = user.getJSONObject("additionalInfo");

        // if user has 'activeContract' field
        if (additionalInfo.has("unsignedContract")){

            JSONArray unsignedContract = additionalInfo.getJSONArray("unsignedContract");


            if (unsignedContract.length() == 5){ // only save latest 5 signed contract
                unsignedContract.remove(0); // remove the oldest contract
                unsignedContract.put(contractId); // add latest contract
            } else {
                unsignedContract.put(contractId);
            }

            // update additionalInfo with new active Contract
            additionalInfo.remove("unsignedContract");
            additionalInfo.put("unsignedContract", unsignedContract);

        } else {
            JSONArray activeContract = new JSONArray();
            activeContract.put(contractId);

            // update additionalInfo with new active Contract
            additionalInfo.put("unsignedContract", activeContract);
        }
        ApiRequest.put("/user/" + userId, user.toString());
    }

    /**
     * Method to add signed contract into tutor/student additionalInfo
     * @param userId id of the tutor/student
     * @param contractId id of the contract
     */
    default void patchSignedContract(String userId, String contractId) {
        JSONObject user = new JSONObject(ApiRequest.get("/user/" + userId).body());
        JSONObject additionalInfo = user.getJSONObject("additionalInfo");

        // if user has 'activeContract' field
        if (additionalInfo.has("activeContract")){

            JSONArray activeContract = additionalInfo.getJSONArray("activeContract");

            if (activeContract.length() == 5){ // only save latest 5 signed contract
                activeContract.remove(0); // remove the oldest contract
                activeContract.put(contractId); // add latest contract
            } else {
                activeContract.put(contractId);
            }

            // update additionalInfo with new active Contract
            additionalInfo.remove("activeContract");
            additionalInfo.put("activeContract", activeContract);

        } else {
            JSONArray activeContract = new JSONArray();
            activeContract.put(contractId);

            // update additionalInfo with new active Contract
            additionalInfo.put("activeContract", activeContract);
        }
        ApiRequest.put("/user/" + userId, user.toString());
    }
}
