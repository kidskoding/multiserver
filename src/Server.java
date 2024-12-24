import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

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
    }
}
