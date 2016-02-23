package httpserver;


import httpserver.configurations.configuration;
import httpserver.requests.HttpRequestParser;
import httpserver.requests.HttpRequest;
import httpserver.resources.NaiveCachingResourceProvider;
import httpserver.responses.HttpResponce;
import httpserver.responses.HttpRequestProcessor;


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
            HttpResponce res = null;
            HttpRequestParser reqParser = new HttpRequestParser();
            HttpRequestProcessor resProc = new HttpRequestProcessor(rp);

            try {
                req = new HttpRequestParser().parseRequestParams(this.is).getRequestObject();
            } catch (IOException e) {
                e.printStackTrace(); //for simplicity
            }

            try {
                res = resProc.ProcessRequest(req);
                writeResponse(resProc.getResponceBytes(res));

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

        private void writeResponse(byte[] resBytes) throws IOException {
            os.write(resBytes);
            os.flush();
        }
    }
}
