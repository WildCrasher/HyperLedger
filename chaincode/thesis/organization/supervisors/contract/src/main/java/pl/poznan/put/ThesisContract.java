/*
SPDX-License-Identifier: Apache-2.0
*/
package pl.poznan.put;

import java.util.ArrayList;
import java.util.logging.Logger;

import com.google.gson.Gson;
import pl.poznan.put.ledgerapi.State;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeStub;

@Contract(name = "pl.poznan.put.thesis", info = @Info(title = "Thesis contract", description = "", version = "0.0.1"))
@Default
public final class ThesisContract implements ContractInterface {

    private static final Logger LOG = Logger.getLogger(ThesisContract.class.getName());

    @Override
    public Context createContext(final ChaincodeStub stub) {
        return new ThesisContext(stub);
    }

    public ThesisContract() {

    }

    @Transaction()
    public void instantiate(final ThesisContext ctx) {
        // No implementation required with this example
        // It could be where data migration is performed, if necessary
        LOG.info("No data migration to perform");
    }

    @Transaction()
    public void initLedger(final ThesisContext ctx) {
        Thesis thesis1 = Thesis.createInstance("Promotor1", "1", "2020-09-18",
                "", Thesis.FREE, "temat1");
        Thesis thesis2 = Thesis.createInstance("Promotor2", "2", "2020-09-18",
                "student1", Thesis.OWNED, "temat2");
        ctx.getThesisList().addThesis(thesis1);
    }


    @Transaction()
    public Thesis issue(final ThesisContext ctx, final String supervisor, final String thesisNumber,
                        final String issueDateTime, final String topic) {

        System.out.println(ctx);

        Thesis thesis = Thesis.createInstance(supervisor, thesisNumber, issueDateTime, " ", "Z", topic);

        thesis.setFree();

        System.out.println(thesis);

        ctx.getThesisList().addThesis(thesis);

        return thesis;
    }

    @Transaction()
    public Thesis asignStudent(final ThesisContext ctx, final String thesisNumber, final String student) {

        String thesisKey = State.makeKey(new String[] {thesisNumber});
        Thesis thesis = ctx.getThesisList().getThesis(thesisKey);

        if (!thesis.isFree()) {
            throw new RuntimeException("Thesis " + thesisNumber + " is already asigned to " + thesis.getStudent());
        }

        if (thesis.isFree()) {
            thesis.setStudent(student);
            thesis.setOwned();
        }

        ctx.getThesisList().updateThesis(thesis);
        return thesis;
    }

    @Transaction()
    public Thesis queryThesis(final ThesisContext ctx, final String thesisNumber) {
        String thesisKey = State.makeKey(new String[] {thesisNumber});
        Thesis thesis = ctx.getThesisList().getThesis(thesisKey);
        if (thesis == null) {
            throw new RuntimeException("Thesis " + thesisNumber + " not found");
        }

        return thesis;
    }

    @Transaction()
    public String queryAllThesis(final ThesisContext ctx) {

        ArrayList<Thesis> thesis = ctx.getThesisList().getAllThesis();
        if (thesis == null) {
            throw new RuntimeException("No thesis found");
        }

        return new Gson().toJson(thesis);
    }
}
