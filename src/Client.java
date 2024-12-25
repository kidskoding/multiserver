import java.io.*;
import java.net.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.util.Scanner;

public class Client implements ActionListener {

    private static Socket socket;
    private static JTextArea displayArea, userlistArea;
    private static JTextField messageField;

    public static void main(String[] args) throws Exception {
        String myIP = InetAddress.getLocalHost().getHostAddress();
        int myPort = 8080;

        Scanner scan = new Scanner(System.in);
        System.out.print("Enter IP to connect to: ");
        String serverIP = scan.nextLine();
        System.out.print("Enter port to connect to: ");
        int serverPort = scan.nextInt(); scan.nextLine();
        System.out.print("Enter username: ");
        String username = scan.nextLine();

        System.out.println("Connecting to server...");
        socket = new Socket(serverIP, serverPort);

        sendMessage(username);

        new Client();

        System.out.println("Successfully joined the server!");
        displayArea.append(username + " has joined the server!\n");

        Thread talkThread = new Thread(new TalkThread());
        Thread listenThread = new Thread(new ListenThread());
        talkThread.start();
        listenThread.start();
    }

    public Client() {
        JFrame frame = new JFrame("TCP Multiserver");
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        int vsb = ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS;
        int hsb = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS;

        Font font = new Font("Times New Roman", Font.PLAIN, 40);
        displayArea = new JTextArea();
        displayArea.setFont(font);
        JScrollPane displayPane = new JScrollPane(displayArea, vsb, hsb);
        displayPane.setPreferredSize(new Dimension(750, 700));

        userlistArea = new JTextArea();
        userlistArea.setFont(font);
        JScrollPane userlistPane = new JScrollPane(userlistArea, vsb, hsb);
        userlistPane.setPreferredSize(new Dimension(250, 700));

        messageField = new JTextField("");
        messageField.setFont(font);
        messageField.setPreferredSize(new Dimension(1000, 100));

        panel.add(displayPane, BorderLayout.CENTER);
        panel.add(userlistPane, BorderLayout.WEST);
        panel.add(messageField, BorderLayout.SOUTH);

        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setAlwaysOnTop(true);
        frame.setAlwaysOnTop(false);

        messageField.addActionListener(this);
        messageField.grabFocus();
        messageField.requestFocus();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            String message = messageField.getText();
            messageField.setText("");
            sendMessage(message);
        } catch(Exception err) {
            err.printStackTrace();
        }
    }

    public static void sendMessage(String message) {
        try {
            OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream());
            PrintWriter pw = new PrintWriter(osw, true);
            pw.println(message);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private static class TalkThread implements Runnable {
        @Override
        public void run() {
            try {
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
        @Override
        public void run() {
            try {
                InputStreamReader isr = new InputStreamReader(socket.getInputStream());
                BufferedReader br = new BufferedReader(isr);
                while(!socket.isClosed()) {
                    String message = br.readLine();
                    displayArea.append(message + "\n");
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
}
