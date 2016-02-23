package httpserver;


import httpserver.configurations.configuration;
import httpserver.requests.HttpRequestParser;
import httpserver.requests.httprequest;
import httpserver.resources.resourcesProvider;
import httpserver.responses.httpresponse;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;

public class server {

    private resourcesProvider rp;
    private configuration config;

    public server(configuration config) {
        this.config = config;
        this.rp = new resourcesProvider(config);
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
        private final resourcesProvider rp;

        private SocketProcessor(Socket s, resourcesProvider rp) throws IOException {
            this.s = s;
            this.is = s.getInputStream();
            this.os = s.getOutputStream();
            this.rp = rp;
        }

        public void run() {
            httprequest req = null;
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

        private httprequest parseRequest() throws IOException{

            HttpRequestParser parser = new HttpRequestParser();
            parser.parseRequest(is);

            httprequest req = new httprequest();
            req.resource = parser.requestParams.get("RESOURCE").substring(1);
            req.method = parser.requestParams.get("METHOD");
            req.AcceptCharset = parser.requestParams.get("Accept-Charset");
            if (parser.requestParams.containsKey("If-Match")) {
                req.etag = parser.requestParams.get("If-Match").replace("\"","");
                //etag should no contain " symbol
            }
            return req;
        }

        private httpresponse getResponce(httprequest req) {

            httpresponse res = new httpresponse();

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
            res.ContentType = "text/html";
            res.charset = "utf-8";
            res.Code = 200;

            return res;
        }

        private void writeResponse(httpresponse res) throws IOException {

            /*String response = "HTTP/1.1 200 OK\r\n" +
                    "Server: YarServer/2009-09-09\r\n" +
                    "Content-Type: text/html\r\n" +
                    "Content-Length: " + 0 + "\r\n" +
                    "Connection: close\r\n\r\n";
            String result = response + s;*/
            os.write(res.getBytes());
            os.flush();
        }




    }
}
