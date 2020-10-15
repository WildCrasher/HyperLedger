/*
SPDX-License-Identifier: Apache-2.0
*/
package pl.poznan.put.ledgerapi;

import static java.nio.charset.StandardCharsets.UTF_8;

import org.json.JSONObject;

/**
 * State class. States have a class, unique key, and a lifecycle current state
 * the current state is determined by the specific subclass
 */
public class State {

    private String key;

    /**
     * @param {String|Object} class An identifiable class of the instance
     * @param {keyParts[]} elements to pull together to make a key for the objects
     */
    public State() {

    }

    /**
     * @return
     */
    public String getKey() {
        return this.key;
    }

    /**
     * @return
     */
    public String[] getSplitKey() {
        return State.splitKey(this.key);
    }

    /**
     * Convert object to buffer containing JSON data serialization Typically used
     * before putState()ledger API
     *
     * @param {Object} JSON object to serialize
     * @return {buffer} buffer with the data to store
     */
    public static byte[] serialize(final Object object) {
        String jsonStr = new JSONObject(object).toString();
        return jsonStr.getBytes(UTF_8);
    }

    /**
     * Join the keyParts to make a unififed string
     *
     * @param (String[]) keyParts
     */
    public static String makeKey(final String[] keyParts) {
        return String.join(":", keyParts);
    }

    public static String[] splitKey(final String key) {
        return key.split(":");
    }

    /**
     * @param newKey
     */
    protected void setKey(final String newKey) {
        this.key = newKey;
    }
}
