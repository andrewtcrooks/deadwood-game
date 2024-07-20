import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

/**
 * Utility class for serializing and deserializing objects to and from JSON.
 */
public class JsonUtil {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Serialize object to JSON string
     * 
     * @param obj the object to serialize
     * @return the JSON string
     */
    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }

    /**
     * Deserialize JSON string to object
     * 
     * @param json the JSON string
     * @param classOfT the class of the object
     * @return the object
     */
    public static <T> T fromJson(String json, Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }

    /**
     * Save object to JSON file
     * 
     * @param obj the object to save
     * @param filename the filename to save to
     * @throws IOException if an I/O error occurs
     */
    public static void saveToJsonFile(Object obj, String filename) throws IOException {
        try (FileWriter writer = new FileWriter(filename)) {
            gson.toJson(obj, writer);
        }
    }

    /** 
     * Load object from JSON file
     * 
     * @param filename the filename to load from
     * @param classOfT the class of the object
     * @return the object
     * @throws IOException if an I/O error occurs
     */
    public static <T> T loadFromJsonFile(String filename, Class<T> classOfT) throws IOException {
        try (Reader reader = new FileReader(filename)) {
            return gson.fromJson(reader, classOfT);
        }
    }
}
