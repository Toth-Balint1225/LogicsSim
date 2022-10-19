package hu.unipannon.sim.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

/**
 * Utility shorthand for json parsing, so the gson is hidden from the server implementation.
 * That means the json parsing backend can change without any effect on the server itself.
 * @author Tóth Bálint
 */
public class JsonParser {

    /**
     * The reusable gson instance.
     */
    private Gson gson;

    /**
     * The self instance for the singleton.
     */
    private static JsonParser instance = null;

    /**
     * Private constructor that creates the gson
     */
    private JsonParser() {
        GsonBuilder builder = new GsonBuilder();
        this.gson = builder.create();
    }

    /**
     * Standard singleton instance query.
     * @return The instance of the singleton.
     */
    public static JsonParser getInstance() {
        if (instance == null) {
            instance = new JsonParser();
        }
        return instance;
    }

    /**
     * The parse-to-object functionallity.
     * @param <T> The class that the string needs to be parsed into.
     * @param json The string representation of an object.
     * @param type The typeclass of the target class.
     * @return The object of the json string representation parsed into the specified type.
     * @throws Exception
     */
    public<T> T toClass(String json, Class<T> type) throws JsonParseException {
        T ret = null;
        try {
            ret = gson.fromJson(json, type);
        } catch (JsonSyntaxException ex) {
            JsonParseException toThrow = new JsonParseException("Syntax error occured parsing json object: " + json);
            toThrow.setStackTrace(ex.getStackTrace());
            throw toThrow;
        }
        return ret;
    }

    public String toJson(Object obj) {
        return gson.toJson(obj);
    }
}
