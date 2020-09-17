package pl.poznan.put;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.ChaincodeStub;

class ThesisContext extends Context {

    public ThesisContext(ChaincodeStub stub) {
        super(stub);
        this.thesisList = new ThesisList(this);
    }

    public ThesisList thesisList;

}