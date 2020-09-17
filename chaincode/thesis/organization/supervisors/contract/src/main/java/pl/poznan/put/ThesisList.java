/*
SPDX-License-Identifier: Apache-2.0
*/

package pl.poznan.put;

import pl.poznan.put.ledgerapi.StateList;
import org.hyperledger.fabric.contract.Context;

public class ThesisList {

    private StateList stateList;

    public ThesisList(Context ctx) {
        this.stateList = StateList.getStateList(ctx, ThesisList.class.getSimpleName(), Thesis::deserialize);
    }

    public ThesisList addThesis(Thesis thesis) {
        stateList.addState(thesis);
        return this;
    }

    public Thesis getThesis(String thesisKey) {
        return (Thesis) this.stateList.getState(thesisKey);
    }

    public ThesisList updateThesis(Thesis thesis) {
        this.stateList.updateState(thesis);
        return this;
    }
}
