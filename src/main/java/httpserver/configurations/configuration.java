package httpserver.configurations;


import java.io.File;
import java.io.IOException;
import java.nio.file.*;

public class configuration {

    public Path rootDir;
    public boolean cached;
    public int port;

    public configuration(boolean cached, String rootDir, int port) {
        this.cached = cached;
        this.rootDir = Paths.get(rootDir).toAbsolutePath().normalize();
        this.port = port;
    }

}
