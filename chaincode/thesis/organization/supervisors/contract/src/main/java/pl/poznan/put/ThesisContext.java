package pl.poznan.put;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.ChaincodeStub;

class ThesisContext extends Context {

    ThesisContext(final ChaincodeStub stub) {
        super(stub);
        this.setThesisList(new ThesisList(this));
    }

    private ThesisList thesisList;

    public ThesisList getThesisList() {
        return thesisList;
    }

    public void setThesisList(final ThesisList newThesisList) {
        this.thesisList = thesisList;
    }
}
