package httpserver;


import java.io.IOException;
import java.nio.file.*;

import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;

public class resourcesProvider {


    private HashMap<String,byte[]> Cache;
    private boolean useCache;
    private Path rootDir;

    public resourcesProvider(configuration config) {
        rootDir = config.rootDir;
        useCache = config.cached;

        if (useCache){
            this.Cache = new HashMap<String, byte[]>();
            try {
                reloadCache();
                startCacheValidation();
            } catch (IOException e) {
                System.err.println("Can't start caching for some reason. Please check if resource folder exists.");
                System.exit(-1);
            }

        }

    }

    public byte[] getResource(String resourceName) {

        if (!useCache) { //No caching
            return readResourceBytes(resourceName);
        }

        synchronized (this.Cache) {
            if (!this.Cache.containsKey(resourceName)) { //caching is ON, but Cache does not contain resource
                return this.Cache.get(resourceName);
            } else {
                return readResourceBytes(resourceName);
            }
        }
    }

    //Method loads file from disk to byte array
    private byte[] readResourceBytes (String Resource)  {
        Path data = Paths.get(rootDir.toString(),Resource);
        try {
            return Files.readAllBytes(data);
        } catch (IOException e) {
            return null;
        }
    }


    //method is called every time any file on the server is updated.
    //note: better to update only new or changed files.
    private void reloadCache() {
        synchronized (this.Cache) {
            this.Cache = new HashMap<String, byte[]>();
            try {
                Files.walkFileTree(rootDir, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        String resName = rootDir.relativize(file).toString();
                        Cache.put(resName.replace("\\","/"), readResourceBytes(resName));
                        return FileVisitResult.CONTINUE;
                    }
                });
                System.out.print("Cache updated.");
            } catch (IOException e) {
                System.err.println("Error during cache updating");
            }
        }
    }


    //Method start separate watching thread looking for file system update.
    //If resource folder is updated - cache is reloaded
    private void startCacheValidation() throws IOException {
        resourcesProvider service = this;
        new Thread(new Runnable() {
            @Override
            public void run() {
                WatchService watcher = null;
                try {
                    watcher = rootDir.getFileSystem().newWatchService();
                    rootDir.register(watcher,
                            StandardWatchEventKinds.ENTRY_CREATE,
                            StandardWatchEventKinds.ENTRY_DELETE,
                            StandardWatchEventKinds.ENTRY_MODIFY);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                while (true) {
                    WatchKey key = null;
                    try {
                        key = watcher.take();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (!key.pollEvents().isEmpty()) {
                        service.reloadCache();
                    }
                    key.reset();
                }
            }
        }).start();
    }
}
