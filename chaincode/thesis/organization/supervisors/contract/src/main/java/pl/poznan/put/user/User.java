package pl.poznan.put.user;

import com.google.gson.Gson;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;
import pl.poznan.put.ledgerapi.State;

import java.util.ArrayList;

import static java.nio.charset.StandardCharsets.UTF_8;

@DataType()
public class User extends State {

    @Property()
    private String name;

    @Property()
    private ArrayList<String> thesesId = new ArrayList<>();

    public static User deserialize(final byte[] data) {
        return new Gson().fromJson(new String(data, UTF_8), User.class);
    }

    public static byte[] serialize(final User user) {
        return State.serialize(user);
    }

    public static User createInstance(final String name, final ArrayList<String> thesesId) {
        return new User().setKey().setName(name).setThesesId(thesesId);
    }

    public User setKey() {
        this.setKey(State.makeKey(new String[] {this.name}));
        return this;
    }

    public String getName() {
        return name;
    }

    public User setName(final String newName) {
        this.name = newName;
        return this;
    }

    public ArrayList<String> getThesesId() {
        return thesesId;
    }

    public User setThesesId(final ArrayList<String> newThesesId) {
        this.thesesId = newThesesId;
        return this;
    }

    public User addThesisId(final String thesisId) {
        this.thesesId.add(thesisId);
        return this;
    }

    public User removeThesisId(final String thesisId) {
        this.thesesId.remove(thesisId);
        return this;
    }
}
