package httpserver;


import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class configurationProvider {

    private final String filename;
    public static String DEFAULT_FOLDER = "./resources";
    public static boolean CACHED = true;
    public static int PORT = 8081;

    public configurationProvider(String filename) {
        this.filename = filename;
    }

    public configurationProvider() {
        this.filename = null;
    }

    public configuration getDefaultConfig() {
        return new configuration(CACHED,DEFAULT_FOLDER,PORT);
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
            System.err.println("Default config is used instead");
            return this.getDefaultConfig();
        }

    }

    private String loadfile(String filename) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filename)));
    }


}
