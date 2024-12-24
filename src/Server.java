import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) throws Exception {
        String myIP = InetAddress.getLocalHost().getHostAddress();
        int myPort = 8080;

        ServerSocket serverSocket = new ServerSocket(myPort);
        System.out.println("Hosting Server on " + myIP + ":" + myPort);
        Socket socket = serverSocket.accept();
        System.out.println("Someone connected to the server!");

        String clientIP = socket.getInetAddress().getHostAddress();
        int clientPort = socket.getPort();
        System.out.println("Client connected to " + clientIP + ":" + clientPort);
    }
}
