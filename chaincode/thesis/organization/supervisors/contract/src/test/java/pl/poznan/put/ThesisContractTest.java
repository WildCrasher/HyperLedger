package pl.poznan.put;

import org.hyperledger.fabric.contract.ClientIdentity;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import pl.poznan.put.ledgerapi.State;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

public final class ThesisContractTest {
    private ThesisContract contract;
    private ThesisContext ctx;
    private ChaincodeStub stub;
    private ClientIdentity clientIdentity;

    @BeforeEach
    public void beforeEach() {
        this.contract = new ThesisContract();
        this.ctx = mock(ThesisContext.class);
        this.stub = mock(ChaincodeStub.class);
        this.clientIdentity = mock(ClientIdentity.class);
        when(ctx.getStub()).thenReturn(stub);
        when(ctx.getClientIdentity()).thenReturn(clientIdentity);
        when(ctx.getThesisList()).thenReturn(new ThesisList(ctx));
    }

    @Nested
    class Issue {
        @Test
        public void creates() {
            when(clientIdentity.getMSPID()).thenReturn("supervisorsMSP");

            String date = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
            Thesis thesis = new Thesis()
                    .setThesisNumber("A001")
                    .setFree()
                    .setSupervisor("Promotor")
                    .setIssueDateTime(date)
                    .setTopic("Temat")
                    .setStudent(" ")
                    .setKey();

            byte[] data = State.serialize(thesis);

            contract.issue(ctx, "Promotor", "A001", date, "Temat");

            verify(stub).putState("A001", data);
        }

        @Test
        public void studentCantCreate() {
            when(clientIdentity.getMSPID()).thenReturn("studentsMSP");

            String date = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());

            ChaincodeException ex = assertThrows(
                    ChaincodeException.class,
                    () -> contract.issue(ctx, "Promotor", "A001", date, "Temat")
            );

