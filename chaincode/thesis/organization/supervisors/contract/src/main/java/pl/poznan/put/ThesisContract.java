/*
SPDX-License-Identifier: Apache-2.0
*/
package pl.poznan.put;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    public Thesis issue(final ThesisContext ctx, final String supervisor, final String thesisNumber,
                        final String issueDateTime, final String topic) {

        if (!isUserInOrg(ctx, "supervisor")) {
            throw new ChaincodeException("cannotPerformAction");
        }

        ArrayList<StudentAssignment> studentAssignments = new ArrayList<>();
        Thesis thesis = Thesis.createInstance(supervisor, thesisNumber, issueDateTime, " ", Thesis.FREE, topic,
                studentAssignments);

        ctx.getThesisList().addThesis(thesis);

        return thesis;
    }

    @Transaction()
    public Thesis assignStudent(final ThesisContext ctx, final String thesisNumber, final String student,
                                final int priority) {

        if (!isUserInOrg(ctx, "student")) {
            throw new ChaincodeException("cannotPerformAction");
        }

        String thesisKey = State.makeKey(new String[] {thesisNumber});
        Thesis thesis = ctx.getThesisList().getThesis(thesisKey);

        if (!thesis.getStudent().equals(" ")) {
            throw new RuntimeException("Thesis " + thesisNumber + " is already assigned to " + thesis.getStudent());
        }

        if(thesis.isStudentInAssignments(student)) {
            throw new RuntimeException("Thesis " + thesisNumber + " is already assigned to " + student);
        }

        if(!(priority > 0 && priority < 4)) {
            throw new RuntimeException("Wrong priority");
        }

        String date = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
        StudentAssignment assignment = new StudentAssignment(student, priority, date);
        thesis.addStudentAssignment(assignment);

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
