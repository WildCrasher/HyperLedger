package pl.poznan.put.ledgerapi.impl;

import java.util.ArrayList;
//import java.util.Arrays;

import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;
import pl.poznan.put.ledgerapi.State;
import pl.poznan.put.ledgerapi.StateDeserializer;
import pl.poznan.put.ledgerapi.StateList;
import org.hyperledger.fabric.contract.Context;

/*
SPDX-License-Identifier: Apache-2.0
*/

/**
 * StateList provides a named virtual container for a set of ledger states. Each
 * state has a unique key which associates it with the container, rather than
 * the container containing a link to the state. This minimizes collisions for
 * parallel transactions on different states.
 */
public class StateListImpl implements StateList {

    private Context ctx;
    private String name;
    private Object supportedClasses;
    private StateDeserializer deserializer;

    /**
     * Store Fabric context for subsequent API access, and name of list
     *
     * @param deserializer
     */
    public StateListImpl(final Context ctx, final String listName, final StateDeserializer deserializer) {
        this.ctx = ctx;
        this.name = listName;
        this.deserializer = deserializer;

    }

    /**
     * Add a state to the list. Creates a new state in worldstate with appropriate
     * composite key. Note that state defines its own key. State object is
     * serialized before writing.
     */
    @Override
    public StateList addState(final State state) {
        byte[] data = State.serialize(state);
        this.ctx.getStub().putState(state.getKey(), data);

        return this;
    }

    /**
     * Get a state from the list using supplied keys. Form composite keys to
     * retrieve state from world state. State data is deserialized into JSON object
     * before being returned.
     */
    @Override
    public State getState(final String key) {
        byte[] data = this.ctx.getStub().getState(key);
        if (data != null) {
            State state = this.deserializer.deserialize(data);
            return state;
        } else {
            return null;
        }
    }

    /**
     * Get all states from the list.
     * State data is deserialized into JSON object
     * before being returned.
     */
    @Override
    public ArrayList<State> getAllStates() {
        ArrayList<State> queryResults = new ArrayList<State>();

        QueryResultsIterator<KeyValue> results = this.ctx.getStub().getStateByRange(" ", "Z");

        for (KeyValue result: results) {
            queryResults.add(this.deserializer.deserialize(result.getValue()));
        }

        return queryResults;
    }

    /**
     * Update a state in the list. Puts the new state in world state with
     * appropriate composite key. Note that state defines its own key. A state is
     * serialized before writing. Logic is very similar to addState() but kept
     * separate becuase it is semantically distinct.
     */
    @Override
    public StateList updateState(final State state) {
        byte[] data = State.serialize(state);
        this.ctx.getStub().putState(state.getKey(), data);

        return this;
    }

    /**
     * Delete a state in the list.
     */
    @Override
    public StateList deleteState(final String key) {
        this.ctx.getStub().delState(key);
        return this;
    }
}
