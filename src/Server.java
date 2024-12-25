import java.io.*;
import java.net.*;

import java.util.LinkedHashSet;
import java.util.Scanner;
import java.util.Set;

public class Server {
    private static Set<ClientData> clients;

    public static void main(String[] args) throws Exception {
        clients = new LinkedHashSet<ClientData>();

        Thread acceptThread = new Thread(new AcceptThread());
        acceptThread.start();
        Thread talkThread = new Thread(new TalkThread());
        talkThread.start();
    }

    public static void broadcast(String message) {
        for(ClientData c : clients) {
            c.getPrintWriter().println(message);
        }
    }

    public static void broadcastUserList() {
        StringBuilder userList = new StringBuilder("[Users]").append(" ");
        for (ClientData c : clients) {
            userList.append(c.getUsername()).append(" ");
        }
        broadcast(userList.toString());
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

                    InputStreamReader isr = new InputStreamReader(socket.getInputStream());
                    BufferedReader br = new BufferedReader(isr);

                    String username = br.readLine();
                    String connectedIP = socket.getInetAddress().getHostAddress();
                    int connectedPort = socket.getPort();
                    System.out.println(username + " connected to the server!");
                    System.out.println(username + "'s info is " + connectedIP + ":" + connectedPort);
                    broadcast(username + " has joined the server!");
                    clients.add(new ClientData(socket, username));
                    broadcastUserList();

                    Thread listenThread = new Thread(new ListenThread(socket, username));
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
                Scanner scan  = new Scanner(System.in);
                while(true) {
                    String message = scan.nextLine();
                    broadcast("[Server]: " + message);
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static class ListenThread implements Runnable {
        private Socket socket;
        private String username;
        public ListenThread(Socket socket, String username) {
            this.socket = socket;
            this.username = username;
        }
        @Override
        public void run() {
            try {
                InputStreamReader isr = new InputStreamReader(socket.getInputStream());
                BufferedReader br = new BufferedReader(isr);
                while(!socket.isClosed()) {
                    String message = br.readLine();
                    if(message == null) {
                        System.out.println(username + " has left the server!");
                        broadcast(username + " has left the server!");
                        clients.removeIf(c -> c.getSocket().equals(socket));
                        broadcastUserList();
                        break;
                    } else {
                        broadcast("[" + username + "]: " + message);
                    }
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

        public ClientData(Socket socket, String username) throws Exception {
            try {
                this.socket = socket;
                this.username = username;
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
