package pl.poznan.put.thesis;

import org.hyperledger.fabric.contract.ClientIdentity;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import pl.poznan.put.ledgerapi.State;
import pl.poznan.put.user.User;
import pl.poznan.put.user.UserList;

import java.text.ParseException;
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
        when(ctx.getUserList()).thenReturn(new UserList(ctx));
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
                    .setDescription("Opis")
                    .setKey();

            byte[] data = State.serialize(thesis);

            contract.issue(ctx, "Promotor", "A001", date, "Temat", "Opis");

            verify(stub).putState("A001", data);
        }

        @Test
        public void studentCantCreate() {
            when(clientIdentity.getMSPID()).thenReturn("studentsMSP");

            String date = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());

            ChaincodeException ex = assertThrows(
                    ChaincodeException.class,
                    () -> contract.issue(ctx, "Promotor", "A001", date, "Temat", "Opis")
            );

            assertEquals("cannotPerformAction", ex.getMessage());

        }
    }

    @Nested
    class RemoveThesis {
        @Test
        public void remove() {
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

            contract.removeThesis(ctx, "Promotor", "A001");

            verify(stub).delState("A001");
        }

        @Test
        public void studentCantRemove() {
            when(clientIdentity.getMSPID()).thenReturn("studentsMSP");

            ChaincodeException ex = assertThrows(
                    ChaincodeException.class,
                    () -> contract.removeThesis(ctx, "Promotor", "A001")
            );

            assertEquals("cannotPerformAction", ex.getMessage());
        }

        @Test
        public void thesisNotFound() {
            when(clientIdentity.getMSPID()).thenReturn("supervisorsMSP");

            RuntimeException ex = assertThrows(
                    RuntimeException.class,
                    () -> contract.removeThesis(ctx, "Promotor", "A001")
            );

            assertEquals("Thesis A001 not found", ex.getMessage());
        }

        @Test
        public void cantRemoveWithStudents() {
            when(clientIdentity.getMSPID()).thenReturn("supervisorsMSP");

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

            RuntimeException ex = assertThrows(
                    RuntimeException.class,
                    () -> contract.removeThesis(ctx, "Promotor", "A001")
            );

            assertEquals("Cannot remove thesis with students assigned", ex.getMessage());
        }

        @Test
        public void isNotSupervisorOfThesis() {
            when(clientIdentity.getMSPID()).thenReturn("supervisorsMSP");

            String date = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
            Thesis thesis = new Thesis()
                    .setThesisNumber("A001")
                    .setFree()
                    .setSupervisor("Promotor2")
                    .setIssueDateTime(date)
                    .setTopic("Temat")
                    .setStudent(" ")
                    .setKey();

            byte[] data = State.serialize(thesis);

            when(stub.getState("A001")).thenReturn(data);

            RuntimeException ex = assertThrows(
                    RuntimeException.class,
                    () -> contract.removeThesis(ctx, "Promotor", "A001")
            );

            assertEquals("User Promotor is not a supervisor of thesis A001", ex.getMessage());
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
                    .setDescription("Opis")
                    .setKey()
                    .setStudentsAssigned(studentAssignments);

            byte[] data = State.serialize(thesis);

            when(stub.getState("A001")).thenReturn(data);
            when(clientIdentity.getMSPID()).thenReturn("supervisorsMSP");

            thesis.getStudentsAssigned().get(0).setThesisAssigned(1);

            User user = new User().setName("Student").setKey();
            user.addThesisId("A001");

            byte[] userData = State.serialize(user);
            when(stub.getState("Student")).thenReturn(userData);

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

            User user = new User().setName("Student").setKey();
            user.addThesisId("A001");

            byte[] userData = State.serialize(user);

            verify(stub).putState("Student", userData);
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
                    .setOwned()
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

        @Test
        public void wrongPriority() {
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

            int priority = 7;
            RuntimeException ex = assertThrows(
                    RuntimeException.class,
                    () -> contract.assignStudent(ctx, "A001", "Student1", priority)
            );

            assertEquals("Wrong priority", ex.getMessage());

            int priority2 = -1;
            ex = assertThrows(
                    RuntimeException.class,
                    () -> contract.assignStudent(ctx, "A001", "Student1", priority2)
            );

            assertEquals("Wrong priority", ex.getMessage());
        }
    }

    @Nested
    class ChooseStudent {

        @Test
        public void assigns() {
            when(clientIdentity.getMSPID()).thenReturn("supervisorsMSP");

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

            contract.chooseStudent(ctx, "Promotor", "A001", "Student");

            thesis.setStudent("Student");

            byte[] data2 = State.serialize(thesis);

            verify(stub).putState("A001", data2);
        }

        @Test
        public void assignsAfterTimeExpired() {
            when(clientIdentity.getMSPID()).thenReturn("supervisorsMSP");

            String date = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
            ArrayList<StudentAssignment> studentAssignments = new ArrayList<>();
            studentAssignments.add(new StudentAssignment("Student", 3, date));
            Thesis thesis = new Thesis()
                    .setThesisNumber("A001")
                    .setFree()
                    .setSupervisor("Promotor")
                    .setIssueDateTime(date)
                    .setTopic("Temat")
                    .setStudent("Student2")
                    .setAssignmentDate("10-10-2020 20:00:00")
                    .setKey()
                    .setStudentsAssigned(studentAssignments);

            byte[] data = State.serialize(thesis);

            when(stub.getState("A001")).thenReturn(data);

            contract.chooseStudent(ctx, "Promotor", "A001", "Student");

            thesis.setStudent("Student");

            byte[] data2 = State.serialize(thesis);

            verify(stub).putState("A001", data2);
        }

        @Test
        public void studentCantAssign() {
            when(clientIdentity.getMSPID()).thenReturn("studentsMSP");

            ChaincodeException ex = assertThrows(
                    ChaincodeException.class,
                    () -> contract.chooseStudent(ctx, "Promotor", "A001", "Student")
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
                    () -> contract.chooseStudent(ctx, "Promotor", "A001", "Student")
            );

            assertEquals("Student Student is not assigned to thesis A001", ex.getMessage());
        }

        @Test
        public void alreadyApproved() {
            when(clientIdentity.getMSPID()).thenReturn("supervisorsMSP");

            String date = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
            ArrayList<StudentAssignment> studentAssignments = new ArrayList<>();
            studentAssignments.add(new StudentAssignment("Student1", 3, date));
            Thesis thesis = new Thesis()
                    .setThesisNumber("A001")
                    .setOwned()
                    .setSupervisor("Promotor")
                    .setIssueDateTime(date)
                    .setTopic("Temat")
                    .setStudent("Student")
                    .setKey()
                    .setStudentsAssigned(studentAssignments);

            byte[] data = State.serialize(thesis);

            when(stub.getState("A001")).thenReturn(data);

            RuntimeException ex = assertThrows(
                    RuntimeException.class,
                    () -> contract.chooseStudent(ctx, "Promotor", "A001", "Student1")
            );

            assertEquals("Thesis A001 is already approved", ex.getMessage());
        }

        @Test
        public void isNotSupervisorOfThesis() {
            when(clientIdentity.getMSPID()).thenReturn("supervisorsMSP");

            String date = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
            ArrayList<StudentAssignment> studentAssignments = new ArrayList<>();
            studentAssignments.add(new StudentAssignment("Student", 3, date));
            Thesis thesis = new Thesis()
                    .setThesisNumber("A001")
                    .setFree()
                    .setSupervisor("Promotor2")
                    .setIssueDateTime(date)
                    .setTopic("Temat")
                    .setStudent(" ")
                    .setKey()
                    .setStudentsAssigned(studentAssignments);

            byte[] data = State.serialize(thesis);

            when(stub.getState("A001")).thenReturn(data);

            RuntimeException ex = assertThrows(
                    RuntimeException.class,
                    () -> contract.chooseStudent(ctx, "Promotor", "A001", "Student")
            );

            assertEquals("User Promotor is not a supervisor of thesis A001", ex.getMessage());
        }
    }

    @Nested
    class RevokeThesis {
        @Test
        public void revokes() {
            when(clientIdentity.getMSPID()).thenReturn("studentsMSP");

            String date = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
            ArrayList<StudentAssignment> studentAssignments = new ArrayList<>();
            studentAssignments.add(new StudentAssignment("Student", 3, date));
            Thesis thesis = new Thesis()
                    .setThesisNumber("A001")
                    .setFree()
                    .setSupervisor("Promotor")
                    .setIssueDateTime(date)
                    .setTopic("Temat")
                    .setStudent("Student")
                    .setKey()
                    .setStudentsAssigned(studentAssignments);

            byte[] data = State.serialize(thesis);

            when(stub.getState("A001")).thenReturn(data);

            User user = new User().setName("Student").setKey().addThesisId("A001");
            byte[] userData = State.serialize(user);
            when(stub.getState("Student")).thenReturn(userData);

            contract.revokeThesis(ctx, "A001", "Student");

            studentAssignments = new ArrayList<>();
            thesis.setStudentsAssigned(studentAssignments);

            byte[] data2 = State.serialize(thesis);

            verify(stub).putState("A001", data2);

            user.setThesesId(new ArrayList<>());
            byte[] userData2 = State.serialize(user);
            verify(stub).putState("Student", userData2);
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
    @Nested
    class AcceptAssignment {
        @Test
        public void accepts() {
            when(clientIdentity.getMSPID()).thenReturn("studentsMSP");

            String date = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
            ArrayList<StudentAssignment> studentAssignments = new ArrayList<>();
            studentAssignments.add(new StudentAssignment("Student", 2, date));
            studentAssignments.add(new StudentAssignment("Student2", 3, date));
            Thesis thesis = new Thesis()
                    .setThesisNumber("A001")
                    .setFree()
                    .setSupervisor("Promotor")
                    .setIssueDateTime(date)
                    .setTopic("Temat")
                    .setStudent("Student")
                    .setStudentsAssigned(studentAssignments)
                    .setKey();

            byte[] data = State.serialize(thesis);

            when(stub.getState("A001")).thenReturn(data);

            ArrayList<StudentAssignment> studentAssignments2 = new ArrayList<>();
            studentAssignments2.add(new StudentAssignment("Student", 2, date));
            studentAssignments2.add(new StudentAssignment("Student2", 3, date));
            Thesis otherThesis = new Thesis()
                    .setThesisNumber("A002")
                    .setFree()
                    .setSupervisor("Promotor")
                    .setIssueDateTime(date)
                    .setTopic("Temat")
                    .setStudent("Student")
                    .setStudentsAssigned(studentAssignments2)
                    .setKey();

            byte[] otherThesisData = State.serialize(otherThesis);

            when(stub.getState("A002")).thenReturn(otherThesisData);

            User user = new User().setName("Student").setKey().addThesisId("A001").addThesisId("A002");
            byte[] userData = State.serialize(user);
            when(stub.getState("Student")).thenReturn(userData);

            User user2 = new User().setName("Student2").setKey().addThesisId("A001").addThesisId("A002");
            byte[] userData2 = State.serialize(user2);
            when(stub.getState("Student2")).thenReturn(userData2);

            try {
                contract.acceptAssignment(ctx, "A001", "Student");
            } catch (ParseException ignored) { }

            thesis.setOwned();
            thesis.removeStudentAssignment("Student2");
            byte[] data2 = State.serialize(thesis);
            verify(stub).putState("A001", data2);

            otherThesis.removeStudentAssignment("Student");
            otherThesis.setStudent(" ");
            byte[] otherThesisData2 = State.serialize(otherThesis);
            verify(stub).putState("A002", otherThesisData2);

            user.removeThesisId("A002");
            userData = State.serialize(user);
            verify(stub).putState("Student", userData);

            user2.removeThesisId("A001");
            userData2 = State.serialize(user2);
            verify(stub).putState("Student2", userData2);
        }

        @Test
        public void supervisorCantAccept() {
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

            ChaincodeException ex = assertThrows(
                    ChaincodeException.class,
                    () -> contract.acceptAssignment(ctx, "A001", "Student")
            );

            assertEquals("cannotPerformAction", ex.getMessage());
        }

        @Test
        public void alreadyAccepted() {
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
                    () -> contract.acceptAssignment(ctx, "A001", "Student")
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
                    .setStudent("Student2")
                    .setKey();

            byte[] data = State.serialize(thesis);

            when(stub.getState("A001")).thenReturn(data);

            RuntimeException ex = assertThrows(
                    RuntimeException.class,
                    () -> contract.acceptAssignment(ctx, "A001", "Student")
            );

            assertEquals("User Student is not assigned to thesis A001", ex.getMessage());
        }

        @Test
        public void timeExpired() {
            when(clientIdentity.getMSPID()).thenReturn("studentsMSP");

            String date = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
            Thesis thesis = new Thesis()
                    .setThesisNumber("A001")
                    .setFree()
                    .setSupervisor("Promotor")
                    .setIssueDateTime(date)
                    .setTopic("Temat")
                    .setStudent("Student")
                    .setAssignmentDate("10-10-2020 20:00:00")
                    .setKey();

            byte[] data = State.serialize(thesis);

            when(stub.getState("A001")).thenReturn(data);

            RuntimeException ex = assertThrows(
                    RuntimeException.class,
                    () -> contract.acceptAssignment(ctx, "A001", "Student")
            );

            assertEquals("Time to accept expired", ex.getMessage());
        }
    }

    @Nested
    class DeclineAssignment {
        @Test
        public void declines() {
            when(clientIdentity.getMSPID()).thenReturn("studentsMSP");

            String date = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
            ArrayList<StudentAssignment> studentAssignments = new ArrayList<>();
            studentAssignments.add(new StudentAssignment("Student", 2, date));
            Thesis thesis = new Thesis()
                    .setThesisNumber("A001")
                    .setFree()
                    .setSupervisor("Promotor")
                    .setIssueDateTime(date)
                    .setTopic("Temat")
                    .setStudent("Student")
                    .setStudentsAssigned(studentAssignments)
                    .setKey();

            byte[] data = State.serialize(thesis);

            when(stub.getState("A001")).thenReturn(data);

            User user = new User().setName("Student").setKey().addThesisId("A001");
            byte[] userData = State.serialize(user);
            when(stub.getState("Student")).thenReturn(userData);

            try {
                contract.declineAssignment(ctx, "A001", "Student");
            } catch (ParseException ignored) { }

            thesis.setStudent(" ");
            thesis.setAssignmentDate("");
            thesis.setStudentsAssigned(new ArrayList<>());

            byte[] data2 = State.serialize(thesis);

            verify(stub).putState("A001", data2);

            user.setThesesId(new ArrayList<>());
            byte[] userData2 = State.serialize(user);
            verify(stub).putState("Student", userData2);

        }

        @Test
        public void supervisorCantDecline() {
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

            ChaincodeException ex = assertThrows(
                    ChaincodeException.class,
                    () -> contract.declineAssignment(ctx, "A001", "Student")
            );

            assertEquals("cannotPerformAction", ex.getMessage());
        }

        @Test
        public void alreadyAccepted() {
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
                    () -> contract.declineAssignment(ctx, "A001", "Student")
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
                    .setStudent("Student2")
                    .setKey();

            byte[] data = State.serialize(thesis);

            when(stub.getState("A001")).thenReturn(data);

            RuntimeException ex = assertThrows(
                    RuntimeException.class,
                    () -> contract.declineAssignment(ctx, "A001", "Student")
            );

            assertEquals("User Student is not assigned to thesis A001", ex.getMessage());
        }
    }
}
