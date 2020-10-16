package pl.poznan.put;

import org.hyperledger.fabric.contract.ClientIdentity;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import pl.poznan.put.ledgerapi.State;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

            assertTrue(ex.getMessage().equals("cannotPerformAction"));

        }
    }

    @Nested
    class Query {
        @Test
        public void getThesis() {
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
            when(clientIdentity.getMSPID()).thenReturn("supervisorsMSP");

            assertTrue(contract.queryThesis(ctx, "A001").equals(thesis));
        }

        @Test
        public void thesisNotFound() {
            when(stub.getState("A001")).thenReturn(null);
            when(clientIdentity.getMSPID()).thenReturn("supervisorsMSP");

            RuntimeException ex = assertThrows(
                    RuntimeException.class,
                    () -> contract.queryThesis(ctx, "A001")
            );

            assertTrue(ex.getMessage().equals("Thesis A001 not found"));
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

            contract.assignStudent(ctx, "A001", "Student");

            thesis.setStudent("Student");

            byte[] data2 = State.serialize(thesis);

            verify(stub).putState("A001", data2);
        }

        @Test
        public void supervisorCantAssign() {
            when(clientIdentity.getMSPID()).thenReturn("supervisorsMSP");

            ChaincodeException ex = assertThrows(
                    ChaincodeException.class,
                    () -> contract.assignStudent(ctx, "A001", "Student")
            );

            assertTrue(ex.getMessage().equals("cannotPerformAction"));

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

            RuntimeException ex = assertThrows(
                    RuntimeException.class,
                    () -> contract.assignStudent(ctx, "A001", "Student")
            );

            assertTrue(ex.getMessage().equals("Thesis A001 is already assigned to Student1"));
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

            assertTrue(ex.getMessage().equals("cannotPerformAction"));
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

            assertTrue(ex.getMessage().equals("Thesis A001 have no student assigned"));
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

            assertTrue(ex.getMessage().equals("Thesis A001 is already approved"));
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

            assertTrue(ex.getMessage().equals("cannotPerformAction"));
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

            assertTrue(ex.getMessage().equals("Thesis A001 is already approved"));
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

            assertTrue(ex.getMessage().equals("Thesis A001 you are not assigned"));
        }
    }
}
