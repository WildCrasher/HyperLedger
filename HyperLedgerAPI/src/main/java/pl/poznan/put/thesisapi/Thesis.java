package pl.poznan.put.thesisapi;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Thesis {

    private String state;
    private String thesisNumber;
    private String supervisor;
    private String issueDateTime;
    private String topic;
    private String student;

    public Thesis() {
        this.issueDateTime = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
    }

    public Thesis(String state, String thesisNumber, String supervisor, String issueDateTime, String topic, String student) {
        this.state = state;
        this.thesisNumber = thesisNumber;
        this.supervisor = supervisor;
        this.issueDateTime = issueDateTime;
        this.topic = topic;
        this.student = student;
    }

    public Thesis(String thesisNumber, String supervisor, String topic) {
        this.thesisNumber = thesisNumber;
        this.supervisor = supervisor;
        this.topic = topic;
        this.issueDateTime = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
    }

    public String getThesisNumber() {
        return thesisNumber;
    }

    public void setThesisNumber(String thesisNumber) {
        this.thesisNumber = thesisNumber;
    }

    public String getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(String supervisor) {
        this.supervisor = supervisor;
    }

    public String getIssueDateTime() {
        return issueDateTime;
    }

    public void setIssueDateTime(String issueDateTime) {
        this.issueDateTime = issueDateTime;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getStudent() {
        return student;
    }

    public void setStudent(String student) {
        this.student = student;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
