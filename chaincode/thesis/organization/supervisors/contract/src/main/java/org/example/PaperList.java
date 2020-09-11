/*
SPDX-License-Identifier: Apache-2.0
*/

package org.example;

import org.example.ledgerapi.StateList;
import org.hyperledger.fabric.contract.Context;

public class PaperList {

    private StateList stateList;

    public PaperList(Context ctx) {
        this.stateList = StateList.getStateList(ctx, PaperList.class.getSimpleName(), Thesis::deserialize);
    }

    public PaperList addPaper(Thesis paper) {
        stateList.addState(paper);
        return this;
    }

    public Thesis getPaper(String paperKey) {
        return (Thesis) this.stateList.getState(paperKey);
    }

    public PaperList updatePaper(Thesis paper) {
        this.stateList.updateState(paper);
        return this;
    }
}
