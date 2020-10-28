package pl.poznan.put.thesisapi.entities;

public class ChooseStudentDto {
    private String thesisNumber;
    private String student;

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
}
