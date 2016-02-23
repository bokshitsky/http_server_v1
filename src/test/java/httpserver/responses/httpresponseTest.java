package httpserver.responses;

import httpserver.resources.NaiveCachingResourceProvider;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;


public class httpresponseTest {

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testGetHeader() throws Exception {
        NaiveCachingResourceProvider rp =  Mockito.mock(NaiveCachingResourceProvider.class);
        HttpRequestProcessor proc = new HttpRequestProcessor(rp);


        HttpResponce res = new HttpResponce();
        res.Code = 200;
        res.RequestedCharset = "UTF-8";
        res.BodyContent = "sassaassS".getBytes();
        res.ContentType = "text/html";
        System.out.println(new String(proc.getResponceBytes(res)));

        res = new HttpResponce();
        res.Code = 404;
        System.out.println(new String(proc.getResponceBytes(res)));

        res = new HttpResponce();
        res.Code = 400;
        System.out.println(new String(proc.getResponceBytes(res)));

        res = new HttpResponce();
        res.Code = 405;
        System.out.println(new String(proc.getResponceBytes(res)));

        res = new HttpResponce();
        res.Code = 412;
        System.out.println(new String(proc.getResponceBytes(res)));

        res.getClass();

    }
}