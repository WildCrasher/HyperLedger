/*
SPDX-License-Identifier: Apache-2.0
*/
package pl.poznan.put.thesis;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.google.gson.Gson;
import pl.poznan.put.StudentAssignment;
import pl.poznan.put.ledgerapi.State;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ChaincodeException;
import pl.poznan.put.user.User;

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

        String studentKey = State.makeKey(new String[] {student});
        User user = ctx.getUserList().getUser(studentKey);
        if (user == null) {
            user = new User().setName(student).setKey();
        }

        if (thesis.isOwned()) {
            throw new RuntimeException("Thesis " + thesisNumber + " is already assigned to " + thesis.getStudent());
        }

        if (thesis.isStudentInAssignments(student)) {
            throw new RuntimeException("Thesis " + thesisNumber + " is already assigned to " + student);
        }

        if (!(priority > 0 && priority < 4)) {
            throw new RuntimeException("Wrong priority");
        }

        String date = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
        StudentAssignment assignment = new StudentAssignment(student, priority, date);
        thesis.addStudentAssignment(assignment);

        user.addThesisId(thesis.getThesisNumber());

        ctx.getThesisList().updateThesis(thesis);
        ctx.getUserList().updateUser(user);
        return thesis;
    }

    @Transaction()
    public Thesis chooseStudent(final ThesisContext ctx, final String thesisNumber, final String student)
            throws ParseException {

        if (!isUserInOrg(ctx, "supervisor")) {
            throw new ChaincodeException("cannotPerformAction");
        }

        String thesisKey = State.makeKey(new String[] {thesisNumber});
        Thesis thesis = ctx.getThesisList().getThesis(thesisKey);

        if (!thesis.isStudentInAssignments(student)) {
            throw new RuntimeException("Student " + student + " is not assigned to thesis " + thesisNumber);
        }

        if (thesis.isOwned()) {
            throw new RuntimeException("Thesis " + thesisNumber + " is already approved");
        }

        if (!thesis.getStudent().equals(" ") && thesis.getAssignmentDateDiff(TimeUnit.DAYS) < 14) {
            throw new RuntimeException("Thesis " + thesisNumber + " is already assigned");
        }

        thesis.setStudent(student);

        ctx.getThesisList().updateThesis(thesis);
        return thesis;
    }

    @Transaction
    public Thesis acceptAssignment(final ThesisContext ctx, final String thesisNumber, final String username)
            throws ParseException {
        if (!isUserInOrg(ctx, "student")) {
            throw new ChaincodeException("cannotPerformAction");
        }

        String thesisKey = State.makeKey(new String[] {thesisNumber});
        Thesis thesis = ctx.getThesisList().getThesis(thesisKey);

        if (thesis.isOwned()) {
            throw new RuntimeException("Thesis " + thesisNumber + " is already approved");
        }

        if (!thesis.getStudent().equals(username)) {
            throw new RuntimeException("User " + username + " is not assigned to thesis " + thesisNumber);
        }

        if (thesis.getAssignmentDateDiff(TimeUnit.DAYS) > 14) {
            throw new RuntimeException("Time to accept expired");
        }

        thesis.setOwned();

        //remove user from every other thesis
        User user = ctx.getUserList().getUser(username);
        Thesis thesisIt = null;
        for (Iterator<String> it = user.getThesesId().iterator(); it.hasNext();) {
            String thesisId = it.next();
            if (!thesisId.equals(thesisNumber)) {
                thesisIt = ctx.getThesisList().getThesis(thesisId);
                thesisIt.removeStudentAssignment(username);
                if (thesisIt.isFree() && thesisIt.getStudent().equals(username)) {
                   thesisIt.setStudent(" ");
                }
                ctx.getThesisList().updateThesis(thesisIt);
                it.remove();
            }
        }
        ctx.getUserList().updateUser(user);

        //remove other users from assignments
        for (Iterator<StudentAssignment> it = thesis.getStudentsAssigned().iterator(); it.hasNext();) {
            StudentAssignment assignment = it.next();
            if (!assignment.getStudentName().equals(username)) {
                user = ctx.getUserList().getUser(assignment.getStudentName());
                user.removeThesisId(thesisNumber);
                ctx.getUserList().updateUser(user);
                it.remove();
            }
        }

        ctx.getThesisList().updateThesis(thesis);

        return thesis;
    }

    @Transaction
    public Thesis declineAssignment(final ThesisContext ctx, final String thesisNumber, final String username)
            throws ParseException {
        if (!isUserInOrg(ctx, "student")) {
            throw new ChaincodeException("cannotPerformAction");
        }

        String thesisKey = State.makeKey(new String[] {thesisNumber});
        Thesis thesis = ctx.getThesisList().getThesis(thesisKey);

        if (thesis.isOwned()) {
            throw new RuntimeException("Thesis " + thesisNumber + " is already approved");
        }

        if (!thesis.getStudent().equals(username)) {
            throw new RuntimeException("User " + username + " is not assigned to thesis " + thesisNumber);
        }

        String studentKey = State.makeKey(new String[] {username});
        User user = ctx.getUserList().getUser(studentKey);

        thesis.setStudent(" ");
        thesis.setAssignmentDate("");
        thesis.removeStudentAssignment(username);

        ctx.getThesisList().updateThesis(thesis);

        user.removeThesisId(thesisNumber);

        ctx.getUserList().updateUser(user);

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

        if (thesis.isStudentInAssignments(username)) {
            thesis.removeStudentAssignment(username);
        } else {
            throw new RuntimeException("Thesis " + thesisNumber + " you are not assigned");
        }

        String studentKey = State.makeKey(new String[] {username});
        User user = ctx.getUserList().getUser(studentKey);

        ctx.getThesisList().updateThesis(thesis);

        user.removeThesisId(thesisNumber);

        ctx.getUserList().updateUser(user);

        return thesis;
    }

    @Transaction()
    public Thesis queryThesis(final ThesisContext ctx, final String thesisNumber) {
        String thesisKey = State.makeKey(new String[] {thesisNumber});
        Thesis thesis = ctx.getThesisList().getThesis(thesisKey);

        if (thesis == null) {
            throw new RuntimeException("Thesis " + thesisNumber + " not found");
        }

        User user = null;
        for (StudentAssignment assignment : thesis.getStudentsAssigned()) {
            user = ctx.getUserList().getUser(assignment.getStudentName());
            assignment.setThesisAssigned(user.getThesesId().size());
        }

        return thesis;
    }

    @Transaction()
    public String queryAllThesis(final ThesisContext ctx) {

        ArrayList<Thesis> theses = ctx.getThesisList().getAllThesis();
        if (theses == null) {
            throw new RuntimeException("No thesis found");
        }

        theses.removeIf(thesis -> !thesis.isFree() && !thesis.isOwned());

        return new Gson().toJson(theses);
    }

    @Transaction()
    public String queryStudentTheses(final ThesisContext ctx, final String student) {
        User user = ctx.getUserList().getUser(student);

        if (user == null) {
            return new Gson().toJson(new ArrayList<>());
        }

        ArrayList<Thesis> results = new ArrayList<>();

        for (String thesisId : user.getThesesId()) {
            results.add(ctx.getThesisList().getThesis(thesisId));
        }

        return new Gson().toJson(results);
    }

    private Boolean isUserInOrg(final Context ctx, final String org) {
        String userMSPID = ctx.getClientIdentity().getMSPID().toLowerCase();
        return userMSPID.contains(org);
    }
}
