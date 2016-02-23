package httpserver;

import httpserver.configurations.configuration;
import httpserver.configurations.JsonConfigProvider;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class jsonConfigProviderTest {

    JsonConfigProvider provider;

    @Before
    public void setUp() throws Exception {
        //Right now test is based on file "configuration.txt" from project root dir.
        provider = new JsonConfigProvider("config.txt");
    }

    @Test
    public void testGetDefaultConfig() throws Exception {
        configuration config = provider.getDefaultConfig();
        assertEquals(config.cached,true);
        assertEquals(config.port,8081);
        assertEquals(config.rootDir.getFileName().toString(),"resources");
    }

    @Test
    public void testGetConfig() throws Exception {
        configuration config = provider.getConfig();
        assertEquals(config.cached,true);
        assertEquals(config.port,8090);
        assertEquals(config.rootDir.getFileName().toString(),"resources");

    }
}