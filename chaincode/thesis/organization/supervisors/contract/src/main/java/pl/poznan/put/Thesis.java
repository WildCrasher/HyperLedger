/*
 * SPDX-License-Identifier: Apache-2.0
 */

package pl.poznan.put;

import static java.nio.charset.StandardCharsets.UTF_8;

import pl.poznan.put.ledgerapi.State;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;
import org.json.JSONObject;
import org.json.JSONPropertyIgnore;

@DataType()
public final class Thesis extends State {

    public static final String FREE = "FREE";
    public static final  String OWNED = "OWNED";

    @Property()
    private String state = "";

    public String getState() {
        return state;
    }

    public Thesis setState(final String newState) {
        this.state = newState;
        return this;
    }

    @JSONPropertyIgnore()
    public boolean isFree() {
        return this.state.equals(Thesis.FREE);
    }

    @JSONPropertyIgnore()
    public boolean isOwned() {
        return this.state.equals(Thesis.OWNED);
    }

    public Thesis setFree() {
        this.state = Thesis.FREE;
        return this;
    }

    public Thesis setOwned() {
        this.state = Thesis.OWNED;
        return this;
    }

    @Property()
    private String thesisNumber;

    @Property()
    private String supervisor;

    @Property()
    private String issueDateTime;

    @Property()
    private String topic;

    @Property()
    private String student;

    public String getStudent() {
        return student;
    }

    public Thesis setStudent(final String newStudent) {
        this.student = newStudent;
        return this;
    }

    public Thesis() {
        super();
    }

    public Thesis setKey() {
        this.setKey(State.makeKey(new String[] {this.thesisNumber}));
        return this;
    }

    public String getThesisNumber() {
        return thesisNumber;
    }

    public Thesis setThesisNumber(final String newThesisNumber) {
        this.thesisNumber = newThesisNumber;
        return this;
    }

    public String getSupervisor() {
        return supervisor;
    }

    public Thesis setSupervisor(final String newSupervisor) {
        this.supervisor = newSupervisor;
        return this;
    }

    public String getIssueDateTime() {
        return issueDateTime;
    }

    public Thesis setIssueDateTime(final String newIssueDateTime) {
        this.issueDateTime = newIssueDateTime;
        return this;
    }

    public String getTopic() {
        return topic;
    }

    public Thesis setTopic(final String newTopic) {
        this.topic = newTopic;
        return this;
    }

    @Override
    public String toString() {
        return "Thesis::" + this.getKey() + "   " + this.getThesisNumber() + " " + getSupervisor() + " "
                + getTopic() + " " + getStudent();
    }

    /**
     * Deserialize a state data to thesis
     *
     * @param {Buffer} data to form back into the object
     */
    public static Thesis deserialize(final byte[] data) {
        JSONObject json = new JSONObject(new String(data, UTF_8));

        String supervisor = json.getString("supervisor");
        String thesisNumber = json.getString("thesisNumber");
        String issueDateTime = json.getString("issueDateTime");
        String student = json.getString("student");
        String state = json.getString("state");
        String topic = json.getString("topic");
        return createInstance(supervisor, thesisNumber, issueDateTime, student, state, topic);
    }

    public static byte[] serialize(final Thesis thesis) {
        return State.serialize(thesis);
    }

    /**
     * Factory method to create a thesis object
     */
    public static Thesis createInstance(final String supervisor, final String thesisNumber, final String issueDateTime,
                                        final String student, final String state, final String topic) {
        return new Thesis().setSupervisor(supervisor).setThesisNumber(thesisNumber).setKey()
                .setIssueDateTime(issueDateTime).setStudent(student).setState(state).setTopic(topic);
    }

    public boolean equals(Object o) {
        if(this == o) return true;
        if(this.getClass() != o.getClass()) return false;

        Thesis thesis = (Thesis) o;
        return this.getSupervisor().equals(thesis.getSupervisor())
                && this.getStudent().equals(thesis.getStudent())
                && this.getIssueDateTime().equals(thesis.getIssueDateTime())
                && this.getState().equals(thesis.getState())
                && this.getThesisNumber().equals(thesis.getThesisNumber())
                && this.getTopic().equals(thesis.getTopic())
                && this.getKey().equals(thesis.getKey());
    }

}
