package httpserver.responses;


import httpserver.requests.HttpRequest;
import httpserver.resources.NaiveCachingResourceProvider;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class HttpRequestProcessor {

    private final NaiveCachingResourceProvider rp;

    public static final HashMap<Integer,String> Messages = getCodesMessages();
    public static final HashMap<String,String> Suffixes = getSuffixMap();

    //MAPPING {RESPONSE CODE:RESPONCE MSG}
    private static HashMap<Integer,String> getCodesMessages(){
        HashMap<Integer,String> Map= new HashMap<Integer,String>();
        Map.put(200,"OK");
        Map.put(404,"Not Found");
        Map.put(405,"Method Not Allowed");
        Map.put(400,"Bad Request");
        Map.put(412,"Precondition Failed");
        return Map;
    }

    //MAPPING {FILE EXTENSION:CONTENT TYPE}
    private static HashMap<String,String> getSuffixMap(){
        HashMap<String,String> Map= new HashMap<String,String>();
        Map.put(".js","application/javascript");
        Map.put(".jpg","image/jpeg");
        Map.put(".jpeg","image/jpeg");
        Map.put(".html","text/html");
        Map.put(".txt","text/html");
        return Map;
    }

    public HttpRequestProcessor(NaiveCachingResourceProvider resourceProvider) {
        this.rp = resourceProvider;
    }


    private String getResponceContentType(String Resource) {
        String suffix = Resource.substring(Resource.lastIndexOf(".")).toLowerCase();
        return Suffixes.get(suffix);
    }

    public HttpResponce ProcessRequest(HttpRequest req) {
        HttpResponce res = new HttpResponce();

        try {
            res.Code = 404; //DEFAULT

            if (!req.Method.equals("GET")) { //ONLY GET REQUEST IS SUPPORTED
                System.out.println(req.Method.length());
                res.Code = 405;
                return res;
            }

            byte[] resourceBytes = rp.getResource(req.Resource);
            if (resourceBytes == null) {    //IF RESOURCE IS NOT AVILABLE RETURN 404
                return res;
            }

            if (req.IfMatch) {
                String ActualETag = rp.getResourceETag(req.Resource);
                String RequestETag = req.ETag;
                if (!ActualETag.equals(RequestETag)) {
                    res.Code = 412;
                    return res;
                }
            }

            res.ContentType = getResponceContentType(req.Resource);
            res.RequestedCharset = req.AcceptCharset != null ? req.AcceptCharset : rp.textFilesCharset.name().toLowerCase();
            res.ETag = rp.getResourceETag(req.Resource);
            if (!res.RequestedCharset.toLowerCase().equals(rp.textFilesCharset.name().toLowerCase())){
                res.BodyContent = new String(resourceBytes,rp.textFilesCharset).getBytes(res.RequestedCharset);
            }else{
                res.BodyContent = resourceBytes;
            }
            res.Code = 200;
            return res;

        } catch (Exception e) {
            res.Code = 400;
            return res;
        }
    }

    public byte[] getResponceBytes(HttpResponce res) {
        String headerText = "HTTP/1.1 " + res.Code + Messages.get(res.Code) + "\r\n";
        headerText += "Server: BokshServer\r\n";
        String body = null;
        switch (res.Code){
            case 200:
                headerText += "Content-Type: " + res.ContentType;
                if (!res.ContentType.equals("image/jpeg")){
                    headerText +=  "; charset="+res.RequestedCharset.toLowerCase() + "\r\n";
                }else{
                    headerText += "\r\n";
                }
                headerText += "Content-Length: " + res.BodyContent.length + "\r\n";
                headerText += "ETag: " + "\"" + res.ETag + "\"" + "\r\n";
                headerText += "Connection: close\r\n\r\n";
                byte[] headerBytes = headerText.getBytes();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream(headerBytes.length + res.BodyContent.length);

                try {
                    outputStream.write(headerBytes);
                    outputStream.write(res.BodyContent);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return outputStream.toByteArray();

            default:
                headerText += "Connection: close\r\n\r\n";
                return headerText.getBytes();

        }
    }
}
