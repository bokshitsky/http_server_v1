package httpserver;


import httpserver.configurations.configuration;
import httpserver.configurations.JsonConfigProvider;


public class main {

    public static void main(String[] args) throws Throwable {

        configuration config;
        if (args.length >= 1) {
            JsonConfigProvider provider = new JsonConfigProvider(args[0]);
            config = provider.getConfig();
        } else{
            config = JsonConfigProvider.getDefaultConfig();
        }

        new server(config).start();
    }

}