            assertEquals("cannotPerformAction", ex.getMessage());

        }
    }

    @Nested
    class Query {
        @Test
        public void getThesis() {
            String date = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
            ArrayList<StudentAssignment> studentAssignments = new ArrayList<>();
            studentAssignments.add(new StudentAssignment("Student", 3, date));
            Thesis thesis = new Thesis()
                    .setThesisNumber("A001")
                    .setFree()
                    .setSupervisor("Promotor")
                    .setIssueDateTime(date)
                    .setTopic("Temat")
                    .setStudent(" ")
                    .setKey()
                    .setStudentsAssigned(studentAssignments);

            byte[] data = State.serialize(thesis);

            when(stub.getState("A001")).thenReturn(data);
            when(clientIdentity.getMSPID()).thenReturn("supervisorsMSP");

            assertEquals(contract.queryThesis(ctx, "A001"), thesis);
        }

        @Test
        public void thesisNotFound() {
            when(stub.getState("A001")).thenReturn(null);
            when(clientIdentity.getMSPID()).thenReturn("supervisorsMSP");

            RuntimeException ex = assertThrows(
                    RuntimeException.class,
                    () -> contract.queryThesis(ctx, "A001")
            );

            assertEquals("Thesis A001 not found", ex.getMessage());
        }
    }

    @Nested
    class AssignStudent {
        @Test
        public void assigns() {
            when(clientIdentity.getMSPID()).thenReturn("studentsMSP");

            String date = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
            Thesis thesis = new Thesis()
                    .setThesisNumber("A001")
                    .setFree()
                    .setSupervisor("Promotor")
                    .setIssueDateTime(date)
                    .setTopic("Temat")
                    .setStudent(" ")
                    .setKey();

            byte[] data = State.serialize(thesis);

            when(stub.getState("A001")).thenReturn(data);

            int priority = 3;
            contract.assignStudent(ctx, "A001", "Student", priority);

            StudentAssignment assignment = new StudentAssignment("Student", priority, date);
            thesis.addStudentAssignment(assignment);

            byte[] data2 = State.serialize(thesis);

            verify(stub).putState("A001", data2);
        }

        @Test
        public void supervisorCantAssign() {
            when(clientIdentity.getMSPID()).thenReturn("supervisorsMSP");

            int priority = 2;
            ChaincodeException ex = assertThrows(
                    ChaincodeException.class,
                    () -> contract.assignStudent(ctx, "A001", "Student", priority)
            );

            assertEquals("cannotPerformAction", ex.getMessage());

        }

        @Test
        public void alreadyAssigned() {
            when(clientIdentity.getMSPID()).thenReturn("studentsMSP");

            String date = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
            Thesis thesis = new Thesis()
                    .setThesisNumber("A001")
                    .setFree()
                    .setSupervisor("Promotor")
                    .setIssueDateTime(date)
                    .setTopic("Temat")
                    .setStudent("Student1")
                    .setKey();

            byte[] data = State.serialize(thesis);

            when(stub.getState("A001")).thenReturn(data);

            int priority = 2;
            RuntimeException ex = assertThrows(
                    RuntimeException.class,
                    () -> contract.assignStudent(ctx, "A001", "Student", priority)
            );

            assertEquals("Thesis A001 is already assigned to Student1", ex.getMessage());
        }

        @Test
        public void alreadyInAssignments() {
            when(clientIdentity.getMSPID()).thenReturn("studentsMSP");

            String date = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
            ArrayList<StudentAssignment> studentAssignments = new ArrayList<>();
            studentAssignments.add(new StudentAssignment("Student1", 3, date));
            Thesis thesis = new Thesis()
                    .setThesisNumber("A001")
                    .setFree()
                    .setSupervisor("Promotor")
                    .setIssueDateTime(date)
                    .setTopic("Temat")
                    .setStudent(" ")
                    .setKey()
                    .setStudentsAssigned(studentAssignments);

            byte[] data = State.serialize(thesis);

            when(stub.getState("A001")).thenReturn(data);

            int priority = 2;
            RuntimeException ex = assertThrows(
                    RuntimeException.class,
                    () -> contract.assignStudent(ctx, "A001", "Student1", priority)
            );

            assertEquals("Thesis A001 is already assigned to Student1", ex.getMessage());
        }
    }

    @Nested
    class Approve {

        @Test
        public void approves() {
            when(clientIdentity.getMSPID()).thenReturn("supervisorsMSP");

            String date = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
            Thesis thesis = new Thesis()
                    .setThesisNumber("A001")
                    .setFree()
                    .setSupervisor("Promotor")
                    .setIssueDateTime(date)
                    .setTopic("Temat")
                    .setStudent("Student")
                    .setKey();

            byte[] data = State.serialize(thesis);

            when(stub.getState("A001")).thenReturn(data);

            contract.approveThesis(ctx, "A001");

            thesis.setOwned();

            byte[] data2 = State.serialize(thesis);

            verify(stub).putState("A001", data2);
        }

        @Test
        public void studentCantAssign() {
            when(clientIdentity.getMSPID()).thenReturn("studentsMSP");

            ChaincodeException ex = assertThrows(
                    ChaincodeException.class,
                    () -> contract.approveThesis(ctx, "A001")
            );

            assertEquals("cannotPerformAction", ex.getMessage());
        }

        @Test
        public void studentNotAssigned() {
            when(clientIdentity.getMSPID()).thenReturn("supervisorsMSP");

            String date = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
            Thesis thesis = new Thesis()
                    .setThesisNumber("A001")
                    .setFree()
                    .setSupervisor("Promotor")
                    .setIssueDateTime(date)
                    .setTopic("Temat")
                    .setStudent(" ")
                    .setKey();

            byte[] data = State.serialize(thesis);

            when(stub.getState("A001")).thenReturn(data);

            RuntimeException ex = assertThrows(
                    RuntimeException.class,
                    () -> contract.approveThesis(ctx, "A001")
            );

            assertEquals("Thesis A001 have no student assigned", ex.getMessage());
        }

        @Test
        public void alreadyApproved() {
            when(clientIdentity.getMSPID()).thenReturn("supervisorsMSP");

            String date = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
            Thesis thesis = new Thesis()
                    .setThesisNumber("A001")
                    .setOwned()
                    .setSupervisor("Promotor")
                    .setIssueDateTime(date)
                    .setTopic("Temat")
                    .setStudent("Student")
                    .setKey();

            byte[] data = State.serialize(thesis);

            when(stub.getState("A001")).thenReturn(data);

            RuntimeException ex = assertThrows(
                    RuntimeException.class,
                    () -> contract.approveThesis(ctx, "A001")
            );

            assertEquals("Thesis A001 is already approved", ex.getMessage());
        }
    }

    @Nested
    class RevokeThesis {
        @Test
        public void revokes() {
            when(clientIdentity.getMSPID()).thenReturn("studentsMSP");

            String date = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
            Thesis thesis = new Thesis()
                    .setThesisNumber("A001")
                    .setFree()
                    .setSupervisor("Promotor")
                    .setIssueDateTime(date)
                    .setTopic("Temat")
                    .setStudent("Student")
                    .setKey();

            byte[] data = State.serialize(thesis);

            when(stub.getState("A001")).thenReturn(data);

            contract.revokeThesis(ctx, "A001", "Student");

            thesis.setStudent(" ");

            byte[] data2 = State.serialize(thesis);

            verify(stub).putState("A001", data2);
        }

        @Test
        public void supervisorCantRevoke() {
            when(clientIdentity.getMSPID()).thenReturn("supervisorsMSP");

            ChaincodeException ex = assertThrows(
                    ChaincodeException.class,
                    () -> contract.revokeThesis(ctx, "A001", "Student")
            );

            assertEquals("cannotPerformAction", ex.getMessage());
        }

        @Test
        public void thesisAlreadyApproved() {
            when(clientIdentity.getMSPID()).thenReturn("studentsMSP");

            String date = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
            Thesis thesis = new Thesis()
                    .setThesisNumber("A001")
                    .setOwned()
                    .setSupervisor("Promotor")
                    .setIssueDateTime(date)
                    .setTopic("Temat")
                    .setStudent("Student")
                    .setKey();

            byte[] data = State.serialize(thesis);

            when(stub.getState("A001")).thenReturn(data);

            RuntimeException ex = assertThrows(
                    RuntimeException.class,
                    () -> contract.revokeThesis(ctx, "A001", "Student")
            );

            assertEquals("Thesis A001 is already approved", ex.getMessage());
        }

        @Test
        public void studentNotAssigned() {
            when(clientIdentity.getMSPID()).thenReturn("studentsMSP");

            String date = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
            Thesis thesis = new Thesis()
                    .setThesisNumber("A001")
                    .setFree()
                    .setSupervisor("Promotor")
                    .setIssueDateTime(date)
                    .setTopic("Temat")
                    .setStudent(" ")
                    .setKey();

            byte[] data = State.serialize(thesis);

            when(stub.getState("A001")).thenReturn(data);

            RuntimeException ex = assertThrows(
                    RuntimeException.class,
                    () -> contract.revokeThesis(ctx, "A001", "Student")
            );

            assertEquals("Thesis A001 you are not assigned", ex.getMessage());
        }
    }
}
