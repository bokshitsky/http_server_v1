package httpserver.responses;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by boksh on 23.02.2016.
 */
public class httpresponseTest {

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testGetHeader() throws Exception {
        httpresponse res = new httpresponse();
        res.Code = 200;
        res.charset = "UTF-8";
        res.Content = "sassaassS".getBytes();
        res.ContentType = "text/html";
        System.out.println(new String(res.getBytes()));

        res = new httpresponse();
        res.Code = 404;
        System.out.println(new String(res.getBytes()));

        res = new httpresponse();
        res.Code = 400;
        System.out.println(new String(res.getBytes()));

        res = new httpresponse();
        res.Code = 405;
        System.out.println(new String(res.getBytes()));

    }
}