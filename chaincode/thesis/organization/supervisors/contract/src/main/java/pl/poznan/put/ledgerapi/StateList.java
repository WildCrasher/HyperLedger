package pl.poznan.put.ledgerapi;

import pl.poznan.put.ledgerapi.impl.StateListImpl;
import org.hyperledger.fabric.contract.Context;

import java.util.ArrayList;

public interface StateList {

    /*
     * SPDX-License-Identifier: Apache-2.0
     */

    /**
     * StateList provides a named virtual container for a set of ledger states. Each
     * state has a unique key which associates it with the container, rather than
     * the container containing a link to the state. This minimizes collisions for
     * parallel transactions on different states.
     */

    /**
     * Store Fabric context for subsequent API access, and name of list
     */
    static StateList getStateList(Context ctx, String listName, StateDeserializer deserializer) {
        return new StateListImpl(ctx, listName, deserializer);
    }

    /**
     * Add a state to the list.
     */
    StateList addState(State state);

    /**
     * Get a state from the list using supplied keys.
     */
    State getState(String key);

    /**
     * Get all states from the list.
     * State data is deserialized into JSON object
     * before being returned.
     */
    ArrayList<State> getAllStates();

    /**
     * Update a state in the list.
     */
    StateList updateState(State state);

    /**
     * Delete a state in the list.
     */
    StateList deleteState(String key);

}
