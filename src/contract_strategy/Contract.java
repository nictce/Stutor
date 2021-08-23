package contract_strategy;

import org.json.JSONObject;

public class Contract {

    private ContractStrategy strategy;

    public void setStrategy(ContractStrategy strategy) {
        this.strategy = strategy;
    }

    public void postContract(JSONObject contractDetail){
        strategy.postContract(contractDetail);
    }

    public void signContract(JSONObject contractDetail, boolean isTutor) {
        strategy.signContract(contractDetail, isTutor);
    }


}
