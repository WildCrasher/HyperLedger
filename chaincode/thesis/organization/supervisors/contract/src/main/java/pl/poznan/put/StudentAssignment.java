package pl.poznan.put;

public final class StudentAssignment {

    private String studentName;
    private int priority;
    private String date;

    public StudentAssignment() {

    }

    public StudentAssignment(final String newStudentName, final int newPriority, final String newDate) {
        this.studentName = newStudentName;
        this.priority = newPriority;
        this.date = newDate;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(final String newStudentName) {
        this.studentName = newStudentName;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(final int newPriority) {
        this.priority = newPriority;
    }

    public String getDate() {
        return date;
    }

    public void setDate(final String newDate) {
        this.date = newDate;
    }

    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (this.getClass() != o.getClass()) {
            return false;
        }

        StudentAssignment studentAssignment = (StudentAssignment) o;
        return this.getStudentName().equals(studentAssignment.getStudentName())
                && this.getPriority() == studentAssignment.getPriority()
                && this.getDate().equals(studentAssignment.getDate());
    }

    public int hashCode() {
        return 0;
    }
}
