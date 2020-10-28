package pl.poznan.put.user;

import org.hyperledger.fabric.contract.Context;
import pl.poznan.put.ledgerapi.StateList;
import pl.poznan.put.thesis.Thesis;
import pl.poznan.put.thesis.ThesisList;

public class UserList {

    private StateList stateList;

    public UserList(final Context ctx) {
        this.stateList = StateList.getStateList(ctx, UserList.class.getSimpleName(), User::deserialize);
    }

    public UserList addUser(final User user) {
        stateList.addState(user);
        return this;
    }

    public User getUser(final String username) {
        return (User) this.stateList.getState(username);
    }

    public UserList updateUser(final User user) {
        this.stateList.updateState(user);
        return this;
    }
}
