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
import org.hyperledger.fabric.shim.ChaincodeException;

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

        if (!isUserInOrg(ctx, "supervisor")) {
            throw new ChaincodeException("cannotPerformAction");
        }

        Thesis thesis = Thesis.createInstance(supervisor, thesisNumber, issueDateTime, " ", Thesis.FREE, topic);

        ctx.getThesisList().addThesis(thesis);

        return thesis;
    }

    @Transaction()
    public Thesis assignStudent(final ThesisContext ctx, final String thesisNumber, final String student) {

        if (!isUserInOrg(ctx, "student")) {
            throw new ChaincodeException("cannotPerformAction");
        }

        String thesisKey = State.makeKey(new String[] {thesisNumber});
        Thesis thesis = ctx.getThesisList().getThesis(thesisKey);

        if (!thesis.getStudent().equals(" ")) {
            throw new RuntimeException("Thesis " + thesisNumber + " is already assigned to " + thesis.getStudent());
        }

        thesis.setStudent(student);

        ctx.getThesisList().updateThesis(thesis);
        return thesis;
    }

    @Transaction()
    public Thesis approveThesis(final ThesisContext ctx, final String thesisNumber) {

        if (!isUserInOrg(ctx, "supervisor")) {
            throw new ChaincodeException("cannotPerformAction");
        }

        String thesisKey = State.makeKey(new String[] {thesisNumber});
        Thesis thesis = ctx.getThesisList().getThesis(thesisKey);

        if (thesis.getStudent().equals(" ")) {
            throw new RuntimeException("Thesis " + thesisNumber + " have no student assigned");
        }

        if (thesis.isOwned()) {
            throw new RuntimeException("Thesis " + thesisNumber + " is already approved");
        }

        if (thesis.isFree()) {
            thesis.setOwned();
        }

        ctx.getThesisList().updateThesis(thesis);
        return thesis;
    }

    @Transaction()
    public Thesis revokeThesis(final ThesisContext ctx, final String thesisNumber, final String username) {

        if (!isUserInOrg(ctx, "student")) {
            throw new ChaincodeException("cannotPerformAction");
        }

        String thesisKey = State.makeKey(new String[] {thesisNumber});
        Thesis thesis = ctx.getThesisList().getThesis(thesisKey);

        if (thesis.isOwned()) {
            throw new RuntimeException("Thesis " + thesisNumber + " is already approved");
        }

        if (thesis.isStudentAssigned(username)) {
            thesis.setFree();
            thesis.setStudent(" ");
        } else {
            throw new RuntimeException("Thesis " + thesisNumber + " you are not assigned");
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

    private Boolean isUserInOrg(final Context ctx, final String org) {
        String userMSPID = ctx.getClientIdentity().getMSPID().toLowerCase();
        if (userMSPID.contains(org)) {
            return true;
        }

        return false;
    }
}
