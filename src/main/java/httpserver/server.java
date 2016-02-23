package httpserver;


import httpserver.configurations.configuration;
import httpserver.requests.HttpRequestParser;
import httpserver.requests.HttpRequest;
import httpserver.resources.NaiveCachingResourceProvider;
import httpserver.responses.HttpResponce;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class server {

    private NaiveCachingResourceProvider rp;
    private configuration config;

    public server(configuration config) {
        this.config = config;
        this.rp = new NaiveCachingResourceProvider(config);
    }

    public void start()  {
        ServerSocket ss = null;
        try {
            ss = new ServerSocket(config.port);
        } catch (IOException e) {
            System.err.println("Error during server start.");
            System.exit(-1);
        }

        while (true) {
            Socket s = null;
            try {
                s = ss.accept();
                System.err.println("Client accepted");
                new Thread(new SocketProcessor(s, rp)).start();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private static class SocketProcessor implements Runnable {

        private Socket s;
        private InputStream is;
        private OutputStream os;
        private final NaiveCachingResourceProvider rp;

        private SocketProcessor(Socket s, NaiveCachingResourceProvider rp) throws IOException {
            this.s = s;
            this.is = s.getInputStream();
            this.os = s.getOutputStream();
            this.rp = rp;
        }

        public void run() {
            HttpRequest req = null;
            try {
                req = parseRequest();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                writeResponse(getResponce(req));
            } catch (IOException e) {
                e.printStackTrace();
            }  finally {
                try {
                    s.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

        //PARSE HTTP REQUEST.
        private HttpRequest parseRequest() throws IOException{

            HttpRequestParser parser = new HttpRequestParser();
            parser.parseRequest(is);

            HttpRequest req = new HttpRequest();
            req.resource = parser.requestParams.get("RESOURCE").substring(1);
            req.method = parser.requestParams.get("METHOD");
            req.AcceptCharset = parser.requestParams.get("Accept-Charset");
            if (parser.requestParams.containsKey("If-Match")) {
                req.etag = parser.requestParams.get("If-Match").replace("\"","");
                //etag should no contain " symbol
            }
            return req;
        }

        //PROCESS HTTP REQUEST TO HTTP RESPONSE
        private HttpResponce getResponce(HttpRequest req) {

            HttpResponce res = new HttpResponce();

            res.Code = 404;

            if (!req.method.equals("GET")) {
                System.out.println(req.method.length());
                res.Code = 405;
                return res;
            }

            byte[] resourceBytes = rp.getResource(req.resource);
            if (resourceBytes == null) {
                return res;
            }
            res.Content = resourceBytes;
            res.setContentTypeBySuffix(req.resource);
            res.charset = "utf-8";
            res.Code = 200;

            return res;
        }

        private void writeResponse(HttpResponce res) throws IOException {
            os.write(res.getBytes());
            os.flush();
        }

    }
}
