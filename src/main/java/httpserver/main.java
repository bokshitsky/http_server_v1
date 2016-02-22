package httpserver;


import java.net.ServerSocket;
import java.net.Socket;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;

/**
 * Created by yar 09.09.2009
 */
public class main {

    public static void main(String[] args) throws Throwable {

        if (args.length >= 1) {
            configurationProvider rs = new configurationProvider(args[0]);
        }

        resourcesProvider rs = new resourcesProvider(config);

        configuration config = new configuration(true,"./resources",8081);

        ServerSocket ss = new ServerSocket(8081);
        while (true) {
            Socket s = ss.accept();
            System.err.println("Client accepted");
            new Thread(new SocketProcessor(s)).start();
        }
    }

    private static class SocketProcessor implements Runnable {

        private Socket s;
        private InputStream is;
        private OutputStream os;

        private SocketProcessor(Socket s) throws Throwable {
            this.s = s;
            this.is = s.getInputStream();
            this.os = s.getOutputStream();
        }

        public void run() {
            try {
                readInputHeaders();
                writeResponse("<html><body><h1>Hello from Habrahabr</h1></body></html>");
            } catch (Throwable t) {
                //do nothing
            } finally {
                try {
                    s.close();
                } catch (Throwable t) {
                    //do nothing
                }
            }
        }

        private void writeResponse(String s) throws Throwable {
            String response = "HTTP/1.1 200 OK\r\n" +
                    "Server: YarServer/2009-09-09\r\n" +
                    "Content-Type: text/html\r\n" +
                    "Content-Length: " + s.length() + "\r\n" +
                    "Connection: close\r\n\r\n";
            String result = response + s;
            os.write(result.getBytes());
            os.flush();
        }

        private void readInputHeaders() throws Throwable {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            while(true) {
                String s = br.readLine();
                System.out.println(s);
                System.out.flush();
                if(s == null || s.trim().length() == 0) {
                    break;
                }
            }
        }
    }
}

