package pl.poznan.put.thesis;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.ChaincodeStub;
import pl.poznan.put.user.UserList;

class ThesisContext extends Context {

    ThesisContext(final ChaincodeStub stub) {
        super(stub);
        this.setThesisList(new ThesisList(this));
        this.setUserList(new UserList(this));
    }

    private ThesisList thesisList;
    private UserList userList;

    public ThesisList getThesisList() {
        return thesisList;
    }

    public void setThesisList(final ThesisList newThesisList) {
        this.thesisList = newThesisList;
    }

    public UserList getUserList() {
        return userList;
    }

    public void setUserList(final UserList newUserList) {
        this.userList = newUserList;
    }
}
