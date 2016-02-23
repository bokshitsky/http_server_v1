package httpserver.configurations;


import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JsonConfigProvider implements IConfigProvider {

    private final String filename;
    public static String DEFAULT_FOLDER = "./resources";
    public static boolean DEFAULT_CACHING = true;
    public static int DEFAULT_PORT = 8081;

    public JsonConfigProvider(String filename) {
        this.filename = filename;
    }

    public JsonConfigProvider() {
        this.filename = null;
    }

    public static configuration getDefaultConfig() {
        return new configuration(DEFAULT_CACHING,DEFAULT_FOLDER, DEFAULT_PORT);
    }


    public configuration getConfig() {
        try {
            JSONObject json = new JSONObject(loadfile(this.filename));
            return new configuration(
                    json.getBoolean("cached"),
                    json.getString("root"),
                    json.getInt("port"));
        } catch (IOException e) {
            System.err.println("Can't load specified configuration file: " + this.filename);
            System.err.println("Default configuration is used instead");
            return this.getDefaultConfig();
        }

    }

    private String loadfile(String filename) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filename)));
    }


}
