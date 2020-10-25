package pl.poznan.put.thesisapi.entities;

public class AssignStudentDto {

    private String student;
    private String thesisNumber;
    private int priority;

    public String getThesisNumber() {
        return thesisNumber;
    }

    public void setThesisNumber(String thesisNumber) {
        this.thesisNumber = thesisNumber;
    }

    public String getStudent() {
        return student;
    }

    public void setStudent(String student) {
        this.student = student;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
