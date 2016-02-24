package httpserver.resources;


import httpserver.configurations.configuration;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;

import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;


public class NaiveCachingResourceProvider {

    private HashMap<String,byte[]> Cache;
    private HashMap<String, String> ETagCache;
    private boolean useCache;
    private Path rootDir;
    public Charset textFilesCharset;


    public NaiveCachingResourceProvider(configuration config) {
        rootDir = config.rootDir;
        useCache = config.cached;
        textFilesCharset = config.textFilesCharset;

        if (useCache){
            this.Cache = new HashMap<String, byte[]>();
            this.ETagCache = new HashMap<String, String>();
            try {
                reloadCache();
                startCacheValidation();
            } catch (IOException e) {
                System.err.println("Can't start caching for some reason. Please check if required folder exists.");
                System.exit(-1);
            }
        }
    }

    public byte[] getResource(String resourceName) {

        if (!useCache) { //No caching
            return readResourceBytes(resourceName);
        }

        synchronized (this.Cache) {
            if (!this.Cache.containsKey(resourceName)) {
                //caching is ON, but Cache does not contain Resource for some reason
                return this.Cache.get(resourceName);
            } else {
                return readResourceBytes(resourceName);
            }
        }
    }

    public String getResourceETag(String resourceName) {

        if (!useCache) { //No caching
            return getMD5ETag(readResourceBytes(resourceName));
        }

        synchronized (this.Cache) {
            synchronized (this.ETagCache) {
                if (!this.Cache.containsKey(resourceName)) {
                    //caching is ON, but Cache does not contain Resource for some reason
                    return this.ETagCache.get(resourceName);
                } else {
                    return getMD5ETag(readResourceBytes(resourceName));
                }
            }
        }
    }

    private String getMD5ETag(byte[] content){
        String result = null;
        byte[] bytes = null;
        try {
            if (content!=null){
                bytes = MessageDigest.getInstance("MD5").digest(content);

            StringBuffer hexString = new StringBuffer();
            for (int i=0;i<bytes.length;i++) {
                String hex=Integer.toHexString(0xff & bytes[i]);
                if(hex.length()==1) hexString.append('0');
                hexString.append(hex);
            }
                return hexString.toString();
            } else {
                return null;
            }

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }


    //Method takes Resource name and gets bytes from disk
    private byte[] readResourceBytes (String Resource)  {
        Path data = Paths.get(rootDir.toString(),Resource);
        try {
            return Files.readAllBytes(data);
        } catch (Throwable e) {
            return null;
        }
    }


    //method is called every time any file on the server is updated.
    //note: better to update only new or changed files.
    private void reloadCache() {
        synchronized (this.Cache) {
            synchronized (this.ETagCache) {
                this.Cache = new HashMap<String, byte[]>();
                this.ETagCache = new HashMap<String, String>();
                    try {
                        Files.walkFileTree(rootDir, new SimpleFileVisitor<Path>() {
                            @Override
                            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                                String resName = rootDir.relativize(file).toString();
                                byte[] bytes = readResourceBytes(resName);
                                Cache.put(resName.replace("\\","/"), bytes);
                                ETagCache.put(resName.replace("\\","/"), getMD5ETag(bytes));
                                return FileVisitResult.CONTINUE;
                            }
                        });
                        System.out.println("CACHE WAS UPDATED");
                    } catch (IOException e) {
                        System.err.println("Error during cache updating");
                }
            }
        }
    }

    //Method start separate watching thread looking for file system update.
    //If Resource folder is updated - cache is reloaded
    private void startCacheValidation() throws IOException {
        NaiveCachingResourceProvider service = this;
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
