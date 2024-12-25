import java.io.*;
import java.net.*;

import java.util.LinkedHashSet;
import java.util.Scanner;
import java.util.Set;

public class Server {
    private static Set<ClientData> clients;

    public static void main(String[] args) throws Exception {
        clients = new LinkedHashSet<>();

        Thread acceptThread = new Thread(new AcceptThread());
        acceptThread.start();
        Thread talkThread = new Thread(new TalkThread());
        talkThread.start();

        while(true) {}
    }

    public static void broadcast(String message) {
        for(ClientData c : clients) {
            c.getPrintWriter().println(message);
        }
    }

    private static class AcceptThread implements Runnable {
        public void run() {
            try {
                String myIP = InetAddress.getLocalHost().getHostAddress();
                int myPort = 8080;

                ServerSocket serverSocket = new ServerSocket(myPort);
                System.out.println("Hosting Server on " + myIP + ":" + myPort);

                while(!serverSocket.isClosed()) {
                    System.out.println("Waiting for connections...");
                    Socket socket = serverSocket.accept();
                    clients.add(new ClientData(socket));
                    System.out.println("Someone connected to the server!");

                    Thread listenThread = new Thread(new ListenThread(socket));
                    listenThread.start();
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static class TalkThread implements Runnable {
        @Override
        public void run() {
            try {
                System.out.println("Talk thread started!");
                Scanner scan  = new Scanner(System.in);
                while(true) {
                    String message = scan.nextLine();
                    broadcast(message);
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
                    broadcast(message);
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static class ClientData {
        private Socket socket;
        private String username;
        private BufferedReader br;
        private PrintWriter pw;

        public ClientData(Socket socket) throws Exception {
            try {
                this.socket = socket;
                this.username = "undefined";
                InputStreamReader isr = new InputStreamReader(socket.getInputStream());
                this.br = new BufferedReader(isr);
                OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream());
                this.pw = new PrintWriter(osw, true);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        public Socket getSocket() {
            return this.socket;
        }
        public String getUsername() {
            return this.username;
        }
        public BufferedReader getBufferedReader() {
            return this.br;
        }
        public PrintWriter getPrintWriter() {
            return this.pw;
        }
    }
}
