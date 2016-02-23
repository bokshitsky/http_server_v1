package httpserver.configurations;


import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;

public class configuration {

    public Path rootDir;
    public boolean cached;
    public int port;
    public Charset textFilesCharset;

    public configuration(boolean cached, String rootDir, int port, Charset textFilesCharset) {
        this.cached = cached;
        this.rootDir = Paths.get(rootDir).toAbsolutePath().normalize();
        this.port = port;
        this.textFilesCharset = textFilesCharset;
    }

}
