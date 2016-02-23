package httpserver;


import httpserver.configurations.configuration;
import httpserver.configurations.configurationProvider;
import httpserver.resources.resourcesProvider;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.nio.charset.Charset;


public class main {

    public static void main(String[] args) throws Throwable {

        configuration config;
        if (args.length >= 1) {
            configurationProvider cp = new configurationProvider(args[0]);
            config = cp.getConfig();
        } else{
            config = configurationProvider.getDefaultConfig();
        }

        new server(config).start();
    }

}

