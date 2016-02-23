package httpserver.responses;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class HttpResponce {


    public int Code;
    public String ContentType;
    public String RequestedCharset;
    public String ETag;
    public byte[] BodyContent;

}
