import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
    public static void main(String[] args) throws Exception {
        String myIP = InetAddress.getLocalHost().getHostAddress();
        int myPort = 8080;

        ServerSocket serverSocket = new ServerSocket(myPort);
        System.out.println("Hosting Server on " + myIP + ":" + myPort);
        System.out.println("Waiting for connections...");
        Socket socket = serverSocket.accept();
        System.out.println("Someone connected to the server!");

        String theirIP = socket.getInetAddress().getHostAddress();
        int theirPort = socket.getPort();
        System.out.println("Client Info is " + theirIP + ":" + theirPort);

        Thread talkThread = new Thread(new TalkThread(socket));
        Thread listenThread = new Thread(new ListenThread(socket));
        talkThread.start();
        listenThread.start();
    }

    private static class TalkThread implements Runnable {
        private Socket socket;
        public TalkThread(Socket socket) {
            this.socket = socket;
        }
        @Override
        public void run() {
            try {
                System.out.println("Talk thread started!");
                OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream());
                PrintWriter pw = new PrintWriter(osw, true);
                Scanner scan  = new Scanner(System.in);
                while(!socket.isClosed()) {
                    String message = scan.nextLine();
                    pw.println(message);
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static class ListenThread implements Runnable {
        private Socket socket;
        public ListenThread(Socket socket) {
            this.socket = socket;
        }
        @Override
        public void run() {
            try {
                System.out.println("Listen thread started!");
                InputStreamReader isr = new InputStreamReader(socket.getInputStream());
                BufferedReader br = new BufferedReader(isr);
                while(!socket.isClosed()) {
                    String message = br.readLine();
                    System.out.println(message);
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
}
