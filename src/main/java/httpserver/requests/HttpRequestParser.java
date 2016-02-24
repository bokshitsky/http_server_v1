package httpserver.requests;

import java.io.*;
import java.util.HashMap;



public class HttpRequestParser {

    private HashMap<String, String> requestParams;

    public HttpRequestParser() {
        requestParams = new HashMap<String, String>();
    }

    //SIMPLE HTTP REQUEST PARSING. WORKS ONLY WITH SIMPLE GET REQUSETS.
    public HttpRequestParser parseRequestParams(InputStream stream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        int index = 0;
        String[] params;
        while(true) {
            String s = br.readLine();
            if(s == null || s.trim().length() == 0) {
                break;
            }
            switch (index) {
                case 0:
                    params = s.split(" ");
                    requestParams.put("METHOD",params[0]);
                    requestParams.put("RESOURCE",params[1]);
                    break;
                default:
                    params = s.split(": ",2);
                    requestParams.put(params[0],params[1].trim());
            }
            index+=1;
        }
        return this;

    }

    public HttpRequest getRequestObject(){
        HttpRequest req = new HttpRequest();
        req.Resource = requestParams.get("RESOURCE").substring(1);
        req.Method = requestParams.get("METHOD");
        req.AcceptCharset = requestParams.get("Accept-Charset");

        if (requestParams.containsKey("If-Match")) {
            req.IfMatch = true;
            req.ETag = requestParams.get("If-Match").replace("\"","");
            //ETag should no contain " symbol
        }
        return req;
    }

}