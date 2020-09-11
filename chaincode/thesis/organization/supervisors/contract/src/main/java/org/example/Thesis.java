/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.example;

import static java.nio.charset.StandardCharsets.UTF_8;

import org.example.ledgerapi.State;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;
import org.json.JSONObject;
import org.json.JSONPropertyIgnore;

@DataType()
public class Thesis extends State {

    public final static String FREE = "FREE";
    public final static String OWNED = "OWNED";

    @Property()
    private String state="";

    public String getState() {
        return state;
    }

    public Thesis setState(String state) {
        this.state = state;
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

    public Thesis setStudent(String student) {
        this.student = student;
        return this;
    }

    public Thesis() {
        super();
    }

    public Thesis setKey() {
        this.key = State.makeKey(new String[] { this.thesisNumber });
        return this;
    }

    public String getThesisNumber() {
        return thesisNumber;
    }

    public Thesis setThesisNumber(String thesisNumber) {
        this.thesisNumber = thesisNumber;
        return this;
    }

    public String getSupervisor() {
        return supervisor;
    }

    public Thesis setSupervisor(String supervisor) {
        this.supervisor = supervisor;
        return this;
    }

    public String getIssueDateTime() {
        return issueDateTime;
    }

    public Thesis setIssueDateTime(String issueDateTime) {
        this.issueDateTime = issueDateTime;
        return this;
    }

    public String getTopic() {
        return topic;
    }

    public Thesis setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    @Override
    public String toString() {
        return "Thesis::" + this.key + "   " + this.getThesisNumber() + " " + getSupervisor() + " " + getTopic() + " " + getStudent();
    }

    /**
     * Deserialize a state data to thesis
     *
     * @param {Buffer} data to form back into the object
     */
    public static Thesis deserialize(byte[] data) {
        JSONObject json = new JSONObject(new String(data, UTF_8));

        String supervisor = json.getString("supervisor");
        String thesisNumber = json.getString("thesisNumber");
        String issueDateTime = json.getString("issueDateTime");
        String student = json.getString("student");
        String state = json.getString("state");
        String topic = json.getString("topic");
        return createInstance(supervisor, thesisNumber, issueDateTime, student, state, topic);
    }

    public static byte[] serialize(Thesis paper) {
        return State.serialize(paper);
    }

    /**
     * Factory method to create a thesis object
     */
    public static Thesis createInstance(String supervisor, String thesisNumber, String issueDateTime,
                                        String student, String state, String topic) {
        return new Thesis().setSupervisor(supervisor).setThesisNumber(thesisNumber).setKey()
                .setIssueDateTime(issueDateTime).setStudent(student).setState(state).setTopic(topic);
    }


}
