package httpserver.responses;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;

public class httpresponse {


    public static final HashMap<Integer,String> Messages = getCodesMessages();
    public static final HashMap<String,String> Suffixes = getSuffixMap();
    public int Code;
    public String ContentType;
    public String charset;
    public byte[] Content;

    private static HashMap<Integer,String> getCodesMessages(){
        HashMap<Integer,String> Map= new HashMap<Integer,String>();
        Map.put(200,"OK");
        Map.put(404,"Not Found");
        Map.put(405,"Method Not Allowed");
        Map.put(400,"Bad Request");
        Map.put(412,"Precondition Failed");
        return Map;
    }

    private static HashMap<String,String> getSuffixMap(){
        HashMap<String,String> Map= new HashMap<String,String>();
        Map.put(".js","application/javascript");
        Map.put(".jpg","image/jpeg");
        Map.put(".jpeg","image/jpeg");
        Map.put(".html","text/html");
        return Map;
    }

    public httpresponse setContentTypeBySuffix(String resource) {
        String suffix = Suffixes.get(resource.substring(resource.length()-4));
        return this;
    }

    public byte[] getBytes() {
        String headerText = "HTTP/1.1 " + Code + Messages.get(Code) + "\r\n";
        headerText += "Server: BokshServer\r\n";
        String body = null;
        switch (Code){
            case 200:
                headerText += "Content-Type: " + ContentType;
                if (!ContentType.equals("image/jpeg")){
                    headerText +=  "; charset="+charset.toLowerCase() + "\r\n";
                }else{
                    headerText += "\r\n";
                }
                headerText += "Content-Length: " + Content.length + "\r\n";
                headerText += "Connection: close\r\n\r\n";
                byte[] headerBytes = headerText.getBytes();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream(headerBytes.length + Content.length);

                try {
                    outputStream.write(headerBytes);
                    outputStream.write(Content);
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
