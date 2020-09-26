/*
SPDX-License-Identifier: Apache-2.0
*/

package pl.poznan.put;

import pl.poznan.put.ledgerapi.State;
import pl.poznan.put.ledgerapi.StateList;
import org.hyperledger.fabric.contract.Context;

import java.util.ArrayList;

public final class ThesisList {

    private StateList stateList;

    public ThesisList(final Context ctx) {
        this.stateList = StateList.getStateList(ctx, ThesisList.class.getSimpleName(), Thesis::deserialize);
    }

    public ThesisList addThesis(final Thesis thesis) {
        stateList.addState(thesis);
        return this;
    }

    public Thesis getThesis(final String thesisKey) {
        return (Thesis) this.stateList.getState(thesisKey);
    }

    public ArrayList<Thesis> getAllThesis() {
        ArrayList<? extends State> result = this.stateList.getAllStates();
        return (ArrayList<Thesis>) result;
    }

    public ThesisList updateThesis(final Thesis thesis) {
        this.stateList.updateState(thesis);
        return this;
    }
}
