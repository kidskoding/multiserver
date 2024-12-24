import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws Exception {
        String myIP = InetAddress.getLocalHost().getHostAddress();
        int myPort = 8080;

        Scanner scan = new Scanner(System.in);
        System.out.print("Enter IP to connect to: ");
        String theirIP = scan.nextLine();
        System.out.print("Enter port to connect to: ");
        int theirPort = scan.nextInt(); scan.nextLine();
        System.out.println("Connecting to server...");
        Socket socket = new Socket(theirIP, theirPort);

        System.out.println("Successfully joined the server!");
    }
}
