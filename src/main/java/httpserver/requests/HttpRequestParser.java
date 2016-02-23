package httpserver.requests;

import java.io.*;
import java.util.HashMap;



public class HttpRequestParser {

    public HashMap<String, String> requestParams;

    public HttpRequestParser() {
        requestParams = new HashMap<String, String>();
    }

    //Works with required for GET requests only
    public void parseRequest(InputStream stream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        int index = 0;
        String[] params;
        while(true) {
            String s = br.readLine();
            if(s == null || s.trim().length() == 0) { break; }
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
    }
}