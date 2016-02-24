package httpserver.requests;

public class HttpRequest {

    //All fields are public. No costructor is used for simplicity
    public String AcceptCharset;
    public String Resource;
    public boolean IfMatch = false;
    public String ETag;
    public String Method;

}
